package com.myshopnet.client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.myshopnet.client.utils.UIUtils;

import java.util.*;

public class CustomerHandler {
    private final Gson gson = new Gson();

    private Client client;
    private JsonObject currentUser;
    private Scanner scanner;

    public CustomerHandler(Client client, JsonObject currentUser) {
        this.client = client;
        this.currentUser = currentUser;
        this.scanner = client.getScanner();
    }

    public void showCustomerMenu() {
        while (true) {
            UIUtils.printMenuHeader("CUSTOMER MANAGEMENT");
            UIUtils.printLine("Network-wide customer management");
            UIUtils.printEmptyLine();

            UIUtils.printMenuOption(1, "View All Customers");
            UIUtils.printMenuOption(2, "Search Customer");
            UIUtils.printMenuOption(3, "Add New Customer");
            UIUtils.printMenuOption(4, "Update Customer");
            UIUtils.printMenuOption(5, "Process Customer Purchase");
            UIUtils.printMenuOption(0, "Back to Main Menu");

            UIUtils.printMenuFooter();

            int choice = UIUtils.getIntInput(scanner);

            switch (choice) {
                case 1:
                    viewAllCustomers();
                    break;
                case 2:
                    searchCustomer();
                    break;
                case 3:
                    addNewCustomer();
                    break;
                case 4:
                    updateCustomer();
                    break;
                case 5:
                    processCustomerPurchase();
                    break;
                case 0:
                    return;
                default:
                    UIUtils.showError("Invalid choice. Please try again.");
                    UIUtils.waitForEnter(scanner);
            }
        }
    }

    private void viewAllCustomers() {
        UIUtils.printMenuHeader("ALL CUSTOMERS");

        Request request = new Request("getAllCustomers", "");
        JsonObject response = client.sendRequest(request);

        if (response != null && response.has("success") && response.get("success").getAsBoolean()) {
            displayCustomersDataJson(response.getAsJsonArray("message"));
        } else {
            UIUtils.showError("Failed to retrieve customer data");
        }

        UIUtils.waitForEnter(scanner);
    }

    private void displayCustomersData(String response) {
        // legacy no-op retained for compatibility
    }

    private void displayCustomersDataJson(com.google.gson.JsonArray customerArray) {
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
    }


    private void searchCustomer() {
        UIUtils.printMenuHeader("SEARCH CUSTOMER");

        String customerId = UIUtils.getStringInput(scanner, "Enter customer ID: ");

        JsonObject data = new JsonObject();
        data.addProperty("customerId", customerId);
        Request request = new Request("getCustomer", gson.toJson(data));
        JsonObject response = client.sendRequest(request);

        if (response != null && response.has("success") && response.get("success").getAsBoolean()) {
            JsonObject cust = response.getAsJsonObject("message");
            List<String[]> rows = new ArrayList<>();
            rows.add(new String[]{
                    cust.get("userId").getAsString(),
                    cust.get("fullName").getAsString(),
                    cust.get("phone").getAsString(),
                    cust.get("role").getAsString()
            });
            UIUtils.printTable(new String[]{"Customer ID","Full Name","Phone","Type"}, rows);
        } else {
            UIUtils.showError(response != null && response.has("message") ? response.get("message").getAsString() : "Customer not found or connection error");
        }

        UIUtils.waitForEnter(scanner);
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

    private void showCustomerPlanDetails(String customerType) {
        String planDetails = "";
        switch (customerType) {
            case "NEW":
                planDetails = "Welcome Plan: 5% discount";
                break;
            case "RETURNING":
                planDetails = "Loyalty Plan: 10 usd coupon";
                break;
            case "VIP":
                planDetails = "VIP Plan: 20% discount";
                break;
        }
        UIUtils.showInfo("Customer Plan: " + planDetails);
    }

    private void addNewCustomer() {
        UIUtils.printMenuHeader("ADD NEW CUSTOMER");
        String fullName = UIUtils.getStringInput(scanner, "Full Name: ");
        String passportId = UIUtils.getStringInput(scanner, "Passport/ID: ");
        String phone = UIUtils.getStringInput(scanner, "Phone Number: ");
        JsonObject data = new JsonObject();
        data.addProperty("fullName", fullName);
        data.addProperty("passportId", passportId);
        data.addProperty("phoneNumber", phone);
        Request request = new Request("createCustomer", gson.toJson(data));
        JsonObject resp = client.sendRequest(request);
        if (resp != null && resp.has("success") && resp.get("success").getAsBoolean()) {
            UIUtils.showSuccess("Customer created successfully");
        } else {
            UIUtils.showError(resp != null && resp.has("message") ? resp.get("message").getAsString() : "Failed to create customer");
        }
        UIUtils.waitForEnter(scanner);
    }

    private void updateCustomer() {
        UIUtils.printMenuHeader("UPDATE CUSTOMER");
        UIUtils.showInfo("Update customer is not implemented in this client.");
        UIUtils.waitForEnter(scanner);
    }

    private void viewPurchaseHistory() {
        UIUtils.printMenuHeader("CUSTOMER PURCHASE HISTORY");
        UIUtils.showInfo("Purchase history is not implemented in this client.");
        UIUtils.waitForEnter(scanner);
    }

    private void displayPurchaseHistory(String response) { // legacy not used
        try {
            // Expected format: PURCHASE_HISTORY|date:productName:quantity:amount:discount|...
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
            data.addProperty("branchId", currentUser.get("branchId").getAsString());
            data.addProperty("customerId", customerId);
            JsonObject products = new JsonObject();
            products.addProperty(productId, quantity);
            data.add("products", products);

            Request request = new Request("performOrder", gson.toJson(data));
            JsonObject response = client.sendRequest(request);

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