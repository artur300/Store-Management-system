package com.myshopnet.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.myshopnet.AppState;
import com.myshopnet.client.utils.UIUtils;


import java.util.*;

public class EmployeeMenu implements Menu {
    private Scanner scanner;

    public EmployeeMenu() {
        this.scanner = Singletons.CLIENT.getScanner();
    }

    public void show() {
        while(!AppState.chatActive) {
            UIUtils.printMenuHeader("EMPLOYEE MENU");
            UIUtils.printLine((((AdminMenu)Singletons.ADMIN_MENU).getBranchByBranchId(Auth.getCurrentUser().get("branchId").getAsString())));

            UIUtils.printEmptyLine();

            UIUtils.printMenuOption(1, "Start Chat");
            UIUtils.printMenuOption(2, "View All Customers");
            UIUtils.printMenuOption(3, "Manage Branch Stock");
            UIUtils.printMenuOption(4, "View Orders of Branch");
            UIUtils.printMenuOption(0, "Log Out");

            UIUtils.printMenuFooter();

            int choice = UIUtils.getIntInput(scanner);

            switch (choice) {
                case 1:
                    startChat();
                    break;
                case 2:
                    ((AdminMenu)Singletons.ADMIN_MENU).viewAllCustomers();
                    break;
                case 3:
                    Singletons.STOCK_MENU.show();
                    break;
                case 4:
                    viewOrdersFromBranch();
                    break;
                case 0:
                    Singletons.CLIENT.logout();
                    return;
                default:
                    UIUtils.showError("Invalid choice. Please try again.");
                    UIUtils.waitForEnter(scanner);
            }
        }
    }

    private void viewOrdersFromBranch() {

    }

    private void startChat() {
        Map<String, String> requestToStartChat = new HashMap<>();
        String branchIdChosen = getBranchPick();
        String currentUserId = Auth.getUsername();

        requestToStartChat.put("userIdRequesting", currentUserId);
        requestToStartChat.put("branchId", branchIdChosen);

        Request request = new Request("chatStart", Singletons.GSON.toJson(requestToStartChat));

        JsonObject response = Singletons.CLIENT.sendRequest(request);

        if (response != null && response.get("success").getAsBoolean() && response.get("chatSuccess").getAsBoolean()) {
            System.out.println("Found employee, joining chat");

            try {
                Thread.sleep(2000);
            }
            catch (InterruptedException e) {}

            Auth.setCurrentChatId(response.get("chatId").getAsString());
            Singletons.CHAT_CLIENT.startChat();
        }
        else if (response != null && response.get("success").getAsBoolean() && !response.get("chatSuccess").getAsBoolean()) {
            UIUtils.showError(response.get("message").getAsString());
            UIUtils.printLine("You have been added to the branch queue. You'll be notified when someone is available.");
            UIUtils.waitForEnter(scanner);
        }
        else if (response != null && !response.get("success").getAsBoolean()) {
            UIUtils.showError(response.get("message").getAsString());
            UIUtils.waitForEnter(scanner);
        }
    }

    private void endChat() {
    }

    private String getBranchPick() {
        Map<String, String> req = new HashMap<>();
        List<String> branchesId = new ArrayList<>();
        req.put("userId", Auth.getUsername());

        Request request = new Request("getAllBranches", Singletons.GSON.toJson(req));
        JsonObject response = Singletons.CLIENT.sendRequest(request);

        if (response != null && !response.get("success").getAsBoolean()) {
            String error = response != null ? response.get("message").getAsString() : "Connection error";
            UIUtils.showError(error);
            UIUtils.waitForEnter(scanner);
            return null;
        }

        if (response == null) {
            UIUtils.showError("Response is null");
            UIUtils.waitForEnter(scanner);
            return null;
        }

        JsonArray branches = JsonParser.parseString(response.get("message").getAsString()).getAsJsonArray();
        int i = 1;
        for (JsonElement el: branches){
            JsonObject b = el.getAsJsonObject();

            boolean sameBranchAsUserBranch = b.get("id").getAsString().equals(Auth.getCurrentUser().get("branchId").getAsString());

            if (!sameBranchAsUserBranch) {
                branchesId.add(b.get("id").getAsString());
                UIUtils.printMenuOption(i++, b.get("name").getAsString());
            }
        }

        UIUtils.printLine("Enter the branch you want chat with from: ");
        int choice = UIUtils.getIntInput(scanner);

        if (choice < 1 || choice > branchesId.size()) {
            UIUtils.showError("Invalid choice. Please try again.");
            return null;
        }

        return branchesId.get(choice - 1);
    }
}
