package com.documenteditor.command;

import com.documenteditor.util.Logger;
import java.util.Stack;

/**
 * COMMAND PATTERN: Invoker
 * Manages command execution history and provides undo/redo functionality.
 */
public class CommandHistory {
    private Stack<Command> undoStack;
    private Stack<Command> redoStack;
    private Logger logger;
    
    public CommandHistory() {
        this.undoStack = new Stack<>();
        this.redoStack = new Stack<>();
        this.logger = Logger.getInstance();
    }
    
    /**
     * Execute a command and add it to the history.
     * @param command The command to execute
     */
    public void executeCommand(Command command) {
        command.execute();
        undoStack.push(command);
        redoStack.clear(); // Clear redo stack when new command is executed
        logger.log("Command executed: " + command.getDescription());
    }
    
    /**
     * Undo the last command.
     * @return true if undo was successful, false if nothing to undo
     */
    public boolean undo() {
        if (undoStack.isEmpty()) {
            return false;
        }
        
        Command command = undoStack.pop();
        command.undo();
        redoStack.push(command);
        logger.log("Command undone: " + command.getDescription());
        return true;
    }
    
    /**
     * Redo the last undone command.
     * @return true if redo was successful, false if nothing to redo
     */
    public boolean redo() {
        if (redoStack.isEmpty()) {
            return false;
        }
        
        Command command = redoStack.pop();
        command.execute();
        undoStack.push(command);
        logger.log("Command redone: " + command.getDescription());
        return true;
    }
    
    /**
     * Check if undo is available.
     * @return true if there are commands to undo
     */
    public boolean canUndo() {
        return !undoStack.isEmpty();
    }
    
    /**
     * Check if redo is available.
     * @return true if there are commands to redo
     */
    public boolean canRedo() {
        return !redoStack.isEmpty();
    }
    
    /**
     * Get the number of commands that can be undone.
     * @return The size of the undo stack
     */
    public int getUndoCount() {
        return undoStack.size();
    }
    
    /**
     * Get the number of commands that can be redone.
     * @return The size of the redo stack
     */
    public int getRedoCount() {
        return redoStack.size();
    }
    
    /**
     * Clear all command history.
     */
    public void clear() {
        undoStack.clear();
        redoStack.clear();
        logger.log("Command history cleared");
    }
}
