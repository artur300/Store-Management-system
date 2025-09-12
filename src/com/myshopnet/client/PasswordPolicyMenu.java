package com.myshopnet.client;

import com.google.gson.JsonObject;
import com.myshopnet.client.utils.UIUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class PasswordPolicyMenu implements Menu {
    private Scanner scanner;
    private Menu goBackMenu;

    public PasswordPolicyMenu() {
        this.scanner = new Scanner(System.in);
    }

    public Menu getGoBackMenu() {
        return goBackMenu;
    }

    public void setGoBackMenu(Menu goBackMenu) {
        this.goBackMenu = goBackMenu;
    }

    @Override
    public void show() {
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

        requestMap.put("userId", Auth.getCurrentUser().get("userId").getAsString());
        Request request = new Request("viewPasswordPolicy", Singletons.GSON.toJson(requestMap));
        JsonObject response = Singletons.CLIENT.sendRequest(request);

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

        String userId = Auth.getCurrentUser().get("userId").getAsString();

        requestMap.put("userId", userId);
        requestMap.put("minChars", minAlphabetic);
        requestMap.put("maxChars", maxAlphabetic);
        requestMap.put("minNumbers", minNumeric);
        requestMap.put("maxNumbers", maxNumeric);
        requestMap.put("hasSpecialCharacters", Boolean.toString(requireComplex));

        Request request = new Request("updatePasswordPolicy", Singletons.GSON.toJson(requestMap));
        JsonObject response = Singletons.CLIENT.sendRequest(request);

        if (response != null && response.get("success").getAsBoolean()) {
            UIUtils.showSuccess("Password policy updated successfully!");
        } else {
            String error = response != null ? response.get("message").getAsString() : "Connection error";
            UIUtils.showError(error);
        }

        UIUtils.waitForEnter(scanner);
    }
}
