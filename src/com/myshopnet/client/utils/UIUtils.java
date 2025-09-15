package com.myshopnet.client.utils;

import com.myshopnet.AppState;
import com.myshopnet.client.InputManager;

import java.util.List;
import java.util.Scanner;

public class UIUtils {
    private static InputManager inputManager = InputManager.getInstance();
    private static final int CONSOLE_WIDTH = 50;

    public static void clearScreen() {
        if (AppState.chatActive) return;
        for (int i = 0; i < 3; i++) {
            System.out.println();
        }
    }

    public static void printBorder() {
        if (AppState.chatActive) return;
        System.out.println("╔" + "═".repeat(CONSOLE_WIDTH - 2) + "╗");
    }

    public static void printBottomBorder() {
        if (AppState.chatActive) return;
        System.out.println("╚" + "═".repeat(CONSOLE_WIDTH - 2) + "╝");
    }

    public static void printTitle(String title) {
        if (AppState.chatActive) return;
        int len = Math.min(title.length(), CONSOLE_WIDTH - 2);
        String t = title.substring(0, len);
        int padding = Math.max(0, (CONSOLE_WIDTH - t.length() - 2) / 2);
        System.out.println("║" + " ".repeat(padding) + t +
                " ".repeat(Math.max(0, CONSOLE_WIDTH - t.length() - padding - 2)) + "║");
    }

    public static void printLine(String text) {
        if (AppState.chatActive) return;
        String t = text;
        if (t.length() > CONSOLE_WIDTH - 4) {
            t = t.substring(0, CONSOLE_WIDTH - 7) + "...";
        }
        System.out.println("║ " + t + " ".repeat(CONSOLE_WIDTH - t.length() - 3) + "║");
    }

    public static void printEmptyLine() {
        if (AppState.chatActive) return;
        System.out.println("║" + " ".repeat(CONSOLE_WIDTH - 2) + "║");
    }

    public static void printMenuHeader(String header) {
        if (AppState.chatActive) return;
        clearScreen();
        printBorder();
        printTitle(header);
        printBorder();
        printEmptyLine();
    }

    public static void printMenuOption(int number, String option) {
        if (AppState.chatActive) return;
        String formatted = number + ". " + option;
        printLine(formatted);
    }

    public static void printMenuFooter() {
        if (AppState.chatActive) return;
        printEmptyLine();
        printBottomBorder();
        System.out.print("Enter your choice: ");
    }

    public static int getIntInput(Scanner scanner) {
        return getIntInput();
    }

    public static int getIntInput() {
        if (AppState.chatActive) return -1;

        while (!AppState.chatActive) {
            try {
                String input = inputManager.takeInput();
                if (AppState.chatActive) return -1;

                return Integer.parseInt(input.trim());
            } catch (NumberFormatException e) {
                if (!AppState.chatActive) {
                    System.out.print("Invalid input. Please enter a number: ");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return -1;
            }
        }

        return -1;
    }

    public static String getStringInput(String prompt) {
        if (AppState.chatActive) return "";

        System.out.print(prompt);

        try {
            String input = inputManager.takeInput();
            return AppState.chatActive ? "" : input.trim();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "";
        }
    }

    public static String getStringInput(Scanner scanner, String prompt) {
        return getStringInput(prompt);
    }

    public static void showError(String message) {
        if (AppState.chatActive) return;
        System.out.println("\n❌ Error: " + message);
        System.out.println("Press Enter to continue...");
    }

    public static void showSuccess(String message) {
        if (AppState.chatActive) return;
        System.out.println("\n✅ Success: " + message);
        System.out.println("Press Enter to continue...");
    }

    public static void showInfo(String message) {
        if (AppState.chatActive) return;
        System.out.println("\nℹ️ Info: " + message);
    }

    public static void waitForEnter(Scanner scanner) {
        waitForEnter();
    }

    public static void waitForEnter() {
        if (AppState.chatActive) return;

        try {
            InputManager.getInstance().takeInput();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void printTable(String[] headers, List<String[]> rows) {
        if (AppState.chatActive) return;
        if (headers == null || rows == null) return;

        int[] widths = new int[headers.length];
        for (int i = 0; i < headers.length; i++) {
            widths[i] = headers[i].length();
            for (String[] row : rows) {
                if (i < row.length && row[i] != null) {
                    widths[i] = Math.max(widths[i], row[i].length());
                }
            }
        }

        printTableRow(headers, widths);
        printTableSeparator(widths);

        for (String[] row : rows) {
            printTableRow(row, widths);
        }
    }

    private static void printTableRow(String[] row, int[] widths) {
        if (AppState.chatActive) return;
        System.out.print("║ ");
        for (int i = 0; i < row.length; i++) {
            String cell = row[i] != null ? row[i] : "";
            System.out.printf("%-" + widths[i] + "s", cell);
            if (i < row.length - 1) {
                System.out.print(" │ ");
            }
        }
        System.out.println(" ║");
    }

    private static void printTableSeparator(int[] widths) {
        if (AppState.chatActive) return;
        System.out.print("║ ");
        for (int i = 0; i < widths.length; i++) {
            System.out.print("─".repeat(widths[i]));
            if (i < widths.length - 1) {
                System.out.print(" ┼ ");
            }
        }
        System.out.println(" ║");
    }
}
