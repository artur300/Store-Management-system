package com.myshopnet.client;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.myshopnet.client.utils.UIUtils;
import com.myshopnet.utils.GsonSingleton;
import com.google.gson.JsonParser;

import java.util.HashMap;
import java.util.Map;
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