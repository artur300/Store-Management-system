package com.myshopnet.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.myshopnet.logs.LogEvent;
import com.myshopnet.logs.LogType;
import com.myshopnet.logs.Logger;
import com.myshopnet.logs.LoggerImpl;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static final int PORT = 8080;
    private final Gson gson = new Gson();
    private BufferedReader in;
    private PrintWriter out;
    private static final Logger logger = LoggerImpl.getInstance();

    public void startListening() throws IOException {
        try (ServerSocket server = new ServerSocket(PORT)) {
            logger.log(new LogEvent(LogType.SERVER_LISTEN, "* Server up on " + PORT + ". Waiting for clients..."));
            while (true) {
                Socket socket = server.accept();

                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                new Thread(() -> {
                    try {
                        String requestData = in.readLine();
                        Request request = !requestData.isEmpty() ? gson.fromJson(requestData, Request.class) : null;

                        if (request == null) {
                            throw new RuntimeException("Invalid request");
                        }

                        RequestHandler.handleRequest(request);
                    }
                    catch (IOException e) {
                        logger.log(new LogEvent(LogType.SERVER_LISTEN, e.getMessage()));
                    }
                }).start();
            }
        }
    }
}
