package com.documenteditor.visitor;

import com.documenteditor.model.*;

/**
 * VISITOR PATTERN: Concrete Visitor
 * Exports document to HTML format.
 */
public class HTMLExportVisitor implements Visitor {
    private StringBuilder html;
    
    public HTMLExportVisitor() {
        this.html = new StringBuilder();
    }
    
    @Override
    public void visitDocument(Document document) {
        html.setLength(0); // Reset
        html.append("<!DOCTYPE html>\n");
        html.append("<html>\n<head>\n");
        html.append("<title>").append(escapeHtml(document.getTitle())).append("</title>\n");
        html.append("</head>\n<body>\n");
        
        for (DocumentElement element : document.getChildren()) {
            element.accept(this);
        }
        
        html.append("</body>\n</html>");
    }
    
    @Override
    public void visitParagraph(Paragraph paragraph) {
        html.append("<p>").append(escapeHtml(paragraph.getText())).append("</p>\n");
    }
    
    @Override
    public void visitHeadline(Headline headline) {
        int level = headline.getLevel();
        html.append("<h").append(level).append(">");
        html.append(escapeHtml(headline.getText()));
        html.append("</h").append(level).append(">\n");
    }
    
    @Override
    public void visitImage(Image image) {
        html.append("<img src=\"").append(escapeHtml(image.getFilename())).append("\" ");
        html.append("width=\"").append(image.getWidth()).append("\" ");
        html.append("height=\"").append(image.getHeight()).append("\" ");
        html.append("alt=\"").append(escapeHtml(image.getFilename())).append("\" />\n");
    }
    
    public String getHTML() {
        return html.toString();
    }
    
    private String escapeHtml(String text) {
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");
    }
}
