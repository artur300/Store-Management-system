package com.myshopnet.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.myshopnet.client.utils.UIUtils;

import java.util.*;

public class EmployeeMenu implements Menu {
    private Scanner scanner;

    public EmployeeMenu() {
        this.scanner =  Singletons.CLIENT.getScanner();
    }

    public void show() {
        while (true) {
            UIUtils.printMenuHeader("EMPLOYEE MENU");

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
                    viewAllCustomers();
                    break;
                case 3:
                    Singletons.STOCK_MENU.show();
                case 4:
                    viewOrdersFromBranch();
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
        Singletons.CHAT_MENU.show();
    }

    private void viewAllCustomers() {
        UIUtils.printMenuHeader("ALL CUSTOMERS");

        Request request = new Request("getAllCustomers", "");
        JsonObject response = Singletons.CLIENT.sendRequest(request);

        if (response != null && response.has("success") && response.get("success").getAsBoolean()) {
            displayCustomersDataJson(response.getAsJsonArray("message"));
        } else {
            UIUtils.showError("Failed to retrieve customer data");
        }

        UIUtils.waitForEnter(scanner);
    }

    public void displayCustomersDataJson(JsonArray customerArray) {
        try {
            List<String[]> rows = new ArrayList<>();
            for (var el : customerArray) {
                JsonObject customer = el.getAsJsonObject();
                rows.add(new String[]{
                        customer.get("userId").getAsString(),
                        customer.get("fullName").getAsString(),
                        customer.get("phone").getAsString(),
                        customer.get("role").getAsString()
                });
            }
            String[] headers = {"Customer ID", "Full Name", "Phone Number", "Type"};
            UIUtils.printTable(headers, rows);
            UIUtils.showInfo("Total customers: " + rows.size());
        } catch (Exception e) {
            UIUtils.showError("Error displaying customers: " + e.getMessage());
        }

        UIUtils.waitForEnter(scanner);
    }
}