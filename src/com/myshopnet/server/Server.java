package com.myshopnet.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.myshopnet.chat.PushServer;
import com.myshopnet.logs.LogEvent;
import com.myshopnet.logs.LogType;
import com.myshopnet.logs.Logger;
import com.myshopnet.utils.Singletons;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private final Logger logger = Singletons.LOGGER;
    private static final int PORT = 8080;
    private final Gson gson = new Gson();
    private BufferedReader in;
    private PrintWriter out;

    public void startListening() throws IOException {
        try {
            Thread pushThread = new Thread(new PushServer());
            pushThread.setDaemon(true);
            pushThread.start();
        } catch (Exception ignored) { }
        try (ServerSocket server = new ServerSocket(PORT)) {
            logger.log(new LogEvent(LogType.SERVER_LISTEN, "* Server up on " + PORT + ". Waiting for clients..."));
            while (true) {
                System.out.println("Waiting for clients...");

                Socket socket = server.accept();

                logger.log(new LogEvent(LogType.REQUEST_RECIEVED, "Request from " + socket.getRemoteSocketAddress() + " recieved"));

                new Thread(() -> {
                    try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
                        String requestData;

                        while ((requestData = in.readLine()) != null) {
                            if (requestData.trim().isEmpty()) {
                                continue;
                            }

                            try {
                                Request request = gson.fromJson(requestData, Request.class);
                                String response = RequestHandler.handleRequest(request);
                                out.println(response);
                                out.flush();
                            }
                            catch (Exception ex) {
                                out.println("{\"error\":\"Invalid request\"}");
                                out.flush();
                                logger.log(new LogEvent(LogType.REQUEST_RECIEVED, "Bad request: " + ex.getMessage()));
                            }
                        }


                    }
                    catch (IOException e) {
                        logger.log(new LogEvent(LogType.SERVER_LISTEN, e.getMessage()));
                    }
                }).start();
            }
        }
    }
}
