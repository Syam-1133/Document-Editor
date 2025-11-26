package com.documenteditor.strategy;

/**
 * STRATEGY PATTERN: Strategy interface
 * Defines the interface for word counting strategies.
 */
public interface WordCountStrategy {
    /**
     * Count words in the given text.
     * @param text The text to count words in
     * @return The number of words
     */
    int countWords(String text);
}
