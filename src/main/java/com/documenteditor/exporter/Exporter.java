package com.documenteditor.exporter;

import com.documenteditor.model.Document;
import java.io.IOException;

/**
 * DECORATOR PATTERN: Component interface
 * Base interface for document exporters.
 */
public interface Exporter {
    /**
     * Export the document to a file.
     * @param document The document to export
     * @param filename The output filename
     * @throws IOException If an I/O error occurs
     */
    void export(Document document, String filename) throws IOException;
    
    /**
     * Get the exported content as a string.
     * @param document The document to export
     * @return The exported content
     */
    String getContent(Document document);
}
