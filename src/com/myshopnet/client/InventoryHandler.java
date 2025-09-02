import com.myshopnet.client.Client;
import com.myshopnet.client.utils.UIUtils;

import java.util.*;

public class InventoryHandler {
    private Client client;
    private User currentUser;
    private Scanner scanner;

    public InventoryHandler(Client client, User currentUser) {
        this.client = client;
        this.currentUser = currentUser;
        this.scanner = client.getScanner();
    }

    public void showInventoryMenu() {
        while (true) {
            UIUtils.printMenuHeader("INVENTORY MANAGEMENT");
            UIUtils.printLine("Branch: " + currentUser.getBranchId());
            UIUtils.printEmptyLine();

            UIUtils.printMenuOption(1, "View Current Stock");
            UIUtils.printMenuOption(2, "Search Product");
            UIUtils.printMenuOption(3, "Process Sale");
            UIUtils.printMenuOption(4, "Process Purchase/Restock");
            UIUtils.printMenuOption(5, "Low Stock Alert");
            UIUtils.printMenuOption(0, "Back to Main Menu");

            UIUtils.printMenuFooter();

            int choice = UIUtils.getIntInput(scanner);

            switch (choice) {
                case 1:
                    viewCurrentStock();
                    break;
                case 2:
                    searchProduct();
                    break;
                case 3:
                    processSale();
                    break;
                case 4:
                    processPurchase();
                    break;
                case 5:
                    showLowStock();
                    break;
                case 0:
                    return;
                default:
                    UIUtils.showError("Invalid choice. Please try again.");
                    UIUtils.waitForEnter(scanner);
            }
        }
    }

    private void viewCurrentStock() {
        UIUtils.printMenuHeader("CURRENT STOCK");

        String request = "GET_INVENTORY|" + currentUser.getBranchId();
        String response = client.sendRequest(request);

        if (response != null && response.startsWith("INVENTORY_DATA")) {
            displayInventoryData(response);
        } else {
            UIUtils.showError("Failed to retrieve inventory data");
        }

        UIUtils.waitForEnter(scanner);
    }

    private void displayInventoryData(String response) {
        try {
            // Expected format: INVENTORY_DATA|productId:productName:quantity:price|...
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

        String request = String.format("SEARCH_PRODUCT|%s|%s", currentUser.getBranchId(), searchTerm);
        String response = client.sendRequest(request);

        if (response != null && response.startsWith("PRODUCT_FOUND")) {
            displaySearchResults(response);
        } else {
            UIUtils.showError("Product not found or connection error");
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

    private void processSale() {
        UIUtils.printMenuHeader("PROCESS SALE");

        String productId = UIUtils.getStringInput(scanner, "Product ID: ");
        String quantityStr = UIUtils.getStringInput(scanner, "Quantity: ");
        String customerId = UIUtils.getStringInput(scanner, "Customer ID (optional): ");

        try {
            int quantity = Integer.parseInt(quantityStr);

            String request = String.format("PROCESS_SALE|%s|%s|%d|%s|%s",
                    currentUser.getBranchId(), productId, quantity, customerId, currentUser.getEmployeeNumber());
            String response = client.sendRequest(request);

            if (response != null && response.startsWith("SALE_SUCCESS")) {
                String[] parts = response.split("\\|");
                if (parts.length > 1) {
                    UIUtils.showSuccess("Sale completed! Transaction ID: " + parts[1]);
                    if (parts.length > 2) {
                        UIUtils.showInfo("Total amount: $" + parts[2]);
                    }
                }
            } else {
                String error = response != null ? response.replace("SALE_FAILED|", "") : "Connection error";
                UIUtils.showError(error);
            }
        } catch (NumberFormatException e) {
            UIUtils.showError("Invalid quantity format");
        }

        UIUtils.waitForEnter(scanner);
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
                    currentUser.getBranchId(), productId, productName, quantity, price, currentUser.getEmployeeNumber());
            String response = client.sendRequest(request);

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

    private void showLowStock() {
        UIUtils.printMenuHeader("LOW STOCK ALERT");

        String thresholdStr = UIUtils.getStringInput(scanner, "Minimum stock threshold (default 10): ");
        int threshold = 10;

        try {
            if (!thresholdStr.trim().isEmpty()) {
                threshold = Integer.parseInt(thresholdStr);
            }
        } catch (NumberFormatException e) {
            UIUtils.showInfo("Using default threshold: 10");
        }

        String request = String.format("LOW_STOCK_ALERT|%s|%d", currentUser.getBranchId(), threshold);
        String response = client.sendRequest(request);

        if (response != null && response.startsWith("LOW_STOCK_DATA")) {
            displayLowStockData(response);
        } else {
            UIUtils.showError("Failed to retrieve low stock data");
        }

        UIUtils.waitForEnter(scanner);
    }

    private void displayLowStockData(String response) {
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
                                productData[2] + " ⚠️",
                                "$" + productData[3]
                        });
                    }
                }

                if (rows.isEmpty()) {
                    UIUtils.printLine("No low stock items found");
                } else {
                    UIUtils.showInfo("Found " + rows.size() + " items with low stock:");
                    String[] headers = {"Product ID", "Product Name", "Quantity", "Price"};
                    UIUtils.printTable(headers, rows);
                }
            }
        } catch (Exception e) {
            UIUtils.showError("Error displaying low stock data: " + e.getMessage());
        }
    }