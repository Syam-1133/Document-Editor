package com.documenteditor.model;

import com.documenteditor.visitor.Visitor;
import java.util.HashMap;
import java.util.Map;

/**
 * COMPOSITE PATTERN: Leaf class
 * Represents a headline element with a level (1-3).
 */
public class Headline implements DocumentElement {
    private String text;
    private int level; // 1, 2, or 3

    public Headline(String text, int level) {
        this.text = text;
        this.level = Math.max(1, Math.min(3, level)); // Clamp between 1 and 3
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitHeadline(this);
    }

    @Override
    public String render() {
        String prefix = "#".repeat(level);
        return prefix + " " + text;
    }

    @Override
    public Object toSerializable() {
        Map<String, Object> data = new HashMap<>();
        data.put("type", "Headline");
        data.put("text", text);
        data.put("level", level);
        return data;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = Math.max(1, Math.min(3, level));
    }
}
