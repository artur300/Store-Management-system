package com.myshopnet.logs;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class LoggerImpl implements Logger {
    private static final LoggerImpl INSTANCE = new LoggerImpl();
    private final PrintWriter out;
    private final DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private LoggerImpl() {
        try {
            Path p = Paths.get("app.log"); // ייכתב ליד ההרצה
            out = new PrintWriter(new BufferedWriter(new FileWriter(p.toFile(), true)), true);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static LoggerImpl getInstance() { return INSTANCE; }

    public synchronized void log(LogEvent ev) {
        String line = "[" + fmt.format(LocalDateTime.now()) + "] "
                + ev.getType() + " - " + ev.getMessage();
        out.println(line);
        System.out.println(line); // גם לקונסול
    }
}

