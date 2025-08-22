package com.myshopnet.auth;

import com.myshopnet.data.Data;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionRegistry {



    public static final class SessionInfo {
        private final String token;
        private final String username;
        private final long loginAt;

        public SessionInfo(String token, String username) {
            this.token = token;
            this.username = username;
            this.loginAt = System.currentTimeMillis();
        }
        public String getToken() { return token; }
        public String getUsername() { return username; }
        public long getLoginAt() { return loginAt; }
    }



    public synchronized boolean isUserLoggedIn(String username) {
        return Data.getUserSessions().containsKey(username);
    }

    public synchronized SessionInfo create(String username) {
        if (isUserLoggedIn(username))
            throw new IllegalStateException("User already logged-in");
        String token = UUID.randomUUID().toString();
        SessionInfo info = new SessionInfo(token, username);
        Data.getUserSessions().put(token, info);
        Data.getUserIds().put(username, token);
        return info;
    }

    public synchronized void end(String token) {
        SessionInfo info = Data.getUserSessions().remove(token);

        if (info != null) Data.getUserIds().remove(info.getUsername());
    }

    public SessionInfo get(String token) {
        return Data.getUserSessions().get(token);
    }
}

