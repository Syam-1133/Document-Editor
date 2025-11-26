package com.documenteditor.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * SINGLETON PATTERN: Singleton class
 * Thread-safe logger for the application.
 * Provides a global point of access for logging to console and file.
 */
public class Logger {
    private static volatile Logger instance;
    private static final Object lock = new Object();
    private PrintWriter fileWriter;
    private DateTimeFormatter formatter;
    private static final String LOG_FILE = "document_editor.log";

    /**
     * Private constructor to prevent direct instantiation.
     */
    private Logger() {
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        try {
            fileWriter = new PrintWriter(new BufferedWriter(new FileWriter(LOG_FILE, true)), true);
            System.out.println("Logger initialized successfully. Log file: " + LOG_FILE);
        } catch (IOException e) {
            System.err.println("CRITICAL: Failed to initialize logger file: " + e.getMessage());
            System.err.println("Logs will only be written to console.");
            e.printStackTrace();
        }
    }

    /**
     * Get the singleton instance (thread-safe with double-checked locking).
     * 
     * @return The Logger instance
     */
    public static Logger getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new Logger();
                }
            }
        }
        return instance;
    }

    /**
     * Log a message to both console and file.
     * 
     * @param message The message to log
     */
    public synchronized void log(String message) {
        String timestamp = LocalDateTime.now().format(formatter);
        String logEntry = String.format("[%s] %s", timestamp, message);

        // Log to console
        System.out.println(logEntry);

        // Log to file
        if (fileWriter != null) {
            fileWriter.println(logEntry);
        }
    }

    /**
     * Log an error message.
     * 
     * @param message The error message
     */
    public synchronized void error(String message) {
        String timestamp = LocalDateTime.now().format(formatter);
        String logEntry = String.format("[%s] ERROR: %s", timestamp, message);

        // Log to console (stderr)
        System.err.println(logEntry);

        // Log to file
        if (fileWriter != null) {
            fileWriter.println(logEntry);
        }
    }

    /**
     * Log an exception.
     * 
     * @param message The message
     * @param e       The exception
     */
    public synchronized void error(String message, Exception e) {
        error(message + " - " + e.getMessage());
    }

    /**
     * Close the logger and release resources.
     */
    public synchronized void close() {
        if (fileWriter != null) {
            fileWriter.close();
        }
    }
}
