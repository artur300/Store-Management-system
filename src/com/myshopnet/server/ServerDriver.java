package com.myshopnet.server;

import com.myshopnet.utils.PopulateUsers;

import java.io.IOException;

public class ServerDriver {
    public static void main(String[] args) throws IOException {
        Server server = new Server();
        PopulateUsers.populate();

        server.startListening();
    }
}
