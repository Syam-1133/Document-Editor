package com.documenteditor.model;

import com.documenteditor.visitor.Visitor;
import java.util.HashMap;
import java.util.Map;

/**
 * COMPOSITE PATTERN: Leaf class
 * Represents a paragraph element in the document.
 */
public class Paragraph implements DocumentElement {
    private String text;
    
    public Paragraph(String text) {
        this.text = text;
    }
    
    @Override
    public void accept(Visitor visitor) {
        visitor.visitParagraph(this);
    }
    
    @Override
    public String render() {
        return text;
    }
    
    @Override
    public Object toSerializable() {
        Map<String, Object> data = new HashMap<>();
        data.put("type", "Paragraph");
        data.put("text", text);
        return data;
    }
    
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
}
