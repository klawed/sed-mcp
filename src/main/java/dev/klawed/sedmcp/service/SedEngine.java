package dev.klawed.sedmcp.service;

import dev.klawed.sedmcp.model.SedOperation;
import dev.klawed.sedmcp.model.SedResult;

import java.util.List;

/**
 * Interface for the sed engine because apparently we need to abstract 
 * everything these days. At least this makes testing less painful.
 */
public interface SedEngine {
    
    /**
     * Execute a single sed operation on the given content.
     * 
     * @param content The text to operate on
     * @param operation The sed operation to perform
     * @return Result containing the modified text and operation details
     * @throws SedEngineException if the operation fails spectacularly
     */
    SedResult executeOperation(String content, SedOperation operation);
    
    /**
     * Execute multiple sed operations in sequence. Because one operation 
     * is never enough in the world of text manipulation.
     * 
     * @param content The text to operate on
     * @param operations List of operations to perform in order
     * @return Result containing the final modified text
     * @throws SedEngineException if any operation decides to misbehave
     */
    SedResult executeBatch(String content, List<SedOperation> operations);
    
    /**
     * Preview what an operation would do without actually doing it.
     * For the cautious types who don't like surprises.
     * 
     * @param content The text to preview changes on
     * @param operation The operation to preview
     * @return Result showing what would happen
     * @throws SedEngineException if even previewing manages to fail
     */
    SedResult previewOperation(String content, SedOperation operation);
    
    /**
     * Validate that a sed operation is syntactically correct.
     * Because we'd rather catch nonsense early than deal with it later.
     * 
     * @param operation The operation to validate
     * @throws SedEngineException if the operation is malformed
     */
    void validateOperation(SedOperation operation);
    
    /**
     * Check if the engine supports a specific operation type.
     * Not all sed engines are created equal, sadly.
     * 
     * @param operationType The operation type to check
     * @return true if supported, false if you're out of luck
     */
    boolean supportsOperation(SedOperation.OperationType operationType);
}
