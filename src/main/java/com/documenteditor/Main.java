package com.documenteditor;

import com.documenteditor.cli.ConsoleInterface;
import com.documenteditor.util.Logger;

/**
 * Main entry point for the Document Editor application.
 * Demonstrates the use of multiple design patterns in a cohesive system.
 */
public class Main {
    public static void main(String[] args) {
        Logger logger = Logger.getInstance();
        logger.log("Document Editor Application Started");

        ConsoleInterface cli = new ConsoleInterface();
        cli.run();

        logger.log("Document Editor Application Terminated");
        logger.close();
    }
}
