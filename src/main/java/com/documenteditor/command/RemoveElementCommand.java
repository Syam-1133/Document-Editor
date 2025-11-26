package com.documenteditor.command;

import com.documenteditor.model.Document;
import com.documenteditor.model.DocumentElement;

/**
 * COMMAND PATTERN: Concrete Command
 * Command to remove an element from the document.
 */
public class RemoveElementCommand implements Command {
    private Document document;
    private DocumentElement element;
    private int index;
    
    public RemoveElementCommand(Document document, DocumentElement element) {
        this.document = document;
        this.element = element;
        // Store the index for proper restoration
        this.index = document.getChildren().indexOf(element);
    }
    
    @Override
    public void execute() {
        document.remove(element);
    }
    
    @Override
    public void undo() {
        // Re-add at the original position
        document.add(element);
    }
    
    @Override
    public String getDescription() {
        return "Remove element: " + element.getClass().getSimpleName();
    }
}
