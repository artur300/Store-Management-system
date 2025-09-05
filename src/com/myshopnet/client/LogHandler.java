package com.myshopnet.client;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.myshopnet.client.Client;
import com.myshopnet.client.utils.UIUtils;

import java.util.*;

public class LogHandler {
    private final Gson gson = new Gson();

    private Client client;
    private JsonObject currentUser;
    private Scanner scanner;

    public LogHandler(Client client, JsonObject currentUser) {
        this.client = client;
        this.currentUser = currentUser;
        this.scanner = client.getScanner();
    }

    public void showLogMenu() {
        while (true) {
            UIUtils.printMenuHeader("SYSTEM LOGS");
            UIUtils.printLine("View and manage system activity logs");
            UIUtils.printEmptyLine();

            UIUtils.printMenuOption(1, "Employee Registration Logs");
            UIUtils.printMenuOption(2, "Customer Registration Logs");
            UIUtils.printMenuOption(3, "Transaction Logs");
            UIUtils.printMenuOption(4, "Chat Activity Logs");
            UIUtils.printMenuOption(5, "System Activity Logs");
            UIUtils.printMenuOption(6, "Error Logs");
            if (currentUser.get("employeeType").getAsString().equals("ADMIN")) {
                UIUtils.printMenuOption(7, "Export Logs");
                UIUtils.printMenuOption(8, "Log Analytics");
            }
            UIUtils.printMenuOption(0, "Back to Main Menu");

            UIUtils.printMenuFooter();

            int choice = UIUtils.getIntInput(scanner);

            switch (choice) {
                case 1:
                    viewEmployeeRegistrationLogs();
                    break;
                case 2:
                    viewCustomerRegistrationLogs();
                    break;
                case 3:
                    viewTransactionLogs();
                    break;
                case 4:
                    viewChatActivityLogs();
                    break;
                case 5:
                    viewSystemActivityLogs();
                    break;
                case 6:
                    viewErrorLogs();
                    break;
                case 7:
                    if (currentUser.get("employeeType").getAsString().equals("ADMIN")) {
                        exportLogs();
                    }
                    break;
                case 8:
                    if (currentUser.get("employeeType").getAsString().equals("ADMIN")) {
                        showLogAnalytics();
                    }
                    break;
                case 0:
                    return;
                default:
                    UIUtils.showError("Invalid choice. Please try again.");
                    UIUtils.waitForEnter(scanner);
            }
        }
    }

    private void viewEmployeeRegistrationLogs() {
        UIUtils.printMenuHeader("EMPLOYEE REGISTRATION LOGS");

        String dateFilter = UIUtils.getStringInput(scanner, "Date filter (YYYY-MM-DD) or leave empty: ");
        String branchFilter = currentUser.get("employeeType").getAsString().equals("ADMIN") ?
                UIUtils.getStringInput(scanner, "Branch filter or leave empty: ") :
                currentUser.get("branchId").getAsString();

        String request = String.format("GET_EMPLOYEE_REG_LOGS|%s|%s|%s",
                dateFilter, branchFilter, currentUser.get("employeeNumber").getAsString());
        JsonObject response = client.sendRequest(request);

        if (response != null && response.startsWith("EMPLOYEE_REG_LOGS")) {
            displayEmployeeRegLogs(response);
        } else {
            UIUtils.showError("Failed to retrieve employee registration logs");
        }

        UIUtils.waitForEnter(scanner);
    }

    private void displayEmployeeRegLogs(String response) {
        try {
            // Expected format: EMPLOYEE_REG_LOGS|timestamp:employeeId:employeeName:registeredBy:action:branch|...
            String[] parts = response.split("\\|");
            if (parts.length > 1) {
                List<String[]> rows = new ArrayList<>();

                for (int i = 1; i < parts.length; i++) {
                    String[] logData = parts[i].split(":");
                    if (logData.length >= 6) {
                        rows.add(new String[]{
                                logData[0], // Timestamp
                                logData[1], // Employee ID
                                logData[2], // Employee Name
                                logData[3], // Registered By
                                logData[4], // Action
                                logData[5]  // Branch
                        });
                    }
                }

                String[] headers = {"Timestamp", "Emp ID", "Name", "Registered By", "Action", "Branch"};
                UIUtils.printTable(headers, rows);
                UIUtils.showInfo("Total employee registration entries: " + rows.size());
            } else {
                UIUtils.printLine("No employee registration logs found");
            }
        } catch (Exception e) {
            UIUtils.showError("Error displaying logs: " + e.getMessage());
        }
    }

    private void viewCustomerRegistrationLogs() {
        UIUtils.printMenuHeader("CUSTOMER REGISTRATION LOGS");

        String dateFilter = UIUtils.getStringInput(scanner, "Date filter (YYYY-MM-DD) or leave empty: ");
        String customerType = UIUtils.getStringInput(scanner, "Customer type filter (NEW/RETURNING/VIP) or leave empty: ");

        String request = String.format("GET_CUSTOMER_REG_LOGS|%s|%s|%s",
                dateFilter, customerType, currentUser.get("employeeNumber").getAsString());
        JsonObject response = client.sendRequest(request);

        if (response != null && response.startsWith("CUSTOMER_REG_LOGS")) {
            displayCustomerRegLogs(response);
        } else {
            UIUtils.showError("Failed to retrieve customer registration logs");
        }

        UIUtils.waitForEnter(scanner);
    }

    private void displayCustomerRegLogs(String response) {
        try {
            // Expected format: CUSTOMER_REG_LOGS|timestamp:customerId:customerName:customerType:registeredBy:branch|...
            String[] parts = response.split("\\|");
            if (parts.length > 1) {
                List<String[]> rows = new ArrayList<>();

                for (int i = 1; i < parts.length; i++) {
                    String[] logData = parts[i].split(":");
                    if (logData.length >= 6) {
                        rows.add(new String[]{
                                logData[0], // Timestamp
                                logData[1], // Customer ID
                                logData[2], // Customer Name
                                logData[3], // Customer Type
                                logData[4], // Registered By
                                logData[5]  // Branch
                        });
                    }
                }

                String[] headers = {"Timestamp", "Customer ID", "Name", "Type", "Registered By", "Branch"};
                UIUtils.printTable(headers, rows);
                UIUtils.showInfo("Total customer registration entries: " + rows.size());
            } else {
                UIUtils.printLine("No customer registration logs found");
            }
        } catch (Exception e) {
            UIUtils.showError("Error displaying logs: " + e.getMessage());
        }
    }

    private void viewTransactionLogs() {
        UIUtils.printMenuHeader("TRANSACTION LOGS");

        String dateFilter = UIUtils.getStringInput(scanner, "Date filter (YYYY-MM-DD) or leave empty: ");
        String transactionType = UIUtils.getStringInput(scanner, "Transaction type (SALE/PURCHASE) or leave empty: ");
        String branchFilter = currentUser.get("employeeType").getAsString().equals("ADMIN") ?
                UIUtils.getStringInput(scanner, "Branch filter or leave empty: ") :
                currentUser.get("branchId").getAsString();

        String request = String.format("GET_TRANSACTION_LOGS|%s|%s|%s|%s",
                dateFilter, transactionType, branchFilter, currentUser.get("employeeNumber").getAsString());
        JsonObject response = client.sendRequest(request);

        if (response != null && response.startsWith("TRANSACTION_LOGS")) {
            displayTransactionLogs(response);
        } else {
            UIUtils.showError("Failed to retrieve transaction logs");
        }

        UIUtils.waitForEnter(scanner);
    }

    private void displayTransactionLogs(String response) {
        try {
            // Expected format: TRANSACTION_LOGS|timestamp:transactionId:type:productId:quantity:amount:employeeId:customerId:branch|...
            String[] parts = response.split("\\|");
            if (parts.length > 1) {
                List<String[]> rows = new ArrayList<>();
                double totalAmount = 0;

                for (int i = 1; i < parts.length; i++) {
                    String[] logData = parts[i].split(":");
                    if (logData.length >= 9) {
                        rows.add(new String[]{
                                logData[0], // Timestamp
                                logData[1], // Transaction ID
                                logData[2], // Type
                                logData[3], // Product ID
                                logData[4], // Quantity
                                "$" + logData[5], // Amount
                                logData[6], // Employee ID
                                logData[7], // Customer ID
                                logData[8]  // Branch
                        });
                        totalAmount += Double.parseDouble(logData[5]);
                    }
                }

                String[] headers = {"Time", "Trans ID", "Type", "Product", "Qty", "Amount", "Employee", "Customer", "Branch"};
                UIUtils.printTable(headers, rows);
                UIUtils.showInfo("Total transactions: " + rows.size() + ", Total amount: $" + String.format("%.2f", totalAmount));
            } else {
                UIUtils.printLine("No transaction logs found");
            }
        } catch (Exception e) {
            UIUtils.showError("Error displaying logs: " + e.getMessage());
        }
    }

    private void viewChatActivityLogs() {
        UIUtils.printMenuHeader("CHAT ACTIVITY LOGS");

        String dateFilter = UIUtils.getStringInput(scanner, "Date filter (YYYY-MM-DD) or leave empty: ");
        String branchFilter = currentUser.get("employeeType").getAsString().equals("ADMIN") ?
                UIUtils.getStringInput(scanner, "Branch filter or leave empty: ") :
                currentUser.get("branchId").getAsString();

        UIUtils.printLine("Include chat content? (y/N): ");
        String includeContent = scanner.nextLine().trim().toLowerCase();
        boolean showContent = includeContent.equals("y") || includeContent.equals("yes");

        String request = String.format("GET_CHAT_ACTIVITY_LOGS|%s|%s|%s|%s",
                dateFilter, branchFilter, showContent, currentUser.get("employeeNumber").getAsString());
        JsonObject response = client.sendRequest(request);

        if (response != null && response.startsWith("CHAT_ACTIVITY_LOGS")) {
            displayChatActivityLogs(response, showContent);
        } else {
            UIUtils.showError("Failed to retrieve chat activity logs");
        }

        UIUtils.waitForEnter(scanner);
    }

    private void displayChatActivityLogs(JsonObject  response, boolean showContent) {
        try {
            // Expected format: CHAT_ACTIVITY_LOGS|timestamp:chatId:participants:duration:messageCount:branches:content|...
            String[] parts = response.split("\\|");
            if (parts.length > 1) {
                List<String[]> rows = new ArrayList<>();

                for (int i = 1; i < parts.length; i++) {
                    String[] logData = parts[i].split(":", 7);
                    if (logData.length >= 6) {
                        if (showContent && logData.length > 6) {
                            rows.add(new String[]{
                                    logData[0], // Timestamp
                                    logData[1], // Chat ID
                                    logData[2], // Participants
                                    logData[3] + " min", // Duration
                                    logData[4], // Message Count
                                    logData[5], // Branches
                                    logData[6].substring(0, Math.min(50, logData[6].length())) + "..." // Content preview
                            });
                        } else {
                            rows.add(new String[]{
                                    logData[0], // Timestamp
                                    logData[1], // Chat ID
                                    logData[2], // Participants
                                    logData[3] + " min", // Duration
                                    logData[4], // Message Count
                                    logData[5]  // Branches
                            });
                        }
                    }
                }

                String[] headers = showContent ?
                        new String[]{"Time", "Chat ID", "Participants", "Duration", "Messages", "Branches", "Content"} :
                        new String[]{"Time", "Chat ID", "Participants", "Duration", "Messages", "Branches"};
                UIUtils.printTable(headers, rows);
                UIUtils.showInfo("Total chat sessions: " + rows.size());
            } else {
                UIUtils.printLine("No chat activity logs found");
            }
        } catch (Exception e) {
            UIUtils.showError("Error displaying logs: " + e.getMessage());
        }
    }

    private void viewSystemActivityLogs() {
        UIUtils.printMenuHeader("SYSTEM ACTIVITY LOGS");

        String dateFilter = UIUtils.getStringInput(scanner, "Date filter (YYYY-MM-DD) or leave empty: ");
        String activityType = UIUtils.getStringInput(scanner, "Activity type (LOGIN/LOGOUT/ACCESS/CONFIG) or leave empty: ");
        String severityLevel = UIUtils.getStringInput(scanner, "Severity (INFO/WARN/ERROR) or leave empty: ");

        String request = String.format("GET_SYSTEM_ACTIVITY_LOGS|%s|%s|%s|%s",
                dateFilter, activityType, severityLevel, currentUser.get("employeeNumber").getAsString());
        JsonObject response = client.sendRequest(request);

        if (response != null && response.startsWith("SYSTEM_ACTIVITY_LOGS")) {
            displaySystemActivityLogs(response);
        } else {
            UIUtils.showError("Failed to retrieve system activity logs");
        }

        UIUtils.waitForEnter(scanner);
    }

    private void displaySystemActivityLogs(String response) {
        try {
            // Expected format: SYSTEM_ACTIVITY_LOGS|timestamp:userId:activityType:description:ipAddress:severity:module|...
            String[] parts = response.split("\\|");
            if (parts.length > 1) {
                List<String[]> rows = new ArrayList<>();

                for (int i = 1; i < parts.length; i++) {
                    String[] logData = parts[i].split(":", 7);
                    if (logData.length >= 7) {
                        String severityIcon = getSeverityIcon(logData[5]);
                        rows.add(new String[]{
                                logData[0], // Timestamp
                                logData[1], // User ID
                                logData[2], // Activity Type
                                logData[3], // Description
                                logData[4], // IP Address
                                severityIcon + logData[5], // Severity
                                logData[6]  // Module
                        });
                    }
                }

                String[] headers = {"Timestamp", "User", "Type", "Description", "IP", "Severity", "Module"};
                UIUtils.printTable(headers, rows);
                UIUtils.showInfo("Total system activity entries: " + rows.size());
            } else {
                UIUtils.printLine("No system activity logs found");
            }
        } catch (Exception e) {
            UIUtils.showError("Error displaying logs: " + e.getMessage());
        }
    }

    private void viewErrorLogs() {
        UIUtils.printMenuHeader("ERROR LOGS");

        String dateFilter = UIUtils.getStringInput(scanner, "Date filter (YYYY-MM-DD) or leave empty: ");
        String errorLevel = UIUtils.getStringInput(scanner, "Error level (CRITICAL/HIGH/MEDIUM/LOW) or leave empty: ");

        String request = String.format("GET_ERROR_LOGS|%s|%s|%s",
                dateFilter, errorLevel, currentUser.get("employeeNumber").getAsString());
        JsonObject response = client.sendRequest(request);

        if (response != null && response.startsWith("ERROR_LOGS")) {
            displayErrorLogs(response);
        } else {
            UIUtils.showError("Failed to retrieve error logs");
        }

        UIUtils.waitForEnter(scanner);
    }

    private void displayErrorLogs(String response) {
        try {
            // Expected format: ERROR_LOGS|timestamp:errorCode:errorMessage:userId:module:stackTrace:severity|...
            String[] parts = response.split("\\|");
            if (parts.length > 1) {
                List<String[]> rows = new ArrayList<>();

                for (int i = 1; i < parts.length; i++) {
                    String[] logData = parts[i].split(":", 7);
                    if (logData.length >= 7) {
                        String severityIcon = getSeverityIcon(logData[6]);
                        rows.add(new String[]{
                                logData[0], // Timestamp
                                logData[1], // Error Code
                                logData[2].substring(0, Math.min(40, logData[2].length())), // Error Message (truncated)
                                logData[3], // User ID
                                logData[4], // Module
                                severityIcon + logData[6] // Severity
                        });
                    }
                }

                String[] headers = {"Timestamp", "Code", "Message", "User", "Module", "Severity"};
                UIUtils.printTable(headers, rows);
                UIUtils.showInfo("Total error entries: " + rows.size());

                if (!rows.isEmpty()) {
                    String errorCode = UIUtils.getStringInput(scanner, "Enter error code for details (or press Enter to skip): ");
                    if (!errorCode.trim().isEmpty()) {
                        viewErrorDetails(errorCode);
                    }
                }
            } else {
                UIUtils.printLine("No error logs found");
            }
        } catch (Exception e) {
            UIUtils.showError("Error displaying logs: " + e.getMessage());
        }
    }

    private void viewErrorDetails(String errorCode) {
        UIUtils.printMenuHeader("ERROR DETAILS - " + errorCode);

        String request = "GET_ERROR_DETAILS|" + errorCode + "|" + currentUser.get("employeeNumber").getAsString();
        JsonObject response = client.sendRequest(request);

        if (response != null && response.startsWith("ERROR_DETAILS")) {
            try {
                // Expected format: ERROR_DETAILS|timestamp:errorCode:fullMessage:userId:module:stackTrace:severity:resolution
                String[] parts = response.split("\\|");
                if (parts.length > 1) {
                    String[] errorData = parts[1].split(":", 8);
                    if (errorData.length >= 8) {
                        UIUtils.printLine("Timestamp: " + errorData[0]);
                        UIUtils.printLine("Error Code: " + errorData[1]);
                        UIUtils.printLine("Full Message: " + errorData[2]);
                        UIUtils.printLine("User ID: " + errorData[3]);
                        UIUtils.printLine("Module: " + errorData[4]);
                        UIUtils.printLine("Severity: " + errorData[6]);
                        UIUtils.printLine("Resolution: " + errorData[7]);
                        UIUtils.printEmptyLine();
                        UIUtils.printLine("Stack Trace:");
                        UIUtils.printLine(errorData[5].replace("\\n", "\n"));
                    }
                }
            } catch (Exception e) {
                UIUtils.showError("Error displaying error details: " + e.getMessage());
            }
        } else {
            UIUtils.showError("Failed to retrieve error details");
        }

        UIUtils.waitForEnter(scanner);
    }

    private void exportLogs() {
        UIUtils.printMenuHeader("EXPORT LOGS");
        UIUtils.printEmptyLine();

        UIUtils.printMenuOption(1, "Export All Logs");
        UIUtils.printMenuOption(2, "Export Specific Log Type");
        UIUtils.printMenuOption(3, "Export by Date Range");
        UIUtils.printMenuOption(4, "Export Error Logs Only");
        UIUtils.printMenuOption(0, "Back");

        UIUtils.printMenuFooter();

        int choice = UIUtils.getIntInput(scanner);

        switch (choice) {
            case 1:
                exportAllLogs();
                break;
            case 2:
                exportSpecificLogType();
                break;
            case 3:
                exportByDateRange();
                break;
            case 4:
                exportErrorLogsOnly();
                break;
            case 0:
                return;
            default:
                UIUtils.showError("Invalid choice. Please try again.");
                UIUtils.waitForEnter(scanner);
        }
    }

    private void exportAllLogs() {
        UIUtils.printMenuHeader("EXPORT ALL LOGS");

        String format = UIUtils.getStringInput(scanner, "Export format (CSV/JSON/XML): ");
        String fileName = UIUtils.getStringInput(scanner, "File name (optional): ");

        String request = String.format("EXPORT_ALL_LOGS|%s|%s|%s",
                format.toUpperCase(), fileName, currentUser.get("employeeNumber").getAsString());
        JsonObject response = client.sendRequest(request);

        if (response != null && response.startsWith("EXPORT_SUCCESS")) {
            String[] parts = response.split("\\|");
            UIUtils.showSuccess("Logs exported successfully!");
            if (parts.length > 1) {
                UIUtils.showInfo("File: " + parts[1]);
            }
            if (parts.length > 2) {
                UIUtils.showInfo("Records: " + parts[2]);
            }
        } else {
            String error = response != null ? response.replace("EXPORT_FAILED|", "") : "Connection error";
            UIUtils.showError(error);
        }

        UIUtils.waitForEnter(scanner);
    }

    private void exportSpecificLogType() {
        UIUtils.printMenuHeader("EXPORT SPECIFIC LOG TYPE");

        UIUtils.printLine("Select log type:");
        UIUtils.printLine("1. Employee Registration");
        UIUtils.printLine("2. Customer Registration");
        UIUtils.printLine("3. Transactions");
        UIUtils.printLine("4. Chat Activity");
        UIUtils.printLine("5. System Activity");
        UIUtils.printLine("6. Error Logs");
        System.out.print("Select type (1-6): ");

        int typeChoice = UIUtils.getIntInput(scanner);
        String logType = "";

        switch (typeChoice) {
            case 1:
                logType = "EMPLOYEE_REG";
                break;
            case 2:
                logType = "CUSTOMER_REG";
                break;
            case 3:
                logType = "TRANSACTIONS";
                break;
            case 4:
                logType = "CHAT_ACTIVITY";
                break;
            case 5:
                logType = "SYSTEM_ACTIVITY";
                break;
            case 6:
                logType = "ERROR_LOGS";
                break;
            default:
                UIUtils.showError("Invalid selection");
                return;
        }

        String format = UIUtils.getStringInput(scanner, "Export format (CSV/JSON/XML): ");
        String fileName = UIUtils.getStringInput(scanner, "File name (optional): ");

        String request = String.format("EXPORT_SPECIFIC_LOGS|%s|%s|%s|%s",
                logType, format.toUpperCase(), fileName, currentUser.get("employeeNumber").getAsString());
        JsonObject response = client.sendRequest(request);

        if (response != null && response.startsWith("EXPORT_SUCCESS")) {
            String[] parts = response.split("\\|");
            UIUtils.showSuccess("Logs exported successfully!");
            if (parts.length > 1) {
                UIUtils.showInfo("File: " + parts[1]);
            }
            if (parts.length > 2) {
                UIUtils.showInfo("Records: " + parts[2]);
            }
        } else {
            String error = response != null ? response.replace("EXPORT_FAILED|", "") : "Connection error";
            UIUtils.showError(error);
        }

        UIUtils.waitForEnter(scanner);
    }

    private void exportByDateRange() {
        UIUtils.printMenuHeader("EXPORT BY DATE RANGE");

        String fromDate = UIUtils.getStringInput(scanner, "From date (YYYY-MM-DD): ");
        String toDate = UIUtils.getStringInput(scanner, "To date (YYYY-MM-DD): ");
        String format = UIUtils.getStringInput(scanner, "Export format (CSV/JSON/XML): ");
        String fileName = UIUtils.getStringInput(scanner, "File name (optional): ");

        String request = String.format("EXPORT_LOGS_BY_DATE|%s|%s|%s|%s|%s",
                fromDate, toDate, format.toUpperCase(), fileName, currentUser.get("employeeNumber").getAsString());
        JsonObject response = client.sendRequest(request);

        if (response != null && response.startsWith("EXPORT_SUCCESS")) {
            String[] parts = response.split("\\|");
            UIUtils.showSuccess("Logs exported successfully!");
            if (parts.length > 1) {
                UIUtils.showInfo("File: " + parts[1]);
            }
            if (parts.length > 2) {
                UIUtils.showInfo("Records: " + parts[2]);
            }
        } else {
            String error = response != null ? response.replace("EXPORT_FAILED|", "") : "Connection error";
            UIUtils.showError(error);
        }

        UIUtils.waitForEnter(scanner);
    }

    private void exportErrorLogsOnly() {
        UIUtils.printMenuHeader("EXPORT ERROR LOGS");

        String severity = UIUtils.getStringInput(scanner, "Minimum severity (CRITICAL/HIGH/MEDIUM/LOW) or leave empty: ");
        String format = UIUtils.getStringInput(scanner, "Export format (CSV/JSON/XML): ");
        String fileName = UIUtils.getStringInput(scanner, "File name (optional): ");

        String request = String.format("EXPORT_ERROR_LOGS|%s|%s|%s|%s",
                severity, format.toUpperCase(), fileName, currentUser.get("employeeNumber").getAsString());
        JsonObject response = client.sendRequest(request);

        if (response != null && response.startsWith("EXPORT_SUCCESS")) {
            String[] parts = response.split("\\|");
            UIUtils.showSuccess("Error logs exported successfully!");
            if (parts.length > 1) {
                UIUtils.showInfo("File: " + parts[1]);
            }
            if (parts.length > 2) {
                UIUtils.showInfo("Records: " + parts[2]);
            }
        } else {
            String error = response != null ? response.replace("EXPORT_FAILED|", "") : "Connection error";
            UIUtils.showError(error);
        }

        UIUtils.waitForEnter(scanner);
    }

    private void showLogAnalytics() {
        UIUtils.printMenuHeader("LOG ANALYTICS");
        UIUtils.printEmptyLine();

        UIUtils.printMenuOption(1, "Activity Trends");
        UIUtils.printMenuOption(2, "Error Analysis");
        UIUtils.printMenuOption(3, "User Activity Report");
        UIUtils.printMenuOption(4, "Performance Metrics");
        UIUtils.printMenuOption(5, "Security Analysis");
        UIUtils.printMenuOption(0, "Back");

        UIUtils.printMenuFooter();

        int choice = UIUtils.getIntInput(scanner);

        switch (choice) {
            case 1:
                showActivityTrends();
                break;
            case 2:
                showErrorAnalysis();
                break;
            case 3:
                showUserActivityReport();
                break;
            case 4:
                showPerformanceMetrics();
                break;
            case 5:
                showSecurityAnalysis();
                break;
            case 0:
                return;
            default:
                UIUtils.showError("Invalid choice. Please try again.");
                UIUtils.waitForEnter(scanner);
        }
    }

    private void showActivityTrends() {
        UIUtils.printMenuHeader("ACTIVITY TRENDS");

        String period = UIUtils.getStringInput(scanner, "Period (7/30/90 days, default 30): ");
        if (period.trim().isEmpty()) {
            period = "30";
        }

        String request = String.format("GET_ACTIVITY_TRENDS|%s|%s", period, currentUser.get("employeeNumber").getAsString());
        JsonObject response = client.sendRequest(request);

        if (response != null && response.startsWith("ACTIVITY_TRENDS")) {
            displayActivityTrends(response);
        } else {
            UIUtils.showError("Failed to retrieve activity trends");
        }

        UIUtils.waitForEnter(scanner);
    }

    private void displayActivityTrends(String response) {
        try {
            // Expected format: ACTIVITY_TRENDS|totalLogins:totalTransactions:totalErrors:peakHour:activeUsers|...
            String[] parts = response.split("\\|");
            if (parts.length > 1) {
                String[] trends = parts[1].split(":");
                if (trends.length >= 5) {
                    UIUtils.printLine("📊 ACTIVITY SUMMARY");
                    UIUtils.printEmptyLine();
                    UIUtils.printLine("Total Logins: " + trends[0]);
                    UIUtils.printLine("Total Transactions: " + trends[1]);
                    UIUtils.printLine("Total Errors: " + trends[2]);
                    UIUtils.printLine("Peak Activity Hour: " + trends[3] + ":00");
                    UIUtils.printLine("Active Users: " + trends[4]);

                    if (parts.length > 2) {
                        UIUtils.printEmptyLine();
                        UIUtils.printLine("📈 DAILY BREAKDOWN");
                        for (int i = 2; i < parts.length; i++) {
                            String[] dailyData = parts[i].split(":");
                            if (dailyData.length >= 2) {
                                UIUtils.printLine(dailyData[0] + ": " + dailyData[1] + " activities");
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            UIUtils.showError("Error displaying activity trends: " + e.getMessage());
        }
    }

    private void showErrorAnalysis() {
        UIUtils.printMenuHeader("ERROR ANALYSIS");

        String request = "GET_ERROR_ANALYSIS|" + currentUser.get("employeeNumber").getAsString();
        JsonObject response = client.sendRequest(request);

        if (response != null && response.startsWith("ERROR_ANALYSIS")) {
            displayErrorAnalysis(response);
        } else {
            UIUtils.showError("Failed to retrieve error analysis");
        }

        UIUtils.waitForEnter(scanner);
    }

    private void displayErrorAnalysis(String response) {
        try {
            // Expected format: ERROR_ANALYSIS|totalErrors:criticalErrors:mostCommonError:errorRate:resolvedPercentage|...
            String[] parts = response.split("\\|");
            if (parts.length > 1) {
                String[] analysis = parts[1].split(":", 5);
                if (analysis.length >= 5) {
                    UIUtils.printLine("🚨 ERROR SUMMARY");
                    UIUtils.printEmptyLine();
                    UIUtils.printLine("Total Errors: " + analysis[0]);
                    UIUtils.printLine("Critical Errors: " + analysis[1]);
                    UIUtils.printLine("Most Common Error: " + analysis[2]);
                    UIUtils.printLine("Error Rate: " + analysis[3] + "%");
                    UIUtils.printLine("Resolved: " + analysis[4] + "%");

                    if (parts.length > 2) {
                        UIUtils.printEmptyLine();
                        UIUtils.printLine("🔍 TOP ERROR CATEGORIES");
                        List<String[]> rows = new ArrayList<>();

                        for (int i = 2; i < parts.length; i++) {
                            String[] errorCategory = parts[i].split(":");
                            if (errorCategory.length >= 3) {
                                rows.add(new String[]{
                                        errorCategory[0], // Category
                                        errorCategory[1], // Count
                                        errorCategory[2] + "%" // Percentage
                                });
                            }
                        }

                        String[] headers = {"Category", "Count", "Percentage"};
                        UIUtils.printTable(headers, rows);
                    }
                }
            }
        } catch (Exception e) {
            UIUtils.showError("Error displaying error analysis: " + e.getMessage());
        }
    }

    // Utility methods
    private String getSeverityIcon(String severity) {
        switch (severity.toUpperCase()) {
            case "CRITICAL":
                return "🔴 ";
            case "HIGH":
            case "ERROR":
                return "🟠 ";
            case "MEDIUM":
            case "WARN":
                return "🟡 ";
            case "LOW":
            case "INFO":
                return "🟢 ";
            default:
                return "⚪ ";
        }
    }

    // Placeholder methods for remaining analytics functions
    private void showUserActivityReport() {
        UIUtils.showInfo("User activity report - Implementation pending");
        UIUtils.waitForEnter(scanner);
    }

    private void showPerformanceMetrics() {
        UIUtils.showInfo("Performance metrics analysis - Implementation pending");
        UIUtils.waitForEnter(scanner);
    }

    private void showSecurityAnalysis() {
        UIUtils.showInfo("Security analysis - Implementation pending");
        UIUtils.waitForEnter(scanner);
    }
}