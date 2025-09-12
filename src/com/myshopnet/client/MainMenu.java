package com.myshopnet.client;
import com.myshopnet.client.utils.UIUtils;

import java.util.Scanner;

public class MainMenu implements Menu {
    private Scanner scanner;
    private boolean keepRunning;

    public MainMenu() {
        this.scanner = Singletons.CLIENT.getScanner();
        this.keepRunning = true;
    }

    public void show() {
        try {
            while (keepRunning && Singletons.CLIENT.isConnected()) {
                displayMainMenu();
            }
        }
        catch (Exception e) {
            UIUtils.showError(e.getMessage());
            UIUtils.printLine("Going back to first page");
            Singletons.REGISTER_MENU.show();
        }
    }

    private void displayMainMenu() {
        Singletons.CLIENT.showMenuAccordingToUser();
    }
}