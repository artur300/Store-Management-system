package com.myshopnet.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.myshopnet.AppState;
import com.myshopnet.client.models.UserTypeLoggedIn;
import com.myshopnet.client.utils.UIUtils;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Client {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8080;

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Scanner scanner;
    private UserTypeLoggedIn currentUserType = UserTypeLoggedIn.NONE;
    private boolean isConnected = false;
    private ChatClient chatClient = Singletons.CHAT_CLIENT;

    public Client() {
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        try {
            connectToServer();
            showWelcomeScreen();
            loginProcess();
            while (isConnected) {
                if (Auth.getCurrentUser() != null) {
                    showMainMenu();
                }
                while (isConnected && AppState.chatActive) {
                    try { Thread.sleep(200); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                }
                if (!isConnected) {
                    break;
                }
                if (Auth.getCurrentUser() == null) {
                    break;
                }
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
        Auth.setCurrentUserType(UserTypeLoggedIn.NONE);
        Singletons.REGISTER_MENU.show();

        if (Auth.getCurrentUser() == null) {
            return;
        }

        Auth.setCurrentUserType(UserTypeLoggedIn.valueOf(Auth.getCurrentUser().get("role").getAsString()));
        String userId = Auth.getUsername();

        if (Auth.getCurrentUserType().equals(UserTypeLoggedIn.ADMIN) || Auth.getCurrentUserType().equals(UserTypeLoggedIn.EMPLOYEE)) {
            chatClient.start(chatRequest -> {
                chatClient.handleChatRequest(chatRequest);
            });
        }

        showMenuAccordingToUser();
    }

    public void showMenuAccordingToUser() {
        switch (Auth.getCurrentUserType()) {
            case ADMIN:
                Singletons.ADMIN_MENU.show();
                break;

            case EMPLOYEE:
                Singletons.EMPLOYEE_MENU.show();
                break;

            case CUSTOMER:
                Singletons.CUSTOMER_MENU.show();
                break;
        }
    }

    private void showMainMenu() {
        MainMenu menuHandler = new MainMenu();
        menuHandler.show();
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

    public Scanner getScanner() {
        return scanner;
    }

    public boolean isConnected() {
        return isConnected;
    }

    private void cleanup() {
        try {
            if (chatClient != null) chatClient.stop();
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
                chatClient.stop();
                loginProcess();
            }
            else {
                String error = response != null ? response.get("message").getAsString() : "Can't log out";
                UIUtils.showError(error);
            }
        }
    }
}