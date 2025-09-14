package com.myshopnet.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class SocketData {
    private final BufferedReader reader;
    private final PrintStream outputStream;
    private final String clientAddress;

    public SocketData(Socket socket) throws IOException {
        this.reader  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.outputStream = new PrintStream(socket.getOutputStream(), true);
        this.clientAddress = socket.getInetAddress() + ":" + socket.getPort();
    }

    public BufferedReader getReader() { return reader; }
    public PrintStream getOutputStream() { return outputStream; }
    public String getClientAddress() { return clientAddress; }
}

