package com.myshopnet.server;

import com.myshopnet.utils.Singletons;
import com.myshopnet.utils.PopulateUsers;

import java.io.IOException;

public class ServerDriver {
    public static void main(String[] args) throws IOException {
        Server server = Singletons.SERVER;
        PopulateUsers.populate();

        server.startListening();
    }
}
