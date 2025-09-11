package com.myshopnet.client;

import com.google.gson.*;
import com.myshopnet.client.utils.UIUtils;

import java.util.*;
import java.util.List;

public class AdminMenu implements Menu {
    private Scanner scanner;

    public AdminMenu() {
        this.scanner = Singletons.CLIENT.getScanner();
    }

    public void show() {
        while (true) {
            UIUtils.printMenuHeader("ADMIN MENU");
            UIUtils.printLine("System administration tools");
            UIUtils.printEmptyLine();

            UIUtils.printMenuOption(1, "User Account Management");
            UIUtils.printMenuOption(2, "Password Policy Settings");
            UIUtils.printMenuOption(3, "Branch Management");
            UIUtils.printMenuOption(4, "System Logs");
            UIUtils.printMenuOption(0, "Log out");

            UIUtils.printMenuFooter();

            int choice = UIUtils.getIntInput(scanner);

            switch (choice) {
                case 1:
                    viewAllEmployees();
                    break;
                case 2:
                    Singletons.PASSWORD_POLICY_MENU.show();
                    break;
                case 3:
                    ((BranchMenu)Singletons.BRANCH_MENU).setMenuToGoBack(this);
                    Singletons.BRANCH_MENU.show();
                    break;
                case 4:
                    Singletons.LOGIN_MENU.show();
                case 0:
                    Singletons.CLIENT.logout();
                    return;
                default:
                    UIUtils.showError("Invalid choice. Please try again.");
                    UIUtils.waitForEnter(scanner);
            }
        }
    }

    private void manageUserAccounts() {
        UIUtils.printMenuHeader("USER ACCOUNT MANAGEMENT");
        UIUtils.printEmptyLine();

        UIUtils.printMenuOption(1, "View All Employees in Branches");
        UIUtils.printMenuOption(2, "Reset User Password");
        UIUtils.printMenuOption(0, "Back");

        UIUtils.printMenuFooter();

        int choice = UIUtils.getIntInput(scanner);

        switch (choice) {
            case 1:
                viewAllEmployees();
                break;
            case 2:
                resetUserPassword();
                break;
            case 0:
                return;
            default:
                UIUtils.showError("Invalid choice. Please try again.");
                UIUtils.waitForEnter(scanner);
        }
    }

    private void viewAllEmployees() {
        Map<String, String> requestMap = new HashMap<>();
        UIUtils.printMenuHeader("ALL USER ACCOUNTS");

        requestMap.put("userId", Auth.getCurrentUser().get("userId").getAsString());
        Request request = new Request("getAllUserAccounts", Singletons.GSON.toJson(requestMap));
        JsonObject response = Singletons.CLIENT.sendRequest(request);

        if (response != null && response.get("success").getAsBoolean()) {
            displayAccountsData(response.get("data").getAsString());
        } else {
            UIUtils.showError("Failed to retrieve account data");
        }

        UIUtils.waitForEnter(scanner);
    }

    private void displayAccountsData(String userAccounts) {
        try {
            List<String[]> rows = new ArrayList<>();
            JsonArray userAccountsArray = JsonParser.parseString(userAccounts).getAsJsonArray();

            for (JsonElement row : userAccountsArray) {
                if (row.isJsonObject()) {
                    JsonObject rowObject = row.getAsJsonObject();

                    JsonObject userDetailsJson = rowObject.get("user").getAsJsonObject();

                    if (rowObject.has("role")) {
                        String role = userDetailsJson.get("role").getAsString();

                        if (role.equals("EMPLOYEE")) {
                            String branchName = getBranchByBranchId(rowObject.get("branchId").getAsString());
                            String id = userDetailsJson.get("id").getAsString();
                            String accountNumber = userDetailsJson.get("accountNumber").getAsString();
                            String employeeType = userDetailsJson.get("employeeType").getAsString();
                            String employeeNumber = userDetailsJson.get("employeeNumber").getAsString();
                            String employeeStatus = userDetailsJson.get("employeeStatus").getAsString();

                            if (branchName == null) {
                                throw new Exception("Branch name is empty");
                            }

                            rows.add(new String[]{id, accountNumber,branchName, employeeType, employeeNumber, employeeStatus});
                        }
                    }
                }
            }

            String[] headers = {"ID", "Account Number", "Branch Name", "Employee Type", "Employee Number","Employee Status"};
            UIUtils.printTable(headers, rows);
            UIUtils.showInfo("Total accounts: " + rows.size());

            UIUtils.waitForEnter(scanner);
            show();
        }
        catch (Exception e) {
            UIUtils.showError("Error displaying accounts: " + e.getMessage());
        }
    }

    private String getBranchByBranchId(String branchId) {
        Map<String, String> requestMap = new HashMap<>();
        String userId = Auth.getCurrentUser().get("userId").getAsString();
        String name = null;

        requestMap.put("branchId", branchId);
        requestMap.put("userId", userId);

        Request request = new Request("getBranchByBranchId", Singletons.GSON.toJson(requestMap));
        JsonObject responseBranch = Singletons.CLIENT.sendRequest(request);

        if (responseBranch.get("success").getAsBoolean()) {
            JsonObject branchObject = responseBranch.getAsJsonObject("message");

            name = branchObject.get("name").getAsString();
        }
        else {
            UIUtils.showError("Failed to get branch by id: " + branchId);
        }

        return name;
    }

    private void resetUserPassword() {
        Map<String, String> requestMap = new HashMap<>();
        UIUtils.printMenuHeader("RESET USER PASSWORD");

        String username = UIUtils.getStringInput(scanner, "Username: ");
        String newPassword = UIUtils.getStringInput(scanner, "New Password: ");

        requestMap.put("userId", Auth.getCurrentUser().get("userId").getAsString());
        requestMap.put("username", username);
        requestMap.put("newPassword", newPassword);

        Request request = new Request("resetUserPassword", Singletons.GSON.toJson(requestMap));
        JsonObject response = Singletons.CLIENT.sendRequest(request);

        if (response != null && response.get("success").getAsBoolean()) {
            UIUtils.showSuccess("Password reset successfully!");
        } else {
            String error = response != null ? response.get("message").getAsString() : "Connection error";
            UIUtils.showError(error);
        }

        UIUtils.waitForEnter(scanner);
    }
}