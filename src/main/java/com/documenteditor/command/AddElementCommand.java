package com.documenteditor.command;

import com.documenteditor.model.Document;
import com.documenteditor.model.DocumentElement;

/**
 * COMMAND PATTERN: Concrete Command
 * Command to add an element to the document.
 */
public class AddElementCommand implements Command {
    private Document document;
    private DocumentElement element;
    
    public AddElementCommand(Document document, DocumentElement element) {
        this.document = document;
        this.element = element;
    }
    
    @Override
    public void execute() {
        document.add(element);
    }
    
    @Override
    public void undo() {
        document.remove(element);
    }
    
    @Override
    public String getDescription() {
        return "Add element: " + element.getClass().getSimpleName();
    }
}
