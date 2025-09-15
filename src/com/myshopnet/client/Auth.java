package com.myshopnet.client;

import com.google.gson.JsonObject;
import com.myshopnet.client.models.UserTypeLoggedIn;

public class Auth {
    private static String username;
    private static UserTypeLoggedIn currentUserType = UserTypeLoggedIn.NONE;
    private static JsonObject currentUser;
    private static String currentChatId;

    public static JsonObject getCurrentUser() {
        return currentUser;
    }

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        Auth.username = username;
    }

    public static void setCurrentUser(JsonObject currentUser) {
        Auth.currentUser = currentUser;
    }

    public static void setCurrentUserType(UserTypeLoggedIn currentUserType) {
        Auth.currentUserType = currentUserType;
    }

    public static UserTypeLoggedIn getCurrentUserType() {
        return currentUserType;
    }

    public static String getCurrentChatId() {
        return currentChatId;
    }

    public static void setCurrentChatId(String currentChatId) {
        Auth.currentChatId = currentChatId;
    }
}
