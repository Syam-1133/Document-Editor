package com.documenteditor.strategy;

/**
 * STRATEGY PATTERN: Concrete Strategy
 * Basic word counting strategy that splits on whitespace.
 */
public class BasicWordCountStrategy implements WordCountStrategy {
    
    @Override
    public int countWords(String text) {
        if (text == null || text.trim().isEmpty()) {
            return 0;
        }
        
        // Split on whitespace and count non-empty strings
        String[] words = text.trim().split("\\s+");
        return words.length;
    }
}
