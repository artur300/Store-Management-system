package com.myshopnet.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.myshopnet.client.models.UserTypeLoggedIn;
import com.myshopnet.client.utils.UIUtils;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Client {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8080;

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private final Scanner scanner;
    private UserTypeLoggedIn currentUserType = UserTypeLoggedIn.NONE;
    private boolean isConnected = false;

    // Push
    private final PushClient pushClient = new PushClient();
    private boolean pushRunning = false;

    public Client() {
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        try {
            connectToServer();
            showWelcomeScreen();
            loginProcess();
            if (Auth.getCurrentUser() != null) {
                showMainMenu();
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        } finally {
            cleanup();
        }
    }

    private void connectToServer() throws IOException {
        System.out.println("Connecting to server...");
        socket = new Socket(SERVER_HOST, SERVER_PORT);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        isConnected = true;
        System.out.println("Connected successfully!");
    }

    private void showWelcomeScreen() {
        UIUtils.clearScreen();
        UIUtils.printBorder();
        System.out.println("║           BRANCH MANAGEMENT SYSTEM           ║");
        UIUtils.printBorder();
        System.out.println();
    }

    private void loginProcess() {
        Singletons.REGISTER_MENU.show();
        if (Auth.getCurrentUser() == null) {
            return;
        }

        Auth.setCurrentUserType(
                UserTypeLoggedIn.valueOf(Auth.getCurrentUser().get("role").getAsString())
        );

        // PATCH: אין שליחת AVAILABLE כאן (נמנע כפילות ומרוץ).
        // נרשמים לפוש עכשיו, כדי שלא נפספס chatCreated.
        ensurePushSubscribed(); // PATCH

        showMenuAccordingToUser();
    }

    private void ensurePushSubscribed() {
        if (pushRunning || Auth.getCurrentUser() == null) return;

        try {
            String userId = Auth.getCurrentUser().get("userId").getAsString();
            pushClient.start(userId, evt -> {
                try {
                    if (evt == null || !evt.has("type")) return;
                    String type = evt.get("type").getAsString();

                    if ("chatCreated".equalsIgnoreCase(type)) {
                        String chatId = evt.get("chatId").getAsString();
                        // מנווטים לאירוע ב-ChatMenu (דרך ה-hook בממשק Menu)
                        Singletons.CHAT_MENU.onChatCreated(chatId);

                    } else if ("newMessage".equalsIgnoreCase(type)) {
                        String chatId = evt.get("chatId").getAsString();
                        String sender = evt.get("sender").getAsString();
                        String msg = evt.get("message").getAsString();
                        // מנווטים לאירוע הודעה נכנסת
                        Singletons.CHAT_MENU.onIncomingMessage(chatId, sender, msg);
                    }
                } catch (Exception ignored) {}
            });
            pushRunning = true;
        } catch (Exception e) {
            System.err.println("Failed to subscribe to push: " + e.getMessage());
            pushRunning = false;
        }
    }

    public void showMenuAccordingToUser() {
        Auth.setCurrentUserType(UserTypeLoggedIn.valueOf(Auth.getCurrentUser().get("role").getAsString()));

        switch (Auth.getCurrentUserType()) {
            case ADMIN -> Singletons.ADMIN_MENU.show();
            case EMPLOYEE -> Singletons.EMPLOYEE_MENU.show(); // כאן יישלח AVAILABLE יחיד עם userId
            case CUSTOMER -> Singletons.CUSTOMER_MENU.show();
        }
    }

    private void showMainMenu() {
        Singletons.MAIN_MENU.show();
    }

    public UserTypeLoggedIn getCurrentUserType() {
        return currentUserType;
    }

    public synchronized JsonObject sendRequest(Request request) {
        try {
            String requestJson = Singletons.GSON.toJson(request);
            out.println(requestJson);
            out.flush();

            String responseData = in.readLine();
            if (responseData == null) {
                System.err.println("Server closed the connection.");
                isConnected = false;
                return null;
            }
            if (responseData.trim().isEmpty()) {
                return new JsonObject();
            }
            return JsonParser.parseString(responseData).getAsJsonObject();
        } catch (IOException e) {
            System.err.println("Communication error: " + e.getMessage());
            isConnected = false;
            return null;
        }
    }

    public JsonObject sendRequest(String legacyRequest) {
        System.err.println("[Legacy API] Ignored request: " + legacyRequest);
        return null;
    }

    public Scanner getScanner() {
        return scanner;
    }

    public boolean isConnected() {
        return isConnected;
    }

    private void cleanup() {
        try {
            // עצירת push
            try { pushClient.stop(); } catch (Exception ignored) {}

            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null) socket.close();
            if (scanner != null) scanner.close();
        } catch (IOException e) {
            System.err.println("Error during cleanup: " + e.getMessage());
        }
    }

    public void logout() {
        if (Auth.getCurrentUser() != null) {
            Map<String, String> requestMap = new HashMap<>();
            requestMap.put("username", Auth.getUsername());
            Request request = new Request("logout", Singletons.GSON.toJson(requestMap));
            JsonObject response = sendRequest(request);

            if (response != null && response.get("success").getAsBoolean()) {
                UIUtils.showSuccess("Logged out successfully!");
                UIUtils.clearScreen();
                Auth.setCurrentUserType(UserTypeLoggedIn.NONE);
                Singletons.REGISTER_MENU.show();
            } else {
                String error = response != null ? response.get("message").getAsString() : "Can't log out";
                UIUtils.showError(error);
            }
        }
    }
}
