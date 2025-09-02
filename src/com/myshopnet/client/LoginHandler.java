import com.myshopnet.client.utils.UIUtils;

import java.util.Scanner;

public class LoginHandler {
    private Client client;
    private Scanner scanner;

    public LoginHandler(Client client) {
        this.client = client;
        this.scanner = client.getScanner();
    }

    public String handleLogin() {
        int maxAttempts = 3;
        int attempts = 0;

        while (attempts < maxAttempts) {
            UIUtils.printMenuHeader("USER LOGIN");

            String username = UIUtils.getStringInput(scanner, "Username: ");
            String password = UIUtils.getStringInput(scanner, "Password: ");

            // Send login request to server
            String request = String.format("LOGIN|%s|%s", username, password);
            String response = client.sendRequest(request);

            if (response != null && response.startsWith("LOGIN_SUCCESS")) {
                return parseLoginResponse(response);
            } else {
                attempts++;
                String errorMsg = response != null ? response.replace("LOGIN_FAILED|", "") : "Connection error";
                UIUtils.showError(errorMsg + " (Attempt " + attempts + "/" + maxAttempts + ")");

                if (attempts < maxAttempts) {
                    UIUtils.waitForEnter(scanner);
                }
            }
        }

        UIUtils.showError("Maximum login attempts exceeded. Connection will be closed.");
        return null;
    }

    private User parseLoginResponse(String response) {
        try {
            // Expected format: LOGIN_SUCCESS|username|fullName|id|role|branchId|phoneNumber|accountNumber|employeeNumber
            String[] parts = response.split("\\|");
            if (parts.length >= 6) {
                String username = parts[1];
                String fullName = parts[2];
                String id = parts[3];
                UserRole role = UserRole.valueOf(parts[4]);
                String branchId = parts[5];

                User user = new User(username, fullName, id, role, branchId);

                if (parts.length > 6) user.setPhoneNumber(parts[6]);
                if (parts.length > 7) user.setAccountNumber(parts[7]);
                if (parts.length > 8) user.setEmployeeNumber(parts[8]);

                UIUtils.showSuccess("Welcome, " + user.getFullName() + "!");
                UIUtils.waitForEnter(scanner);

                return user;
            }
        } catch (Exception e) {
            UIUtils.showError("Error parsing login response: " + e.getMessage());
        }

        return null;
    }
}