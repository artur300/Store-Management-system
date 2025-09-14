package com.myshopnet.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.myshopnet.client.utils.UIUtils;

import java.util.*;

public class ChatMenu implements Menu {
    private final Scanner scanner;

    // מצב הצ'אט
    private volatile boolean inChat = false;
    private volatile String currentChatId = null;

    // תיאום עם push
    private volatile boolean waitingForChat = false;
    private volatile String pendingChatId = null; // chat שהשרת יצר ואמור להיפתח

    public ChatMenu() {
        this.scanner = Singletons.CLIENT.getScanner();
    }

    // ===== Hooks שמקבל מ-Client דרך ה-Push =====
    @Override
    public synchronized void onChatCreated(String chatId) {
        pendingChatId = chatId;
        waitingForChat = false;
        System.out.println("\n[Notification] Chat is ready.");

        // PATCH: לא פותחים כאן צ'אט על חוט נפרד (כדי לא להתנגש עם Scanner)!
        // הפתיחה תתבצע מלולאה ראשית ב-ChatMenu.show() או מ-EmployeeMenu.show().
    }

    // PATCH: מאפשר ללולאת התפריט לזהות שיש צ'אט שמוכן להיפתח
    public synchronized boolean hasPendingChatToOpen() {
        return !inChat && pendingChatId != null;
    }

    @Override
    public void onIncomingMessage(String chatId, String sender, String msg) {
        if (inChat && Objects.equals(chatId, currentChatId)) {
            String myId = Auth.getCurrentUser().get("userId").getAsString();
            if (myId.equals(sender)) {
                // PATCH: לא להציג echo של הודעות שנשלחו על-ידי עצמי
                return;
            }
            System.out.println("\n[" + sender + "]: " + msg);
        }
    }

    // ===== תצוגת תפריט =====
    @Override
    public void show() {
        while (true) {
            // אם יש צ'אט בהמתנה ולא בתוך צ'אט – נפתח עכשיו (תמיד על ה-thread הראשי)
            if (!inChat && pendingChatId != null) {
                String id = pendingChatId;
                pendingChatId = null;
                startChatSession(id); // קריאות Scanner מתבצעות כאן בלבד
                continue;
            }

            UIUtils.printMenuHeader("CHAT SYSTEM");
            UIUtils.printLine("Inter-branch communication");
            UIUtils.printEmptyLine();

            UIUtils.printMenuOption(1, "Start New Chat");
            UIUtils.printMenuOption(0, "Back to Main Menu");
            UIUtils.printMenuFooter();

            int choice = UIUtils.getIntInput(scanner);

            switch (choice) {
                case 1 -> startNewChat();
                case 0 -> { return; }
                default -> UIUtils.showErrorAndWait(scanner, "Invalid choice. Please try again.");
            }
        }
    }

    private void startNewChat() {
        if (inChat) {
            UIUtils.showErrorAndWait(scanner, "You are already in a chat session.");
            return;
        }

        Map<String, String> requestMap = new HashMap<>();
        UIUtils.printMenuHeader("START NEW CHAT");

        String userId = Auth.getCurrentUser().get("userId").getAsString();
        requestMap.put("userId", userId);

        Request request = new Request("getAllBranches", Singletons.GSON.toJson(requestMap));
        JsonObject response = Singletons.CLIENT.sendRequest(request);

        if (response == null) {
            UIUtils.showErrorAndWait(scanner, "Connection error while loading branches");
            return;
        }

        if (response.get("success").getAsBoolean()) {
            String chosenBranchId = displayAvailableBranches(JsonParser.parseString(response.get("message").getAsString()).getAsJsonArray());
            if (chosenBranchId == null) return;

            Map<String, String> requestMapToChat = new HashMap<>();
            requestMapToChat.put("userIdRequesting", userId);
            requestMapToChat.put("branchId", chosenBranchId);

            Request requestToChat = new Request("startChat", Singletons.GSON.toJson(requestMapToChat));
            JsonObject chatResponse = Singletons.CLIENT.sendRequest(requestToChat);

            if (chatResponse != null && chatResponse.has("success") && chatResponse.get("success").getAsBoolean()) {
                try {
                    if (chatResponse.get("message").isJsonObject()) {
                        // פתיחה מידית (שרת כבר מצא עובד זמין)
                        JsonObject chatObj = chatResponse.getAsJsonObject("message");
                        String chatId = chatObj.get("id").getAsString();
                        pendingChatId = chatId;
                        waitingForChat = false;
                        // פתיחה על ה-thread הראשי (באותה מתודה)
                        startChatSession(chatId);
                    } else {
                        // נכנסנו לתור
                        String msg = chatResponse.get("message").getAsString();
                        if (msg != null && msg.toLowerCase().contains("queue")) {
                            waitingForChat = true;
                            UIUtils.showInfo("All employees are busy. Waiting for a free agent...");
                            waitForChatAndOpen(); // גם כאן הפתיחה מתבצעת על אותו thread
                        } else {
                            UIUtils.showInfoAndWait(scanner, msg);
                        }
                    }
                } catch (Exception ex) {
                    UIUtils.showErrorAndWait(scanner, "Unexpected response from server.");
                }
            } else {
                String err = (chatResponse != null && chatResponse.has("message"))
                        ? chatResponse.get("message").getAsString()
                        : "Connection error";
                UIUtils.showErrorAndWait(scanner, err);
            }
        } else {
            UIUtils.showErrorAndWait(scanner,
                    response.has("message") ? response.get("message").getAsString() : "Failed to load branches");
        }
    }

    private String displayAvailableBranches(JsonArray branches) {
        try {
            while (true) {
                List<String> branchIds = new ArrayList<>();

                if (!branches.isEmpty()) {
                    int i = 0;
                    for (JsonElement row : branches) {
                        JsonObject rowObject = row.getAsJsonObject();
                        branchIds.add(rowObject.get("id").getAsString());
                        UIUtils.printMenuOption(i + 1, rowObject.get("name").getAsString());
                        i++;
                    }
                    UIUtils.printMenuFooter();
                    int choice = UIUtils.getIntInput(scanner);
                    if (choice < 1 || choice > branchIds.size()) {
                        UIUtils.showErrorAndWait(scanner, "Invalid choice. Please try again.");
                        continue;
                    }
                    return branchIds.get(choice - 1);
                } else {
                    UIUtils.showInfoAndWait(scanner, "No branches found.");
                    return null;
                }
            }
        } catch (Exception e) {
            UIUtils.showErrorAndWait(scanner, "Error displaying branches: " + e.getMessage());
        }
        return null;
    }

    // מחכה לאירוע chatCreated ואז פותח — רץ באותו חוט, לא קורא קלט בזמן ההמתנה
    private void waitForChatAndOpen() {
        UIUtils.printEmptyLine();
        UIUtils.printLine("Waiting for available agent...");
        UIUtils.printLine("(Press Ctrl+C to cancel)");
        UIUtils.printBottomBorder();

        while (waitingForChat && pendingChatId == null) {
            try { Thread.sleep(150); } catch (InterruptedException ignored) {}
        }
        if (pendingChatId != null) {
            String id = pendingChatId;
            pendingChatId = null;
            startChatSession(id); // פתיחה כאן, עדיין על ה-thread הראשי
        }
    }

    private void startChatSession(String chatId) {
        this.currentChatId = chatId;
        this.inChat = true;

        UIUtils.clearScreen();
        UIUtils.printBorder();
        UIUtils.printTitle("CHAT SESSION");
        UIUtils.printLine("Chat ID: " + chatId);
        UIUtils.printLine("Type '/exit' to leave chat, '/history' for chat history (not implemented)");
        UIUtils.printBottomBorder();

        while (inChat) {
            System.out.print("You: ");
            String message;
            try {
                message = scanner.nextLine();
            } catch (Exception e) {
                // הגנה רכה: אם קרה משהו עם ה-Scanner, יוצאים מהצ'אט
                System.out.println("\n(Input was interrupted. Leaving chat.)");
                break;
            }
            if (message == null) continue;
            message = message.trim();
            if (message.isEmpty()) continue;

            if ("/exit".equalsIgnoreCase(message)) {
                exitChat();
                break;
            } else if ("/history".equalsIgnoreCase(message)) {
                showCurrentChatHistory();
                continue;
            }

            JsonObject data = new JsonObject();
            data.addProperty("chatId", currentChatId);
            data.addProperty("senderId", Auth.getCurrentUser().get("userId").getAsString());
            data.addProperty("message", message);

            Request req = new Request("sendMessage", data.toString());
            JsonObject res = Singletons.CLIENT.sendRequest(req);

            if (res == null || !res.has("success") || !res.get("success").getAsBoolean()) {
                System.out.println("(!) Failed to send message"
                        + (res != null && res.has("message") ? (": " + res.get("message").getAsString()) : "."));
            }
        }

        this.inChat = false;
        this.currentChatId = null;
    }

    private void exitChat() {
        try {
            JsonObject data = new JsonObject();
            data.addProperty("userId", Auth.getCurrentUser().get("userId").getAsString());
            data.addProperty("chatId", currentChatId);
            Request req = new Request("endChat", Singletons.GSON.toJson(data));
            Singletons.CLIENT.sendRequest(req);
        } catch (Exception ignored) {}
        System.out.println("You have left the chat.");
    }

    private void showCurrentChatHistory() {
        System.out.println("(history not implemented)");
    }
}

