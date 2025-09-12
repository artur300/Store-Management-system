package com.myshopnet.client;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import com.myshopnet.client.utils.UIUtils;

import java.util.*;
import java.util.concurrent.*;

public class ChatMenu implements Menu {
    private Scanner scanner;
    private boolean inChat = false;
    private String currentChatId = null;
    private ExecutorService chatExecutor;

    public ChatMenu() {
        this.scanner = Singletons.CLIENT.getScanner();
        this.chatExecutor = Executors.newSingleThreadExecutor();
    }

    public void show() {
        while (true) {
            UIUtils.printMenuHeader("CHAT SYSTEM");
            UIUtils.printLine("Inter-branch communication");
            UIUtils.printEmptyLine();

            UIUtils.printMenuOption(1, "Start New Chat");
            if (Auth.getCurrentUser().get("employeeType").getAsString().equals("SHIFT_MANAGER")) {
                UIUtils.printMenuOption(2, "Join Existing Chat");
            }
            UIUtils.printMenuOption(0, "Back to Main Menu");

            UIUtils.printMenuFooter();

            int choice = UIUtils.getIntInput(scanner);

            switch (choice) {
                case 1:
                    startNewChat();
                    break;
                case 2:
                    if (Auth.getCurrentUser().get("employeeType").getAsString().equals("SHIFT_MANAGER")) {
                        joinExistingChat();
                    }
                    break;
                case 3:
                    viewChatHistory();
                    break;
                case 0:
                    return;
                default:
                    UIUtils.showError("Invalid choice. Please try again.");
                    UIUtils.waitForEnter(scanner);
            }
        }
    }

    private void startNewChat() {
        Map<String, String> requestMap = new HashMap<>();
        UIUtils.printMenuHeader("START NEW CHAT");

        String userId = Auth.getCurrentUser().get("userId").getAsString();
        requestMap.put("userId", userId);

        // Load branches to choose from
        Request request = new Request("getAllBranches", Singletons.GSON.toJson(requestMap));
        JsonObject response = Singletons.CLIENT.sendRequest(request);

        if (response == null) {
            UIUtils.showError("Connection error while loading branches");
            return;
        }

        if (response.get("success").getAsBoolean()) {
            String chosenBranchId = displayAvailableBranches(response.getAsJsonArray("message"));

            if (chosenBranchId != null) {
                Map<String, String> requestMapToChat = new HashMap<>();
                requestMapToChat.put("userIdRequesting", userId);
                requestMapToChat.put("branchId", chosenBranchId);

                Request requestToChat = new Request("startChat", Singletons.GSON.toJson(requestMapToChat));
                JsonObject chatResponse = Singletons.CLIENT.sendRequest(requestToChat);

                if (chatResponse != null && chatResponse.get("success").getAsBoolean()) {
                    try {
                        if (chatResponse.get("message").isJsonObject()) {
                            JsonObject chatObj = chatResponse.getAsJsonObject("message");
                            String chatId = chatObj.get("id").getAsString();
                            UIUtils.showSuccess("Chat started! ID: " + chatId);
                            startChatSession(chatId);
                        } else {
                            String msg = chatResponse.get("message").getAsString();
                            if (msg != null && msg.toLowerCase().contains("queue")) {
                                UIUtils.showInfo("All employees are busy. You were added to the queue. You'll get a notification when available.");
                            } else {
                                UIUtils.showInfo(msg);
                            }
                        }
                    } catch (Exception ex) {
                        try {
                            String msgStr = chatResponse.get("message").getAsString();
                            JsonObject chatObj = Singletons.GSON.fromJson(msgStr, JsonObject.class);
                            if (chatObj != null && chatObj.has("id")) {
                                String chatId = chatObj.get("id").getAsString();
                                UIUtils.showSuccess("Chat started! ID: " + chatId);
                                startChatSession(chatId);
                                return;
                            }
                        } catch (Exception ignore) { }
                        UIUtils.showError("Unexpected response from server.");
                    }
                } else {
                    String err = (chatResponse != null && chatResponse.has("message")) ? chatResponse.get("message").getAsString() : "Connection error";
                    UIUtils.showError(err);
                }
            }
        } else {
            UIUtils.showError(response.has("message") ? response.get("message").getAsString() : "Failed to load branches");
        }
    }

    private String displayAvailableBranches(JsonArray branches) {
        try {
            while(true) {
                List<String> branchIds = new ArrayList<>();

                if (!branches.isEmpty()) {
                    int i = 0;

                    for (JsonElement row : branches) {
                        JsonObject rowObject = row.getAsJsonObject();

                        branchIds.add(rowObject.get("branchId").getAsString());
                        UIUtils.printMenuOption(i + 1, rowObject.get("name").getAsString());

                        i++;
                    }

                    int choice = UIUtils.getIntInput(scanner);

                    if (choice < 1 || choice > branchIds.size()) {
                        UIUtils.showError("Invalid choice. Please try again.");
                    }

                    return branchIds.get(choice - 1);
                }
            }
        }
        catch (Exception e) {
            UIUtils.showError("Error displaying branches: " + e.getMessage());
        }

        return null;
    }

    private void startChatSession(String chatId) {
        this.currentChatId = chatId;
        this.inChat = true;

        UIUtils.clearScreen();
        UIUtils.printBorder();
        UIUtils.printTitle("CHAT SESSION - " + chatId);
        UIUtils.printBorder();
        UIUtils.printLine("Type '/exit' to leave chat, '/history' for chat history");
        UIUtils.printBottomBorder();

        chatExecutor.submit(new MessageReceiver());

        // Main chat loop
        while (inChat) {
            System.out.print("You: ");
            String message = scanner.nextLine();

            if (message.equals("/exit")) {
                exitChat();
                break;
            } else if (message.equals("/history")) {
                showCurrentChatHistory();
                continue;
            }

            if (!message.trim().isEmpty()) {
                UIUtils.showInfo("Sending chat messages is not implemented in this client yet.");
            }
        }

        this.inChat = false;
        this.currentChatId = null;
    }

    private class MessageReceiver implements Runnable {
        @Override
        public void run() {
            while (inChat) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception ignored) {}
            }
        }
    }

    private void displayNewMessages(String response) {
        try {
            // Expected format: NEW_MESSAGES|sender:timestamp:message|...
            String[] parts = response.split("\\|");
            for (int i = 1; i < parts.length; i++) {
                String[] messageData = parts[i].split(":", 3);
                if (messageData.length >= 3) {
                    System.out.println(messageData[0] + " [" + messageData[1] + "]: " + messageData[2]);
                    System.out.print("You: ");
                }
            }
        } catch (Exception e) {
            System.err.println("Error displaying messages: " + e.getMessage());
        }
    }

    private void exitChat() {
        try {
            JsonObject data = new JsonObject();
            data.addProperty("userId", Auth.getCurrentUser().get("userId").getAsString());
            data.addProperty("chatId", currentChatId);
            Request req = new Request("endChat", Singletons.GSON.toJson(data));
            Singletons.CLIENT.sendRequest(req);
        } catch (Exception ignored) {}
        inChat = false;
        UIUtils.showInfo("You have left the chat.");
    }

    private void showCurrentChatHistory() {
        UIUtils.showInfo("Chat history view is not implemented in this client.");
    }

    private void joinExistingChat() {
        if (!Auth.getCurrentUser().get("employeeType").getAsString().equals("SHIFT_MANAGER")) {
            UIUtils.showError("Only shift managers can join existing chats");
            UIUtils.waitForEnter(scanner);
            return;
        }
        UIUtils.printMenuHeader("JOIN EXISTING CHAT");
        UIUtils.showInfo("Joining existing chats is not implemented in this client.");
        UIUtils.waitForEnter(scanner);
    }

    private void viewPendingRequests() {
        UIUtils.printMenuHeader("PENDING CHAT REQUESTS");
        UIUtils.showInfo("Pending requests view is not implemented in this client.");
        UIUtils.waitForEnter(scanner);
    }

    private void respondToPendingRequest(String requestId) {
        UIUtils.showInfo("Responding to requests is not implemented in this client.");
    }

    private void viewChatHistory() {
        UIUtils.printMenuHeader("CHAT HISTORY");
        UIUtils.showInfo("Chat history is not implemented in this client.");
        UIUtils.waitForEnter(scanner);
    }

    private void displayActiveChats(String response) {
        UIUtils.showInfo("Not implemented.");
    }

    private void displayPendingRequests(JsonObject response) {
        UIUtils.showInfo("Not implemented.");
    }

    private void displayChatHistoryList(JsonObject response) {
        UIUtils.showInfo("Not implemented.");
    }

    private void viewSpecificChatHistory(String chatId) {
        UIUtils.showInfo("Not implemented.");
    }

    private void saveChatHistory(String chatId, String chatData) {
        UIUtils.showInfo("Not implemented.");
    }



    public void cleanup() {
        if (chatExecutor != null && !chatExecutor.isShutdown()) {
            chatExecutor.shutdown();
            try {
                if (!chatExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                    chatExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                chatExecutor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
}