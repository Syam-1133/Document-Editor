package com.documenteditor.observer;

import com.documenteditor.model.Document;

/**
 * OBSERVER PATTERN: Observer interface
 * Defines the interface for objects that should be notified of document changes.
 */
public interface DocumentObserver {
    /**
     * Called when the observed document is modified.
     * @param document The document that was modified
     */
    void update(Document document);
}
