package com.myshopnet;

import java.util.Scanner;
import java.util.function.Consumer;

public class InputManager {
    private static final Scanner scanner = new Scanner(System.in);
    private static volatile Consumer<String> currentConsumer;

    public static synchronized void requestInput(Consumer<String> consumer) {
        currentConsumer = consumer;
    }

    public static synchronized void clearConsumer() {
        currentConsumer = null;
    }
}