package com.documenteditor.visitor;

import com.documenteditor.model.*;

/**
 * VISITOR PATTERN: Visitor interface
 * Defines visit methods for each element type in the document structure.
 */
public interface Visitor {
    void visitDocument(Document document);
    void visitParagraph(Paragraph paragraph);
    void visitHeadline(Headline headline);
    void visitImage(Image image);
}
