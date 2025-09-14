package com.myshopnet.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.myshopnet.client.models.UserTypeLoggedIn;
import com.myshopnet.client.utils.UIUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class RegisterMenu implements Menu {
    private final Scanner scanner = Singletons.CLIENT.getScanner();

    @Override
    public void show() {
        while (Auth.getCurrentUser() == null) {
            UIUtils.printMenuHeader("USER ACCOUNT MANAGEMENT");
            UIUtils.printEmptyLine();
            UIUtils.printMenuOption(1, "Register as Customer");
            UIUtils.printMenuOption(2, "Login");
            UIUtils.printMenuOption(0, "Exit");
            UIUtils.printMenuFooter();

            int choice = UIUtils.getIntInput(scanner);
            switch (choice) {
                case 1 -> registerCustomer();
                case 2 -> doLogin();           // ⬅️ כבר לא קוראים ל-Singletons.LOGIN_MENU
                case 0 -> { return; }
                default -> {
                    UIUtils.showError("Invalid choice. Please try again.");
                    UIUtils.waitForEnter(scanner);
                }
            }
        }
    }

    private void doLogin() {
        String username = UIUtils.getStringInput(scanner, "Username: ");
        String password = UIUtils.getStringInput(scanner, "Password: ");

        Map<String, String> payload = new HashMap<>();
        payload.put("username", username);
        payload.put("password", password);

        Request req = new Request("login", Singletons.GSON.toJson(payload));
        JsonObject res = Singletons.CLIENT.sendRequest(req);

        if (res == null) {
            UIUtils.showError("Connection error.");
            UIUtils.waitForEnter(scanner);
            return;
        }

        if (res.has("success") && res.get("success").getAsBoolean()) {
            // ברוב ה־APIים אצלך "message" הוא JSON במחרוזת
            JsonObject msgObj;
            try {
                msgObj = JsonParser.parseString(res.get("message").getAsString()).getAsJsonObject();
            } catch (Exception e) {
                msgObj = res.getAsJsonObject("message"); // fallback אם חזר כ־object
            }

            // חלק מהשרתים מחזירים UserAccount עם שדה "user"
            JsonObject userJson = msgObj.has("user") ? msgObj.getAsJsonObject("user") : msgObj;

            // נעדכן את ה-Auth: זה מה שהקוד שלך מצפה לו בהמשך (role, userId וכו')
            Auth.setUsername(username);
            Auth.setCurrentUser(userJson);
            Auth.setCurrentUserType(UserTypeLoggedIn.valueOf(userJson.get("role").getAsString()));

            UIUtils.showSuccess("Welcome, " + username + "!");
            UIUtils.waitForEnter(scanner);
        } else {
            String err = res.has("message") ? res.get("message").getAsString() : "Login failed";
            UIUtils.showError(err);
            UIUtils.waitForEnter(scanner);
        }
    }

    private void registerCustomer() {
        String fullName = UIUtils.getStringInput(scanner, "Full name: ");
        String phone    = UIUtils.getStringInput(scanner, "Phone: ");
        String username = UIUtils.getStringInput(scanner, "Username: ");
        String password = UIUtils.getStringInput(scanner, "Password: ");

        Map<String, String> payload = new HashMap<>();
        payload.put("fullName", fullName);
        payload.put("phoneNumber", phone);
        payload.put("username", username);
        payload.put("password", password);

        Request req = new Request("registerCustomer", Singletons.GSON.toJson(payload));
        JsonObject res = Singletons.CLIENT.sendRequest(req);

        if (res == null) {
            UIUtils.showError("Connection error.");
            UIUtils.waitForEnter(scanner);
            return;
        }

        if (res.has("success") && res.get("success").getAsBoolean()) {
            UIUtils.showSuccess("Registration successful! You can log in now.");
            UIUtils.waitForEnter(scanner);
        } else {
            String err = res.has("message") ? res.get("message").getAsString() : "Registration failed";
            UIUtils.showError(err);
            UIUtils.waitForEnter(scanner);
        }
    }

    // לא חובה לממש אירועי push כאן; ההגדרות הדיפולטיות ב-Menu מספיקות
}
