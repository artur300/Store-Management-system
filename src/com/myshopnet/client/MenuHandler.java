// MenuHandler.java
import com.myshopnet.client.Client;
import com.myshopnet.client.utils.UIUtils;

import java.util.Scanner;

public class MenuHandler {
    private Client client;
    private User currentUser;
    private Scanner scanner;
    private boolean keepRunning;

    public MenuHandler(Client client, User currentUser) {
        this.client = client;
        this.currentUser = currentUser;
        this.scanner = client.getScanner();
        this.keepRunning = true;
    }

    public void showMainMenu() {
        while (keepRunning && client.isConnected()) {
            displayMainMenu();
            int choice = UIUtils.getIntInput(scanner);
            handleMainMenuChoice(choice);
        }
    }

    private void displayMainMenu() {
        UIUtils.printMenuHeader("MAIN MENU - " + currentUser.getRole().getDisplayName());
        UIUtils.printLine("Branch: " + currentUser.getBranchId());
        UIUtils.printLine("User: " + currentUser.getFullName());
        UIUtils.printEmptyLine();

        // Common menu options
        UIUtils.printMenuOption(1, "Inventory Management");
        UIUtils.printMenuOption(2, "Customer Management");
        UIUtils.printMenuOption(3, "Chat System");
        UIUtils.printMenuOption(4, "System Logs");

        // Role-based menu options
        if (currentUser.isAdmin()) {
            UIUtils.printMenuOption(5, "Admin Panel");
            UIUtils.printMenuOption(6, "Employee Management");
        } else if (currentUser.canManageEmployees()) {
            UIUtils.printMenuOption(5, "Employee Management");
        }

        UIUtils.printEmptyLine();
        UIUtils.printMenuOption(9, "Change Password");
        UIUtils.printMenuOption(0, "Logout");

        UIUtils.printMenuFooter();
    }

    private void handleMainMenuChoice(int choice) {
        switch (choice) {
            case 1:
                new InventoryHandler(client, currentUser).showInventoryMenu();
                break;
            case 2:
                new CustomerHandler(client, currentUser).showCustomerMenu();
                break;
            case 3:
                new ChatHandler(client, currentUser).showChatMenu();
                break;
            case 4:
                new LogHandler(client, currentUser).showLogMenu();
                break;
            case 5:
                if (currentUser.isAdmin()) {
                    new AdminHandler(client, currentUser).showAdminMenu();
                } else if (currentUser.canManageEmployees()) {
                    new EmployeeHandler(client, currentUser).showEmployeeMenu();
                }
                break;
            case 6:
                if (currentUser.isAdmin()) {
                    new EmployeeHandler(client, currentUser).showEmployeeMenu();
                }
                break;
            case 9:
                changePassword();
                break;
            case 0:
                logout();
                break;
            default:
                UIUtils.showError("Invalid choice. Please try again.");
                UIUtils.waitForEnter(scanner);
        }
    }

    private void changePassword() {
        UIUtils.printMenuHeader("CHANGE PASSWORD");

        String currentPassword = UIUtils.getStringInput(scanner, "Current Password: ");
        String newPassword = UIUtils.getStringInput(scanner, "New Password: ");
        String confirmPassword = UIUtils.getStringInput(scanner, "Confirm New Password: ");

        if (!newPassword.equals(confirmPassword)) {
            UIUtils.showError("Passwords do not match!");
            UIUtils.waitForEnter(scanner);
            return;
        }

        String request = String.format("CHANGE_PASSWORD|%s|%s|%s",
                currentUser.getUsername(), currentPassword, newPassword);
        String response = client.sendRequest(request);

        if (response != null && response.equals("PASSWORD_CHANGED")) {
            UIUtils.showSuccess("Password changed successfully!");
        } else {
            String error = response != null ? response.replace("PASSWORD_CHANGE_FAILED|", "") : "Connection error";
            UIUtils.showError(error);
        }

        UIUtils.waitForEnter(scanner);
    }

    private void logout() {
        UIUtils.printMenuHeader("LOGOUT");
        UIUtils.printLine("Are you sure you want to logout? (y/N)");
        UIUtils.printMenuFooter();

        String confirmation = scanner.nextLine().trim().toLowerCase();
        if (confirmation.equals("y") || confirmation.equals("yes")) {
            String request = "LOGOUT|" + currentUser.getUsername();
            client.sendRequest(request);

            UIUtils.showInfo("Logged out successfully. Goodbye!");
            keepRunning = false;
        }
    }
}