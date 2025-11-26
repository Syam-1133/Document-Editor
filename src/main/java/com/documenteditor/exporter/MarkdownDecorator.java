package com.documenteditor.exporter;

import com.documenteditor.model.*;
import com.documenteditor.util.Logger;

/**
 * DECORATOR PATTERN: Concrete Decorator
 * Exports document to Markdown format.
 */
public class MarkdownDecorator extends ExporterDecorator {
    private Logger logger;

    public MarkdownDecorator(Exporter exporter) {
        super(exporter);
        this.logger = Logger.getInstance();
    }

    @Override
    public String getContent(Document document) {
        // Override completely to provide Markdown format
        StringBuilder md = new StringBuilder();

        md.append("# ").append(document.getTitle()).append("\n\n");

        for (DocumentElement element : document.getChildren()) {
            if (element instanceof Headline) {
                Headline h = (Headline) element;
                md.append("#".repeat(h.getLevel() + 1)).append(" ");
                md.append(h.getText()).append("\n\n");
            } else if (element instanceof Paragraph) {
                Paragraph p = (Paragraph) element;
                md.append(p.getText()).append("\n\n");
            } else if (element instanceof Image) {
                Image img = (Image) element;
                md.append("![").append(img.getFilename()).append("](");
                md.append(img.getFilename()).append(")\n\n");
            }
        }

        return md.toString();
    }

    @Override
    public void export(Document document, String filename) throws java.io.IOException {
        // Write Markdown content directly (not HTML)
        String content = getContent(document);

        try (java.io.BufferedWriter writer = new java.io.BufferedWriter(new java.io.FileWriter(filename))) {
            writer.write(content);
        }

        logger.log("Document exported to Markdown: " + filename);
    }
}
