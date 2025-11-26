package com.documenteditor.exporter;

import com.documenteditor.model.*;
import com.documenteditor.util.Logger;

/**
 * DECORATOR PATTERN: Concrete Decorator
 * Exports document to plain text format.
 */
public class PlainTextDecorator extends ExporterDecorator {
    private Logger logger;

    public PlainTextDecorator(Exporter exporter) {
        super(exporter);
        this.logger = Logger.getInstance();
    }

    @Override
    public String getContent(Document document) {
        // Override completely to provide plain text format
        StringBuilder text = new StringBuilder();

        text.append(document.getTitle().toUpperCase()).append("\n");
        text.append("=".repeat(document.getTitle().length())).append("\n\n");

        for (DocumentElement element : document.getChildren()) {
            if (element instanceof Headline) {
                Headline h = (Headline) element;
                text.append("\n").append(h.getText().toUpperCase()).append("\n");
                text.append("-".repeat(h.getText().length())).append("\n\n");
            } else if (element instanceof Paragraph) {
                Paragraph p = (Paragraph) element;
                text.append(p.getText()).append("\n\n");
            } else if (element instanceof Image) {
                Image img = (Image) element;
                text.append("[IMAGE: ").append(img.getFilename());
                text.append(" (").append(img.getWidth()).append("x");
                text.append(img.getHeight()).append(")]\n\n");
            }
        }

        return text.toString();
    }

    @Override
    public void export(Document document, String filename) throws java.io.IOException {
        // Write plain text content directly (not HTML)
        String content = getContent(document);

        try (java.io.BufferedWriter writer = new java.io.BufferedWriter(new java.io.FileWriter(filename))) {
            writer.write(content);
        }

        logger.log("Document exported to Plain Text: " + filename);
    }
}
