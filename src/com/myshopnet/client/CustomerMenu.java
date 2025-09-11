package com.myshopnet.client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.myshopnet.client.utils.UIUtils;

import java.util.*;

public class CustomerMenu implements Menu {
    private Scanner scanner;

    public CustomerMenu() {
        this.scanner = Singletons.CLIENT.getScanner();
    }

    public void show() {
        while (true) {
            UIUtils.printMenuHeader("CUSTOMER MENU");
            UIUtils.printEmptyLine();

            UIUtils.printMenuOption(1, "View customer plan");
            UIUtils.printMenuOption(2, "Purchase new clothes");
            UIUtils.printMenuOption(3, "View purchase history");
            UIUtils.printMenuOption(0, "Log out");

            UIUtils.printMenuFooter();

            int choice = UIUtils.getIntInput(scanner);

            switch (choice) {
                case 1:
                    showCustomerPlanDetails();
                    break;
                case 2:
                    processCustomerPurchase();
                    break;
                case 3:
                    viewPurchaseHistory();
                case 0:
                    Singletons.CLIENT.logout();
                    return;
                default:
                    UIUtils.showError("Invalid choice. Please try again.");
                    UIUtils.waitForEnter(scanner);
            }
        }
    }

    private void displayCustomerDetails(String response) {
        try {
            String[] parts = response.split("\\|");
            if (parts.length > 1) {
                String[] customerData = parts[1].split(":");
                if (customerData.length >= 4) {
                    UIUtils.printLine("Customer ID: " + customerData[0]);
                    UIUtils.printLine("Full Name: " + customerData[1]);
                    UIUtils.printLine("Phone: " + customerData[2]);
                    UIUtils.printLine("Type: " + customerData[3]);

                    if (customerData.length > 4) {
                        UIUtils.printLine("Purchase Plan: " + customerData[4]);
                    }
                    if (customerData.length > 5) {
                        UIUtils.printLine("Promotions: " + customerData[5]);
                    }
                }
            }
        } catch (Exception e) {
            UIUtils.showError("Error displaying customer details: " + e.getMessage());
        }
    }

    private void showCustomerPlanDetails() {
        String planDetails = "";
        switch (Auth.getCurrentUser().get("customerType").getAsString()) {
            case "New Customer":
                planDetails = "Welcome Plan: 5% discount";
                break;
            case "Returning Customer":
                planDetails = "Loyalty Plan: 10 usd coupon";
                break;
            case "VIP Customer":
                planDetails = "VIP Plan: 20% discount";
                break;
        }
        UIUtils.showInfo("Customer Plan: " + planDetails);
    }

    private void viewPurchaseHistory() {
        UIUtils.printMenuHeader("CUSTOMER PURCHASE HISTORY");
        UIUtils.showInfo("Purchase history is not implemented in this client.");
        UIUtils.waitForEnter(scanner);
    }

    private void displayPurchaseHistory(String response) { // legacy not used
        try {
            String[] parts = response.split("\\|");
            if (parts.length > 1) {
                List<String[]> rows = new ArrayList<>();
                double totalSpent = 0;

                for (int i = 1; i < parts.length; i++) {
                    String[] purchaseData = parts[i].split(":");
                    if (purchaseData.length >= 5) {
                        rows.add(new String[]{
                                purchaseData[0], // Date
                                purchaseData[1], // Product
                                purchaseData[2], // Quantity
                                "$" + purchaseData[3], // Amount
                                "$" + purchaseData[4]  // Discount
                        });
                        totalSpent += Double.parseDouble(purchaseData[3]);
                    }
                }

                String[] headers = {"Date", "Product", "Quantity", "Amount", "Discount"};
                UIUtils.printTable(headers, rows);
                UIUtils.showInfo("Total purchases: " + rows.size() + ", Total spent: $" + String.format("%.2f", totalSpent));
            } else {
                UIUtils.printLine("No purchase history found");
            }
        } catch (Exception e) {
            UIUtils.showError("Error displaying purchase history: " + e.getMessage());
        }
    }

    private void processCustomerPurchase() {
        UIUtils.printMenuHeader("PROCESS CUSTOMER PURCHASE");

        String customerId = UIUtils.getStringInput(scanner, "Customer ID: ");
        String productId = UIUtils.getStringInput(scanner, "Product ID: ");
        String quantityStr = UIUtils.getStringInput(scanner, "Quantity: ");

        try {
            int quantity = Integer.parseInt(quantityStr);

            JsonObject data = new JsonObject();
            data.addProperty("branchId", Auth.getCurrentUser().get("branchId").getAsString());
            data.addProperty("customerId", customerId);
            JsonObject products = new JsonObject();
            products.addProperty(productId, quantity);
            data.add("products", products);

            Request request = new Request("performOrder", Singletons.GSON.toJson(data));
            JsonObject response =  Singletons.CLIENT.sendRequest(request);

            if (response != null && response.has("success") && response.get("success").getAsBoolean()) {
                UIUtils.showSuccess("Order performed successfully");
            } else {
                String error = response != null && response.has("message") ? response.get("message").getAsString() : "Connection error";
                UIUtils.showError(error);
            }
        } catch (NumberFormatException e) {
            UIUtils.showError("Invalid quantity format");
        }

        UIUtils.waitForEnter(scanner);
    }
}