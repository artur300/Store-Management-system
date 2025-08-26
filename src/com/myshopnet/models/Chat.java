package com.myshopnet.models;

import com.myshopnet.chat.ChatColors;
import com.myshopnet.chat.UserSession;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;


public class Chat {
    private static final SimpleDateFormat TS = new SimpleDateFormat("HH:mm");
//    private static final String SENDER_NAME_COLOR = ChatColors.PURPLE;
//    private static final String SENDER_MSG_COLOR  = ChatColors.PURPLE;
//    private static final String RECV_NAME_COLOR   = ChatColors.WHITE;
//    private static final String RECV_MSG_COLOR    = ChatColors.CYAN;
//    private static final String SYSTEM_COLOR      = ChatColors.CYAN;
    private final String id;
    private final Set<User> usersInChat = new CopyOnWriteArraySet<>();
    private static final AtomicInteger SEQ = new AtomicInteger(1);

    private Chat(String id) {
        this.id = id;
    }

    public static Chat createChat(User a, User b) {
        String id = "room " + SEQ.getAndIncrement();
        Chat r = new Chat(id);
        r.usersInChat.add(a);
        r.usersInChat.add(b);
        return r;
    }

    public String getId() { return id; }

// Sends a system message (with timestamp and color) to all users in the room.

    public void system(String text) {
        String line = "[" + TS.format(new Date()) + "] "
                + SYSTEM_COLOR + "* " + text + ChatColors.RESET;
        sendToAll(line);
    }


    public void say(UserSession from, String msg) {
        String ts = "[" + TS.format(new Date()) + "] ";

        for (UserSession u : usersInChat) {
            boolean isSenderView = (u == from);

            String namePart = (isSenderView ? SENDER_NAME_COLOR : RECV_NAME_COLOR)
                    + from.name() + ChatColors.RESET;

            String msgPart  = (isSenderView ? SENDER_MSG_COLOR  : RECV_MSG_COLOR)
                    + msg + ChatColors.RESET;

            u.out().println(ts + namePart + ": " + msgPart);
        }

        for (UserSession sup : supervisors) {
            String namePart = ChatColors.BLUE + from.name() + ChatColors.RESET;
            String msgPart  = ChatColors.BLUE  + msg        + ChatColors.RESET;
            sup.out().println(ts + namePart + ": " + msgPart);
        }
    }


// Adds a supervisor (like an admin) to the chat room.
// Supervisors can monitor the conversation without being regular participants.

    public void addSupervisor(UserSession sup) {
        supervisors.add(sup);
    }

// Removes a user from the room.
// The user is taken out of both participants and supervisors lists,
// ensuring they no longer belong to this chat room.

    public void remove(UserSession u) {
        usersInChat.remove(u);
        supervisors.remove(u);
    }


// Creates a summary of all users in the room.
// Regular participants are listed by their name,
// while supervisors are marked with "SUP:".
// Returns the full list as a single comma-separated string.

    public String participantsSummary() {
        List<String> p = new ArrayList<>();
        for (UserSession u : usersInChat) p.add(u.name());
        for (UserSession s : supervisors) p.add("SUP:" + s.name());
        return String.join(", ", p);
    }


// Sends a given message line to everyone in the chat room.
// It loops through all participants and supervisors
// and prints the message to each user's output stream.

    private void sendToAll(String line) {
        for (UserSession u : usersInChat) u.out().println(line);
        for (UserSession s : supervisors) s.out().println(line);
    }

// Returns the number of participants currently in the chat room.
// (Does not include supervisors, only regular participants.)

    public int participantsCount() {
        return usersInChat.size();
    }

// Returns a new list of all current participants in the chat room.
// This allows other parts of the program to safely access the participants
// without directly modifying the original set.

    public List<UserSession> participantsList() {
        return new ArrayList<>(usersInChat);
    }

// Returns a new list of all supervisors in the chat room.
// This way, other parts of the program can see who the supervisors are
// without changing the original set directly.

    public List<UserSession> supervisorsList() {
        return new ArrayList<>(supervisors);
    }

}


