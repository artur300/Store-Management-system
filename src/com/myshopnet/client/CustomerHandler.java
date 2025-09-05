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

        String request = "GET_ALL_CUSTOMERS";
        JsonObject response = client.sendRequest(request);

        if (response != null && response.startsWith("CUSTOMERS_DATA")) {
            displayCustomersData(response);
        } else {
            UIUtils.showError("Failed to retrieve customer data");
        }

        UIUtils.waitForEnter(scanner);
    }

    private void displayCustomersData(String response) {
        try {
            // Expected format: CUSTOMERS_DATA|id:fullName:phone:type|...
            String[] parts = response.split("\\|");
            if (parts.length > 1) {
                List<String[]> rows = new ArrayList<>();

                for (int i = 1; i < parts.length; i++) {
                    String[] customerData = parts[i].split(":");
                    if (customerData.length >= 4) {
                        rows.add(new String[]{
                                customerData[0], // ID
                                customerData[1], // Full Name
                                customerData[2], // Phone
                                customerData[3]  // Type
                        });
                    }
                }

                String[] headers = {"Customer ID", "Full Name", "Phone Number", "Type"};
                UIUtils.printTable(headers, rows);
                UIUtils.showInfo("Total customers: " + rows.size());
            } else {
                UIUtils.printLine("No customers found");
            }
        } catch (Exception e) {
            UIUtils.showError("Error displaying customers: " + e.getMessage());
        }
    }

    private void searchCustomer() {
        UIUtils.printMenuHeader("SEARCH CUSTOMER");

        String searchTerm = UIUtils.getStringInput(scanner, "Enter customer name, ID, or phone: ");

        String request = "SEARCH_CUSTOMER|" + searchTerm;
        JsonObject response = client.sendRequest(request);

        if (response != null && response.startsWith("CUSTOMER_FOUND")) {
            displayCustomerDetails(response);
        } else {
            UIUtils.showError("Customer not found or connection error");
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

    private void updateCustomer() {
        UIUtils.printMenuHeader("UPDATE CUSTOMER");

        String customerId = UIUtils.getStringInput(scanner, "Customer ID to update: ");

        // First, get current customer data
        String searchRequest = "SEARCH_CUSTOMER|" + customerId;
        JsonObject searchResponse = client.sendRequest(searchRequest);

        if (searchResponse == null || !searchResponse.startsWith("CUSTOMER_FOUND")) {
            UIUtils.showError("Customer not found");
            UIUtils.waitForEnter(scanner);
            return;
        }

        // Display current data and get updates
        UIUtils.printLine("Leave field empty to keep current value:");
        String newFullName = UIUtils.getStringInput(scanner, "New Full Name: ");
        String newPhone = UIUtils.getStringInput(scanner, "New Phone Number: ");

        int typeChoice = UIUtils.getIntInput(scanner);
        String newType = "";

        String request = String.format("UPDATE_CUSTOMER|%s|%s|%s|%s|%s",
                customerId, newFullName, newPhone, newType, currentUser.get("employeeNumber").getAsString());
        JsonObject response = client.sendRequest(request);

        if (response != null && response.equals("CUSTOMER_UPDATED")) {
            UIUtils.showSuccess("Customer updated successfully!");
        } else {
            String error = response != null ? response.replace("CUSTOMER_UPDATE_FAILED|", "") : "Connection error";
            UIUtils.showError(error);
        }

        UIUtils.waitForEnter(scanner);
    }

    private void viewPurchaseHistory() {
        UIUtils.printMenuHeader("CUSTOMER PURCHASE HISTORY");

        String customerId = UIUtils.getStringInput(scanner, "Customer ID: ");

        String request = "GET_PURCHASE_HISTORY|" + customerId;
        JsonObject response = client.sendRequest(request);

        if (response != null && response.startsWith("PURCHASE_HISTORY")) {
            displayPurchaseHistory(response);
        } else {
            UIUtils.showError("Failed to retrieve purchase history or customer not found");
        }

        UIUtils.waitForEnter(scanner);
    }

    private void displayPurchaseHistory(String response) {
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

            String request = String.format("CUSTOMER_PURCHASE|%s|%s|%s|%d|%s|%s",
                    currentUser.get("branchId").getAsString(), customerId, productId, quantity,
                    currentUser.get("employeeNumber").getAsString(), currentUser.get("branchId").getAsString());
            JsonObject response = client.sendRequest(request);

            if (response != null && response.startsWith("PURCHASE_SUCCESS")) {
                String[] parts = response.split("\\|");
                UIUtils.showSuccess("Purchase completed!");
                if (parts.length > 1) {
                    UIUtils.showInfo("Transaction ID: " + parts[1]);
                }
                if (parts.length > 2) {
                    UIUtils.showInfo("Original Amount: $" + parts[2]);
                }
                if (parts.length > 3) {
                    UIUtils.showInfo("Discount Applied: $" + parts[3]);
                }
                if (parts.length > 4) {
                    UIUtils.showInfo("Final Amount: $" + parts[4]);
                }
            } else {
                String error = response != null ? response.replace("PURCHASE_FAILED|", "") : "Connection error";
                UIUtils.showError(error);
            }
        } catch (NumberFormatException e) {
            UIUtils.showError("Invalid quantity format");
        }

        UIUtils.waitForEnter(scanner);
    }
}