package com.documenteditor.factory;

import com.documenteditor.model.DocumentElement;
import com.documenteditor.model.Headline;

/**
 * FACTORY METHOD PATTERN: Concrete Creator
 * Factory for creating Headline elements.
 */
public class HeadlineFactory extends DocumentElementFactory {
    private String text;
    private int level;
    
    public HeadlineFactory() {
        this.text = "";
        this.level = 1;
    }
    
    public HeadlineFactory setText(String text) {
        this.text = text;
        return this;
    }
    
    public HeadlineFactory setLevel(int level) {
        this.level = level;
        return this;
    }
    
    @Override
    public DocumentElement createElement() {
        return new Headline(text, level);
    }
}
