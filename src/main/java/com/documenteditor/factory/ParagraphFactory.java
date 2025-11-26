package com.documenteditor.factory;

import com.documenteditor.model.DocumentElement;
import com.documenteditor.model.Paragraph;

/**
 * FACTORY METHOD PATTERN: Concrete Creator
 * Factory for creating Paragraph elements.
 */
public class ParagraphFactory extends DocumentElementFactory {
    private String text;

    public ParagraphFactory() {
        this.text = "";
    }

    public ParagraphFactory setText(String text) {
        this.text = text;
        return this;
    }

    @Override
    public DocumentElement createElement() {
        return new Paragraph(text);
    }
}
