package com.myshopnet.client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.myshopnet.client.utils.UIUtils;

import java.util.*;

public class StockHandler {
    private final Gson gson = new Gson();

    private Client client;
    private JsonObject currentUser;
    private Scanner scanner;

    public StockHandler(Client client, JsonObject currentUser) {
        this.client = client;
        this.currentUser = currentUser;
        this.scanner = client.getScanner();
    }

    public void showStockMenu() {
        while (true) {
            UIUtils.printMenuHeader("STOCK MANAGEMENT");
            UIUtils.printLine("Branch: " + currentUser.get("branchId").getAsString());
            UIUtils.printEmptyLine();

            UIUtils.printMenuOption(1, "View Current Stock");
            UIUtils.printMenuOption(2, "Search Product");
            UIUtils.printMenuOption(4, "Update / Restock");
            UIUtils.printMenuOption(0, "Back to Main Menu");

            UIUtils.printMenuFooter();

            int choice = UIUtils.getIntInput(scanner);

            switch (choice) {
                case 1:
                    viewStockByBranch();
                    break;
                case 2:
                    searchProduct();
                    break;
                case 3:
                    processPurchase();
                    break;
                case 0:
                    return;
                default:
                    UIUtils.showError("Invalid choice. Please try again.");
                    UIUtils.waitForEnter(scanner);
            }
        }
    }

    public void viewStockByBranch() {

    }

    private void displayStockData(String response) {
        try {
            String[] parts = response.split("\\|");
            if (parts.length > 1) {
                List<String[]> rows = new ArrayList<>();

                for (int i = 1; i < parts.length; i++) {
                    String[] productData = parts[i].split(":");
                    if (productData.length >= 4) {
                        rows.add(new String[]{
                                productData[0], // ID
                                productData[1], // Name
                                productData[2], // Quantity
                                "$" + productData[3] // Price
                        });
                    }
                }

                String[] headers = {"Product ID", "Product Name", "Quantity", "Price"};
                UIUtils.printTable(headers, rows);
            } else {
                UIUtils.printLine("No products in inventory");
            }
        } catch (Exception e) {
            UIUtils.showError("Error displaying inventory: " + e.getMessage());
        }
    }

    private void searchProduct() {
        UIUtils.printMenuHeader("SEARCH PRODUCT");

        String searchTerm = UIUtils.getStringInput(scanner, "Enter product name or ID: ");

        String request = String.format("SEARCH_PRODUCT|%s|%s", currentUser.get("branchId").getAsString(), searchTerm);
        JsonObject response = client.sendRequest(request);

//        if (response != null && response.startsWith("PRODUCT_FOUND")) {
//            displaySearchResults(response);
//        } else {
//            UIUtils.showError("Product not found or connection error");
//        }

        UIUtils.waitForEnter(scanner);
    }

    private void displaySearchResults(String response) {
        try {
            String[] parts = response.split("\\|");
            if (parts.length > 1) {
                List<String[]> rows = new ArrayList<>();

                for (int i = 1; i < parts.length; i++) {
                    String[] productData = parts[i].split(":");
                    if (productData.length >= 4) {
                        rows.add(new String[]{
                                productData[0],
                                productData[1],
                                productData[2],
                                "$" + productData[3]
                        });
                    }
                }

                String[] headers = {"Product ID", "Product Name", "Quantity", "Price"};
                UIUtils.printTable(headers, rows);
            }
        } catch (Exception e) {
            UIUtils.showError("Error displaying search results: " + e.getMessage());
        }
    }



    private void processPurchase() {
        UIUtils.printMenuHeader("PROCESS PURCHASE/RESTOCK");

        String productId = UIUtils.getStringInput(scanner, "Product ID: ");
        String productName = UIUtils.getStringInput(scanner, "Product Name: ");
        String quantityStr = UIUtils.getStringInput(scanner, "Quantity to add: ");
        String priceStr = UIUtils.getStringInput(scanner, "Unit Price: ");

        try {
            int quantity = Integer.parseInt(quantityStr);
            double price = Double.parseDouble(priceStr);

            String request = String.format("PROCESS_PURCHASE|%s|%s|%s|%d|%.2f|%s",
                    currentUser.get("branchId").getAsString(), productId, productName, quantity, price,
                    currentUser.get("employeeNumber").getAsString());
            JsonObject response = client.sendRequest(request);

            if (response != null && response.startsWith("PURCHASE_SUCCESS")) {
                UIUtils.showSuccess("Purchase/Restock completed successfully!");
            } else {
                String error = response != null ? response.replace("PURCHASE_FAILED|", "") : "Connection error";
                UIUtils.showError(error);
            }
        } catch (NumberFormatException e) {
            UIUtils.showError("Invalid number format");
        }

        UIUtils.waitForEnter(scanner);
    }
}