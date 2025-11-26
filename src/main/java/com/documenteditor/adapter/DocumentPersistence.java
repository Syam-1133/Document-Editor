package com.documenteditor.adapter;

import com.documenteditor.model.Document;

/**
 * ADAPTER PATTERN: Target interface
 * Defines the interface our application expects for document persistence.
 */
public interface DocumentPersistence {
    /**
     * Save document to a file.
     * @param document The document to save
     * @param filename The filename to save to
     * @return true if successful, false otherwise
     */
    boolean save(Document document, String filename);
    
    /**
     * Load document from a file.
     * @param filename The filename to load from
     * @return The loaded document, or null if failed
     */
    Document load(String filename);
}
