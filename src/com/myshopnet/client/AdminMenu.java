package com.myshopnet.client;

import com.google.gson.*;
import com.myshopnet.client.utils.UIUtils;

import java.util.*;
import java.util.List;

public class AdminMenu implements Menu {
    private Scanner scanner;

    public AdminMenu() {
        this.scanner = Singletons.CLIENT.getScanner();
    }

    public void show() {
        while (true) {
            UIUtils.printMenuHeader("ADMIN MENU");
            UIUtils.printLine("System administration tools");
            UIUtils.printEmptyLine();

            UIUtils.printMenuOption(1, "Customer Management");
            UIUtils.printMenuOption(2, "Password Policy Settings");
            UIUtils.printMenuOption(3, "Branch Management");
            UIUtils.printMenuOption(4, "System Logs");
            UIUtils.printMenuOption(5, "Employee Management");
            UIUtils.printMenuOption(0, "Log out");

            UIUtils.printMenuFooter();

            int choice = UIUtils.getIntInput(scanner);

            switch (choice) {
                case 1:
                    manageCustomers();
                    break;
                case 2:
                    ((PasswordPolicyMenu)Singletons.PASSWORD_POLICY_MENU).setGoBackMenu(this);
                    Singletons.PASSWORD_POLICY_MENU.show();
                    break;
                case 3:
                    ((BranchMenu)Singletons.BRANCH_MENU).setMenuToGoBack(this);
                    Singletons.BRANCH_MENU.show();
                    break;
                case 4:
                    ((LogMenu)Singletons.LOG_MENU).setMenuToGoBack(this);
                    Singletons.LOG_MENU.show();
                    break;
                case 5:
                    manageEmployees();
                    break;
                case 0:
                    Singletons.CLIENT.logout();
                    return;
                default:
                    UIUtils.showError("Invalid choice. Please try again.");
                    UIUtils.waitForEnter(scanner);
            }
        }
    }

    private void manageCustomers() {
        while (true) {
            UIUtils.printMenuHeader("EMPLOYEE MANAGEMENT");
            UIUtils.printEmptyLine();

            UIUtils.printMenuOption(1, "View All Customers");
            UIUtils.printMenuOption(2, "Add New Customer");
            UIUtils.printMenuOption(3, "Edit Customer Details");
            UIUtils.printMenuOption(4, "Delete Customer");
            UIUtils.printMenuOption(0, "Back");

            UIUtils.printMenuFooter();

            int choice = UIUtils.getIntInput(scanner);

            switch (choice) {
                case 1:
                    viewAllCustomers();
                    break;
                case 2:
                    createCustomer();
                    break;
                case 3:
                    editCustomer();
                    break;
                case 4:
                    deleteCustomer();
                    break;
                case 0:
                    show();
                    return;
                default:
                    UIUtils.showError("Invalid choice. please try again.");
                    UIUtils.waitForEnter(scanner);
            }
        }
    }

    private void viewAllCustomers() {
        Map<String, String> map = new HashMap<>();
        map.put("currentUserId", Auth.getUsername());

        Request request = new Request("getAllCustomers", Singletons.GSON.toJson(map));
        JsonObject response = Singletons.CLIENT.sendRequest(request);

        if (response != null && response.get("success").getAsBoolean()){
            JsonArray customers = JsonParser.parseString(response.get("message").getAsString()).getAsJsonArray();
            renderCustomerTable(customers);
        } else {
            String error = response != null ? response.get("message").getAsString() : "Connection error";
            UIUtils.showError(error);
        }
        UIUtils.waitForEnter(scanner);
    }

    private void createCustomer() {
        UIUtils.printMenuHeader("ADD NEW CUSTOMER");

        String fullName = UIUtils.getStringInput(scanner, "Full Name: ");
        String phoneNumber = UIUtils.getStringInput(scanner, "Phone Number: ");
        String passportId = UIUtils.getStringInput(scanner, "Passport ID: ");
        String username = UIUtils.getStringInput(scanner, "Username (for login): ");
        String password = UIUtils.getStringInput(scanner, "Password (for login): ");

        Map<String, String> map = new HashMap<>();
        map.put("fullName", fullName);
        map.put("phoneNumber", phoneNumber);
        map.put("passportId", passportId);
        map.put("username", username);
        map.put("password", password);

        Request request = new Request("createCustomer", Singletons.GSON.toJson(map));
        JsonObject response = Singletons.CLIENT.sendRequest(request);

        if (response != null && response.get("success").getAsBoolean()){
            UIUtils.showSuccess("Customer added successfully!");
        } else {
            String error = response != null ? response.get("success").getAsString() : "Connection error";
            UIUtils.showError(error);
        }

        UIUtils.waitForEnter(scanner);
    }

    private void deleteCustomer() {
        UIUtils.printMenuHeader("DELETE CUSTOMER");

        String username = UIUtils.getStringInput(scanner, "Username of employee to delete: ");

        Map<String, String> map = new HashMap<>();
        map.put("userId",  Auth.getUsername());
        map.put("username", username);

        JsonObject resp = Singletons.CLIENT.sendRequest(new Request("deleteCustomer", Singletons.GSON.toJson(map)));

        if (resp != null && resp.get("success").getAsBoolean()) {
            UIUtils.showSuccess("Customer deleted.");
        } else {
            String error = resp != null ? resp.get("message").getAsString() : "Connection error";
            UIUtils.showError(error);
        }

        UIUtils.waitForEnter(scanner);
    }

    private void editCustomer() {
        UIUtils.printMenuHeader("UPDATE CUSTOMER");

        String username = UIUtils.getStringInput(scanner, "Employee username to edit: ");

        Map<String, String> current = new HashMap<>();

        current.put("customerId", username);

        Request request = new Request("getCustomer", Singletons.GSON.toJson(current));

        JsonObject response = Singletons.CLIENT.sendRequest(request);

        if (response != null && response.get("success").getAsBoolean()) {
            JsonObject currentCustomer = JsonParser.parseString(response.get("message").getAsString()).getAsJsonObject();

            String fullName = UIUtils.getStringInput(scanner, "Full Name (leave blank to not edit): ");
            String phoneNumber = UIUtils.getStringInput(scanner, "Phone Number (leave blank to not edit): ");
            String passportId = UIUtils.getStringInput(scanner, "Passport ID (leave blank to not edit): ");

            Map<String, String> getMap = new HashMap<>();

            getMap.put("userId", Auth.getUsername());
            getMap.put("username", username);
            getMap.put("fullName", fullName.isBlank() ? currentCustomer.get("fullName").getAsString() : fullName);
            getMap.put("phoneNumber", phoneNumber.isBlank() ? currentCustomer.get("phone").getAsString() : phoneNumber);
            getMap.put("passportId", passportId.isBlank() ? currentCustomer.get("passportId").getAsString() : passportId);

            JsonObject getResp = Singletons.CLIENT.sendRequest(new Request("updateCustomer", Singletons.GSON.toJson(getMap)));

            if (getResp == null || !getResp.get("success").getAsBoolean()) {
                String error = getResp != null ? getResp.get("message").getAsString() : "Connection error";
                UIUtils.showError(error);
                UIUtils.waitForEnter(scanner);
                return;
            }

            if (getResp.get("success").getAsBoolean()) {
                UIUtils.showSuccess("Employee updated.");
            }
            else {
                String error = getResp.get("message").getAsString();
                UIUtils.showError(error);
            }
            UIUtils.waitForEnter(scanner);
        }
        else if (response != null && !response.get("success").getAsBoolean()) {
            String error = response != null ? response.get("message").getAsString() : "Connection error";
            UIUtils.showError(error);
        }
    }


    private void manageUserAccounts() {
        UIUtils.printMenuHeader("USER ACCOUNT MANAGEMENT");
        UIUtils.printEmptyLine();

        UIUtils.printMenuOption(1, "View All Employees in Branches");
        UIUtils.printMenuOption(2, "Reset User Password");
        UIUtils.printMenuOption(0, "Back");

        UIUtils.printMenuFooter();

        int choice = UIUtils.getIntInput(scanner);

        switch (choice) {
            case 1:
                viewAllEmployees();
                break;
            case 2:
                resetUserPassword();
                break;
            case 0:
                return;
            default:
                UIUtils.showError("Invalid choice. Please try again.");
                UIUtils.waitForEnter(scanner);
        }
    }

    public String getBranchByBranchId(String branchId) {
        Map<String, String> requestMap = new HashMap<>();
        String userId = Auth.getCurrentUser().get("userId").getAsString();
        String name = null;

        requestMap.put("branchId", branchId);
        requestMap.put("userId", Auth.getUsername());

        Request request = new Request("getBranchByBranchId", Singletons.GSON.toJson(requestMap));
        JsonObject responseBranch = Singletons.CLIENT.sendRequest(request);

        if (responseBranch.get("success").getAsBoolean()) {
            JsonObject branchObject = JsonParser.parseString(responseBranch.get("message").getAsString()).getAsJsonObject();

            name = branchObject.get("name").getAsString();
        }
        else {
            UIUtils.showError("Failed to get branch by id: " + branchId);
        }

        return name;
    }

    private void resetUserPassword() {
        Map<String, String> requestMap = new HashMap<>();
        UIUtils.printMenuHeader("RESET USER PASSWORD");

        String username = UIUtils.getStringInput(scanner, "Username: ");
        String newPassword = UIUtils.getStringInput(scanner, "New Password: ");

        requestMap.put("userId", Auth.getCurrentUser().get("userId").getAsString());
        requestMap.put("username", username);
        requestMap.put("newPassword", newPassword);

        Request request = new Request("resetUserPassword", Singletons.GSON.toJson(requestMap));
        JsonObject response = Singletons.CLIENT.sendRequest(request);

        if (response != null && response.get("success").getAsBoolean()) {
            UIUtils.showSuccess("Password reset successfully!");
        } else {
            String error = response != null ? response.get("message").getAsString() : "Connection error";
            UIUtils.showError(error);
        }

        UIUtils.waitForEnter(scanner);
    }

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
                case 1:
                    viewAllEmployees();
                    break;
                case 2:
                    viewEmployeeByBranch();
                    break;
                case 3:
                    addNewEmployee();
                    break;
                case 4:
                    editEmployee();
                    break;
                case 5:
                    deleteEmployee();
                    break;
                case 0:
                    show();
                    return;
                default:
                    UIUtils.showError("Invalid choice. please try again.");
                    UIUtils.waitForEnter(scanner);
            }
        }
    }

    private void viewAllEmployees(){
        Map<String, String> map = new HashMap<>();
        map.put("currentUserId", Auth.getUsername());

        Request request = new Request("getAllEmployees", Singletons.GSON.toJson(map));
        JsonObject response = Singletons.CLIENT.sendRequest(request);

        if (response != null && response.get("success").getAsBoolean()){
            JsonArray employees = JsonParser.parseString(response.get("message").getAsString()).getAsJsonArray();
            renderEmployeesTable(employees);
        } else {
            String error = response != null ? response.get("message").getAsString() : "Connection error";
            UIUtils.showError(error);
        }
        UIUtils.waitForEnter(scanner);
    }

    private void viewEmployeeByBranch() {
        Map<String, String> req = new HashMap<>();
        req.put("userId", Auth.getUsername());

        Request request = new Request("getAllBranches", Singletons.GSON.toJson(req));
        JsonObject response = Singletons.CLIENT.sendRequest(request);

        if (response != null && !response.get("success").getAsBoolean()) {
            String error = response != null ? response.get("message").getAsString() : "Connection error";
            UIUtils.showError(error);
            UIUtils.waitForEnter(scanner);
            return;
        }

        JsonArray branches = JsonParser.parseString(response.get("message").getAsString()).getAsJsonArray();
        List<String []> rows = new ArrayList<>();
        for (JsonElement el: branches){
            JsonObject b = el.getAsJsonObject();
            rows.add(new String[]{b.get("id").getAsString(), b.get("name").getAsString()});
        }
        UIUtils.printTable(new String[]{"Branch ID", "Name"}, rows);

        //Select branch and view employes
        String branchId = UIUtils.getStringInput(scanner, "Enter Branch ID: ");
        Map<String, String> map = new HashMap<>();
        map.put("branchId", branchId);

        Request req2 = new Request("getAllEmployeesByBranch", Singletons.GSON.toJson(map));
        JsonObject res2 = Singletons.CLIENT.sendRequest(req2);

        if (res2 != null && res2.get("success").getAsBoolean()) {
            JsonArray employees = JsonParser.parseString(res2.get("message").getAsString()).getAsJsonArray();
            renderEmployeesTable(employees);
        } else {
            String error = res2 != null ? res2.get("message").getAsString() : "Connection error";
            UIUtils.showError(error);
        }
        UIUtils.waitForEnter(scanner);
    }

    private void addNewEmployee(){
        UIUtils.printMenuHeader("ADD NEW EMPLOYEE");

        String fullName = UIUtils.getStringInput(scanner, "Full Name: ");
        String phoneNumber = UIUtils.getStringInput(scanner, "Phone Number: ");
        String accountNumber = UIUtils.getStringInput(scanner, "Account Number: ");
        String branchId = UIUtils.getStringInput(scanner, "Branch ID: ");
        String employeeType = UIUtils.getStringInput(scanner, "Employee Type (Seller, cashier, Shift_manager):  ").toUpperCase();
        String employeeNumber = UIUtils.getStringInput(scanner, "Employee Number: ");
        String username = UIUtils.getStringInput(scanner, "Username (for login): ");
        String password = UIUtils.getStringInput(scanner, "Password (for login): ");

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

        Request request = new Request("addEmployee", Singletons.GSON.toJson(map));
        JsonObject response = Singletons.CLIENT.sendRequest(request);

        if (response != null && response.get("success").getAsBoolean()){
            UIUtils.showSuccess("Employee added successfully!");
        } else {
            String error = response != null ? response.get("message").getAsString() : "Connection error";
            UIUtils.showError(error);
        }

        UIUtils.waitForEnter(scanner);
    }

    private void editEmployee() {
        UIUtils.printMenuHeader("EDIT EMPLOYEE");

        String employeeId = UIUtils.getStringInput(scanner, "Employee ID to edit: ");

        // Ask only the fields you want to change; empty input keeps old value
        String fullName = UIUtils.getStringInput(scanner, "Full Name (leave blank to keep): ");
        String phoneNumber = UIUtils.getStringInput(scanner, "Phone Number (leave blank to keep): ");
        String branchId = UIUtils.getStringInput(scanner, "Branch ID (leave blank to keep): ");
        String employeeType = UIUtils.getStringInput(scanner, "Employee Type (SELLER/CASHIER/SHIFT_MANAGER, blank to keep): ").toUpperCase();
        String employeeNumberStr = UIUtils.getStringInput(scanner, "Employee Number (blank to keep): ");
        String accountNumberStr = UIUtils.getStringInput(scanner, "Account Number (blank to keep): ");

        // Fetch current employee
        Map<String, String> getMap = new HashMap<>();
        getMap.put("employeeId", employeeId);
        JsonObject getResp = Singletons.CLIENT.sendRequest(new Request("getEmployee", Singletons.GSON.toJson(getMap)));

        if (getResp == null || !getResp.get("success").getAsBoolean()) {
            String error = getResp != null ? getResp.get("message").getAsString() : "Connection error";
            UIUtils.showError(error);
            UIUtils.waitForEnter(scanner);
            return;
        }

        JsonObject current = JsonParser.parseString(getResp.get("message").getAsString()).getAsJsonObject();

        JsonObject updated = new JsonObject();
        updated.addProperty("userId", employeeId);
        updated.addProperty("fullName", fullName.isEmpty() ? current.get("fullName").getAsString() : fullName);
        updated.addProperty("phoneNumber", phoneNumber.isEmpty() ? current.get("phoneNumber").getAsString() : phoneNumber);
        updated.addProperty("branchId", branchId.isEmpty() ? current.get("branchId").getAsString() : branchId);
        updated.addProperty("employeeType", employeeType.isEmpty() ? current.get("employeeType").getAsString() : employeeType);
        updated.addProperty("employeeNumber", employeeNumberStr.isEmpty() ? current.get("employeeNumber").getAsLong() : Long.parseLong(employeeNumberStr));
        updated.addProperty("accountNumber", accountNumberStr.isEmpty() ? current.get("accountNumber").getAsLong() : Long.parseLong(accountNumberStr));
        updated.addProperty("employeeStatus", current.get("employeeStatus").getAsString()); // unchanged here
        updated.addProperty("role", current.get("role").getAsString()); // keep role

        JsonObject payload = new JsonObject();
        payload.addProperty("currentUserId", Auth.getUsername());
        payload.add("employee", updated);

        JsonObject resp = Singletons.CLIENT.sendRequest(new Request("updateEmployee", Singletons.GSON.toJson(payload)));

        if (resp != null && resp.get("success").getAsBoolean()) {
            UIUtils.showSuccess("Employee updated.");
        } else {
            String error = resp != null ? resp.get("message").getAsString() : "Connection error";
            UIUtils.showError(error);
        }
        UIUtils.waitForEnter(scanner);
    }

    private void deleteEmployee() {
        UIUtils.printMenuHeader("DELETE EMPLOYEE");

        String employeeId = UIUtils.getStringInput(scanner, "Employee ID to delete: ");

        Map<String, String> map = new HashMap<>();
        map.put("currentUserId", Auth.getUsername());
        map.put("employeeId", employeeId);

        JsonObject resp = Singletons.CLIENT.sendRequest(new Request("deleteEmployee", Singletons.GSON.toJson(map)));

        if (resp != null && resp.get("success").getAsBoolean()) {
            UIUtils.showSuccess("Employee deleted.");
        } else {
            String error = resp != null ? resp.get("message").getAsString() : "Connection error";
            UIUtils.showError(error);
        }
        UIUtils.waitForEnter(scanner);
    }


    public void renderEmployeesTable(JsonArray employees) {
        List<String[]> rows = new ArrayList<>();
        for (JsonElement el : employees) {
            JsonObject e = el.getAsJsonObject();
            String id = e.get("userId").getAsString();
            String fullName = e.has("fullName") && !e.get("fullName").isJsonNull() ? e.get("fullName").getAsString() : "";
            String phone = e.has("phoneNumber") && !e.get("phoneNumber").isJsonNull() ? e.get("phoneNumber").getAsString() : "";
            String accountNumber = String.valueOf(e.get("accountNumber").getAsLong());
            String branchId = e.get("branchId").getAsString();
            String branchName = getBranchByBranchId(e.get("branchId").getAsString());
            String employeeType = e.get("employeeType").getAsString();
            String employeeNumber = String.valueOf(e.get("employeeNumber").getAsLong());
            String employeeStatus = e.get("employeeStatus").getAsString();
            rows.add(new String[]{id, fullName, phone, accountNumber, branchName, branchId, employeeType, employeeNumber, employeeStatus});
        }
        String[] headers = {"ID", "Full Name", "Phone", "Account #", "BranchName" ,"BranchID", "Type", "Emp #", "Status"};
        UIUtils.printTable(headers, rows);
        UIUtils.showInfo("Total employees: " + rows.size());
    }

    private void renderCustomerTable(JsonArray customers) {
        List<String[]> rows = new ArrayList<>();
        for (JsonElement el : customers) {
            JsonObject e = el.getAsJsonObject();
            String id = e.get("userId").getAsString();
            String fullName = e.has("fullName") && !e.get("fullName").isJsonNull() ? e.get("fullName").getAsString() : "";
            String phone = e.has("phone") && !e.get("phone").isJsonNull() ? e.get("phone").getAsString() : "";
            String passportId = e.get("passportId").getAsString();

            rows.add(new String[] { id, fullName, phone, passportId });
        }

        String[] headers = {"ID", "Full Name", "Phone", "Passport ID" };
        UIUtils.printTable(headers, rows);
        UIUtils.showInfo("Total customers: " + rows.size());
    }
}