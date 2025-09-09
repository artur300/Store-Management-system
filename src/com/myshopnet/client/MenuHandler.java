package com.myshopnet.client;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.myshopnet.client.utils.UIUtils;
import com.myshopnet.utils.GsonSingleton;
import com.google.gson.JsonParser;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class MenuHandler {
    private final Gson gson = GsonSingleton.getInstance();

    private Client client;
    private JsonObject currentUser;
    private Scanner scanner;
    private boolean keepRunning;

    public MenuHandler(Client client, JsonObject currentUser) {
        this.client = client;
        this.currentUser = currentUser;
        this.scanner = client.getScanner();
        this.keepRunning = true;
    }

    public void showMainMenu() {
        while (keepRunning && client.isConnected()) {
            displayMainMenu();
            int choice = UIUtils.getIntInput(scanner);
            handleMainMenuChoice(choice);
        }
    }

    private void displayMainMenu() {
        UIUtils.printMenuHeader("MAIN MENU - " + currentUser.get("employeeType").getAsString());
        UIUtils.printLine("Branch: " + currentUser.get("branchId").getAsString());
        UIUtils.printEmptyLine();

        UIUtils.printMenuOption(1, "Inventory Management");
        UIUtils.printMenuOption(2, "Customer Management");
        UIUtils.printMenuOption(3, "Chat System");
        UIUtils.printMenuOption(4, "System Logs");

        if (currentUser.get("role").getAsString().equals("ADMIN")) {
            UIUtils.printMenuOption(5, "Admin Panel");
            UIUtils.printMenuOption(6, "Employee Management");
        }

        UIUtils.printEmptyLine();
        UIUtils.printMenuOption(9, "Change Password");
        UIUtils.printMenuOption(0, "Logout");

        UIUtils.printMenuFooter();
    }

    private void handleMainMenuChoice(int choice) {
        switch (choice) {
            case 1:
                new StockHandler(client, currentUser).showStockMenu();
                break;
            case 2:
                new CustomerHandler(client, currentUser).showCustomerMenu();
                break;
            case 3:
                new ChatHandler(client, currentUser).showChatMenu();
                break;
            case 4:
                new LogHandler(client, currentUser).showLogMenu();
                break;
            case 5:
                if (currentUser.get("employeeType").getAsString().equals("ADMIN")) {
                    new AdminHandler(client, currentUser).showAdminMenu();
                    break;
                }
            case 6: {
                if (currentUser.get("employeeType").getAsString().equals("ADMIN")) {
                    new EmployeeHandler(client, currentUser).showEmployeeMenu();
                }
                break;
            }
            case 7:
                showBranchInfo();
                break;
            case 0:
                logout();
                break;
            default:
                UIUtils.showError("Invalid choice. Please try again.");
                UIUtils.waitForEnter(scanner);
                break;
                }
            }

    private void logout() {
        Map<String, String> requestMap = new HashMap<>();
        UIUtils.printMenuHeader("LOGOUT");
        UIUtils.printLine("Are you sure you want to logout? (y/N)");
        UIUtils.printMenuFooter();

        String confirmation = scanner.nextLine().trim().toLowerCase();
        if (confirmation.equals("y") || confirmation.equals("yes")) {
            requestMap.put("userId", currentUser.get("userId").getAsString());

            client.sendRequest(new Request("logout", gson.toJson(requestMap)));

            UIUtils.showInfo("Logged out successfully. Goodbye!");
            keepRunning = false;
        }
    }
    private void showBranchInfo() {
        Map<String, String> branchRequest = new HashMap<>();
        branchRequest.put("branchId", currentUser.get("branchId").getAsString());

        Request request = new Request("getBranchByBranchId", gson.toJson(branchRequest));
        JsonObject response = client.sendRequest(request);

        if (response != null && response.get("success").getAsBoolean()) {
            String branchData = response.get("message").getAsString();


            JsonObject branchJson = JsonParser.parseString(branchData).getAsJsonObject();

            UIUtils.printMenuHeader("BRANCH INFORMATION");
            System.out.println("Branch ID: " + branchJson.get("id").getAsString());
            System.out.println("Branch Name: " + branchJson.get("name").getAsString());
            UIUtils.waitForEnter(scanner);
        }
     else {
        String error = response != null ? response.get("message").getAsString() : "Unknown error";
        UIUtils.showError("Failed to get branch info: " + error);
        UIUtils.waitForEnter(scanner);
    }
    }

}