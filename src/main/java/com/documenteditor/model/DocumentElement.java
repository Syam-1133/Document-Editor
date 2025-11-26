package com.documenteditor.model;

import com.documenteditor.visitor.Visitor;
import java.util.List;
import java.util.ArrayList;

/**
 * COMPOSITE PATTERN: Component interface
 * Base interface for all document elements.
 * Allows uniform treatment of individual elements and composites.
 */
public interface DocumentElement {
    /**
     * Accept a visitor for the Visitor pattern.
     * @param visitor The visitor to accept
     */
    void accept(Visitor visitor);
    
    /**
     * Render the element to console format.
     * @return String representation of the element
     */
    String render();
    
    /**
     * Add a child element (only applicable to composite elements).
     * @param element The element to add
     */
    default void add(DocumentElement element) {
        throw new UnsupportedOperationException("Cannot add children to this element");
    }
    
    /**
     * Remove a child element (only applicable to composite elements).
     * @param element The element to remove
     */
    default void remove(DocumentElement element) {
        throw new UnsupportedOperationException("Cannot remove children from this element");
    }
    
    /**
     * Get all children (only applicable to composite elements).
     * @return List of child elements
     */
    default List<DocumentElement> getChildren() {
        return new ArrayList<>();
    }
    
    /**
     * Check if this element is a composite (has children).
     * @return true if composite, false otherwise
     */
    default boolean isComposite() {
        return false;
    }
    
    /**
     * Get a JSON-friendly representation for serialization.
     * @return JSON-compatible object representation
     */
    Object toSerializable();
}
