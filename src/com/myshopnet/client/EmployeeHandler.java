package com.myshopnet.client.EmployeeHandler;

import com.myshopnet.client.Client;
import com.myshopnet.client.utils.UIUtils;

import java.util.*;

public class EmployeeHandler {
    private Client client;
    private User currentUser;
    private Scanner scanner;

    public EmployeeHandler(Client client, User currentUser) {
        this.client = client;
        this.currentUser = currentUser;
        this.scanner = client.getScanner();
    }

    public void showEmployeeMenu() {
        while (true) {
            UIUtils.printMenuFooter();

            int choice = UIUtils.getIntInput(scanner);

            switch (choice) {
                case 1:
                    bulkImportEmployees();
                    break;
                case 2:
                    bulkUpdateBranches();
                    break;
                case 3:
                    bulkPasswordReset();
                    break;
                case 4:
                    exportEmployeeData();
                    break;
                case 0:
                    return;
                default:
                    UIUtils.showError("Invalid choice. Please try again.");
                    UIUtils.waitForEnter(scanner);
            }
        }
    }

        private void bulkImportEmployees() {
            UIUtils.printMenuHeader("BULK IMPORT EMPLOYEES");
            UIUtils.printLine("Format: FullName,ID,Phone,AccountNum,Branch,EmpNum,Role");
            UIUtils.printLine("Enter employee data (one per line, empty line to finish):");
            UIUtils.printEmptyLine();

            List<String> employees = new ArrayList<>();
            String line;

            while (!(line = scanner.nextLine().trim()).isEmpty()) {
                employees.add(line);
            }

            if (employees.isEmpty()) {
                UIUtils.showInfo("No employees to import");
                UIUtils.waitForEnter(scanner);
                return;
            }

            String employeeData = String.join("|", employees);
            String request = String.format("BULK_IMPORT_EMPLOYEES|%s|%s", employeeData, currentUser.getEmployeeNumber());
            String response = client.sendRequest(request);

            if (response != null && response.startsWith("BULK_IMPORT_SUCCESS")) {
                String[] parts = response.split("\\|");
                int successCount = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
                UIUtils.showSuccess("Successfully imported " + successCount + " employees");
            } else {
                String error = response != null ? response.replace("BULK_IMPORT_FAILED|", "") : "Connection error";
                UIUtils.showError(error);
            }

            UIUtils.waitForEnter(scanner);
        }

        private void bulkUpdateBranches() {
            UIUtils.printMenuHeader("BULK UPDATE BRANCHES");

            String fromBranch = UIUtils.getStringInput(scanner, "From Branch ID: ");
            String toBranch = UIUtils.getStringInput(scanner, "To Branch ID: ");

            UIUtils.printLine("This will move ALL employees from " + fromBranch + " to " + toBranch);
            System.out.print("Are you sure? (y/N): ");
            String confirm = scanner.nextLine().trim().toLowerCase();

            if (!confirm.equals("y") && !confirm.equals("yes")) {
                UIUtils.showInfo("Operation cancelled");
                UIUtils.waitForEnter(scanner);
                return;
            }

            String request = String.format("BULK_UPDATE_BRANCHES|%s|%s|%s",
                    fromBranch, toBranch, currentUser.getEmployeeNumber());
            String response = client.sendRequest(request);

            if (response != null && response.startsWith("BULK_UPDATE_SUCCESS")) {
                String[] parts = response.split("\\|");
                int updateCount = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
                UIUtils.showSuccess("Successfully updated " + updateCount + " employees");
            } else {
                String error = response != null ? response.replace("BULK_UPDATE_FAILED|", "") : "Connection error";
                UIUtils.showError(error);
            }

            UIUtils.waitForEnter(scanner);
        }

        private void bulkPasswordReset() {
            UIUtils.printMenuHeader("BULK PASSWORD RESET");

            String branchId = UIUtils.getStringInput(scanner, "Branch ID (leave empty for all branches): ");
            String newPassword = UIUtils.getStringInput(scanner, "New temporary password: ");

            UIUtils.printLine("This will reset passwords for all employees" +
                    (branchId.isEmpty() ? "" : " in branch " + branchId));
            System.out.print("Are you sure? (y/N): ");
            String confirm = scanner.nextLine().trim().toLowerCase();

            if (!confirm.equals("y") && !confirm.equals("yes")) {
                UIUtils.showInfo("Operation cancelled");
                UIUtils.waitForEnter(scanner);
                return;
            }

            String request = String.format("BULK_PASSWORD_RESET|%s|%s|%s",
                    branchId, newPassword, currentUser.getEmployeeNumber());
            String response = client.sendRequest(request);

            if (response != null && response.startsWith("BULK_RESET_SUCCESS")) {
                String[] parts = response.split("\\|");
                int resetCount = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
                UIUtils.showSuccess("Successfully reset passwords for " + resetCount + " employees");
            } else {
                String error = response != null ? response.replace("BULK_RESET_FAILED|", "") : "Connection error";
                UIUtils.showError(error);
            }

            UIUtils.waitForEnter(scanner);
        }

        private void exportEmployeeData() {
            UIUtils.printMenuHeader("EXPORT EMPLOYEE DATA");

            String branchFilter = UIUtils.getStringInput(scanner, "Branch ID (leave empty for all): ");
            String format = UIUtils.getStringInput(scanner, "Format (CSV/JSON): ");

            String request = String.format("EXPORT_EMPLOYEE_DATA|%s|%s|%s",
                    branchFilter, format.toUpperCase(), currentUser.getEmployeeNumber());
            String response = client.sendRequest(request);

            if (response != null && response.startsWith("EXPORT_SUCCESS")) {
                String[] parts = response.split("\\|", 3);
                if (parts.length > 2) {
                    UIUtils.showSuccess("Export completed!");
                    UIUtils.showInfo("File path: " + parts[1]);
                    UIUtils.showInfo("Records exported: " + parts[2]);
                }
            } else {
                String error = response != null ? response.replace("EXPORT_FAILED|", "") : "Connection error";
                UIUtils.showError(error);
            }

            UIUtils.waitForEnter(scanner);
        }

        private void showEmployeeAnalytics() {
            UIUtils.printMenuHeader("EMPLOYEE ANALYTICS");
            UIUtils.printEmptyLine();

            UIUtils.printMenuOption(1, "Branch Distribution");
            UIUtils.printMenuOption(2, "Role Distribution");
            UIUtils.printMenuOption(3, "Performance Metrics");
            UIUtils.printMenuOption(4, "Attendance Analysis");
            UIUtils.printMenuOption(5, "Turnover Rate");
            UIUtils.printMenuOption(0, "Back");

            UIUtils.printMenuFooter();

            int choice = UIUtils.getIntInput(scanner);

            switch (choice) {
                case 1:
                    showBranchDistribution();
                    break;
                case 2:
                    showRoleDistribution();
                    break;
                case 3:
                    showPerformanceMetrics();
                    break;
                case 4:
                    showAttendanceAnalysis();
                    break;
                case 5:
                    showTurnoverRate();
                    break;
                case 0:
                    return;
                default:
                    UIUtils.showError("Invalid choice. Please try again.");
                    UIUtils.waitForEnter(scanner);
            }
        }

        private void showBranchDistribution() {
            UIUtils.printMenuHeader("BRANCH DISTRIBUTION");

            String request = "GET_BRANCH_DISTRIBUTION|" + currentUser.getEmployeeNumber();
            String response = client.sendRequest(request);

            if (response != null && response.startsWith("BRANCH_DISTRIBUTION")) {
                displayDistributionData(response, "Branch");
            } else {
                UIUtils.showError("Failed to retrieve branch distribution");
            }

            UIUtils.waitForEnter(scanner);
        }

        private void showRoleDistribution() {
            UIUtils.printMenuHeader("ROLE DISTRIBUTION");

            String request = "GET_ROLE_DISTRIBUTION|" + currentUser.getEmployeeNumber();
            String response = client.sendRequest(request);

            if (response != null && response.startsWith("ROLE_DISTRIBUTION")) {
                displayDistributionData(response, "Role");
            } else {
                UIUtils.showError("Failed to retrieve role distribution");
            }

            UIUtils.waitForEnter(scanner);
        }

        private void displayDistributionData(String response, String type) {
            try {
                // Expected format: DISTRIBUTION_TYPE|category:count:percentage|...
                String[] parts = response.split("\\|");
                if (parts.length > 1) {
                    List<String[]> rows = new ArrayList<>();
                    int total = 0;

                    for (int i = 1; i < parts.length; i++) {
                        String[] distData = parts[i].split(":");
                        if (distData.length >= 3) {
                            rows.add(new String[]{
                                    distData[0], // Category
                                    distData[1], // Count
                                    distData[2] + "%" // Percentage
                            });
                            total += Integer.parseInt(distData[1]);
                        }
                    }

                    String[] headers = {type, "Count", "Percentage"};
                    UIUtils.printTable(headers, rows);
                    UIUtils.showInfo("Total employees: " + total);
                }
            } catch (Exception e) {
                UIUtils.showError("Error displaying distribution: " + e.getMessage());
            }
        }

        private void showPerformanceMetrics() {
            UIUtils.printMenuHeader("PERFORMANCE METRICS");

            String request = "GET_PERFORMANCE_METRICS|" + currentUser.getEmployeeNumber();
            String response = client.sendRequest(request);

            if (response != null && response.startsWith("PERFORMANCE_METRICS")) {
                displayPerformanceMetrics(response);
            } else {
                UIUtils.showError("Failed to retrieve performance metrics");
            }

            UIUtils.waitForEnter(scanner);
        }

        private void displayPerformanceMetrics(String response) {
            try {
                // Expected format: PERFORMANCE_METRICS|avgSales:topPerformer:lowPerformer:avgRating:totalSales
                String[] parts = response.split("\\|");
                if (parts.length > 1) {
                    String[] metrics = parts[1].split(":");
                    if (metrics.length >= 5) {
                        UIUtils.printLine("Average Sales per Employee: $" + metrics[0]);
                        UIUtils.printLine("Top Performer: " + metrics[1]);
                        UIUtils.printLine("Needs Improvement: " + metrics[2]);
                        UIUtils.printLine("Average Customer Rating: " + metrics[3] + "★");
                        UIUtils.printLine("Total Network Sales: $" + metrics[4]);
                    }
                }
            } catch (Exception e) {
                UIUtils.showError("Error displaying metrics: " + e.getMessage());
            }
        }

        private void showAttendanceAnalysis() {
            UIUtils.printMenuHeader("ATTENDANCE ANALYSIS");

            String monthYear = UIUtils.getStringInput(scanner, "Month/Year (MM/YYYY) or leave empty for current: ");

            String request = "GET_ATTENDANCE_ANALYSIS|" + monthYear + "|" + currentUser.getEmployeeNumber();
            String response = client.sendRequest(request);

            if (response != null && response.startsWith("ATTENDANCE_DATA")) {
                displayAttendanceData(response);
            } else {
                UIUtils.showError("Failed to retrieve attendance data");
            }

            UIUtils.waitForEnter(scanner);
        }

        private void displayAttendanceData(String response) {
            try {
                // Expected format: ATTENDANCE_DATA|empNum:name:daysWorked:daysExpected:attendanceRate|...
                String[] parts = response.split("\\|");
                if (parts.length > 1) {
                    List<String[]> rows = new ArrayList<>();

                    for (int i = 1; i < parts.length; i++) {
                        String[] attData = parts[i].split(":");
                        if (attData.length >= 5) {
                            rows.add(new String[]{
                                    attData[0], // Employee Number
                                    attData[1], // Name
                                    attData[2], // Days Worked
                                    attData[3], // Days Expected
                                    attData[4] + "%" // Attendance Rate
                            });
                        }
                    }

                    String[] headers = {"Emp. #", "Name", "Worked", "Expected", "Rate"};
                    UIUtils.printTable(headers, rows);
                }
            } catch (Exception e) {
                UIUtils.showError("Error displaying attendance data: " + e.getMessage());
            }
        }

        private void showTurnoverRate() {
            UIUtils.printMenuHeader("TURNOVER RATE ANALYSIS");

            String period = UIUtils.getStringInput(scanner, "Period (months, default 12): ");
            if (period.trim().isEmpty()) {
                period = "12";
            }

            String request = "GET_TURNOVER_RATE|" + period + "|" + currentUser.getEmployeeNumber();
            String response = client.sendRequest(request);

            if (response != null && response.startsWith("TURNOVER_DATA")) {
                displayTurnoverData(response);
            } else {
                UIUtils.showError("Failed to retrieve turnover data");
            }

            UIUtils.waitForEnter(scanner);
        }

        private void displayTurnoverData(String response) {
            try {
                // Expected format: TURNOVER_DATA|totalHired:totalLeft:turnoverRate:avgTenure:topReasons
                String[] parts = response.split("\\|");
                if (parts.length > 1) {
                    String[] turnoverData = parts[1].split(":");
                    if (turnoverData.length >= 5) {
                        UIUtils.printLine("Total Hired: " + turnoverData[0]);
                        UIUtils.printLine("Total Left: " + turnoverData[1]);
                        UIUtils.printLine("Turnover Rate: " + turnoverData[2] + "%");
                        UIUtils.printLine("Average Tenure: " + turnoverData[3] + " months");
                        UIUtils.printLine("Top Leaving Reasons: " + turnoverData[4]);

                        // Show branch breakdown if available
                        if (parts.length > 2) {
                            UIUtils.printEmptyLine();
                            UIUtils.printLine("Branch Breakdown:");
                            for (int i = 2; i < parts.length; i++) {
                                String[] branchData = parts[i].split(":");
                                if (branchData.length >= 2) {
                                    UIUtils.printLine("  " + branchData[0] + ": " + branchData[1] + "% turnover");
                                }
                            }
                        }
                    }
                }
            }
            catch (Exception e) {
                UIUtils.showError("Error displaying turnover data: " + e.getMessage());
            }
        }

        private void showEmployeeManagement() {
            UIUtils.printLine("Network-wide employee management");
            UIUtils.printEmptyLine();

            UIUtils.printMenuOption(1, "View All Employees");
            UIUtils.printMenuOption(2, "Search Employee");
            UIUtils.printMenuOption(3, "Add New Employee");
            UIUtils.printMenuOption(4, "Update Employee");
            UIUtils.printMenuOption(5, "Deactivate Employee");
            UIUtils.printMenuOption(6, "Employee Performance Report");
            if (currentUser.isAdmin()) {
                UIUtils.printMenuOption(7, "Bulk Operations");
                UIUtils.printMenuOption(8, "Employee Analytics");
            }
            UIUtils.printMenuOption(0, "Back to Main Menu");

            UIUtils.printMenuFooter();

            int choice = UIUtils.getIntInput(scanner);

            switch (choice) {
                case 1:
                    viewAllEmployees();
                    break;
                case 2:
                    searchEmployee();
                    break;
                case 3:
                    addNewEmployee();
                    break;
                case 4:
                    updateEmployee();
                    break;
                case 5:
                    deactivateEmployee();
                    break;
                case 6:
                    showPerformanceReport();
                    break;
                case 7:
                    if (currentUser.isAdmin()) {
                        showBulkOperations();
                    }
                    break;
                case 8:
                    if (currentUser.isAdmin()) {
                        showEmployeeAnalytics();
                    }
                    break;
                case 0:
                    return;
                default:
                    UIUtils.showError("Invalid choice. Please try again.");
                    UIUtils.waitForEnter(scanner);
                }
            }



    private void viewAllEmployees() {
        UIUtils.printMenuHeader("ALL EMPLOYEES");

         String filter = "";
        if (!currentUser.isAdmin()) {
            filter = currentUser.getBranchId();
        }

        String request = "GET_ALL_EMPLOYEES|" + filter;
        String response = client.sendRequest(request);

        if (response != null && response.startsWith("EMPLOYEES_DATA")) {
            displayEmployeesData(response);
        } else {
            UIUtils.showError("Failed to retrieve employee data");
        }

    UIUtils.waitForEnter(scanner);
    }

private void displayEmployeesData(String response) {
    try {
        // Expected format: EMPLOYEES_DATA|empNum:fullName:id:role:branch:phone:status|...
        String[] parts = response.split("\\|");
        if (parts.length > 1) {
            List<String[]> rows = new ArrayList<>();

            for (int i = 1; i < parts.length; i++) {
                String[] empData = parts[i].split(":");
                if (empData.length >= 7) {
                    String status = empData[6].equals("true") ? "Active" : "Inactive";
                    rows.add(new String[]{
                            empData[0], // Employee Number
                            empData[1], // Full Name
                            empData[2], // ID
                            empData[3], // Role
                            empData[4], // Branch
                            empData[5], // Phone
                            status      // Status
                    });
                }
            }

            String[] headers = {"Emp. #", "Full Name", "ID", "Role", "Branch", "Phone", "Status"};
            UIUtils.printTable(headers, rows);
            UIUtils.showInfo("Total employees: " + rows.size());
        } else {
            UIUtils.printLine("No employees found");
        }
    } catch (Exception e) {
        UIUtils.showError("Error displaying employees: " + e.getMessage());
    }
}

private void searchEmployee() {
    UIUtils.printMenuHeader("SEARCH EMPLOYEE");

    String searchTerm = UIUtils.getStringInput(scanner, "Enter employee name, ID, or number: ");

    String request = "SEARCH_EMPLOYEE|" + searchTerm;
    String response = client.sendRequest(request);

    if (response != null && response.startsWith("EMPLOYEE_FOUND")) {
        displayEmployeeDetails(response);
    } else {
        UIUtils.showError("Employee not found or connection error");
    }

    UIUtils.waitForEnter(scanner);
}

private void displayEmployeeDetails(String response) {
    try {
        // Expected format: EMPLOYEE_FOUND|empNum:fullName:id:phone:accountNum:branch:role:status:hireDate
        String[] parts = response.split("\\|");
        if (parts.length > 1) {
            String[] empData = parts[1].split(":");
            if (empData.length >= 8) {
                UIUtils.printLine("Employee Number: " + empData[0]);
                UIUtils.printLine("Full Name: " + empData[1]);
                UIUtils.printLine("ID: " + empData[2]);
                UIUtils.printLine("Phone: " + empData[3]);
                UIUtils.printLine("Account Number: " + empData[4]);
                UIUtils.printLine("Branch: " + empData[5]);
                UIUtils.printLine("Role: " + empData[6]);
                UIUtils.printLine("Status: " + (empData[7].equals("true") ? "Active" : "Inactive"));

                if (empData.length > 8) {
                    UIUtils.printLine("Hire Date: " + empData[8]);
                }
            }
        }
    } catch (Exception e) {
        UIUtils.showError("Error displaying employee details: " + e.getMessage());
    }
}

private void addNewEmployee() {
    UIUtils.printMenuHeader("ADD NEW EMPLOYEE");

    String fullName = UIUtils.getStringInput(scanner, "Full Name: ");
    String id = UIUtils.getStringInput(scanner, "Employee ID: ");
    String phoneNumber = UIUtils.getStringInput(scanner, "Phone Number: ");
    String accountNumber = UIUtils.getStringInput(scanner, "Account Number: ");
    String branchId = UIUtils.getStringInput(scanner, "Branch ID: ");
    String employeeNumber = UIUtils.getStringInput(scanner, "Employee Number: ");

    UIUtils.printLine("Employee Role:");
    UIUtils.printLine("1. Cashier");
    UIUtils.printLine("2. Salesperson");
    UIUtils.printLine("3. Shift Manager");
    if (currentUser.isAdmin()) {
        UIUtils.printLine("4. Administrator");
    }
    System.out.print("Select role (1-" + (currentUser.isAdmin() ? "4" : "3") + "): ");

    int roleChoice = UIUtils.getIntInput(scanner);
    String role;

    switch (roleChoice) {
        case 1:
            role = "CASHIER";
            break;
        case 2:
            role = "SALESPERSON";
            break;
        case 3:
            role = "SHIFT_MANAGER";
            break;
        case 4:
            if (currentUser.isAdmin()) {
                role = "ADMIN";
            } else {
                UIUtils.showError("Invalid role selection");
                return;
            }
            break;
        default:
            UIUtils.showError("Invalid role selection");
            return;
    }

    String password = UIUtils.getStringInput(scanner, "Initial Password: ");

    String request = String.format("ADD_EMPLOYEE|%s|%s|%s|%s|%s|%s|%s|%s|%s",
            fullName, id, phoneNumber, accountNumber, branchId, employeeNumber,
            role, password, currentUser.getEmployeeNumber());
    String response = client.sendRequest(request);

    if (response != null && response.equals("EMPLOYEE_ADDED")) {
        UIUtils.showSuccess("Employee added successfully!");
        UIUtils.showInfo("Username created: " + employeeNumber);
    } else {
        String error = response != null ? response.replace("EMPLOYEE_ADD_FAILED|", "") : "Connection error";
        UIUtils.showError(error);
    }

    UIUtils.waitForEnter(scanner);
}

private void updateEmployee() {
    UIUtils.printMenuHeader("UPDATE EMPLOYEE");

    String employeeNumber = UIUtils.getStringInput(scanner, "Employee Number to update: ");

    String searchRequest = "SEARCH_EMPLOYEE|" + employeeNumber;
    String searchResponse = client.sendRequest(searchRequest);

    if (searchResponse == null || !searchResponse.startsWith("EMPLOYEE_FOUND")) {
        UIUtils.showError("Employee not found");
        UIUtils.waitForEnter(scanner);
        return;
    }

    // Display current data and get updates
    UIUtils.printLine("Leave field empty to keep current value:");
    String newFullName = UIUtils.getStringInput(scanner, "New Full Name: ");
    String newPhone = UIUtils.getStringInput(scanner, "New Phone Number: ");
    String newBranch = UIUtils.getStringInput(scanner, "New Branch ID: ");

    UIUtils.printLine("Update Role:");
    UIUtils.printLine("1. Keep current");
    UIUtils.printLine("2. Cashier");
    UIUtils.printLine("3. Salesperson");
    UIUtils.printLine("4. Shift Manager");
    if (currentUser.isAdmin()) {
        UIUtils.printLine("5. Administrator");
    }
    System.out.print("Select option (1-" + (currentUser.isAdmin() ? "5" : "4") + "): ");

    int roleChoice = UIUtils.getIntInput(scanner);
    String newRole = "NO_CHANGE";

    switch (roleChoice) {
        case 2:
            newRole = "CASHIER";
            break;
        case 3:
            newRole = "SALESPERSON";
            break;
        case 4:
            newRole = "SHIFT_MANAGER";
            break;
        case 5:
            if (currentUser.isAdmin()) {
                newRole = "ADMIN";
            }
            break;
    }

    String request = String.format("UPDATE_EMPLOYEE|%s|%s|%s|%s|%s|%s",
            employeeNumber, newFullName, newPhone, newBranch, newRole, currentUser.getEmployeeNumber());
    String response = client.sendRequest(request);

    if (response != null && response.equals("EMPLOYEE_UPDATED")) {
        UIUtils.showSuccess("Employee updated successfully!");
    } else {
        String error = response != null ? response.replace("EMPLOYEE_UPDATE_FAILED|", "") : "Connection error";
        UIUtils.showError(error);
    }

    UIUtils.waitForEnter(scanner);
}

private void deactivateEmployee() {
    UIUtils.printMenuHeader("DEACTIVATE EMPLOYEE");

    String employeeNumber = UIUtils.getStringInput(scanner, "Employee Number to deactivate: ");
    String reason = UIUtils.getStringInput(scanner, "Reason for deactivation: ");

    UIUtils.printLine("Are you sure you want to deactivate this employee? (y/N)");
    System.out.print("Confirm: ");
    String confirm = scanner.nextLine().trim().toLowerCase();

    if (!confirm.equals("y") && !confirm.equals("yes")) {
        UIUtils.showInfo("Operation cancelled");
        UIUtils.waitForEnter(scanner);
        return;
    }

    String request = String.format("DEACTIVATE_EMPLOYEE|%s|%s|%s",
            employeeNumber, reason, currentUser.getEmployeeNumber());
    String response = client.sendRequest(request);

    if (response != null && response.equals("EMPLOYEE_DEACTIVATED")) {
        UIUtils.showSuccess("Employee deactivated successfully!");
    } else {
        String error = response != null ? response.replace("DEACTIVATE_FAILED|", "") : "Connection error";
        UIUtils.showError(error);
    }

    UIUtils.waitForEnter(scanner);
}

private void showPerformanceReport() {
    UIUtils.printMenuHeader("EMPLOYEE PERFORMANCE REPORT");

    String employeeNumber = UIUtils.getStringInput(scanner, "Employee Number (leave empty for all): ");
    String dateFrom = UIUtils.getStringInput(scanner, "From Date (YYYY-MM-DD, optional): ");
    String dateTo = UIUtils.getStringInput(scanner, "To Date (YYYY-MM-DD, optional): ");

    String request = String.format("GET_PERFORMANCE_REPORT|%s|%s|%s|%s",
            employeeNumber, dateFrom, dateTo, currentUser.getBranchId());
    String response = client.sendRequest(request);

    if (response != null && response.startsWith("PERFORMANCE_DATA")) {
        displayPerformanceData(response);
    } else {
        UIUtils.showError("Failed to retrieve performance data");
    }

    UIUtils.waitForEnter(scanner);
}

private void displayPerformanceData(String response) {
    try {
        // Expected format: PERFORMANCE_DATA|empNum:name:salesCount:totalSales:avgSale:customerRating|...
        String[] parts = response.split("\\|");
        if (parts.length > 1) {
            List<String[]> rows = new ArrayList<>();

            for (int i = 1; i < parts.length; i++) {
                String[] perfData = parts[i].split(":");
                if (perfData.length >= 6) {
                    rows.add(new String[]{
                            perfData[0], // Employee Number
                            perfData[1], // Name
                            perfData[2], // Sales Count
                            "$" + perfData[3], // Total Sales
                            "$" + perfData[4], // Average Sale
                            perfData[5] + "★" // Customer Rating
                    });
                }
            }

            String[] headers = {"Emp. #", "Name", "Sales", "Total", "Avg Sale", "Rating"};
            UIUtils.printTable(headers, rows);
        } else {
            UIUtils.printLine("No performance data found");
        }
    } catch (Exception e) {
        UIUtils.showError("Error displaying performance data: " + e.getMessage());
    }
}