package com.myshopnet.client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.myshopnet.client.models.UserTypeLoggedIn;
import com.myshopnet.client.utils.UIUtils;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8080;
    private final Gson gson = new Gson();

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Scanner scanner;
    private JsonObject currentUser;
    private UserTypeLoggedIn currentUserType = UserTypeLoggedIn.NONE;
    private boolean isConnected = false;
    private PushClient pushClient = new PushClient();

    public Client() {
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        try {
            connectToServer();
            showWelcomeScreen();
            loginProcess();
            if (currentUser != null) {
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
        LoginHandler loginHandler = new LoginHandler(this);
        currentUser = loginHandler.handleLogin();

        if (currentUser == null) return;
        currentUserType = UserTypeLoggedIn.valueOf(currentUser.get("role").getAsString());

        String userId = currentUser.get("userId").getAsString();
        pushClient.start(userId, evt -> {
            try {
                String type = evt.has("type") ? evt.get("type").getAsString() : "";
                if ("chatCreated".equals(type)) {
                    String chatId = evt.get("chatId").getAsString();
                    System.out.println("\n[Notification] A chat is ready for you. Chat ID: " + chatId);
                    System.out.print("Press Enter to continue...");
                }
            } catch (Exception ignored) { }
        });
    }

    private void showMainMenu() {
        MenuHandler menuHandler = new MenuHandler(this, currentUser);
        menuHandler.showMainMenu();
    }

    public synchronized JsonObject sendRequest(Request request) {
        try {
            String requestJson = gson.toJson(request);
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

    public JsonObject getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(JsonObject user) {
        this.currentUser = user;
    }

    public boolean isConnected() {
        return isConnected;
    }

    private void cleanup() {
        try {
            if (pushClient != null) pushClient.stop();
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null) socket.close();
            if (scanner != null) scanner.close();
        } catch (IOException e) {
            System.err.println("Error during cleanup: " + e.getMessage());
        }
    }
}