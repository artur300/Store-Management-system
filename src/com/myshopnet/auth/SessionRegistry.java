package com.myshopnet.auth;

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

    private final Map<String, SessionInfo> byToken = new ConcurrentHashMap<>();
    private final Map<String, String> tokenByUser = new ConcurrentHashMap<>();

    public synchronized boolean isUserLoggedIn(String username) {
        return tokenByUser.containsKey(username);
    }

    public synchronized SessionInfo create(String username) {
        if (isUserLoggedIn(username))
            throw new IllegalStateException("User already logged-in");
        String token = UUID.randomUUID().toString();
        SessionInfo info = new SessionInfo(token, username);
        byToken.put(token, info);
        tokenByUser.put(username, token);
        return info;
    }

    public synchronized void end(String token) {
        SessionInfo info = byToken.remove(token);
        if (info != null) tokenByUser.remove(info.getUsername());
    }

    public SessionInfo get(String token) { return byToken.get(token); }
}

