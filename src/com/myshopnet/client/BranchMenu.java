package com.myshopnet.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.myshopnet.client.models.UserTypeLoggedIn;
import com.myshopnet.client.utils.UIUtils;

import java.util.*;

public class BranchMenu implements Menu {
    private Scanner scanner;
    private Menu menuToGoBack;

    public BranchMenu() {
        this.scanner = new Scanner(System.in);
    }

    public Menu getMenuToGoBack() {
        return menuToGoBack;
    }

    public void setMenuToGoBack(Menu menuToGoBack) {
        this.menuToGoBack = menuToGoBack;
    }

    @Override
    public void show() {
        UIUtils.printMenuHeader("BRANCH MANAGEMENT");
        UIUtils.printEmptyLine();

        UIUtils.printMenuOption(1, "View Branch Info");
        UIUtils.printMenuOption(2, "Manage Branch Stock");

        UIUtils.printMenuOption(0, "Back");

        UIUtils.printMenuFooter();

        int choice = UIUtils.getIntInput(scanner);

        switch (choice) {
            case 1:
                viewBranch();
                break;
            case 2:
                Singletons.STOCK_MENU.show();
                break;
            case 0:
                menuToGoBack.show();
                return;
            default:
                UIUtils.showError("Invalid choice. Please try again.");
                UIUtils.waitForEnter(scanner);
        }
    }

    private void viewBranch() {
        Map<String, String> branchRequest = new HashMap<>();
        branchRequest.put("branchId", Auth.getCurrentUser().get("branchId").getAsString());

        Request request = new Request("getBranchByBranchId", Singletons.GSON.toJson(branchRequest));
        JsonObject response = Singletons.CLIENT.sendRequest(request);

        if (response != null && response.get("success").getAsBoolean()) {
            String branchData = response.get("message").getAsString();


            JsonObject branchJson = JsonParser.parseString(branchData).getAsJsonObject();

            UIUtils.printMenuHeader("BRANCH INFORMATION");
            System.out.println("Branch ID: " + branchJson.get("id").getAsString());
            System.out.println("Branch Name: " + branchJson.get("name").getAsString());
        }
        else {
            String error = response != null ? response.get("message").getAsString() : "Unknown error";
            UIUtils.showError("Failed to get branch info: " + error);
        }

        UIUtils.waitForEnter(scanner);
    }

    private void displayBranchesData(JsonArray response) {
        try {
            List<String[]> rows = new ArrayList<>();

            for (JsonElement element : response) {
                JsonObject branch = element.getAsJsonObject();

                String id = branch.get("id").getAsString();
                String name = branch.get("name").getAsString();

                rows.add(new String[]{id, name});
            }

            String[] headers = {"Branch ID", "Name" };
            UIUtils.printTable(headers, rows);
            UIUtils.showInfo("Total branches: " + rows.size());
        }
        catch (Exception e) {
            UIUtils.showError("Error displaying branches: " + e.getMessage());
        }
    }
}
