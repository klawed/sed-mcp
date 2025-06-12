package dev.klawed.sedmcp.service.impl;

import dev.klawed.sedmcp.model.SedOperation;
import dev.klawed.sedmcp.model.SedResult;
import dev.klawed.sedmcp.service.SedEngine;
import dev.klawed.sedmcp.service.SedEngineException;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Mock implementation of SedEngine for testing purposes.
 * Because sometimes you need to pretend things work before they actually do.
 */
public class MockSedEngine implements SedEngine {
    
    private boolean shouldFail = false;
    private String failureMessage = "Mock failure";
    private long artificialDelay = 0;
    
    public void setShouldFail(boolean shouldFail) {
        this.shouldFail = shouldFail;
    }
    
    public void setFailureMessage(String failureMessage) {
        this.failureMessage = failureMessage;
    }
    
    public void setArtificialDelay(long delayMs) {
        this.artificialDelay = delayMs;
    }
    
    @Override
    public SedResult executeOperation(String content, SedOperation operation) {
        simulateWork();
        
        if (shouldFail) {
            throw new SedEngineException(failureMessage, operation.getOperation().getCommand(), operation.getPattern());
        }
        
        // Simulate some basic operations for testing
        String result = simulateOperation(content, operation);
        
        return SedResult.builder()
                .originalContent(content)
                .modifiedContent(result)
                .success(true)
                .addChange("Applied " + operation.getOperation() + " operation")
                .linesModified(countLines(result) - countLines(content))
                .executionTimeMs(artificialDelay)
                .build();
    }
    
    @Override
    public SedResult executeBatch(String content, List<SedOperation> operations) {
        simulateWork();
        
        if (shouldFail) {
            throw new SedEngineException(failureMessage);
        }
        
        String result = content;
        SedResult.Builder builder = SedResult.builder()
                .originalContent(content)
                .success(true)
                .executionTimeMs(artificialDelay * operations.size());
        
        for (SedOperation op : operations) {
            result = simulateOperation(result, op);
            builder.addChange("Applied " + op.getOperation() + " operation");
        }
        
        return builder
                .modifiedContent(result)
                .linesModified(countLines(result) - countLines(content))
                .build();
    }
    
    @Override
    public SedResult previewOperation(String content, SedOperation operation) {
        // Preview is just execute without side effects in this mock
        return executeOperation(content, operation);
    }
    
    @Override
    public void validateOperation(SedOperation operation) {
        if (shouldFail) {
            throw new SedEngineException("Mock validation failure: " + failureMessage);
        }
        
        // Simulate some basic validation
        if (operation.getOperation() == SedOperation.OperationType.SUBSTITUTE) {
            if (operation.getPattern() == null || operation.getReplacement() == null) {
                throw new SedEngineException("Substitute operation requires pattern and replacement");
            }
        }
    }
    
    @Override
    public boolean supportsOperation(SedOperation.OperationType operationType) {
        // Mock supports all operations because why not
        return true;
    }
    
    private void simulateWork() {
        if (artificialDelay > 0) {
            try {
                Thread.sleep(artificialDelay);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new SedEngineException("Mock operation interrupted");
            }
        }
    }
    
    private String simulateOperation(String content, SedOperation operation) {
        // Handle null content gracefully
        if (content == null) {
            content = "";
        }
        
        // Very basic simulation of sed operations for testing
        switch (operation.getOperation()) {
            case SUBSTITUTE:
                if (operation.getPattern() != null && operation.getReplacement() != null) {
                    String flags = operation.getFlags();
                    if (flags != null && flags.contains("g")) {
                        return content.replaceAll(operation.getPattern(), operation.getReplacement());
                    } else {
                        return content.replaceFirst(operation.getPattern(), operation.getReplacement());
                    }
                }
                return content;
                
            case DELETE:
                if (operation.getPattern() != null) {
                    return content.replaceAll(".*" + operation.getPattern() + ".*\\n?", "");
                }
                return content;
                
            case INSERT:
                if (operation.getText() != null) {
                    return operation.getText() + "\\n" + content;
                }
                return content;
                
            case APPEND:
                if (operation.getText() != null) {
                    return content + "\\n" + operation.getText();
                }
                return content;
                
            default:
                return content;
        }
    }
    
    private int countLines(String content) {
        if (content == null || content.isEmpty()) {
            return 0;
        }
        return content.split("\\n").length;
    }
}
