package com.documenteditor.model;

import com.documenteditor.observer.DocumentObserver;
import com.documenteditor.visitor.Visitor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * COMPOSITE PATTERN: Composite class
 * OBSERVER PATTERN: Subject class
 * Represents a document that can contain multiple elements.
 */
public class Document implements DocumentElement {
    private List<DocumentElement> elements;
    private List<DocumentObserver> observers;
    private String title;
    private boolean modified;

    public Document(String title) {
        this.title = title;
        this.elements = new ArrayList<>();
        this.observers = new ArrayList<>();
        this.modified = false;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitDocument(this);
    }

    @Override
    public String render() {
        StringBuilder sb = new StringBuilder();
        sb.append("=".repeat(50)).append("\n");
        sb.append("Document: ").append(title).append("\n");
        sb.append("=".repeat(50)).append("\n\n");

        for (DocumentElement element : elements) {
            sb.append(element.render()).append("\n");
        }

        return sb.toString();
    }

    @Override
    public void add(DocumentElement element) {
        elements.add(element);
        setModified(true);
        notifyObservers();
    }

    @Override
    public void remove(DocumentElement element) {
        elements.remove(element);
        setModified(true);
        notifyObservers();
    }

    @Override
    public List<DocumentElement> getChildren() {
        return new ArrayList<>(elements);
    }

    @Override
    public boolean isComposite() {
        return true;
    }

    @Override
    public Object toSerializable() {
        Map<String, Object> data = new HashMap<>();
        data.put("type", "Document");
        data.put("title", title);

        List<Object> childData = new ArrayList<>();
        for (DocumentElement element : elements) {
            childData.add(element.toSerializable());
        }
        data.put("elements", childData);

        return data;
    }

    // Observer pattern methods
    public void attach(DocumentObserver observer) {
        observers.add(observer);
    }

    public void detach(DocumentObserver observer) {
        observers.remove(observer);
    }

    private void notifyObservers() {
        for (DocumentObserver observer : observers) {
            observer.update(this);
        }
    }

    // Getters and setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        setModified(true);
    }

    public boolean isModified() {
        return modified;
    }

    public void setModified(boolean modified) {
        this.modified = modified;
    }

    public int getElementCount() {
        return elements.size();
    }
}
