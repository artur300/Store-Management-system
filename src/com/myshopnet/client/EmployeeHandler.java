package com.myshopnet.client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.myshopnet.client.utils.UIUtils;
import com.myshopnet.utils.GsonSingleton;

import java.util.*;

public class EmployeeHandler {
    private final Gson gson = GsonSingleton.getInstance();

    private Client client;
    private JsonObject currentUser;
    private Scanner scanner;

    public EmployeeHandler(Client client, JsonObject currentUser) {
        this.client = client;
        this.currentUser = currentUser;
        this.scanner = client.getScanner();
    }

    public void showEmployeeMenu() {
        while (true) {
            UIUtils.printMenuFooter();

            int choice = UIUtils.getIntInput(scanner);

            switch (choice) {
                case 1:
                    startChat();
                    break;
                case 0:
                    return;
                default:
                    UIUtils.showError("Invalid choice. Please try again.");
                    UIUtils.waitForEnter(scanner);
            }
        }
    }

    public void startChat() {

    }
}