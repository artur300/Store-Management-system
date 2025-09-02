// AdminHandler.java
import com.myshopnet.client.utils.UIUtils;

import java.util.*;

public class AdminHandler {
    private ClientApplication client;
    private User currentUser;
    private Scanner scanner;

    public AdminHandler(ClientApplication client, User currentUser) {
        this.client = client;
        this.currentUser = currentUser;
        this.scanner = client.getScanner();
    }

    public void showAdminMenu() {
        while (true) {
            UIUtils.printMenuHeader("ADMIN PANEL");
            UIUtils.printLine("System administration tools");
            UIUtils.printEmptyLine();

            UIUtils.printMenuOption(1, "User Account Management");
            UIUtils.printMenuOption(2, "Password Policy Settings");
            UIUtils.printMenuOption(3, "System Configuration");
            UIUtils.printMenuOption(4, "Branch Management");
            UIUtils.printMenuOption(5, "Security Settings");
            UIUtils.printMenuOption(6, "System Backup");
            UIUtils.printMenuOption(7, "System Statistics");
            UIUtils.printMenuOption(8, "Audit Trail");
            UIUtils.printMenuOption(0, "Back to Main Menu");

            UIUtils.printMenuFooter();

            int choice = UIUtils.getIntInput(scanner);

            switch (choice) {
                case 1:
                    manageUserAccounts();
                    break;
                case 2:
                    managePasswordPolicies();
                    break;
                case 3:
                    systemConfiguration();
                    break;
                case 4:
                    manageBranches();
                    break;
                case 5:
                    securitySettings();
                    break;
                case 6:
                    systemBackup();
                    break;
                case 7:
                    systemStatistics();
                    break;
                case 8:
                    auditTrail();
                    break;
                case 0:
                    return;
                default:
                    UIUtils.showError("Invalid choice. Please try again.");
                    UIUtils.waitForEnter(scanner);
            }
        }
    }

    private void manageUserAccounts() {
        UIUtils.printMenuHeader("USER ACCOUNT MANAGEMENT");
        UIUtils.printEmptyLine();

        UIUtils.printMenuOption(1, "View All User Accounts");
        UIUtils.printMenuOption(2, "Create Administrator Account");
        UIUtils.printMenuOption(3, "Reset User Password");
        UIUtils.printMenuOption(4, "Lock/Unlock Account");
        UIUtils.printMenuOption(5, "Delete Account");
        UIUtils.printMenuOption(6, "Account Activity Report");
        UIUtils.printMenuOption(0, "Back");

        UIUtils.printMenuFooter();

        int choice = UIUtils.getIntInput(scanner);

        switch (choice) {
            case 1:
                viewAllAccounts();
                break;
            case 2:
                createAdminAccount();
                break;
            case 3:
                resetUserPassword();
                break;
            case 4:
                lockUnlockAccount();
                break;
            case 5:
                deleteAccount();
                break;
            case 6:
                accountActivityReport();
                break;
            case 0:
                return;
            default:
                UIUtils.showError("Invalid choice. Please try again.");
                UIUtils.waitForEnter(scanner);
        }
    }

    private void viewAllAccounts() {
        UIUtils.printMenuHeader("ALL USER ACCOUNTS");

        String request = "GET_ALL_ACCOUNTS|" + currentUser.getEmployeeNumber();
        String response = client.sendRequest(request);

        if (response != null && response.startsWith("ACCOUNTS_DATA")) {
            displayAccountsData(response);
        } else {
            UIUtils.showError("Failed to retrieve account data");
        }

        UIUtils.waitForEnter(scanner);
    }

    private void displayAccountsData(String response) {
        try {
            // Expected format: ACCOUNTS_DATA|username:fullName:role:branch:lastLogin:status|...
            String[] parts = response.split("\\|");
            if (parts.length > 1) {
                List<String[]> rows = new ArrayList<>();

                for (int i = 1; i < parts.length; i++) {
                    String[] accountData = parts[i].split(":");
                    if (accountData.length >= 6) {
                        rows.add(new String[]{
                                accountData[0], // Username
                                accountData[1], // Full Name
                                accountData[2], // Role
                                accountData[3], // Branch
                                accountData[4], // Last Login
                                accountData[5]  // Status
                        });
                    }
                }

                String[] headers = {"Username", "Full Name", "Role", "Branch", "Last Login", "Status"};
                UIUtils.printTable(headers, rows);
                UIUtils.showInfo("Total accounts: " + rows.size());
            }
        } catch (Exception e) {
            UIUtils.showError("Error displaying accounts: " + e.getMessage());
        }
    }

    private void createAdminAccount() {
        UIUtils.printMenuHeader("CREATE ADMINISTRATOR ACCOUNT");

        String fullName = UIUtils.getStringInput(scanner, "Full Name: ");
        String username = UIUtils.getStringInput(scanner, "Username: ");
        String id = UIUtils.getStringInput(scanner, "ID: ");
        String phoneNumber = UIUtils.getStringInput(scanner, "Phone Number: ");
        String branchId = UIUtils.getStringInput(scanner, "Branch ID: ");
        String employeeNumber = UIUtils.getStringInput(scanner, "Employee Number: ");
        String password = UIUtils.getStringInput(scanner, "Password: ");

        String request = String.format("CREATE_ADMIN_ACCOUNT|%s|%s|%s|%s|%s|%s|%s|%s",
                fullName, username, id, phoneNumber, branchId, employeeNumber, password, currentUser.getEmployeeNumber());
        String response = client.sendRequest(request);

        if (response != null && response.equals("ADMIN_ACCOUNT_CREATED")) {
            UIUtils.showSuccess("Administrator account created successfully!");
        } else {
            String error = response != null ? response.replace("ADMIN_CREATE_FAILED|", "") : "Connection error";
            UIUtils.showError(error);
        }

        UIUtils.waitForEnter(scanner);
    }

    private void resetUserPassword() {
        UIUtils.printMenuHeader("RESET USER PASSWORD");

        String username = UIUtils.getStringInput(scanner, "Username: ");
        String newPassword = UIUtils.getStringInput(scanner, "New Password: ");

        UIUtils.printLine("Force password change on next login? (Y/n): ");
        String forceChange = scanner.nextLine().trim().toLowerCase();
        boolean mustChangePassword = !forceChange.equals("n") && !forceChange.equals("no");

        String request = String.format("RESET_USER_PASSWORD|%s|%s|%s|%s",
                username, newPassword, mustChangePassword, currentUser.getEmployeeNumber());
        String response = client.sendRequest(request);

        if (response != null && response.equals("PASSWORD_RESET_SUCCESS")) {
            UIUtils.showSuccess("Password reset successfully!");
            if (mustChangePassword) {
                UIUtils.showInfo("User will be required to change password on next login");
            }
        } else {
            String error = response != null ? response.replace("PASSWORD_RESET_FAILED|", "") : "Connection error";
            UIUtils.showError(error);
        }

        UIUtils.waitForEnter(scanner);
    }

    private void lockUnlockAccount() {
        UIUtils.printMenuHeader("LOCK/UNLOCK ACCOUNT");

        String username = UIUtils.getStringInput(scanner, "Username: ");

        UIUtils.printLine("1. Lock Account");
        UIUtils.printLine("2. Unlock Account");
        System.out.print("Select action (1-2): ");

        int action = UIUtils.getIntInput(scanner);
        String operation = (action == 1) ? "LOCK" : "UNLOCK";
        String reason = "";

        if (action == 1) {
            reason = UIUtils.getStringInput(scanner, "Reason for locking: ");
        }

        String request = String.format("ACCOUNT_LOCK_UNLOCK|%s|%s|%s|%s",
                username, operation, reason, currentUser.getEmployeeNumber());
        String response = client.sendRequest(request);

        if (response != null && response.equals("ACCOUNT_STATUS_UPDATED")) {
            UIUtils.showSuccess("Account " + operation.toLowerCase() + "ed successfully!");
        } else {
            String error = response != null ? response.replace("STATUS_UPDATE_FAILED|", "") : "Connection error";
            UIUtils.showError(error);
        }

        UIUtils.waitForEnter(scanner);
    }

    private void deleteAccount() {
        UIUtils.printMenuHeader("DELETE ACCOUNT");

        String username = UIUtils.getStringInput(scanner, "Username to delete: ");
        String reason = UIUtils.getStringInput(scanner, "Reason for deletion: ");

        UIUtils.printLine("⚠️  WARNING: This action cannot be undone!");
        UIUtils.printLine("Type 'DELETE' to confirm account deletion:");
        System.out.print("Confirmation: ");
        String confirmation = scanner.nextLine().trim();

        if (!confirmation.equals("DELETE")) {
            UIUtils.showInfo("Account deletion cancelled");
            UIUtils.waitForEnter(scanner);
            return;
        }

        String request = String.format("DELETE_ACCOUNT|%s|%s|%s",
                username, reason, currentUser.getEmployeeNumber());
        String response = client.sendRequest(request);

        if (response != null && response.equals("ACCOUNT_DELETED")) {
            UIUtils.showSuccess("Account deleted successfully!");
        } else {
            String error = response != null ? response.replace("DELETE_FAILED|", "") : "Connection error";
            UIUtils.showError(error);
        }

        UIUtils.waitForEnter(scanner);
    }

    private void accountActivityReport() {
        UIUtils.printMenuHeader("ACCOUNT ACTIVITY REPORT");

        String username = UIUtils.getStringInput(scanner, "Username (leave empty for all): ");
        String days = UIUtils.getStringInput(scanner, "Number of days (default 30): ");

        if (days.trim().isEmpty()) {
            days = "30";
        }

        String request = String.format("ACCOUNT_ACTIVITY_REPORT|%s|%s|%s",
                username, days, currentUser.getEmployeeNumber());
        String response = client.sendRequest(request);

        if (response != null && response.startsWith("ACTIVITY_REPORT")) {
            displayActivityReport(response);
        } else {
            UIUtils.showError("Failed to retrieve activity report");
        }

        UIUtils.waitForEnter(scanner);
    }

    private void displayActivityReport(String response) {
        try {
            // Expected format: ACTIVITY_REPORT|username:lastLogin:loginCount:lastActivity|...
            String[] parts = response.split("\\|");
            if (parts.length > 1) {
                List<String[]> rows = new ArrayList<>();

                for (int i = 1; i < parts.length; i++) {
                    String[] activityData = parts[i].split(":");
                    if (activityData.length >= 4) {
                        rows.add(new String[]{
                                activityData[0], // Username
                                activityData[1], // Last Login
                                activityData[2], // Login Count
                                activityData[3]  // Last Activity
                        });
                    }
                }

                String[] headers = {"Username", "Last Login", "Login Count", "Last Activity"};
                UIUtils.printTable(headers, rows);
            }
        } catch (Exception e) {
            UIUtils.showError("Error displaying activity report: " + e.getMessage());
        }
    }

    private void managePasswordPolicies() {
        UIUtils.printMenuHeader("PASSWORD POLICY SETTINGS");
        UIUtils.printEmptyLine();

        UIUtils.printMenuOption(1, "View Current Policy");
        UIUtils.printMenuOption(2, "Update Password Policy");
        UIUtils.printMenuOption(3, "Password Strength Requirements");
        UIUtils.printMenuOption(4, "Account Lockout Policy");
        UIUtils.printMenuOption(0, "Back");

        UIUtils.printMenuFooter();

        int choice = UIUtils.getIntInput(scanner);

        switch (choice) {
            case 1:
                viewPasswordPolicy();
                break;
            case 2:
                updatePasswordPolicy();
                break;
            case 3:
                passwordStrengthRequirements();
                break;
            case 4:
                accountLockoutPolicy();
                break;
            case 0:
                return;
            default:
                UIUtils.showError("Invalid choice. Please try again.");
                UIUtils.waitForEnter(scanner);
        }
    }

    private void viewPasswordPolicy() {
        UIUtils.printMenuHeader("CURRENT PASSWORD POLICY");

        String request = "GET_PASSWORD_POLICY|" + currentUser.getEmployeeNumber();
        String response = client.sendRequest(request);

        if (response != null && response.startsWith("PASSWORD_POLICY")) {
            displayPasswordPolicy(response);
        } else {
            UIUtils.showError("Failed to retrieve password policy");
        }

        UIUtils.waitForEnter(scanner);
    }

    private void displayPasswordPolicy(String response) {
        try {
            // Expected format: PASSWORD_POLICY|minLength:maxAge:complexity:historyCount:lockoutAttempts
            String[] parts = response.split("\\|");
            if (parts.length > 1) {
                String[] policyData = parts[1].split(":");
                if (policyData.length >= 5) {
                    UIUtils.printLine("Minimum Length: " + policyData[0] + " characters");
                    UIUtils.printLine("Maximum Age: " + policyData[1] + " days");
                    UIUtils.printLine("Complexity Required: " + (policyData[2].equals("true") ? "Yes" : "No"));
                    UIUtils.printLine("Password History: " + policyData[3] + " passwords remembered");
                    UIUtils.printLine("Lockout After: " + policyData[4] + " failed attempts");
                }
            }
        } catch (Exception e) {
            UIUtils.showError("Error displaying password policy: " + e.getMessage());
        }
    }

    private void updatePasswordPolicy() {
        UIUtils.printMenuHeader("UPDATE PASSWORD POLICY");

        String minLength = UIUtils.getStringInput(scanner, "Minimum password length (current/default 8): ");
        String maxAge = UIUtils.getStringInput(scanner, "Password max age in days (current/default 90): ");

        UIUtils.printLine("Require complex passwords? (Y/n): ");
        String complexity = scanner.nextLine().trim().toLowerCase();
        boolean requireComplex = !complexity.equals("n") && !complexity.equals("no");

        String historyCount = UIUtils.getStringInput(scanner, "Password history count (current/default 5): ");
        String lockoutAttempts = UIUtils.getStringInput(scanner, "Lockout after failed attempts (current/default 3): ");

        // Set defaults if empty
        if (minLength.trim().isEmpty()) minLength = "8";
        if (maxAge.trim().isEmpty()) maxAge = "90";
        if (historyCount.trim().isEmpty()) historyCount = "5";
        if (lockoutAttempts.trim().isEmpty()) lockoutAttempts = "3";

        String request = String.format("UPDATE_PASSWORD_POLICY|%s|%s|%s|%s|%s|%s",
                minLength, maxAge, requireComplex, historyCount, lockoutAttempts, currentUser.getEmployeeNumber());
        String response = client.sendRequest(request);

        if (response != null && response.equals("PASSWORD_POLICY_UPDATED")) {
            UIUtils.showSuccess("Password policy updated successfully!");
        } else {
            String error = response != null ? response.replace("POLICY_UPDATE_FAILED|", "") : "Connection error";
            UIUtils.showError(error);
        }

        UIUtils.waitForEnter(scanner);
    }

    private void passwordStrengthRequirements() {
        UIUtils.printMenuHeader("PASSWORD STRENGTH REQUIREMENTS");

        UIUtils.printLine("Configure password complexity requirements:");
        UIUtils.printLine("Require uppercase letters? (Y/n): ");
        String uppercase = scanner.nextLine().trim().toLowerCase();
        boolean reqUppercase = !uppercase.equals("n") && !uppercase.equals("no");

        UIUtils.printLine("Require lowercase letters? (Y/n): ");
        String lowercase = scanner.nextLine().trim().toLowerCase();
        boolean reqLowercase = !lowercase.equals("n") && !lowercase.equals("no");

        UIUtils.printLine("Require numbers? (Y/n): ");
        String numbers = scanner.nextLine().trim().toLowerCase();
        boolean reqNumbers = !numbers.equals("n") && !numbers.equals("no");

        UIUtils.printLine("Require special characters? (Y/n): ");
        String special = scanner.nextLine().trim().toLowerCase();
        boolean reqSpecial = !special.equals("n") && !special.equals("no");

        String forbiddenWords = UIUtils.getStringInput(scanner, "Forbidden words (comma-separated): ");

        String request = String.format("UPDATE_PASSWORD_STRENGTH|%s|%s|%s|%s|%s|%s",
                reqUppercase, reqLowercase, reqNumbers, reqSpecial, forbiddenWords, currentUser.getEmployeeNumber());
        String response = client.sendRequest(request);

        if (response != null && response.equals("STRENGTH_REQUIREMENTS_UPDATED")) {
            UIUtils.showSuccess("Password strength requirements updated!");
        } else {
            String error = response != null ? response.replace("STRENGTH_UPDATE_FAILED|", "") : "Connection error";
            UIUtils.showError(error);
        }

        UIUtils.waitForEnter(scanner);
    }

    private void accountLockoutPolicy() {
        UIUtils.printMenuHeader("ACCOUNT LOCKOUT POLICY");

        String lockoutThreshold = UIUtils.getStringInput(scanner, "Failed login attempts before lockout (default 3): ");
        String lockoutDuration = UIUtils.getStringInput(scanner, "Lockout duration in minutes (default 30): ");
        String resetCountAfter = UIUtils.getStringInput(scanner, "Reset failed count after minutes (default 60): ");

        if (lockoutThreshold.trim().isEmpty()) lockoutThreshold = "3";
        if (lockoutDuration.trim().isEmpty()) lockoutDuration = "30";
        if (resetCountAfter.trim().isEmpty()) resetCountAfter = "60";

        String request = String.format("UPDATE_LOCKOUT_POLICY|%s|%s|%s|%s",
                lockoutThreshold, lockoutDuration, resetCountAfter, currentUser.getEmployeeNumber());
        String response = client.sendRequest(request);

        if (response != null && response.equals("LOCKOUT_POLICY_UPDATED")) {
            UIUtils.showSuccess("Account lockout policy updated!");
        } else {
            String error = response != null ? response.replace("LOCKOUT_UPDATE_FAILED|", "") : "Connection error";
            UIUtils.showError(error);
        }

        UIUtils.waitForEnter(scanner);
    }

    private void systemConfiguration() {
        UIUtils.printMenuHeader("SYSTEM CONFIGURATION");
        UIUtils.printEmptyLine();

        UIUtils.printMenuOption(1, "Server Settings");
        UIUtils.printMenuOption(2, "Database Configuration");
        UIUtils.printMenuOption(3, "Logging Settings");
        UIUtils.printMenuOption(4, "Performance Tuning");
        UIUtils.printMenuOption(5, "Feature Toggles");
        UIUtils.printMenuOption(0, "Back");

        UIUtils.printMenuFooter();

        int choice = UIUtils.getIntInput(scanner);

        switch (choice) {
            case 1:
                serverSettings();
                break;
            case 2:
                databaseConfiguration();
                break;
            case 3:
                loggingSettings();
                break;
            case 4:
                performanceTuning();
                break;
            case 5:
                featureToggles();
                break;
            case 0:
                return;
            default:
                UIUtils.showError("Invalid choice. Please try again.");
                UIUtils.waitForEnter(scanner);
        }
    }

    private void serverSettings() {
        UIUtils.printMenuHeader("SERVER SETTINGS");

        String request = "GET_SERVER_SETTINGS|" + currentUser.getEmployeeNumber();
        String response = client.sendRequest(request);

        if (response != null && response.startsWith("SERVER_SETTINGS")) {
            displayServerSettings(response);

            UIUtils.printLine("\nUpdate settings? (y/N): ");
            String update = scanner.nextLine().trim().toLowerCase();

            if (update.equals("y") || update.equals("yes")) {
                updateServerSettings();
            }
        } else {
            UIUtils.showError("Failed to retrieve server settings");
        }

        UIUtils.waitForEnter(scanner);
    }

    private void displayServerSettings(String response) {
        try {
            // Expected format: SERVER_SETTINGS|maxConnections:sessionTimeout:requestTimeout:maxFileSize
            String[] parts = response.split("\\|");
            if (parts.length > 1) {
                String[] settings = parts[1].split(":");
                if (settings.length >= 4) {
                    UIUtils.printLine("Max Connections: " + settings[0]);
                    UIUtils.printLine("Session Timeout: " + settings[1] + " minutes");
                    UIUtils.printLine("Request Timeout: " + settings[2] + " seconds");
                    UIUtils.printLine("Max File Size: " + settings[3] + " MB");
                }
            }
        } catch (Exception e) {
            UIUtils.showError("Error displaying server settings: " + e.getMessage());
        }
    }

    private void updateServerSettings() {
        String maxConnections = UIUtils.getStringInput(scanner, "Max connections (leave empty to keep current): ");
        String sessionTimeout = UIUtils.getStringInput(scanner, "Session timeout in minutes: ");
        String requestTimeout = UIUtils.getStringInput(scanner, "Request timeout in seconds: ");
        String maxFileSize = UIUtils.getStringInput(scanner, "Max file size in MB: ");

        String request = String.format("UPDATE_SERVER_SETTINGS|%s|%s|%s|%s|%s",
                maxConnections, sessionTimeout, requestTimeout, maxFileSize, currentUser.getEmployeeNumber());
        String response = client.sendRequest(request);

        if (response != null && response.equals("SERVER_SETTINGS_UPDATED")) {
            UIUtils.showSuccess("Server settings updated! Restart may be required.");
        } else {
            String error = response != null ? response.replace("SETTINGS_UPDATE_FAILED|", "") : "Connection error";
            UIUtils.showError(error);
        }
    }

    private void databaseConfiguration() {
        UIUtils.printMenuHeader("DATABASE CONFIGURATION");

        String request = "GET_DB_CONFIG|" + currentUser.getEmployeeNumber();
        String response = client.sendRequest(request);

        if (response != null && response.startsWith("DB_CONFIG")) {
            displayDatabaseConfig(response);
        } else {
            UIUtils.showError("Failed to retrieve database configuration");
        }

        UIUtils.waitForEnter(scanner);
    }

    private void displayDatabaseConfig(String response) {
        try {
            // Expected format: DB_CONFIG|connectionPool:queryTimeout:backupSchedule:maintenanceWindow
            String[] parts = response.split("\\|");
            if (parts.length > 1) {
                String[] config = parts[1].split(":");
                if (config.length >= 4) {
                    UIUtils.printLine("Connection Pool Size: " + config[0]);
                    UIUtils.printLine("Query Timeout: " + config[1] + " seconds");
                    UIUtils.printLine("Backup Schedule: " + config[2]);
                    UIUtils.printLine("Maintenance Window: " + config[3]);
                }
            }
        } catch (Exception e) {
            UIUtils.showError("Error displaying database config: " + e.getMessage());
        }
    }

    private void manageBranches() {
        UIUtils.printMenuHeader("BRANCH MANAGEMENT");
        UIUtils.printEmptyLine();

        UIUtils.printMenuOption(1, "View All Branches");
        UIUtils.printMenuOption(2, "Add New Branch");
        UIUtils.printMenuOption(3, "Update Branch Info");
        UIUtils.printMenuOption(4, "Deactivate Branch");
        UIUtils.printMenuOption(5, "Branch Performance Report");
        UIUtils.printMenuOption(0, "Back");

        UIUtils.printMenuFooter();

        int choice = UIUtils.getIntInput(scanner);

        switch (choice) {
            case 1:
                viewAllBranches();
                break;
            case 2:
                addNewBranch();
                break;
            case 3:
                updateBranchInfo();
                break;
            case 4:
                deactivateBranch();
                break;
            case 5:
                branchPerformanceReport();
                break;
            case 0:
                return;
            default:
                UIUtils.showError("Invalid choice. Please try again.");
                UIUtils.waitForEnter(scanner);
        }
    }

    private void viewAllBranches() {
        UIUtils.printMenuHeader("ALL BRANCHES");

        String request = "GET_ALL_BRANCHES|" + currentUser.getEmployeeNumber();
        String response = client.sendRequest(request);

        if (response != null && response.startsWith("BRANCHES_DATA")) {
            displayBranchesData(response);
        } else {
            UIUtils.showError("Failed to retrieve branches data");
        }

        UIUtils.waitForEnter(scanner);
    }

    private void displayBranchesData(String response) {
        try {
            // Expected format: BRANCHES_DATA|branchId:name:address:phone:manager:status|...
            String[] parts = response.split("\\|");
            if (parts.length > 1) {
                List<String[]> rows = new ArrayList<>();

                for (int i = 1; i < parts.length; i++) {
                    String[] branchData = parts[i].split(":");
                    if (branchData.length >= 6) {
                        rows.add(new String[]{
                                branchData[0], // Branch ID
                                branchData[1], // Name
                                branchData[2], // Address
                                branchData[3], // Phone
                                branchData[4], // Manager
                                branchData[5]  // Status
                        });
                    }
                }

                String[] headers = {"Branch ID", "Name", "Address", "Phone", "Manager", "Status"};
                UIUtils.printTable(headers, rows);
                UIUtils.showInfo("Total branches: " + rows.size());
            }
        } catch (Exception e) {
            UIUtils.showError("Error displaying branches: " + e.getMessage());
        }
    }

    private void systemBackup() {
        UIUtils.printMenuHeader("SYSTEM BACKUP");
        UIUtils.printEmptyLine();

        UIUtils.printMenuOption(1, "Create Full Backup");
        UIUtils.printMenuOption(2, "Create Incremental Backup");
        UIUtils.printMenuOption(3, "View Backup History");
        UIUtils.printMenuOption(4, "Restore from Backup");
        UIUtils.printMenuOption(5, "Schedule Automatic Backup");
        UIUtils.printMenuOption(0, "Back");

        UIUtils.printMenuFooter();

        int choice = UIUtils.getIntInput(scanner);

        switch (choice) {
            case 1:
                createFullBackup();
                break;
            case 2:
                createIncrementalBackup();
                break;
            case 3:
                viewBackupHistory();
                break;
            case 4:
                restoreFromBackup();
                break;
            case 5:
                scheduleAutomaticBackup();
                break;
            case 0:
                return;
            default:
                UIUtils.showError("Invalid choice. Please try again.");
                UIUtils.waitForEnter(scanner);
        }
    }

    private void createFullBackup() {
        UIUtils.printMenuHeader("CREATE FULL BACKUP");

        String backupName = UIUtils.getStringInput(scanner, "Backup name (optional): ");
        String description = UIUtils.getStringInput(scanner, "Description (optional): ");

        UIUtils.showInfo("Creating full system backup... This may take several minutes.");

        String request = String.format("CREATE_FULL_BACKUP|%s|%s|%s",
                backupName, description, currentUser.getEmployeeNumber());
        String response = client.sendRequest(request);

        if (response != null && response.startsWith("BACKUP_SUCCESS")) {
            String[] parts = response.split("\\|");
            UIUtils.showSuccess("Full backup created successfully!");
            if (parts.length > 1) {
                UIUtils.showInfo("Backup ID: " + parts[1]);
            }
            if (parts.length > 2) {
                UIUtils.showInfo("Size: " + parts[2]);
            }
        } else {
            String error = response != null ? response.replace("BACKUP_FAILED|", "") : "Connection error";
            UIUtils.showError(error);
        }

        UIUtils.waitForEnter(scanner);
    }

    private void systemStatistics() {
        UIUtils.printMenuHeader("SYSTEM STATISTICS");

        String request = "GET_SYSTEM_STATS|" + currentUser.getEmployeeNumber();
        String response = client.sendRequest(request);

        if (response != null && response.startsWith("SYSTEM_STATS")) {
            displaySystemStats(response);
        } else {
            UIUtils.showError("Failed to retrieve system statistics");
        }

        UIUtils.waitForEnter(scanner);
    }

    private void displaySystemStats(String response) {
        try {
            // Expected format: SYSTEM_STATS|totalUsers:activeUsers:totalBranches:totalProducts:dailyTransactions:systemUptime
            String[] parts = response.split("\\|");
            if (parts.length > 1) {
                String[] stats = parts[1].split(":");
                if (stats.length >= 6) {
                    UIUtils.printLine("📊 SYSTEM OVERVIEW");
                    UIUtils.printEmptyLine();
                    UIUtils.printLine("Total Users: " + stats[0]);
                    UIUtils.printLine("Active Users: " + stats[1]);
                    UIUtils.printLine("Total Branches: " + stats[2]);
                    UIUtils.printLine("Total Products: " + stats[3]);
                    UIUtils.printLine("Daily Transactions: " + stats[4]);
                    UIUtils.printLine("System Uptime: " + stats[5]);

                    if (parts.length > 2) {
                        UIUtils.printEmptyLine();
                        UIUtils.printLine("📈 PERFORMANCE METRICS");
                        String[] perfStats = parts[2].split(":");
                        for (int i = 0; i < perfStats.length; i += 2) {
                            if (i + 1 < perfStats.length) {
                                UIUtils.printLine(perfStats[i] + ": " + perfStats[i + 1]);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            UIUtils.showError("Error displaying system statistics: " + e.getMessage());
        }
    }

    // Additional utility methods for incomplete functions
    private void loggingSettings() {
        UIUtils.showInfo("Logging settings configuration - Implementation pending");
        UIUtils.waitForEnter(scanner);
    }

    private void performanceTuning() {
        UIUtils.showInfo("Performance tuning options - Implementation pending");
        UIUtils.waitForEnter(scanner);
    }

    private void featureToggles() {
        UIUtils.showInfo("Feature toggle management - Implementation pending");
        UIUtils.waitForEnter(scanner);
    }

    private void addNewBranch() {
        UIUtils.showInfo("Add new branch functionality - Implementation pending");
        UIUtils.waitForEnter(scanner);
    }

    private void updateBranchInfo() {
        UIUtils.showInfo("Update branch information - Implementation pending");
        UIUtils.waitForEnter(scanner);
    }

    private void deactivateBranch() {
        UIUtils.showInfo("Deactivate branch functionality - Implementation pending");
        UIUtils.waitForEnter(scanner);
    }

    private void branchPerformanceReport() {
        UIUtils.showInfo("Branch performance report - Implementation pending");
        UIUtils.waitForEnter(scanner);
    }

    private void securitySettings() {
        UIUtils.showInfo("Security settings configuration - Implementation pending");
        UIUtils.waitForEnter(scanner);
    }

    private void createIncrementalBackup() {
        UIUtils.showInfo("Incremental backup creation - Implementation pending");
        UIUtils.waitForEnter(scanner);
    }

    private void viewBackupHistory() {
        UIUtils.showInfo("Backup history view - Implementation pending");
        UIUtils.waitForEnter(scanner);
    }

    private void restoreFromBackup() {
        UIUtils.showInfo("Restore from backup functionality - Implementation pending");
        UIUtils.waitForEnter(scanner);
    }

    private void scheduleAutomaticBackup() {
        UIUtils.showInfo("Schedule automatic backup - Implementation pending");
        UIUtils.waitForEnter(scanner);
    }

    private void auditTrail() {
        UIUtils.showInfo("Audit trail management - Implementation pending");
        UIUtils.waitForEnter(scanner);
    }
}