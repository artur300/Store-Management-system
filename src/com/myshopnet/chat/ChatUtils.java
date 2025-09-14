package com.myshopnet.chat;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

public final class ChatUtils {
    private static final SimpleDateFormat TS = new SimpleDateFormat("HH:mm");

    private ChatUtils() {}

    public static String sys(String text) {
        return "[" + TS.format(new Date()) + "] * " + text;
    }

    public static void broadcastSys(Collection<UserSession> recipients, String text) {
        String line = sys(text);
        for (UserSession s : recipients) {
            if (s != null && s.out() != null) {
                s.out().println(line);
            }
        }
    }

    public static void log(String msg) {
        System.out.println(new Date() + " " + msg);
    }
}

