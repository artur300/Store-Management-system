package com.myshopnet.client;

public class ClientDriver {
    public static void main(String[] args) {
        // חשוב: שימוש ב-Singletons כדי לשמר את אותו אינסטנס בין התפריטים
        Singletons.CLIENT.start();
    }
}

