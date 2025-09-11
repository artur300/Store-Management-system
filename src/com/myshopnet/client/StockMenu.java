package com.myshopnet.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.myshopnet.client.utils.UIUtils;

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
            UIUtils.printLine("Branch: " + Auth.getCurrentUser().get("branchId").getAsString());
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
                JsonObject branch = JsonParser.parseString(response.get("message").getAsString()).getAsJsonObject();

                UIUtils.waitForEnter(scanner);
            }
            else if (response.get("error") != null) {
                UIUtils.showError(response.get("message").getAsString());
            }

            String[] headers = { "Product ID", "Product Name", "Quantity", "Price" };
            UIUtils.printTable(headers, rows);
        }
        catch (Exception e) {
            UIUtils.showError("Error displaying inventory: " + e.getMessage());
        }
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