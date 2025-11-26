package com.documenteditor.command;

/**
 * COMMAND PATTERN: Command interface
 * Represents an action that can be executed, undone, and redone.
 */
public interface Command {
    /**
     * Execute the command.
     */
    void execute();
    
    /**
     * Undo the command.
     */
    void undo();
    
    /**
     * Get a description of the command for logging.
     * @return A string description
     */
    String getDescription();
}
