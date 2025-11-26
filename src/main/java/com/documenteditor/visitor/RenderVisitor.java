package com.documenteditor.visitor;

import com.documenteditor.model.*;

/**
 * VISITOR PATTERN: Concrete Visitor
 * Renders document to console-friendly format.
 */
public class RenderVisitor implements Visitor {
    private StringBuilder output;
    
    public RenderVisitor() {
        this.output = new StringBuilder();
    }
    
    @Override
    public void visitDocument(Document document) {
        output.setLength(0); // Reset
        output.append("=".repeat(60)).append("\n");
        output.append("Document: ").append(document.getTitle()).append("\n");
        output.append("=".repeat(60)).append("\n\n");
        
        for (DocumentElement element : document.getChildren()) {
            element.accept(this);
            output.append("\n");
        }
    }
    
    @Override
    public void visitParagraph(Paragraph paragraph) {
        output.append(paragraph.getText()).append("\n");
    }
    
    @Override
    public void visitHeadline(Headline headline) {
        String prefix = "#".repeat(headline.getLevel());
        output.append(prefix).append(" ").append(headline.getText()).append("\n");
    }
    
    @Override
    public void visitImage(Image image) {
        output.append("[Image: ")
              .append(image.getFilename())
              .append(" (")
              .append(image.getWidth())
              .append("x")
              .append(image.getHeight())
              .append(")]\n");
    }
    
    public String getOutput() {
        return output.toString();
    }
}
