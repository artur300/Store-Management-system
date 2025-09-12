package com.myshopnet.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.myshopnet.client.models.Product;
import com.myshopnet.client.utils.UIUtils;

import java.util.*;

public class CustomerMenu implements Menu {
    private Scanner scanner;
    private Map<String, Product> cart;

    public CustomerMenu() {
        this.scanner = Singletons.CLIENT.getScanner();
        this.cart = new HashMap<>();
    }

    public void show() {
        while (true) {
            UIUtils.printMenuHeader("CUSTOMER MENU");
            UIUtils.printEmptyLine();

            UIUtils.printMenuOption(1, "View customer plan");
            UIUtils.printMenuOption(2, "Add items to cart");
            UIUtils.printMenuOption(3, "View purchase history");
            UIUtils.printMenuOption(4, "View cart");
            UIUtils.printMenuOption(0, "Log out");

            UIUtils.printMenuFooter();

            int choice = UIUtils.getIntInput(scanner);

            switch (choice) {
                case 1:
                    showCustomerPlanDetails();
                case 2:
                    addItemsToCart();
                case 3:
                    viewPurchaseHistory();
                case 4:
                    viewCart();
                case 0:
                    Singletons.CLIENT.logout();
                    return;
                default:
                    UIUtils.showError("Invalid choice. Please try again.");
                    UIUtils.waitForEnter(scanner);
            }
        }
    }

    private void viewCart() {
        renderCartMenu();

        UIUtils.printEmptyLine();

        if (!cart.isEmpty()) {
            UIUtils.printMenuOption(1, "Buy clothes");
        }
        UIUtils.printMenuOption(2, "Go back");

        UIUtils.printMenuFooter();

        int choice = UIUtils.getIntInput(scanner);

        try {
            switch (choice) {
                case 1:
                    buy();
                    break;
                case 2:
                    show();
                    break;
                default:
                    UIUtils.showError("Invalid choice. Please try again.");
                    UIUtils.waitForEnter(scanner);
            }
        }
        catch (Exception e) {
            UIUtils.showError(e.getMessage());
            UIUtils.waitForEnter(scanner);
        }
    }

    private void buy() {
        checkIfCartInStock();

        Map<String, String> requestMap = new HashMap<>();

        List<Map<String,String>> productSkuQuantity = new ArrayList<>();
        Map<String, String> orderMap;

        for (Map.Entry<String, Product> productEntry : cart.entrySet()) {
            orderMap = new HashMap<>();

            String sku = productEntry.getKey();
            Integer quantity = productEntry.getValue().getQuantity();
            String branchId = productEntry.getValue().getBranchId();

            orderMap.put("sku", sku);
            orderMap.put("quantity", quantity.toString());
            orderMap.put("branchId", branchId);

            productSkuQuantity.add(orderMap);
        }

        requestMap.put("customerId", Auth.getUsername());
        requestMap.put("products", Singletons.GSON.toJson(productSkuQuantity));

        Request request = new Request("performOrder", Singletons.GSON.toJson(requestMap));
        JsonObject response = Singletons.CLIENT.sendRequest(request);

        if (response.get("success").getAsBoolean()) {
            UIUtils.showSuccess("Order processed successfully. Enjoy your clothes!");
        }
        else if (!response.get("success").getAsBoolean()) {
            UIUtils.showError(response.get("message").getAsString());
        }

        cart = new HashMap<>();

        UIUtils.waitForEnter(scanner);
    }

    private void checkIfCartInStock() {
        Map<String, String> requestMap = new HashMap<>();

        for (Map.Entry<String, Product> productEntry : cart.entrySet()) {
            String sku = productEntry.getKey();
            Integer quantity = productEntry.getValue().getQuantity();
            String branchId = productEntry.getValue().getBranchId();

            requestMap.put("sku", sku);
            requestMap.put("quantity", quantity.toString());
            requestMap.put("branchId", branchId);

            Request request = new Request("checkIfProductInStockInBranch", Singletons.GSON.toJson(requestMap));
            JsonObject response = Singletons.CLIENT.sendRequest(request);

            if (response.get("success").getAsBoolean()) {
                if (!Boolean.parseBoolean(response.get("message").getAsString())) {
                    throw new RuntimeException(String.format("The product %s is not in stock.", productEntry.getValue().getName()));
                }
            }
            else {
                throw new RuntimeException("Can't perform buying proccess");
            }

            requestMap = new HashMap<>();
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

    private void addItemsToCart() {
        UIUtils.printMenuHeader("ADD NEW ITEMS");

       try {
           String branchIdChosen = getBranchPick();
           Product product = getProductPick(branchIdChosen);
           UIUtils.printLine("Enter quantity: ");
           Integer quantity = UIUtils.getIntInput(scanner);
           product.setQuantity(quantity);

           if (quantity <= 0) {
               throw new RuntimeException("Invalid quantity. Please try again.");
           }

           if (branchIdChosen == null) {
               throw new RuntimeException("Invalid branchId. Please try again.");
           }

           if (product == null) {
               throw new RuntimeException("Invalid productId. Please try again.");
           }

           cart.put(product.getSku(), product);
           UIUtils.printLine("Successfully added " + product.getSku() + " to the cart. Press enter to continue.");
       }
       catch (Exception e) {
           UIUtils.showError(e.getMessage());
       }

       show();
    }


    private String getBranchPick() {
        Map<String, String> req = new HashMap<>();
        List<String> branchesId = new ArrayList<>();
        req.put("userId", Auth.getUsername());

        Request request = new Request("getAllBranches", Singletons.GSON.toJson(req));
        JsonObject response = Singletons.CLIENT.sendRequest(request);

        if (response != null && !response.get("success").getAsBoolean()) {
            String error = response != null ? response.get("message").getAsString() : "Connection error";
            UIUtils.showError(error);
            UIUtils.waitForEnter(scanner);
            return null;
        }

        if (response == null) {
            UIUtils.showError("Response is null");
            UIUtils.waitForEnter(scanner);
            return null;
        }

        JsonArray branches = JsonParser.parseString(response.get("message").getAsString()).getAsJsonArray();
        int i = 1;
        for (JsonElement el: branches){
            JsonObject b = el.getAsJsonObject();
            branchesId.add(b.get("id").getAsString());
            UIUtils.printMenuOption(i++, b.get("name").getAsString());
        }

        UIUtils.printLine("Enter the branch you want to purchase from: ");
        int choice = UIUtils.getIntInput(scanner);

        if (choice < 1 || choice > branchesId.size()) {
            UIUtils.showError("Invalid choice. Please try again.");
            return null;
        }

        return branchesId.get(choice - 1);
    }

    private Product getProductPick(String branchIdChosen) {
        List<Product> productsActual = new ArrayList<>();
        Request request = new Request("getAllProducts", null);
        JsonObject response = Singletons.CLIENT.sendRequest(request);

        if (response != null && !response.get("success").getAsBoolean()) {
            String error = response.get("message").getAsString();
            UIUtils.showError(error);
            UIUtils.waitForEnter(scanner);
            return null;
        }

        if (response == null) {
            UIUtils.showError("Response is null");
            UIUtils.waitForEnter(scanner);
            return null;
        }

        JsonArray products = JsonParser.parseString(response.get("message").getAsString()).getAsJsonArray();
        int i = 1;
        for (JsonElement el : products) {
            JsonObject b = el.getAsJsonObject();

            Product product = new Product(
                    b.get("sku").getAsString(),
                    b.get("name").getAsString(),
                    b.get("price").getAsDouble(),
                    branchIdChosen,
                    0
            );

            productsActual.add(product);
            UIUtils.printMenuOption(i++, String.format("%s (%s) - $%s", b.get("name").getAsString(),
                    b.get("sku").getAsString() , b.get("price").getAsString()));
        }

        UIUtils.printLine("Enter the product you want to purchase: ");
        int choice = UIUtils.getIntInput(scanner);

        if (choice < 0 || choice > productsActual.size()) {
            UIUtils.showError("Invalid choice. Please try again.");
            return null;
        }

        return productsActual.get(choice - 1);
    }

    private void renderCartMenu() {
        List<String[]> rows = new ArrayList<>();
        Double total = 0.0, discountTotal = 0.0;

        for (Product p : cart.values()) {
            total += p.getPrice() * p.getQuantity();

            rows.add(new String[] { p.getSku(), p.getName(), String.valueOf(p.getQuantity()), String.valueOf(p.getPrice())});
        }

        String[] headers = {"Product SKU", "Product Name", "Quantity", "Product Price"};
        UIUtils.printTable(headers, rows);
        UIUtils.showInfo("Total price before discount: " + total);

        String customerType = Auth.getCurrentUser().get("customerType").getAsString();

        switch (customerType) {
            case "New Customer":
                discountTotal = total * 0.95;
                break;
            case "Returning Customer":
                discountTotal = total - 10 < 0 ? 0 : total - 10;
                break;
           case "VIP Customer":
               discountTotal = total * 0.80;
               break;
        }

        UIUtils.showInfo("Total price after discount: " + discountTotal);
    }
}