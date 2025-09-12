package com.myshopnet.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.myshopnet.client.utils.UIUtils;
import com.myshopnet.models.Admin;

import java.util.*;

public class StockMenu implements Menu {
    private Scanner scanner;
    private Menu menuToGoBack;

    public StockMenu() {
        this.scanner = Singletons.CLIENT.getScanner();
    }

    public Scanner getScanner() {
        return scanner;
    }

    public void setScanner(Scanner scanner) {
        this.scanner = scanner;
    }

    public Menu getMenuToGoBack() {
        return menuToGoBack;
    }

    public void setMenuToGoBack(Menu menuToGoBack) {
        this.menuToGoBack = menuToGoBack;
    }

    public void show() {
        while (true) {
            UIUtils.printMenuHeader("STOCK MANAGEMENT");
            UIUtils.printLine("Branch: " + ((AdminMenu)Singletons.ADMIN_MENU).getBranchByBranchId(Auth.getCurrentUser().get("branchId").getAsString()));
            UIUtils.printEmptyLine();

            UIUtils.printMenuOption(1, "View Current Stock");
            UIUtils.printMenuOption(2, "Update / Restock a product");
            UIUtils.printMenuOption(0, "Back to Main Menu");

            UIUtils.printMenuFooter();

            int choice = UIUtils.getIntInput(scanner);

            switch (choice) {
                case 1:
                    viewStockByBranchId();
                    break;
                case 2:
                    updateProductStock();
                    break;
                case 0:
                    Singletons.MAIN_MENU.show();
                    return;
                default:
                    UIUtils.showError("Invalid choice. Please try again.");
                    UIUtils.waitForEnter(scanner);
            }
        }
    }

    public void viewStockByBranchId() {
        List<String[]> rows = new ArrayList<>();
        Map<String, String> map = new HashMap<>();

        try {
            map.put("branchId", Auth.getCurrentUser().get("branchId").getAsString());

            Request request = new Request("getBranchByBranchId", Singletons.GSON.toJson(map));
            JsonObject response = Singletons.CLIENT.sendRequest(request);

            if (response != null && response.get("success").getAsBoolean()) {
                JsonObject branchData = JsonParser.parseString(response.get("message").getAsString()).getAsJsonObject();

                UIUtils.printMenuHeader("CURRENT STOCK - " + branchData.get("name").getAsString());

                JsonArray stockArray = branchData.getAsJsonArray("stock");

                for (int i = 0; i < stockArray.size(); i++) {
                    JsonObject stockItem = stockArray.get(i).getAsJsonObject();

                    rows.add(new String[]{
                            stockItem.get("sku").getAsString(),
                            stockItem.get("name").getAsString(),
                            stockItem.get("quantity").getAsString(),
                            "$" + stockItem.get("price").getAsString()
                    });
                }

                String[] headers = {"SKU", "Product Name", "Quantity", "Price"};
                UIUtils.printTable(headers, rows);
                UIUtils.showInfo("Total products: " + rows.size());
            } else {
                String error = response != null ? response.get("message").getAsString() : "Connection error";
                UIUtils.showError(error);
            }

            UIUtils.waitForEnter(scanner);
        } catch (Exception e) {
            UIUtils.showError("Error displaying inventory: " + e.getMessage());
            UIUtils.waitForEnter(scanner);
        }
    }
    private void updateProductStock() {
        UIUtils.printMenuHeader("UPDATE PRODUCT STOCK");

        String productId = UIUtils.getStringInput(scanner, "Product ID: ");
        String newQuantityStr = UIUtils.getStringInput(scanner, "New Quantity: ");

        try {
            Long newQuantity = Long.parseLong(newQuantityStr);

            Map<String, String> map = new HashMap<>();
            map.put("branchId", Auth.getCurrentUser().get("branchId").getAsString());
            map.put("userId", Auth.getUsername());
            map.put("productId", productId);
            map.put("stock", newQuantity.toString());

            Request request = new Request("updateBranchStock", Singletons.GSON.toJson(map));
            JsonObject response = Singletons.CLIENT.sendRequest(request);

            if (response != null && response.get("success").getAsBoolean()) {
                UIUtils.showSuccess("Stock updated successfully!");
            } else {
                String error = response != null ? response.get("message").getAsString() : "Connection error";
                UIUtils.showError(error);
            }

        } catch (NumberFormatException e) {
            UIUtils.showError("Invalid quantity format. Please enter a number.");
        }

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
}