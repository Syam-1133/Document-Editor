package com.documenteditor.exporter;

import com.documenteditor.model.Document;
import com.documenteditor.util.Logger;
import com.documenteditor.visitor.HTMLExportVisitor;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * DECORATOR PATTERN: Concrete Component
 * Basic HTML exporter that provides core HTML export functionality.
 */
public class BasicHTMLExporter implements Exporter {
    protected Logger logger;
    
    public BasicHTMLExporter() {
        this.logger = Logger.getInstance();
    }
    
    @Override
    public void export(Document document, String filename) throws IOException {
        String content = getContent(document);
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(content);
        }
        
        logger.log("Document exported to HTML: " + filename);
    }
    
    @Override
    public String getContent(Document document) {
        HTMLExportVisitor visitor = new HTMLExportVisitor();
        document.accept(visitor);
        return visitor.getHTML();
    }
}
