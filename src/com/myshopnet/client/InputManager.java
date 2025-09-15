package com.myshopnet.client;

import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class InputManager {
    private static final InputManager INSTANCE = new InputManager();
    private final BlockingQueue<String> inputQueue = new LinkedBlockingQueue<>();
    private final Scanner scanner = new Scanner(System.in);
    private volatile boolean shutdown = false;
    private Thread readerThread;

    private InputManager() {
        startReader();
    }

    public static InputManager getInstance() {
        return INSTANCE;
    }

    private void startReader() {
        readerThread = new Thread(() -> {
            while (!shutdown && !Thread.currentThread().isInterrupted()) {
                try {
                    if (System.in.available() > 0 && scanner.hasNextLine()) {
                        String line = scanner.nextLine();
                        if (line != null) {
                            inputQueue.offer(line);
                        }
                    } else {
                        Thread.sleep(50);
                    }
                } catch (Exception e) {
                    if (!shutdown) {
                        System.err.println("Error reading input: " + e.getMessage());
                    }
                }
            }
            // Do not close System.in here; keep scanner open for app lifetime
        }, "InputReaderThread");
        readerThread.setDaemon(true);
        readerThread.start();
    }

    public void clearQueue() {
        inputQueue.clear();
    }

    public String pollInput() {
        return inputQueue.poll();
    }

    public String pollInput(long timeout, TimeUnit unit) throws InterruptedException {
        return inputQueue.poll(timeout, unit);
    }

    public String takeInput() throws InterruptedException {
        return inputQueue.take();
    }

    public void shutdown() {
        shutdown = true;
        try {
            if (readerThread != null && readerThread.isAlive()) {
                readerThread.interrupt();
                readerThread.join(500);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
