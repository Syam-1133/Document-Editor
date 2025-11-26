package com.documenteditor.exporter;

import com.documenteditor.model.Document;
import com.documenteditor.util.Logger;
import com.documenteditor.visitor.SimplePDFExportVisitor;

import java.io.IOException;

/**
 * Decorator Pattern: Adds PDF-style export capability.
 * Wraps any exporter and provides PDF-like document generation.
 * 
 * This decorator adds PDF export functionality without modifying existing exporters.
 * by adding PDF export functionality without modifying existing exporters.
 * 
 * Note: Uses SimplePDFExportVisitor for no-dependency implementation.
 * For production, replace with PDFExportVisitor using iText library (see
 * pom.xml).
 */
public class PDFDecorator extends ExporterDecorator {

    public PDFDecorator(Exporter exporter) {
        super(exporter);
    }

    @Override
    public void export(Document document, String filename) throws IOException {
        Logger logger = Logger.getInstance();

        // Note: SimplePDFExportVisitor creates formatted text, not binary PDF
        // For real PDFs, use PDFExportVisitor.java.maven_only with Maven build
        String pdfFilename = filename;
        if (!filename.endsWith(".pdf") && !filename.endsWith(".txt")) {
            pdfFilename = filename + ".pdf";
        }

        logger.log("Starting PDF export to: " + pdfFilename);

        try {
            // Use Visitor pattern to generate PDF-style document
            SimplePDFExportVisitor pdfVisitor = new SimplePDFExportVisitor(pdfFilename);
            document.accept(pdfVisitor);
            pdfVisitor.close();

            logger.log("PDF export completed successfully: " + pdfFilename);
        } catch (Exception e) {
            logger.error("PDF export failed", e);
            throw new IOException("Failed to export PDF: " + e.getMessage(), e);
        }
    }

    @Override
    public String getContent(Document document) {
        // PDF is binary/formatted, return metadata instead
        return "PDF Document: " + document.getTitle() +
                " (Elements: " + document.getChildren().size() + ")";
    }
}
