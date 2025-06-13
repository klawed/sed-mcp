package dev.klawed.sedmcp.service.impl;

import dev.klawed.sedmcp.model.SedOperation;
import dev.klawed.sedmcp.model.SedResult;
import dev.klawed.sedmcp.service.SedEngine;
import dev.klawed.sedmcp.service.SedEngineException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Real sed engine implementation that actually does sed operations.
 * Supports basic substitution, deletion, and print operations.
 * 
 * Unlike the mock, this one actually performs regex operations and
 * modifies text content like a proper sed implementation should.
 */
@Service
public class RealSedEngine implements SedEngine {
    
    private static final Logger logger = LoggerFactory.getLogger(RealSedEngine.class);
    
    @Override
    public SedResult executeOperation(String content, SedOperation operation) {
        logger.debug("Executing sed operation: {} on content length: {}", 
                     operation.getOperationType(), content.length());
        
        long startTime = System.currentTimeMillis();
        
        try {
            validateOperation(operation);
            
            SedResult result = performOperation(content, operation, false);
            
            long executionTime = System.currentTimeMillis() - startTime;
            
            return SedResult.builder()
                    .success(true)
                    .originalContent(content)
                    .modifiedContent(result.getModifiedContent())
                    .modified(result.isModified())
                    .linesModified(result.getLinesModified())
                    .changesApplied(result.getChangesApplied())
                    .warnings(result.getWarnings())
                    .executionTimeMs(executionTime)
                    .build();
                    
        } catch (Exception e) {
            logger.error("Error executing sed operation: {}", e.getMessage(), e);
            return SedResult.builder()
                    .success(false)
                    .originalContent(content)
                    .modifiedContent(content)
                    .modified(false)
                    .linesModified(0)
                    .changesApplied(List.of())
                    .warnings(List.of())
                    .executionTimeMs(System.currentTimeMillis() - startTime)
                    .error(e.getMessage())
                    .build();
        }
    }
    
    @Override
    public SedResult executeBatch(String content, List<SedOperation> operations) {
        logger.debug("Executing batch of {} sed operations", operations.size());
        
        long startTime = System.currentTimeMillis();
        String currentContent = content;
        List<String> allChanges = new ArrayList<>();
        List<String> allWarnings = new ArrayList<>();
        int totalLinesModified = 0;
        boolean anyModified = false;
        
        try {
            for (int i = 0; i < operations.size(); i++) {
                SedOperation operation = operations.get(i);
                logger.debug("Executing batch operation {}/{}: {}", i + 1, operations.size(), operation.getOperationType());
                
                SedResult result = executeOperation(currentContent, operation);
                
                if (!result.isSuccess()) {
                    throw new SedEngineException("Batch operation failed at step " + (i + 1) + ": " + result.getError());
                }
                
                currentContent = result.getModifiedContent();
                allChanges.addAll(result.getChangesApplied());
                allWarnings.addAll(result.getWarnings());
                totalLinesModified += result.getLinesModified();
                anyModified = anyModified || result.isModified();
            }
            
            return SedResult.builder()
                    .success(true)
                    .originalContent(content)
                    .modifiedContent(currentContent)
                    .modified(anyModified)
                    .linesModified(totalLinesModified)
                    .changesApplied(allChanges)
                    .warnings(allWarnings)
                    .executionTimeMs(System.currentTimeMillis() - startTime)
                    .build();
                    
        } catch (Exception e) {
            logger.error("Error executing batch sed operations: {}", e.getMessage(), e);
            return SedResult.builder()
                    .success(false)
                    .originalContent(content)
                    .modifiedContent(content)
                    .modified(false)
                    .linesModified(0)
                    .changesApplied(allChanges)
                    .warnings(allWarnings)
                    .executionTimeMs(System.currentTimeMillis() - startTime)
                    .error(e.getMessage())
                    .build();
        }
    }
    
    @Override
    public SedResult previewOperation(String content, SedOperation operation) {
        logger.debug("Previewing sed operation: {}", operation.getOperationType());
        
        try {
            validateOperation(operation);
            
            SedResult result = performOperation(content, operation, true);
            
            return SedResult.builder()
                    .success(true)
                    .originalContent(content)
                    .modifiedContent(result.getModifiedContent())
                    .modified(result.isModified())
                    .linesModified(result.getLinesModified())
                    .changesApplied(result.getChangesApplied())
                    .warnings(result.getWarnings())
                    .executionTimeMs(0) // Previews don't track execution time
                    .build();
                    
        } catch (Exception e) {
            logger.error("Error previewing sed operation: {}", e.getMessage(), e);
            return SedResult.builder()
                    .success(false)
                    .originalContent(content)
                    .modifiedContent(content)
                    .modified(false)
                    .linesModified(0)
                    .changesApplied(List.of())
                    .warnings(List.of())
                    .executionTimeMs(0)
                    .error(e.getMessage())
                    .build();
        }
    }
    
    @Override
    public void validateOperation(SedOperation operation) {
        if (operation == null) {
            throw new SedEngineException("Operation cannot be null");
        }
        
        if (operation.getOperationType() == null) {
            throw new SedEngineException("Operation type is required");
        }
        
        if (!supportsOperation(operation.getOperationType())) {
            throw new SedEngineException("Unsupported operation type: " + operation.getOperationType());
        }
        
        switch (operation.getOperationType()) {
            case SUBSTITUTE:
                validateSubstituteOperation(operation);
                break;
            case DELETE:
                validateDeleteOperation(operation);
                break;
            case PRINT:
                validatePrintOperation(operation);
                break;
            default:
                throw new SedEngineException("Validation not implemented for operation type: " + operation.getOperationType());
        }
    }
    
    @Override
    public boolean supportsOperation(SedOperation.OperationType operationType) {
        return switch (operationType) {
            case SUBSTITUTE, DELETE, PRINT -> true;
            default -> false;
        };
    }
    
    private SedResult performOperation(String content, SedOperation operation, boolean preview) {
        return switch (operation.getOperationType()) {
            case SUBSTITUTE -> performSubstitution(content, operation, preview);
            case DELETE -> performDeletion(content, operation, preview);
            case PRINT -> performPrint(content, operation, preview);
            default -> throw new SedEngineException("Unsupported operation: " + operation.getOperationType());
        };
    }
    
    private SedResult performSubstitution(String content, SedOperation operation, boolean preview) {
        String pattern = operation.getPattern();
        String replacement = operation.getReplacement();
        String flags = operation.getFlags() != null ? operation.getFlags() : "";
        
        try {
            Pattern regex = Pattern.compile(pattern, getRegexFlags(flags));
            Matcher matcher = regex.matcher(content);
            
            List<String> changes = new ArrayList<>();
            int linesModified = 0;
            
            String result;
            if (flags.contains("g")) {
                // Global replacement
                StringBuffer sb = new StringBuffer();
                while (matcher.find()) {
                    matcher.appendReplacement(sb, replacement);
                    changes.add(String.format("Replaced '%s' with '%s'", matcher.group(), replacement));
                }
                matcher.appendTail(sb);
                result = sb.toString();
                linesModified = changes.size();
            } else {
                // Replace only first occurrence
                if (matcher.find()) {
                    result = matcher.replaceFirst(replacement);
                    changes.add(String.format("Replaced '%s' with '%s'", matcher.group(), replacement));
                    linesModified = 1;
                } else {
                    result = content;
                }
            }
            
            boolean modified = !content.equals(result);
            
            return SedResult.builder()
                    .success(true)
                    .originalContent(content)
                    .modifiedContent(result)
                    .modified(modified)
                    .linesModified(linesModified)
                    .changesApplied(changes)
                    .warnings(List.of())
                    .build();
                    
        } catch (PatternSyntaxException e) {
            throw new SedEngineException("Invalid regex pattern: " + e.getMessage(), e);
        }
    }
    
    private SedResult performDeletion(String content, SedOperation operation, boolean preview) {
        String pattern = operation.getPattern();
        String flags = operation.getFlags() != null ? operation.getFlags() : "";
        
        try {
            Pattern regex = Pattern.compile(pattern, getRegexFlags(flags));
            String[] lines = content.split("\n");
            List<String> resultLines = new ArrayList<>();
            List<String> changes = new ArrayList<>();
            
            for (int i = 0; i < lines.length; i++) {
                String line = lines[i];
                if (regex.matcher(line).find()) {
                    changes.add(String.format("Deleted line %d: '%s'", i + 1, line));
                } else {
                    resultLines.add(line);
                }
            }
            
            String result = String.join("\n", resultLines);
            boolean modified = !content.equals(result);
            
            return SedResult.builder()
                    .success(true)
                    .originalContent(content)
                    .modifiedContent(result)
                    .modified(modified)
                    .linesModified(changes.size())
                    .changesApplied(changes)
                    .warnings(List.of())
                    .build();
                    
        } catch (PatternSyntaxException e) {
            throw new SedEngineException("Invalid regex pattern: " + e.getMessage(), e);
        }
    }
    
    private SedResult performPrint(String content, SedOperation operation, boolean preview) {
        String pattern = operation.getPattern();
        String flags = operation.getFlags() != null ? operation.getFlags() : "";
        
        try {
            Pattern regex = Pattern.compile(pattern, getRegexFlags(flags));
            String[] lines = content.split("\n");
            List<String> matchedLines = new ArrayList<>();
            List<String> changes = new ArrayList<>();
            
            for (int i = 0; i < lines.length; i++) {
                String line = lines[i];
                if (regex.matcher(line).find()) {
                    matchedLines.add(line);
                    changes.add(String.format("Matched line %d: '%s'", i + 1, line));
                }
            }
            
            String result = String.join("\n", matchedLines);
            
            return SedResult.builder()
                    .success(true)
                    .originalContent(content)
                    .modifiedContent(result)
                    .modified(true) // Print always "modifies" by filtering content
                    .linesModified(matchedLines.size())
                    .changesApplied(changes)
                    .warnings(List.of())
                    .build();
                    
        } catch (PatternSyntaxException e) {
            throw new SedEngineException("Invalid regex pattern: " + e.getMessage(), e);
        }
    }
    
    private int getRegexFlags(String flags) {
        int regexFlags = 0;
        if (flags.contains("i")) {
            regexFlags |= Pattern.CASE_INSENSITIVE;
        }
        if (flags.contains("m")) {
            regexFlags |= Pattern.MULTILINE;
        }
        if (flags.contains("s")) {
            regexFlags |= Pattern.DOTALL;
        }
        return regexFlags;
    }
    
    private void validateSubstituteOperation(SedOperation operation) {
        if (operation.getPattern() == null || operation.getPattern().trim().isEmpty()) {
            throw new SedEngineException("Substitute operation requires a pattern");
        }
        
        if (operation.getReplacement() == null) {
            throw new SedEngineException("Substitute operation requires a replacement (can be empty string)");
        }
        
        // Validate regex pattern
        try {
            Pattern.compile(operation.getPattern());
        } catch (PatternSyntaxException e) {
            throw new SedEngineException("Invalid regex pattern: " + e.getMessage(), e);
        }
    }
    
    private void validateDeleteOperation(SedOperation operation) {
        if (operation.getPattern() == null || operation.getPattern().trim().isEmpty()) {
            throw new SedEngineException("Delete operation requires a pattern");
        }
        
        // Validate regex pattern
        try {
            Pattern.compile(operation.getPattern());
        } catch (PatternSyntaxException e) {
            throw new SedEngineException("Invalid regex pattern: " + e.getMessage(), e);
        }
    }
    
    private void validatePrintOperation(SedOperation operation) {
        if (operation.getPattern() == null || operation.getPattern().trim().isEmpty()) {
            throw new SedEngineException("Print operation requires a pattern");
        }
        
        // Validate regex pattern
        try {
            Pattern.compile(operation.getPattern());
        } catch (PatternSyntaxException e) {
            throw new SedEngineException("Invalid regex pattern: " + e.getMessage(), e);
        }
    }
}