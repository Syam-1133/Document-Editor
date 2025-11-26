package com.documenteditor.factory;

import com.documenteditor.model.DocumentElement;
import com.documenteditor.model.Image;

/**
 * FACTORY METHOD PATTERN: Concrete Creator
 * Factory for creating Image elements.
 */
public class ImageFactory extends DocumentElementFactory {
    private String filename;
    private int width;
    private int height;
    
    public ImageFactory() {
        this.filename = "";
        this.width = 100;
        this.height = 100;
    }
    
    public ImageFactory setFilename(String filename) {
        this.filename = filename;
        return this;
    }
    
    public ImageFactory setWidth(int width) {
        this.width = width;
        return this;
    }
    
    public ImageFactory setHeight(int height) {
        this.height = height;
        return this;
    }
    
    @Override
    public DocumentElement createElement() {
        return new Image(filename, width, height);
    }
}
