package com.myshopnet.client;

import com.google.gson.*;
import com.myshopnet.client.utils.UIUtils;
import com.myshopnet.server.RequestHandler;
import com.myshopnet.utils.GsonSingleton;

import java.util.*;

public class AdminHandler {
    private final Gson gson = GsonSingleton.getInstance();
    private Client client;
    private JsonObject currentUser;
    private Scanner scanner;

    public AdminHandler(Client client, JsonObject currentUser) {
        this.client = client;
        this.currentUser = currentUser;
        this.scanner = client.getScanner();
    }

    public void showAdminMenu() {
        while (true) {
            UIUtils.printMenuHeader("ADMIN PANEL");
            UIUtils.printLine("System administration tools");
            UIUtils.printEmptyLine();

            UIUtils.printMenuOption(1, "User Account Management");
            UIUtils.printMenuOption(2, "Password Policy Settings");
            UIUtils.printMenuOption(3, "Branch Management");
            UIUtils.printMenuOption(0, "Back to Main Menu");

            UIUtils.printMenuFooter();

            int choice = UIUtils.getIntInput(scanner);

            switch (choice) {
                case 1:
                    manageUserAccounts();
                    break;
                case 2:
                    managePasswordPolicies();
                    break;
                case 3:
                    manageBranches();
                    break;
                case 0:
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

        UIUtils.printMenuOption(1, "View All User Accounts");
        UIUtils.printMenuOption(2, "Reset User Password");
        UIUtils.printMenuOption(0, "Back");

        UIUtils.printMenuFooter();

        int choice = UIUtils.getIntInput(scanner);

        switch (choice) {
            case 1:
                viewAllAccounts();
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

    private void viewAllAccounts() {
        Map<String, String> requestMap = new HashMap<>();
        UIUtils.printMenuHeader("ALL USER ACCOUNTS");

        requestMap.put("userId", currentUser.get("userId").getAsString());
        Request request = new Request("getAllUserAccounts", gson.toJson(requestMap));
        JsonObject response = client.sendRequest(request);

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

                            rows.add(new String[]{id, accountNumber,branchName, employeeType, employeeNumber, employeeStatus});
                        }
                    }
                }
            }

            String[] headers = {"ID", "Account Number", "Branch Name", "Employee Type", "Employee Number","Employee Status"};
            UIUtils.printTable(headers, rows);
            UIUtils.showInfo("Total accounts: " + rows.size());
        }
        catch (Exception e) {
            UIUtils.showError("Error displaying accounts: " + e.getMessage());
        }
    }

    private String getBranchByBranchId(String branchId) {
        String userId = currentUser.get("userId").getAsString();
        String name = "";

        JsonObject responseBranch = client.sendRequest(new Request("getBranchByBranchId", branchId));

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

        requestMap.put("userId", currentUser.get("userId").getAsString());
        requestMap.put("username", username);
        requestMap.put("newPassword", newPassword);

        Request request = new Request("resetUserPassword", gson.toJson(requestMap));
        JsonObject response = client.sendRequest(request);

        if (response != null && response.get("success").getAsBoolean()) {
            UIUtils.showSuccess("Password reset successfully!");
        } else {
            String error = response != null ? response.get("message").getAsString() : "Connection error";
            UIUtils.showError(error);
        }

        UIUtils.waitForEnter(scanner);
    }

    private void managePasswordPolicies() {
        UIUtils.printMenuHeader("PASSWORD POLICY SETTINGS");
        UIUtils.printEmptyLine();

        UIUtils.printMenuOption(1, "View Current Policy");
        UIUtils.printMenuOption(2, "Update Password Policy");
        UIUtils.printMenuOption(0, "Back");

        UIUtils.printMenuFooter();

        int choice = UIUtils.getIntInput(scanner);

        switch (choice) {
            case 1:
                viewPasswordPolicy();
                break;
            case 2:
                updatePasswordPolicy();
                break;
            case 0:
                return;
            default:
                UIUtils.showError("Invalid choice. Please try again.");
                UIUtils.waitForEnter(scanner);
        }
    }

    private void viewPasswordPolicy() {
        Map<String, String> requestMap = new HashMap();
        UIUtils.printMenuHeader("CURRENT PASSWORD POLICY");

        requestMap.put("userId", currentUser.get("userId").getAsString());
        Request request = new Request("viewPasswordPolicy", gson.toJson(requestMap));
        JsonObject response = client.sendRequest(request);

        if (response != null && response.get("success").getAsBoolean()) {
            displayPasswordPolicy(response);
        } else {
            UIUtils.showError("Failed to retrieve password policy");
        }

        UIUtils.waitForEnter(scanner);
    }

    private void displayPasswordPolicy(JsonObject response) {
        try {
            UIUtils.printLine("Minimum Alphabetic length: " +
                    response.get("minimumAlphabeticCharacters").getAsString() + " characters");
            UIUtils.printLine("Maximum Alphabetic length: " +
                    response.get("maximumAlphabeticCharacters").getAsString() + " characters");
            UIUtils.printLine("Minimum Numeric length: " +
                    response.get("minimumNumericCharacters").getAsString() + " characters");
            UIUtils.printLine("Maximum Numeric length: " +
                    response.get("maximumNumericCharacters").getAsString() + " characters");
            UIUtils.printLine("Include special characters: " +
                    response.get("includeSigns").getAsBoolean() + " characters");
        } catch (Exception e) {
            UIUtils.showError("Error displaying password policy: " + e.getMessage());
        }
    }

    private void updatePasswordPolicy() {
        Map<String, String> requestMap = new HashMap();
        UIUtils.printMenuHeader("UPDATE PASSWORD POLICY");

        UIUtils.printLine("Require special characters? (Y/n): ");
        String specialChars = scanner.nextLine().trim().toLowerCase();
        boolean requireComplex = !specialChars.equals("n") && !specialChars.equals("no");

        String minAlphabetic = UIUtils.getStringInput(scanner, "Minimum password alphabetic characters (current/default 8): ");
        String maxAlphabetic = UIUtils.getStringInput(scanner, "Maximum password alphabetic characters (current/default 8): ");

        String minNumeric = UIUtils.getStringInput(scanner, "Minimum password numeric characters (current/default 8): ");
        String maxNumeric = UIUtils.getStringInput(scanner, "Maximum password numeric characters (current/default 8): ");

        if (minAlphabetic.trim().isEmpty()) minAlphabetic = "8";
        if (maxAlphabetic.trim().isEmpty()) maxAlphabetic = "8";
        if (minNumeric.trim().isEmpty()) minNumeric = "8";
        if (maxNumeric.trim().isEmpty()) maxNumeric = "8";

        String userId = currentUser.get("userId").getAsString();

        requestMap.put("userId", userId);
        requestMap.put("minChars", minAlphabetic);
        requestMap.put("maxChars", maxAlphabetic);
        requestMap.put("minNumbers", minNumeric);
        requestMap.put("maxNumbers", maxNumeric);
        requestMap.put("hasSpecialCharacters", specialChars);

        Request request = new Request("updatePasswordPolicy", gson.toJson(requestMap));
        JsonObject response = client.sendRequest(request);

        if (response != null && response.get("success").getAsBoolean()) {
            UIUtils.showSuccess("Password policy updated successfully!");
        } else {
            String error = response != null ? response.get("message").getAsString() : "Connection error";
            UIUtils.showError(error);
        }

        UIUtils.waitForEnter(scanner);
    }

    private void manageBranches() {
        UIUtils.printMenuHeader("BRANCH MANAGEMENT");
        UIUtils.printEmptyLine();

        UIUtils.printMenuOption(1, "View All Branches");
        UIUtils.printMenuOption(2, "Add New Branch");
        UIUtils.printMenuOption(3, "Update Branch Info");
        UIUtils.printMenuOption(0, "Back");

        UIUtils.printMenuFooter();

        int choice = UIUtils.getIntInput(scanner);

        switch (choice) {
            case 1:
                viewAllBranches();
                break;
            case 2:
                addNewBranch();
                break;
            case 3:
                updateBranchInfo();
                break;
            case 0:
                return;
            default:
                UIUtils.showError("Invalid choice. Please try again.");
                UIUtils.waitForEnter(scanner);
        }
    }

    private void viewAllBranches() {
        Map<String, String> requestMap = new HashMap<>();
        UIUtils.printMenuHeader("ALL BRANCHES");

        requestMap.put("userId", currentUser.get("userId").getAsString());
        Request request = new Request("getAllBranches", gson.toJson(requestMap));
        JsonObject response = client.sendRequest(request);

        if (response != null && response.get("success").getAsBoolean()) {
            displayBranchesData(response.getAsJsonArray("message"));
        } else {
            UIUtils.showError("Failed to retrieve branches data");
        }

        UIUtils.waitForEnter(scanner);
    }

    private void displayBranchesData(JsonArray response) {
        try {
            List<String[]> rows = new ArrayList<>();

            for (JsonElement element : response) {
                JsonObject branch = element.getAsJsonObject();

                String id = branch.get("id").getAsString();
                String name = branch.get("name").getAsString();

                rows.add(new String[]{id, name});
            }

            String[] headers = {"Branch ID", "Name" };
            UIUtils.printTable(headers, rows);
            UIUtils.showInfo("Total branches: " + rows.size());
            }
        catch (Exception e) {
            UIUtils.showError("Error displaying branches: " + e.getMessage());
        }
    }

    private void addNewBranch() {
        UIUtils.showInfo("Add new branch functionality");
        UIUtils.waitForEnter(scanner);
    }

    private void updateBranchInfo() {
        UIUtils.showInfo("Update branch information");
        UIUtils.waitForEnter(scanner);
    }

}