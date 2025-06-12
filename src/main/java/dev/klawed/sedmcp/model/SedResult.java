package dev.klawed.sedmcp.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Result of applying sed operations. Because we need to track every single 
 * change like some kind of obsessive text editing accountant.
 */
public class SedResult {
    
    private final String originalContent;
    private final String modifiedContent;
    private final List<String> changesApplied;
    private final int linesModified;
    private final boolean success;
    private final String errorMessage;
    private final List<String> warnings;
    private final long executionTimeMs;
    
    private SedResult(Builder builder) {
        this.originalContent = builder.originalContent;
        this.modifiedContent = builder.modifiedContent;
        this.changesApplied = new ArrayList<>(builder.changesApplied);
        this.linesModified = builder.linesModified;
        this.success = builder.success;
        this.errorMessage = builder.errorMessage;
        this.warnings = new ArrayList<>(builder.warnings);
        this.executionTimeMs = builder.executionTimeMs;
    }
    
    public String getOriginalContent() { return originalContent; }
    public String getModifiedContent() { return modifiedContent; }
    public List<String> getChangesApplied() { return new ArrayList<>(changesApplied); }
    public int getLinesModified() { return linesModified; }
    public boolean isSuccess() { return success; }
    public String getErrorMessage() { return errorMessage; }
    public List<String> getWarnings() { return new ArrayList<>(warnings); }
    public long getExecutionTimeMs() { return executionTimeMs; }
    
    public boolean hasWarnings() {
        return !warnings.isEmpty();
    }
    
    public boolean isModified() {
        return !Objects.equals(originalContent, modifiedContent);
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static SedResult success(String original, String modified) {
        return builder()
                .originalContent(original)
                .modifiedContent(modified)
                .success(true)
                .build();
    }
    
    public static SedResult failure(String original, String error) {
        return builder()
                .originalContent(original)
                .modifiedContent(original)
                .success(false)
                .errorMessage(error)
                .build();
    }
    
    public static class Builder {
        private String originalContent = "";
        private String modifiedContent = "";
        private List<String> changesApplied = new ArrayList<>();
        private int linesModified = 0;
        private boolean success = false;
        private String errorMessage;
        private List<String> warnings = new ArrayList<>();
        private long executionTimeMs = 0;
        
        public Builder originalContent(String originalContent) {
            this.originalContent = originalContent != null ? originalContent : "";
            return this;
        }
        
        public Builder modifiedContent(String modifiedContent) {
            this.modifiedContent = modifiedContent != null ? modifiedContent : "";
            return this;
        }
        
        public Builder addChange(String change) {
            this.changesApplied.add(change);
            return this;
        }
        
        public Builder changesApplied(List<String> changes) {
            this.changesApplied = new ArrayList<>(changes != null ? changes : new ArrayList<>());
            return this;
        }
        
        public Builder linesModified(int linesModified) {
            this.linesModified = linesModified;
            return this;
        }
        
        public Builder success(boolean success) {
            this.success = success;
            return this;
        }
        
        public Builder errorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }
        
        public Builder addWarning(String warning) {
            this.warnings.add(warning);
            return this;
        }
        
        public Builder warnings(List<String> warnings) {
            this.warnings = new ArrayList<>(warnings != null ? warnings : new ArrayList<>());
            return this;
        }
        
        public Builder executionTimeMs(long executionTimeMs) {
            this.executionTimeMs = executionTimeMs;
            return this;
        }
        
        public SedResult build() {
            return new SedResult(this);
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SedResult sedResult = (SedResult) o;
        return linesModified == sedResult.linesModified &&
               success == sedResult.success &&
               executionTimeMs == sedResult.executionTimeMs &&
               Objects.equals(originalContent, sedResult.originalContent) &&
               Objects.equals(modifiedContent, sedResult.modifiedContent) &&
               Objects.equals(changesApplied, sedResult.changesApplied) &&
               Objects.equals(errorMessage, sedResult.errorMessage) &&
               Objects.equals(warnings, sedResult.warnings);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(originalContent, modifiedContent, changesApplied, 
                          linesModified, success, errorMessage, warnings, executionTimeMs);
    }
    
    @Override
    public String toString() {
        return String.format("SedResult{success=%s, linesModified=%d, changes=%d, warnings=%d, time=%dms}", 
                           success, linesModified, changesApplied.size(), warnings.size(), executionTimeMs);
    }
}
