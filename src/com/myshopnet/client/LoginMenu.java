package com.myshopnet.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.myshopnet.client.utils.UIUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class LoginMenu implements Menu {
    private String username, password;

    private Scanner scanner;

    public LoginMenu() {
        this.scanner =  Singletons.CLIENT.getScanner();
    }

    public void show() {
         username = UIUtils.getStringInput(scanner, "Username: ");
         password = UIUtils.getStringInput(scanner, "Password: ");
    }

    public JsonObject handleLogin() {
        int maxAttempts = 3;
        int attempts = 0;
        Map<String, String> loginRequest = new HashMap<>();

        while (attempts < maxAttempts) {
            show();

            UIUtils.printMenuHeader("USER LOGIN");

            loginRequest.put("username", username);
            loginRequest.put("password", password);

            Request request = new Request("login", Singletons.GSON.toJson(loginRequest));
            JsonObject response =  Singletons.CLIENT.sendRequest(request);

            if (response != null && response.get("success").getAsBoolean()) {
                JsonObject responseObject = JsonParser.parseString(response.get("message").getAsString()).getAsJsonObject();

                return parseLoginResponse(responseObject);
            }
            else if (response != null && !response.get("success").getAsBoolean()) {
                attempts++;

                String errorMsg = response.get("message").getAsString();
                UIUtils.showError(errorMsg + " (Attempt " + attempts + "/" + maxAttempts + ")");

                if (attempts < maxAttempts) {
                    UIUtils.waitForEnter(scanner);
                }
            }
        }

        UIUtils.showError("Maximum login attempts exceeded. Connection will be closed.");
        return null;
    }

    private JsonObject parseLoginResponse(JsonObject response) {
        try {
            if (response != null) {
                JsonObject user = response.getAsJsonObject("user");

                if (user == null) {
                    throw new Exception("User couldn't be parsed");
                }

                UIUtils.showSuccess("Welcome, " + username + "!");
                UIUtils.waitForEnter(scanner);
                Auth.setCurrentUser(user);
                Auth.setUsername(response.get("username").getAsString());

                return user;
            }
            else {
                throw new Exception("Can't parse login response");
            }
        } catch (Exception e) {
            UIUtils.showError("Error parsing login response: " + e.getMessage());
        }

        return null;
    }
}