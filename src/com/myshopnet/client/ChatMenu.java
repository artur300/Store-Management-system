package com.myshopnet.client;
import com.google.gson.JsonObject;
import com.myshopnet.AppState;
import com.myshopnet.client.utils.UIUtils;

import java.util.Scanner;

public class ChatMenu {
    private final Client client;
    private final String chatId;
    private final String otherUserId;
    private final Scanner scanner;

    public ChatMenu(Client client, String chatId, String otherUserId) {
        this.client = client;
        this.chatId = chatId;
        this.otherUserId = otherUserId;
        this.scanner = client.getScanner();
    }

    public void show() {
        int choice;
        do {
            UIUtils.clearScreen();
            UIUtils.printBorder();
            System.out.println("║                CHAT SESSION                ║");
            UIUtils.printBorder();
            System.out.println("1. Send Message");
            System.out.println("2. End Chat");
            System.out.println("0. Back");
            choice = UIUtils.getIntInput(scanner);

            switch (choice) {
                case 1 -> sendMessage();
                case 2 -> endChat();
            }
        } while (!AppState.chatActive);
    }

    private void sendMessage() {
        System.out.print("Enter your message: ");
        String msg = scanner.nextLine();

        Request req = new Request(
                "sendMessage",
                String.format("{\"chatId\":\"%s\",\"senderId\":\"%s\",\"receiverId\":\"%s\",\"message\":\"%s\"}",
                        chatId,
                        Auth.getCurrentUser().get("userId").getAsString(),
                        otherUserId,
                        msg
                )
        );

        JsonObject res = client.sendRequest(req);
        if (res != null && res.get("success").getAsBoolean()) {
            UIUtils.showSuccess("Message sent!");
        } else {
            UIUtils.showError("Failed to send message: " +
                    (res != null ? res.get("message").getAsString() : ""));
        }
        UIUtils.waitForEnter(scanner);
    }

    private void endChat() {
        Request req = new Request(
                "endChat",
                String.format("{\"chatId\":\"%s\",\"userId\":\"%s\"}",
                        chatId,
                        Auth.getCurrentUser().get("userId").getAsString())
        );

        JsonObject res = client.sendRequest(req);
        if (res != null && res.get("success").getAsBoolean()) {
            UIUtils.showSuccess("Chat ended!");
        } else {
            UIUtils.showError("Failed to end chat: " +
                    (res != null ? res.get("message").getAsString() : ""));
        }
        UIUtils.waitForEnter(scanner);
    }
}

