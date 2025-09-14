package com.myshopnet.client;

import com.google.gson.*;
import com.myshopnet.client.utils.UIUtils;

import java.util.*;

public class AdminMenu implements Menu {
    private final Scanner scanner;

    public AdminMenu() {
        this.scanner = Singletons.CLIENT.getScanner();
    }

    @Override
    public void show() {
        while (true) {
            UIUtils.printMenuHeader("ADMIN MENU");
            UIUtils.printLine("System administration tools");
            UIUtils.printEmptyLine();

            UIUtils.printMenuOption(1, "Customer Management");
            UIUtils.printMenuOption(2, "Password Policy Settings");
            UIUtils.printMenuOption(3, "Branch Management");
            // הוסר: System Logs (לא קיים LOG_MENU)
            UIUtils.printMenuOption(4, "Employee Management");
            UIUtils.printMenuOption(0, "Log out");
            UIUtils.printMenuFooter();

            int choice = UIUtils.getIntInput(scanner);

            switch (choice) {
                case 1 -> manageCustomers();
                case 2 -> Singletons.PASSWORD_POLICY_MENU.show();
                case 3 -> {
                    ((BranchMenu) Singletons.BRANCH_MENU).setMenuToGoBack(this);
                    Singletons.BRANCH_MENU.show();
                }
                case 4 -> manageEmployees();
                case 0 -> {
                    Singletons.CLIENT.logout();
                    return;
                }
                default -> {
                    UIUtils.showError("Invalid choice. Please try again.");
                    UIUtils.waitForEnter(scanner);
                }
            }
        }
    }

    /* ===================== Customers ===================== */

    private void manageCustomers() {
        while (true) {
            UIUtils.printMenuHeader("CUSTOMER MANAGEMENT");
            UIUtils.printEmptyLine();

            UIUtils.printMenuOption(1, "View All Customers");
            UIUtils.printMenuOption(2, "Add New Customer");
            UIUtils.printMenuOption(3, "Edit Customer Details");
            UIUtils.printMenuOption(4, "Delete Customer");
            UIUtils.printMenuOption(0, "Back");
            UIUtils.printMenuFooter();

            int choice = UIUtils.getIntInput(scanner);

            switch (choice) {
                case 1 -> viewAllCustomers();
                case 2 -> createCustomer();
                case 3 -> updateCustomer();
                case 4 -> deleteCustomer();
                case 0 -> { return; }
                default -> {
                    UIUtils.showError("Invalid choice. Please try again.");
                    UIUtils.waitForEnter(scanner);
                }
            }
        }
    }

    private void viewAllCustomers() {
        Map<String, String> map = new HashMap<>();
        map.put("currentUserId", Auth.getUsername());

        JsonObject response = Singletons.CLIENT.sendRequest(
                new Request("getAllCustomers", Singletons.GSON.toJson(map)));

        if (response != null && response.has("success") && response.get("success").getAsBoolean()) {
            JsonArray customers = JsonParser.parseString(safeMessage(response)).getAsJsonArray();
            renderCustomerTable(customers);
        } else {
            UIUtils.showError(safeMessage(response));
        }
        UIUtils.waitForEnter(scanner);
    }

    private void createCustomer() {
        UIUtils.printMenuHeader("ADD NEW CUSTOMER");

        String fullName   = UIUtils.getStringInput(scanner, "Full Name: ");
        String phone      = UIUtils.getStringInput(scanner, "Phone Number: ");
        String passportId = UIUtils.getStringInput(scanner, "Passport ID: ");
        String username   = UIUtils.getStringInput(scanner, "Username (for login): ");
        String password   = UIUtils.getStringInput(scanner, "Password (for login): ");

        Map<String, String> map = new HashMap<>();
        map.put("fullName", fullName);
        map.put("phoneNumber", phone);
        map.put("passportId", passportId);
        map.put("username", username);
        map.put("password", password);

        JsonObject response = Singletons.CLIENT.sendRequest(
                new Request("createCustomer", Singletons.GSON.toJson(map)));

        if (response != null && response.has("success") && response.get("success").getAsBoolean()) {
            UIUtils.showSuccess("Customer added successfully!");
        } else {
            UIUtils.showError(safeMessage(response));
        }
        UIUtils.waitForEnter(scanner);
    }

    private void updateCustomer() {
        UIUtils.printMenuHeader("UPDATE CUSTOMER");

        String username   = UIUtils.getStringInput(scanner, "Customer username to edit: ");
        String fullName   = UIUtils.getStringInput(scanner, "Full Name: ");
        String phone      = UIUtils.getStringInput(scanner, "Phone Number: ");
        String passportId = UIUtils.getStringInput(scanner, "Passport ID: ");

        Map<String, String> map = new HashMap<>();
        map.put("userId", Auth.getUsername());
        map.put("username", username);
        map.put("fullName", fullName);
        map.put("phoneNumber", phone);
        map.put("passportId", passportId);

        JsonObject resp = Singletons.CLIENT.sendRequest(
                new Request("updateCustomer", Singletons.GSON.toJson(map)));

        if (resp != null && resp.has("success") && resp.get("success").getAsBoolean()) {
            UIUtils.showSuccess("Customer updated.");
        } else {
            UIUtils.showError(safeMessage(resp));
        }
        UIUtils.waitForEnter(scanner);
    }

    private void deleteCustomer() {
        UIUtils.printMenuHeader("DELETE CUSTOMER");

        String username = UIUtils.getStringInput(scanner, "Username of customer to delete: ");

        Map<String, String> map = new HashMap<>();
        map.put("userId",  Auth.getUsername());
        map.put("username", username);

        JsonObject resp = Singletons.CLIENT.sendRequest(
                new Request("deleteCustomer", Singletons.GSON.toJson(map)));

        if (resp != null && resp.has("success") && resp.get("success").getAsBoolean()) {
            UIUtils.showSuccess("Customer deleted.");
        } else {
            UIUtils.showError(safeMessage(resp));
        }
        UIUtils.waitForEnter(scanner);
    }

    /* ===================== Employees ===================== */

    private void manageEmployees() {
        while (true) {
            UIUtils.printMenuHeader("EMPLOYEE MANAGEMENT");
            UIUtils.printEmptyLine();

            UIUtils.printMenuOption(1, "View All Employees");
            UIUtils.printMenuOption(2, "View Employees by Branch");
            UIUtils.printMenuOption(3, "Add New Employee");
            UIUtils.printMenuOption(4, "Edit Employee Details");
            UIUtils.printMenuOption(5, "Delete Employee");
            UIUtils.printMenuOption(0, "Back");
            UIUtils.printMenuFooter();

            int choice = UIUtils.getIntInput(scanner);

            switch (choice) {
                case 1 -> viewAllEmployees();
                case 2 -> viewEmployeeByBranch();
                case 3 -> addNewEmployee();
                case 4 -> editEmployee();
                case 5 -> deleteEmployee();
                case 0 -> { return; }
                default -> {
                    UIUtils.showError("Invalid choice. Please try again.");
                    UIUtils.waitForEnter(scanner);
                }
            }
        }
    }

    private void viewAllEmployees() {
        Map<String, String> map = new HashMap<>();
        map.put("currentUserId", Auth.getUsername());

        JsonObject response = Singletons.CLIENT.sendRequest(
                new Request("getAllEmployees", Singletons.GSON.toJson(map)));

        if (response != null && response.has("success") && response.get("success").getAsBoolean()) {
            JsonArray employees = JsonParser.parseString(safeMessage(response)).getAsJsonArray();
            renderEmployeesTable(employees);
        } else {
            UIUtils.showError(safeMessage(response));
        }
        UIUtils.waitForEnter(scanner);
    }

    private void viewEmployeeByBranch() {
        Map<String, String> req = new HashMap<>();
        req.put("userId", Auth.getUsername());

        JsonObject response = Singletons.CLIENT.sendRequest(
                new Request("getAllBranches", Singletons.GSON.toJson(req)));

        if (response == null || !response.has("success") || !response.get("success").getAsBoolean()) {
            UIUtils.showError(safeMessage(response));
            UIUtils.waitForEnter(scanner);
            return;
        }

        JsonArray branches = JsonParser.parseString(safeMessage(response)).getAsJsonArray();
        List<String[]> rows = new ArrayList<>();
        for (JsonElement el : branches) {
            JsonObject b = el.getAsJsonObject();
            rows.add(new String[]{b.get("id").getAsString(), b.get("name").getAsString()});
        }
        UIUtils.printTable(new String[]{"Branch ID", "Name"}, rows);

        String branchId = UIUtils.getStringInput(scanner, "Enter Branch ID: ");
        Map<String, String> map = new HashMap<>();
        map.put("branchId", branchId);

        JsonObject res2 = Singletons.CLIENT.sendRequest(
                new Request("getAllEmployeesByBranch", Singletons.GSON.toJson(map)));

        if (res2 != null && res2.has("success") && res2.get("success").getAsBoolean()) {
            JsonArray employees = JsonParser.parseString(safeMessage(res2)).getAsJsonArray();
            renderEmployeesTable(employees);
        } else {
            UIUtils.showError(safeMessage(res2));
        }
        UIUtils.waitForEnter(scanner);
    }

    private void addNewEmployee() {
        UIUtils.printMenuHeader("ADD NEW EMPLOYEE");

        String fullName       = UIUtils.getStringInput(scanner, "Full Name: ");
        String phoneNumber    = UIUtils.getStringInput(scanner, "Phone Number: ");
        String accountNumber  = UIUtils.getStringInput(scanner, "Account Number: ");
        String branchId       = UIUtils.getStringInput(scanner, "Branch ID: ");
        String employeeType   = UIUtils.getStringInput(scanner, "Employee Type (SELLER/CASHIER/SHIFT_MANAGER): ").toUpperCase();
        String employeeNumber = UIUtils.getStringInput(scanner, "Employee Number: ");
        String username       = UIUtils.getStringInput(scanner, "Username (for login): ");
        String password       = UIUtils.getStringInput(scanner, "Password (for login): ");

        Map<String, String> map = new HashMap<>();
        map.put("currentUserId", Auth.getUsername());
        map.put("fullName", fullName);
        map.put("phoneNumber", phoneNumber);
        map.put("accountNumber", accountNumber);
        map.put("branchId", branchId);
        map.put("employeeType", employeeType);
        map.put("employeeNumber", employeeNumber);
        map.put("username", username);
        map.put("password", password);

        JsonObject response = Singletons.CLIENT.sendRequest(
                new Request("addEmployee", Singletons.GSON.toJson(map)));

        if (response != null && response.has("success") && response.get("success").getAsBoolean()) {
            UIUtils.showSuccess("Employee added successfully!");
        } else {
            UIUtils.showError(safeMessage(response));
        }
        UIUtils.waitForEnter(scanner);
    }

    private void editEmployee() {
        UIUtils.printMenuHeader("EDIT EMPLOYEE");

        String employeeId      = UIUtils.getStringInput(scanner, "Employee ID to edit: ");
        String fullName        = UIUtils.getStringInput(scanner, "Full Name (leave blank to keep): ");
        String phoneNumber     = UIUtils.getStringInput(scanner, "Phone Number (leave blank to keep): ");
        String branchId        = UIUtils.getStringInput(scanner, "Branch ID (leave blank to keep): ");
        String employeeTypeInp = UIUtils.getStringInput(scanner, "Employee Type (SELLER/CASHIER/SHIFT_MANAGER, blank to keep): ").toUpperCase();
        String employeeNumber  = UIUtils.getStringInput(scanner, "Employee Number (blank to keep): ");
        String accountNumber   = UIUtils.getStringInput(scanner, "Account Number (blank to keep): ");

        // קבלת העובד הקיים
        Map<String, String> getMap = new HashMap<>();
        getMap.put("employeeId", employeeId);
        JsonObject getResp = Singletons.CLIENT.sendRequest(
                new Request("getEmployee", Singletons.GSON.toJson(getMap)));

        if (getResp == null || !getResp.has("success") || !getResp.get("success").getAsBoolean()) {
            UIUtils.showError(safeMessage(getResp));
            UIUtils.waitForEnter(scanner);
            return;
        }

        JsonObject current = JsonParser.parseString(safeMessage(getResp)).getAsJsonObject();

        JsonObject updated = new JsonObject();
        updated.addProperty("userId", employeeId);
        updated.addProperty("fullName", fullName.isEmpty() ? current.get("fullName").getAsString() : fullName);
        updated.addProperty("phoneNumber", phoneNumber.isEmpty() ? current.get("phoneNumber").getAsString() : phoneNumber);
        updated.addProperty("branchId", branchId.isEmpty() ? current.get("branchId").getAsString() : branchId);
        updated.addProperty("employeeType", employeeTypeInp.isEmpty() ? current.get("employeeType").getAsString() : employeeTypeInp);
        updated.addProperty("employeeNumber", employeeNumber.isEmpty() ? current.get("employeeNumber").getAsLong() : Long.parseLong(employeeNumber));
        updated.addProperty("accountNumber", accountNumber.isEmpty() ? current.get("accountNumber").getAsLong() : Long.parseLong(accountNumber));
        updated.addProperty("employeeStatus", current.get("employeeStatus").getAsString());
        updated.addProperty("role", current.get("role").getAsString());

        JsonObject payload = new JsonObject();
        payload.addProperty("currentUserId", Auth.getUsername());
        payload.add("employee", updated);

        JsonObject resp = Singletons.CLIENT.sendRequest(
                new Request("updateEmployee", Singletons.GSON.toJson(payload)));

        if (resp != null && resp.has("success") && resp.get("success").getAsBoolean()) {
            UIUtils.showSuccess("Employee updated.");
        } else {
            UIUtils.showError(safeMessage(resp));
        }
        UIUtils.waitForEnter(scanner);
    }

    private void deleteEmployee() {
        UIUtils.printMenuHeader("DELETE EMPLOYEE");

        String employeeId = UIUtils.getStringInput(scanner, "Employee ID to delete: ");

        Map<String, String> map = new HashMap<>();
        map.put("currentUserId", Auth.getUsername());
        map.put("employeeId", employeeId);

        JsonObject resp = Singletons.CLIENT.sendRequest(
                new Request("deleteEmployee", Singletons.GSON.toJson(map)));

        if (resp != null && resp.has("success") && resp.get("success").getAsBoolean()) {
            UIUtils.showSuccess("Employee deleted.");
        } else {
            UIUtils.showError(safeMessage(resp));
        }
        UIUtils.waitForEnter(scanner);
    }

    /* ===================== Helpers ===================== */

    public String getBranchByBranchId(String branchId) {
        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("branchId", branchId);
        requestMap.put("userId", Auth.getUsername());

        JsonObject response = Singletons.CLIENT.sendRequest(
                new Request("getBranchByBranchId", Singletons.GSON.toJson(requestMap)));

        if (response != null && response.has("success") && response.get("success").getAsBoolean()) {
            JsonObject branchObject = JsonParser.parseString(safeMessage(response)).getAsJsonObject();
            return branchObject.get("name").getAsString();
        } else {
            return branchId; // fallback – מציג מזהה אם לא הצלחנו להביא שם
        }
    }

    public void renderEmployeesTable(JsonArray employees) {
        List<String[]> rows = new ArrayList<>();
        for (JsonElement el : employees) {
            JsonObject e = el.getAsJsonObject();
            String id            = e.get("userId").getAsString();
            String fullName      = e.has("fullName") && !e.get("fullName").isJsonNull() ? e.get("fullName").getAsString() : "";
            String phone         = e.has("phoneNumber") && !e.get("phoneNumber").isJsonNull() ? e.get("phoneNumber").getAsString() : "";
            String accountNumber = String.valueOf(e.get("accountNumber").getAsLong());
            String branchId      = e.get("branchId").getAsString();
            String branchName    = getBranchByBranchId(branchId);
            String employeeType  = e.get("employeeType").getAsString();
            String employeeNum   = String.valueOf(e.get("employeeNumber").getAsLong());
            String status        = e.get("employeeStatus").getAsString();
            rows.add(new String[]{id, fullName, phone, accountNumber, branchName, branchId, employeeType, employeeNum, status});
        }
        String[] headers = {"ID", "Full Name", "Phone", "Account #", "BranchName", "BranchID", "Type", "Emp #", "Status"};
        UIUtils.printTable(headers, rows);
        UIUtils.showInfo("Total employees: " + rows.size());
    }

    private void renderCustomerTable(JsonArray customers) {
        List<String[]> rows = new ArrayList<>();
        for (JsonElement el : customers) {
            JsonObject e = el.getAsJsonObject();
            String id        = e.get("userId").getAsString();
            String fullName  = e.has("fullName") && !e.get("fullName").isJsonNull() ? e.get("fullName").getAsString() : "";
            String phone     = e.has("phone") && !e.get("phone").isJsonNull() ? e.get("phone").getAsString() : "";
            String passport  = e.get("passportId").getAsString();
            rows.add(new String[]{id, fullName, phone, passport});
        }
        String[] headers = {"ID", "Full Name", "Phone", "Passport ID"};
        UIUtils.printTable(headers, rows);
        UIUtils.showInfo("Total customers: " + rows.size());
    }

    private static String safeMessage(JsonObject resp) {
        if (resp == null) return "Connection error";
        return resp.has("message") && !resp.get("message").isJsonNull()
                ? resp.get("message").getAsString()
                : "Unknown error";
    }
}
