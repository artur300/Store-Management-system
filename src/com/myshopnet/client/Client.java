package com.myshopnet.client;

import com.myshopnet.client.utils.UIUtils;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8080;

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Scanner scanner;
    private String currentUserId;
    private boolean isConnected = false;

    public Client() {
        this.scanner = new Scanner(System.in);
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.start();
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
    }

    private void showMainMenu() {
        MenuHandler menuHandler = new MenuHandler(this, currentUser);
        menuHandler.showMainMenu();
    }

    // Communication methods
    public User sendRequest(String request) {
        try {
            out.println(request);
            return in.readLine();
        } catch (IOException e) {
            System.err.println("Communication error: " + e.getMessage());
            return null;
        }
    }

    public Scanner getScanner() {
        return scanner;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public boolean isConnected() {
        return isConnected;
    }

    private void cleanup() {
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null) socket.close();
            if (scanner != null) scanner.close();
        } catch (IOException e) {
            System.err.println("Error during cleanup: " + e.getMessage());
        }
    }
}