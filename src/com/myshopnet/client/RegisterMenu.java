package com.myshopnet.client;

import com.google.gson.JsonObject;
import com.myshopnet.client.utils.UIUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class RegisterMenu implements Menu {
    private final Scanner scanner;

    public RegisterMenu() {
        scanner = new Scanner(System.in);
    }

    @Override
    public void show() {
        UIUtils.printMenuHeader("USER ACCOUNT MANAGEMENT");
        UIUtils.printEmptyLine();

        UIUtils.printMenuOption(1, "Register as Customer");
        UIUtils.printMenuOption(2, "Login");
        UIUtils.printMenuOption(0, "Exit");

        UIUtils.printMenuFooter();

        int choice = UIUtils.getIntInput(scanner);

        switch (choice) {
            case 1:
                registerUser();
                break;
            case 2:
                ((LoginMenu)(Singletons.LOGIN_MENU)).handleLogin();
                break;
            case 0:
                System.exit(0);
                return;
            default:
                UIUtils.showError("Invalid choice. Please try again.");
                UIUtils.waitForEnter(scanner);
        }

        UIUtils.printMenuFooter();
    }

    private void registerUser() {
        Map<String, String> requestMap = new HashMap<String, String>();

        String username = UIUtils.getStringInput(scanner, "Username: ");
        String newPassword = UIUtils.getStringInput(scanner, "Password: ");
        String passportId = UIUtils.getStringInput(scanner, "PassportId: ");
        String fullName = UIUtils.getStringInput(scanner, "Full Name: ");
        String phoneNumber = UIUtils.getStringInput(scanner, "Phone Number: ");

        requestMap.put("username", username);
        requestMap.put("password", newPassword);
        requestMap.put("passportId", passportId);
        requestMap.put("fullName", fullName);
        requestMap.put("phoneNumber", phoneNumber);

        Request request = new Request("createCustomer", Singletons.GSON.toJson(requestMap));
        JsonObject responseUser = Singletons.CLIENT.sendRequest(request);

        if (responseUser != null && responseUser.get("success").getAsBoolean()) {
            UIUtils.showSuccess("Account successfully registered! You can now login.");
        } else {
            String error = responseUser != null ? responseUser.get("message").getAsString() : "Error while creating account.";
            UIUtils.showError(error);
        }

        UIUtils.waitForEnter(scanner);

        show();
    }
}

