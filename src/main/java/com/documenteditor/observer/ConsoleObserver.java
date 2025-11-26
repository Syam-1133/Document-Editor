package com.documenteditor.observer;

import com.documenteditor.model.Document;
import com.documenteditor.util.Logger;

/**
 * OBSERVER PATTERN: Concrete Observer
 * Observes document changes and updates the console UI.
 */
public class ConsoleObserver implements DocumentObserver {
    private Logger logger;
    
    public ConsoleObserver() {
        this.logger = Logger.getInstance();
    }
    
    @Override
    public void update(Document document) {
        if (document.isModified()) {
            logger.log("Document '" + document.getTitle() + "' has been modified");
            System.out.println("[DOCUMENT MODIFIED] " + document.getTitle() + 
                             " (Elements: " + document.getElementCount() + ")");
        }
    }
}
