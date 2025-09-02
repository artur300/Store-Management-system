package com.myshopnet.client.utils;

import java.util.List;
import java.util.Scanner;

public class UIUtils {
    private static final int CONSOLE_WIDTH = 50;

    public static void clearScreen() {
        try {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        } catch (Exception e) {
            System.out.print("\033[2J\033[H");
        }
    }

    public static void printBorder() {
        System.out.println("╔" + "═".repeat(CONSOLE_WIDTH - 2) + "╗");
    }

    public static void printBottomBorder() {
        System.out.println("╚" + "═".repeat(CONSOLE_WIDTH - 2) + "╝");
    }

    public static void printTitle(String title) {
        int padding = (CONSOLE_WIDTH - title.length() - 2) / 2;
        System.out.println("║" + " ".repeat(padding) + title +
                " ".repeat(CONSOLE_WIDTH - title.length() - padding - 2) + "║");
    }

    public static void printLine(String text) {
        if (text.length() > CONSOLE_WIDTH - 4) {
            text = text.substring(0, CONSOLE_WIDTH - 7) + "...";
        }
        System.out.println("║ " + text + " ".repeat(CONSOLE_WIDTH - text.length() - 3) + "║");
    }

    public static void printEmptyLine() {
        System.out.println("║" + " ".repeat(CONSOLE_WIDTH - 2) + "║");
    }

    public static void printMenuHeader(String header) {
        clearScreen();
        printBorder();
        printTitle(header);
        printBorder();
        printEmptyLine();
    }

    public static void printMenuOption(int number, String option) {
        String formatted = number + ". " + option;
        printLine(formatted);
    }

    public static void printMenuFooter() {
        printEmptyLine();
        printBottomBorder();
        System.out.print("Enter your choice: ");
    }

    public static int getIntInput(Scanner scanner) {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a number: ");
            }
        }
    }

    public static String getStringInput(Scanner scanner, String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    public static void showError(String message) {
        System.out.println("\n❌ Error: " + message);
        System.out.println("Press Enter to continue...");
    }

    public static void showSuccess(String message) {
        System.out.println("\n✅ Success: " + message);
        System.out.println("Press Enter to continue...");
    }

    public static void showInfo(String message) {
        System.out.println("\nℹ️ Info: " + message);
    }

    public static void waitForEnter(Scanner scanner) {
        scanner.nextLine();
    }

    public static void printTable(String[] headers, List<String[]> rows) {
        if (headers == null || rows == null) return;

        // Calculate column widths
        int[] widths = new int[headers.length];
        for (int i = 0; i < headers.length; i++) {
            widths[i] = headers[i].length();
            for (String[] row : rows) {
                if (i < row.length && row[i] != null) {
                    widths[i] = Math.max(widths[i], row[i].length());
                }
            }
        }

        // Print headers
        printTableRow(headers, widths);
        printTableSeparator(widths);

        // Print rows
        for (String[] row : rows) {
            printTableRow(row, widths);
        }
    }

    private static void printTableRow(String[] row, int[] widths) {
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