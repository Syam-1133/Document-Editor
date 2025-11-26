package com.documenteditor.model;

import com.documenteditor.visitor.Visitor;
import java.util.HashMap;
import java.util.Map;

/**
 * COMPOSITE PATTERN: Leaf class
 * Represents an image element with filename and dimensions.
 */
public class Image implements DocumentElement {
    private String filename;
    private int width;
    private int height;
    
    public Image(String filename, int width, int height) {
        this.filename = filename;
        this.width = width;
        this.height = height;
    }
    
    @Override
    public void accept(Visitor visitor) {
        visitor.visitImage(this);
    }
    
    @Override
    public String render() {
        return String.format("[Image: %s (%dx%d)]", filename, width, height);
    }
    
    @Override
    public Object toSerializable() {
        Map<String, Object> data = new HashMap<>();
        data.put("type", "Image");
        data.put("filename", filename);
        data.put("width", width);
        data.put("height", height);
        return data;
    }
    
    public String getFilename() {
        return filename;
    }
    
    public void setFilename(String filename) {
        this.filename = filename;
    }
    
    public int getWidth() {
        return width;
    }
    
    public void setWidth(int width) {
        this.width = width;
    }
    
    public int getHeight() {
        return height;
    }
    
    public void setHeight(int height) {
        this.height = height;
    }
}
