package dev.klawed.sedmcp.service;

/**
 * Exception for when sed operations go sideways. Because of course 
 * we need our own exception type for text editing failures.
 */
public class SedEngineException extends RuntimeException {
    
    private final String operation;
    private final String pattern;
    private final int lineNumber;
    
    public SedEngineException(String message) {
        super(message);
        this.operation = null;
        this.pattern = null;
        this.lineNumber = -1;
    }
    
    public SedEngineException(String message, Throwable cause) {
        super(message, cause);
        this.operation = null;
        this.pattern = null;
        this.lineNumber = -1;
    }
    
    public SedEngineException(String message, String operation, String pattern) {
        super(message);
        this.operation = operation;
        this.pattern = pattern;
        this.lineNumber = -1;
    }
    
    public SedEngineException(String message, String operation, String pattern, int lineNumber) {
        super(message);
        this.operation = operation;
        this.pattern = pattern;
        this.lineNumber = lineNumber;
    }
    
    public String getOperation() {
        return operation;
    }
    
    public String getPattern() {
        return pattern;
    }
    
    public int getLineNumber() {
        return lineNumber;
    }
    
    @Override
    public String getMessage() {
        StringBuilder msg = new StringBuilder(super.getMessage());
        
        if (operation != null) {
            msg.append(" [operation: ").append(operation).append("]");
        }
        if (pattern != null) {
            msg.append(" [pattern: ").append(pattern).append("]");
        }
        if (lineNumber > 0) {
            msg.append(" [line: ").append(lineNumber).append("]");
        }
        
        return msg.toString();
    }
}
