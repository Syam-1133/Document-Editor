package com.documenteditor.factory;

import com.documenteditor.model.*;

/**
 * FACTORY METHOD PATTERN: Creator interface
 * Defines the factory method for creating document elements.
 */
public abstract class DocumentElementFactory {
    
    /**
     * Factory method to create a document element.
     * @return A new DocumentElement instance
     */
    public abstract DocumentElement createElement();
    
    /**
     * Static factory method to get the appropriate factory.
     * @param type The type of element to create
     * @return The appropriate factory instance
     */
    public static DocumentElementFactory getFactory(String type) {
        switch (type.toLowerCase()) {
            case "paragraph":
                return new ParagraphFactory();
            case "headline":
                return new HeadlineFactory();
            case "image":
                return new ImageFactory();
            default:
                throw new IllegalArgumentException("Unknown element type: " + type);
        }
    }
}
