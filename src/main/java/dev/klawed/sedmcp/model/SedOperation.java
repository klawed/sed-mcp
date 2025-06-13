package dev.klawed.sedmcp.model;

import java.util.Objects;

/**
 * Represents a single sed operation because apparently we need to reinvent 
 * a 50-year-old Unix tool in Java.
 */
public class SedOperation {
    
    public enum OperationType {
        SUBSTITUTE("s"),
        DELETE("d"), 
        PRINT("p"),
        APPEND("a"),
        INSERT("i"),
        CHANGE("c");
        
        private final String command;
        
        OperationType(String command) {
            this.command = command;
        }
        
        public String getCommand() {
            return command;
        }
        
        public static OperationType fromCommand(String command) {
            for (OperationType type : values()) {
                if (type.command.equals(command)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Unknown sed operation: " + command);
        }
    }
    
    private final OperationType operation;
    private final String pattern;
    private final String replacement;
    private final String flags;
    private final String address;
    private final String text; // for insert/append/change operations
    
    private SedOperation(Builder builder) {
        this.operation = Objects.requireNonNull(builder.operation, "Operation cannot be null");
        this.pattern = builder.pattern;
        this.replacement = builder.replacement;
        this.flags = builder.flags != null ? builder.flags : "";
        this.address = builder.address;
        this.text = builder.text;
        
        validateOperation();
    }
    
    private void validateOperation() {
        switch (operation) {
            case SUBSTITUTE:
                if (pattern == null || replacement == null) {
                    throw new IllegalArgumentException("Substitute operation requires pattern and replacement");
                }
                break;
            case INSERT:
            case APPEND:
            case CHANGE:
                if (text == null) {
                    throw new IllegalArgumentException(operation + " operation requires text");
                }
                break;
            case DELETE:
            case PRINT:
                if (pattern == null && address == null) {
                    throw new IllegalArgumentException(operation + " operation requires pattern or address");
                }
                break;
        }
    }
    
    public OperationType getOperation() { return operation; }
    public OperationType getOperationType() { return operation; } // Added method to fix compilation error
    public String getPattern() { return pattern; }
    public String getReplacement() { return replacement; }
    public String getFlags() { return flags; }
    public String getAddress() { return address; }
    public String getText() { return text; }
    
    public boolean hasFlag(char flag) {
        return flags.indexOf(flag) >= 0;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private OperationType operation;
        private String pattern;
        private String replacement;
        private String flags;
        private String address;
        private String text;
        
        public Builder operation(OperationType operation) {
            this.operation = operation;
            return this;
        }
        
        public Builder pattern(String pattern) {
            this.pattern = pattern;
            return this;
        }
        
        public Builder replacement(String replacement) {
            this.replacement = replacement;
            return this;
        }
        
        public Builder flags(String flags) {
            this.flags = flags;
            return this;
        }
        
        public Builder address(String address) {
            this.address = address;
            return this;
        }
        
        public Builder text(String text) {
            this.text = text;
            return this;
        }
        
        public SedOperation build() {
            return new SedOperation(this);
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SedOperation that = (SedOperation) o;
        return operation == that.operation &&
               Objects.equals(pattern, that.pattern) &&
               Objects.equals(replacement, that.replacement) &&
               Objects.equals(flags, that.flags) &&
               Objects.equals(address, that.address) &&
               Objects.equals(text, that.text);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(operation, pattern, replacement, flags, address, text);
    }
    
    @Override
    public String toString() {
        return String.format("SedOperation{op=%s, pattern='%s', replacement='%s', flags='%s', address='%s', text='%s'}", 
                             operation, pattern, replacement, flags, address, text);
    }
}