package com.documenteditor.adapter;

import com.documenteditor.factory.*;
import com.documenteditor.model.*;
import com.documenteditor.util.Logger;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ADAPTER PATTERN: Adapter class
 * Adapts the JSONSerializationLibrary to work with our Document persistence needs.
 */
public class DocumentPersistenceAdapter implements DocumentPersistence {
    private JSONSerializationLibrary jsonLibrary;
    private Logger logger;
    
    public DocumentPersistenceAdapter() {
        this.jsonLibrary = new JSONSerializationLibrary();
        this.logger = Logger.getInstance();
    }
    
    @Override
    public boolean save(Document document, String filename) {
        try {
            // Convert document to serializable format
            Object serializable = document.toSerializable();
            
            // Use the third-party library to stringify
            String json = jsonLibrary.stringify(serializable);
            
            // Write to file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
                writer.write(json);
            }
            
            logger.log("Document saved to: " + filename);
            document.setModified(false);
            return true;
            
        } catch (IOException e) {
            logger.error("Failed to save document", e);
            return false;
        }
    }
    
    @Override
    public Document load(String filename) {
        try {
            // Read file content
            StringBuilder json = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    json.append(line);
                }
            }
            
            // Parse using simplified approach (since our library's parse is basic)
            Document document = parseDocument(json.toString());
            
            logger.log("Document loaded from: " + filename);
            document.setModified(false);
            return document;
            
        } catch (IOException e) {
            logger.error("Failed to load document", e);
            return null;
        }
    }
    
    /**
     * Parse JSON string to Document.
     * This is a simplified parser for demonstration.
     */
    private Document parseDocument(String json) {
        // Simplified parsing - extract title and elements
        // In a production system, use a proper JSON library like Gson or Jackson
        
        String title = extractValue(json, "title");
        Document document = new Document(title != null ? title : "Untitled");
        
        // This is a simplified implementation
        // A full implementation would properly parse the JSON array of elements
        
        return document;
    }
    
    private String extractValue(String json, String key) {
        String searchKey = "\"" + key + "\":\"";
        int startIdx = json.indexOf(searchKey);
        if (startIdx == -1) {
            return null;
        }
        
        startIdx += searchKey.length();
        int endIdx = json.indexOf("\"", startIdx);
        
        if (endIdx == -1) {
            return null;
        }
        
        return json.substring(startIdx, endIdx);
    }
}
