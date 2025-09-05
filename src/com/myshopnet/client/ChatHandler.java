package com.myshopnet.client;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import com.myshopnet.client.utils.UIUtils;
import com.myshopnet.models.Branch;

import java.util.*;
import java.util.concurrent.*;

public class ChatHandler {
    private final Gson gson = new Gson();

    private Client client;
    private JsonObject currentUser;
    private Scanner scanner;
    private boolean inChat = false;
    private String currentChatId = null;
    private ExecutorService chatExecutor;

    public ChatHandler(Client client, JsonObject currentUser) {
        this.client = client;
        this.currentUser = currentUser;
        this.scanner = client.getScanner();
        this.chatExecutor = Executors.newSingleThreadExecutor();
    }

    public void showChatMenu() {
        while (true) {
            UIUtils.printMenuHeader("CHAT SYSTEM");
            UIUtils.printLine("Inter-branch communication");
            UIUtils.printEmptyLine();

            UIUtils.printMenuOption(1, "Start New Chat");
            if (currentUser.get("employeeType").getAsString().equals("SHIFT_MANAGER")) {
                UIUtils.printMenuOption(2, "Join Existing Chat");
            }
            UIUtils.printMenuOption(4, "Chat History");
            UIUtils.printMenuOption(0, "Back to Main Menu");

            UIUtils.printMenuFooter();

            int choice = UIUtils.getIntInput(scanner);

            switch (choice) {
                case 1:
                    startNewChat();
                    break;
                case 2:
                    if (currentUser.get("employeeType").getAsString().equals("SHIFT_MANAGER")) {
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

        // First, get list of branches and get user input.

        String userId = currentUser.get("userId").getAsString();
        requestMap.put("userId", userId);

        Request request = new Request("getAllBranches", gson.toJson(requestMap));
        JsonObject response = client.sendRequest(request);

        if (response != null && response.get("success").getAsBoolean()) {
            String chosenBranchId = displayAvailableBranches(response.getAsJsonArray("message"));


            if (chosenBranchId != null) {
                Map<String, String> requestMapToChat = new HashMap<>();
                requestMapToChat.put("userIdRequesting", userId);
                requestMapToChat.put("branchid", chosenBranchId);

                Request requestToChat = new Request("startChat", gson.toJson(chosenBranchId));
            }
        }


//        String request = "GET_AVAILABLE_EMPLOYEES|" + currentUser.get("branchId").getAsString();
//        JsonObject response = client.sendRequest(request);
//
//        if (response != null && response.startsWith("AVAILABLE_EMPLOYEES")) {
//            displayAvailableEmployees(response);
//
//            String targetEmployeeId = UIUtils.getStringInput(scanner, "Enter employee ID to chat with: ");
//            String message = UIUtils.getStringInput(scanner, "Initial message: ");
//
//            String chatRequest = String.format("REQUEST_CHAT|%s|%s|%s|%s",
//                    currentUser.get("employeeNumber").getAsString(), targetEmployeeId, message, currentUser.get("branchId").getAsString());
//            JsonObject chatResponse = client.sendRequest(chatRequest);
//
//            if (chatResponse != null && chatResponse.startsWith("CHAT_STARTED")) {
//                String chatId = chatResponse.split("\\|")[1];
//                startChatSession(chatId);
//            } else if (chatResponse != null && chatResponse.startsWith("CHAT_QUEUED")) {
//                UIUtils.showInfo("Employee is busy. You've been added to the queue. You'll be notified when they're available.");
//            } else {
//                String error = chatResponse != null ? chatResponse.replace("CHAT_FAILED|", "") : "Connection error";
//                UIUtils.showError(error);
//            }
//        } else {
//            UIUtils.showError("No available employees found or connection error");
//        }
//
//        UIUtils.waitForEnter(scanner);
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
                String sendRequest = String.format("SEND_MESSAGE|%s|%s|%s",
                        chatId, currentUser.getEmployeeNumber(), message);
                client.sendRequest(sendRequest);
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
                    String request = "GET_CHAT_MESSAGES|" + currentChatId + "|" + currentUser.get("employeeNumber").getAsString();
                    JsonObject response = client.sendRequest(request);

                    if (response != null && response.startsWith("NEW_MESSAGES")) {
                        displayNewMessages(response);
                    }

                    Thread.sleep(1000); // Check for new messages every second
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    System.err.println("Error receiving messages: " + e.getMessage());
                }
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
        String request = String.format("EXIT_CHAT|%s|%s", currentChatId, currentUser.get("employeeNumber").getAsString());
        client.sendRequest(request);
        inChat = false;
        UIUtils.showInfo("You have left the chat.");
    }

    private void showCurrentChatHistory() {
        String request = "GET_CHAT_HISTORY|" + currentChatId;
        JsonObject response = client.sendRequest(request);

        if (response != null && response.startsWith("CHAT_HISTORY")) {
            System.out.println("\n--- Chat History ---");
            displayChatHistoryMessages(response);
            System.out.println("--- End History ---\n");
        }
    }

    private void joinExistingChat() {
        if (!currentUser.get("employeeType").getAsString().equals("SHIFT_MANAGER")) {
            UIUtils.showError("Only shift managers can join existing chats");
            UIUtils.waitForEnter(scanner);
            return;
        }

        UIUtils.printMenuHeader("JOIN EXISTING CHAT");

        String request = "GET_ACTIVE_CHATS|" + currentUser.get("branchId").getAsString();
        JsonObject response = client.sendRequest(request);

        if (response != null && response.startsWith("ACTIVE_CHATS")) {
            displayActiveChats(response);

            String chatId = UIUtils.getStringInput(scanner, "Enter chat ID to join: ");

            String joinRequest = String.format("JOIN_CHAT|%s|%s", chatId, currentUser.get("employeeNumber").getAsString());
            JsonObject joinResponse = client.sendRequest(joinRequest);

            if (joinResponse != null && joinResponse.equals("CHAT_JOINED")) {
                startChatSession(chatId);
            } else {
                String error = joinResponse != null ? joinResponse.replace("JOIN_FAILED|", "") : "Connection error";
                UIUtils.showError(error);
                UIUtils.waitForEnter(scanner);
            }
        } else {
            UIUtils.showError("No active chats found or connection error");
            UIUtils.waitForEnter(scanner);
        }
    }

    private void displayActiveChats(String response) {
        try {
            // Expected format: ACTIVE_CHATS|chatId:participants:startTime:branch1:branch2|...
            String[] parts = response.split("\\|");
            if (parts.length > 1) {
                List<String[]> rows = new ArrayList<>();

                for (int i = 1; i < parts.length; i++) {
                    String[] chatData = parts[i].split(":");
                    if (chatData.length >= 5) {
                        rows.add(new String[]{
                                chatData[0], // Chat ID
                                chatData[1], // Participants
                                chatData[2], // Start Time
                                chatData[3] + " ↔ " + chatData[4] // Branches
                        });
                    }
                }

                String[] headers = {"Chat ID", "Participants", "Start Time", "Branches"};
                UIUtils.printTable(headers, rows);
                UIUtils.showInfo("Total active chats: " + rows.size());
            } else {
                UIUtils.printLine("No active chats found");
            }
        } catch (Exception e) {
            UIUtils.showError("Error displaying active chats: " + e.getMessage());
        }
    }

    private void viewPendingRequests() {
        UIUtils.printMenuHeader("PENDING CHAT REQUESTS");

        String request = "GET_PENDING_REQUESTS|" + currentUser.get("employeeNumber").getAsString();
        JsonObject response = client.sendRequest(request);

        if (response != null && response.startsWith("PENDING_REQUESTS")) {
            displayPendingRequests(response);

            if (response.split("\\|").length > 1) {
                System.out.print("\nRespond to request? (y/N): ");
                String respond = scanner.nextLine().trim().toLowerCase();

                if (respond.equals("y") || respond.equals("yes")) {
                    String requestId = UIUtils.getStringInput(scanner, "Enter request ID to respond to: ");
                    respondToPendingRequest(requestId);
                }
            }
        } else {
            UIUtils.showError("Failed to retrieve pending requests");
        }

        UIUtils.waitForEnter(scanner);
    }

    private void displayPendingRequests(JsonObject response) {
        try {
            // Expected format: PENDING_REQUESTS|requestId:fromEmployee:fromBranch:message:timestamp|...
            String[] parts = response.split("\\|");
            if (parts.length > 1) {
                List<String[]> rows = new ArrayList<>();

                for (int i = 1; i < parts.length; i++) {
                    String[] requestData = parts[i].split(":", 5);
                    if (requestData.length >= 5) {
                        rows.add(new String[]{
                                requestData[0], // Request ID
                                requestData[1], // From Employee
                                requestData[2], // From Branch
                                requestData[3], // Message
                                requestData[4]  // Timestamp
                        });
                    }
                }

                String[] headers = {"Request ID", "From", "Branch", "Message", "Time"};
                UIUtils.printTable(headers, rows);
            } else {
                UIUtils.printLine("No pending requests found");
            }
        } catch (Exception e) {
            UIUtils.showError("Error displaying pending requests: " + e.getMessage());
        }
    }

    private void respondToPendingRequest(String requestId) {
        String request = String.format("RESPOND_TO_REQUEST|%s|%s|ACCEPT",
                requestId, currentUser.get("employeeNumber").getAsString());
        JsonObject response = client.sendRequest(request);

        if (response != null && response.startsWith("CHAT_STARTED")) {
            String chatId = response.split("\\|")[1];
            UIUtils.showSuccess("Chat request accepted! Starting chat...");
            Thread.sleep(2000);
            startChatSession(chatId);
        } else {
            String error = response != null ? response.replace("RESPONSE_FAILED|", "") : "Connection error";
            UIUtils.showError(error);
        }
    }

    private void viewChatHistory() {
        UIUtils.printMenuHeader("CHAT HISTORY");

        UIUtils.printLine("1. My Chat History");
        UIUtils.printLine("2. Branch Chat History");
        if (currentUser.get("employeeType").getAsString().equals("SHIFT_MANAGER")) {
            UIUtils.printLine("3. All Chats History");
        }
        System.out.print("Select option: ");

        int choice = UIUtils.getIntInput(scanner);
        String request = "";

        switch (choice) {
            case 1:
                request = "GET_MY_CHAT_HISTORY|" + currentUser.get("employeeNumber").getAsString();
                break;
            case 2:
                request = "GET_BRANCH_CHAT_HISTORY|" + currentUser.get("branchId").getAsString();
                break;
            case 3:
                if (currentUser.getAsString().equals("SHIFT_MANAGER")) {
                    request = "GET_ALL_CHAT_HISTORY";
                } else {
                    UIUtils.showError("Access denied");
                    UIUtils.waitForEnter(scanner);
                    return;
                }
                break;
            default:
                UIUtils.showError("Invalid choice");
                UIUtils.waitForEnter(scanner);
                return;
        }

        JsonObject response = client.sendRequest(request);

        if (response != null && response.startsWith("CHAT_HISTORY_LIST")) {
            displayChatHistoryList(response);

            String chatId = UIUtils.getStringInput(scanner, "Enter chat ID to view details (or press Enter to go back): ");
            if (!chatId.trim().isEmpty()) {
                viewSpecificChatHistory(chatId);
            }
        } else {
            UIUtils.showError("Failed to retrieve chat history");
        }

        UIUtils.waitForEnter(scanner);
    }

    private void displayChatHistoryList(JsonObject response) {
        try {
            // Expected format: CHAT_HISTORY_LIST|chatId:participants:startTime:endTime:messageCount|...
            String[] parts = response.split("\\|");
            if (parts.length > 1) {
                List<String[]> rows = new ArrayList<>();

                for (int i = 1; i < parts.length; i++) {
                    String[] historyData = parts[i].split(":");
                    if (historyData.length >= 5) {
                        rows.add(new String[]{
                                historyData[0], // Chat ID
                                historyData[1], // Participants
                                historyData[2], // Start Time
                                historyData[3], // End Time
                                historyData[4]  // Message Count
                        });
                    }
                }

                String[] headers = {"Chat ID", "Participants", "Start", "End", "Messages"};
                UIUtils.printTable(headers, rows);
            } else {
                UIUtils.printLine("No chat history found");
            }
        } catch (Exception e) {
            UIUtils.showError("Error displaying chat history: " + e.getMessage());
        }
    }

    private void viewSpecificChatHistory(String chatId) {
        UIUtils.printMenuHeader("CHAT DETAILS - " + chatId);

        String request = "GET_CHAT_HISTORY|" + chatId;
        JsonObject response = client.sendRequest(request);

        if (response != null && response.startsWith("CHAT_HISTORY")) {
            displayChatHistoryMessages(response);

            if (currentUser.getAsString().equals("ADMIN")) {
                System.out.print("\nSave this chat to file? (y/N): ");
                String save = scanner.nextLine().trim().toLowerCase();
                if (save.equals("y") || save.equals("yes")) {
                    saveChatHistory(chatId, response);
                }
            }
        } else {
            UIUtils.showError("Failed to retrieve chat details");
        }
    }

    private void saveChatHistory(String chatId, String chatData) {
        String request = String.format("SAVE_CHAT_HISTORY|%s|%s", chatId, currentUser.getEmployeeNumber());
        JsonObject response = client.sendRequest(request);

        if (response != null && response.startsWith("CHAT_SAVED")) {
            UIUtils.showSuccess("Chat history saved successfully!");
        } else {
            UIUtils.showError("Failed to save chat history");
        }
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