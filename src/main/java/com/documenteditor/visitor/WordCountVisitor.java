package com.documenteditor.visitor;

import com.documenteditor.model.*;
import com.documenteditor.strategy.WordCountStrategy;

/**
 * VISITOR PATTERN: Concrete Visitor
 * STRATEGY PATTERN: Context (uses WordCountStrategy)
 * Counts words in the document using a pluggable counting strategy.
 */
public class WordCountVisitor implements Visitor {
    private int wordCount;
    private WordCountStrategy strategy;
    
    public WordCountVisitor(WordCountStrategy strategy) {
        this.strategy = strategy;
        this.wordCount = 0;
    }
    
    @Override
    public void visitDocument(Document document) {
        wordCount = 0;
        for (DocumentElement element : document.getChildren()) {
            element.accept(this);
        }
    }
    
    @Override
    public void visitParagraph(Paragraph paragraph) {
        wordCount += strategy.countWords(paragraph.getText());
    }
    
    @Override
    public void visitHeadline(Headline headline) {
        wordCount += strategy.countWords(headline.getText());
    }
    
    @Override
    public void visitImage(Image image) {
        // Images don't contribute to word count
    }
    
    public int getWordCount() {
        return wordCount;
    }
    
    public void reset() {
        wordCount = 0;
    }
}
