package com.documenteditor.exporter;

import com.documenteditor.model.Document;
import java.io.IOException;

/**
 * DECORATOR PATTERN: Decorator base class
 * Abstract decorator for adding functionality to exporters.
 */
public abstract class ExporterDecorator implements Exporter {
    protected Exporter wrappedExporter;
    
    public ExporterDecorator(Exporter exporter) {
        this.wrappedExporter = exporter;
    }
    
    @Override
    public void export(Document document, String filename) throws IOException {
        wrappedExporter.export(document, filename);
    }
    
    @Override
    public String getContent(Document document) {
        return wrappedExporter.getContent(document);
    }
}
