package com.myshopnet.utils;

import com.google.gson.Gson;

public class GsonSingleton {
    private static Gson gson = new Gson();

    private GsonSingleton() { }

    public static Gson getInstance() {
        return gson;
    }
}
