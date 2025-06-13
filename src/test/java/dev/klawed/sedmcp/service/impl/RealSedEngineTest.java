package dev.klawed.sedmcp.service.impl;

import dev.klawed.sedmcp.model.SedOperation;
import dev.klawed.sedmcp.model.SedResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RealSedEngineTest {

    private RealSedEngine sedEngine;

    @BeforeEach
    void setUp() {
        sedEngine = new RealSedEngine();
    }

    @Test
    void testSubstituteOperation() {
        // Test basic substitution
        String content = "hello world\nhello universe";
        SedOperation operation = SedOperation.builder()
                .operation(SedOperation.OperationType.SUBSTITUTE)
                .pattern("hello")
                .replacement("hi")
                .build();
        
        SedResult result = sedEngine.executeOperation(content, operation);
        
        assertTrue(result.isSuccess());
        assertTrue(result.isModified());
        assertEquals("hi world\nhello universe", result.getModifiedContent());
        assertEquals(1, result.getLinesModified());
    }

    @Test
    void testSubstituteWithGlobalFlag() {
        // Test global substitution
        String content = "hello hello world";
        SedOperation operation = SedOperation.builder()
                .operation(SedOperation.OperationType.SUBSTITUTE)
                .pattern("hello")
                .replacement("hi")
                .flags("g")
                .build();
        
        SedResult result = sedEngine.executeOperation(content, operation);
        
        assertTrue(result.isSuccess());
        assertTrue(result.isModified());
        assertEquals("hi hi world", result.getModifiedContent());
        assertEquals(2, result.getLinesModified());
    }

    @Test
    void testDeleteOperation() {
        // Test line deletion
        String content = "line 1\nline 2\nline 3";
        SedOperation operation = SedOperation.builder()
                .operation(SedOperation.OperationType.DELETE)
                .pattern("line 2")
                .build();
        
        SedResult result = sedEngine.executeOperation(content, operation);
        
        assertTrue(result.isSuccess());
        assertTrue(result.isModified());
        assertEquals("line 1\nline 3", result.getModifiedContent());
        assertEquals(1, result.getLinesModified());
    }

    @Test
    void testPrintOperation() {
        // Test line printing (filters content)
        String content = "apple\nbanana\napricot";
        SedOperation operation = SedOperation.builder()
                .operation(SedOperation.OperationType.PRINT)
                .pattern("ap.*")
                .build();
        
        SedResult result = sedEngine.executeOperation(content, operation);
        
        assertTrue(result.isSuccess());
        assertEquals("apple\napricot", result.getModifiedContent());
        assertEquals(2, result.getLinesModified());
    }

    @Test
    void testInvalidRegexPattern() {
        // Test invalid regex pattern - should throw exception during validation
        String content = "test content";
        SedOperation operation = SedOperation.builder()
                .operation(SedOperation.OperationType.SUBSTITUTE)
                .pattern("[invalid")
                .replacement("valid")
                .build();
        
        SedResult result = sedEngine.executeOperation(content, operation);
        assertFalse(result.isSuccess());
        assertNotNull(result.getError());
    }

    @Test
    void testNoMatchesFound() {
        // Test when no matches are found
        String content = "hello world";
        SedOperation operation = SedOperation.builder()
                .operation(SedOperation.OperationType.SUBSTITUTE)
                .pattern("goodbye")
                .replacement("hi")
                .build();
        
        SedResult result = sedEngine.executeOperation(content, operation);
        
        assertTrue(result.isSuccess());
        assertFalse(result.isModified());
        assertEquals(content, result.getModifiedContent());
        assertEquals(0, result.getLinesModified());
    }

    @Test
    void testSupportsOperation() {
        assertTrue(sedEngine.supportsOperation(SedOperation.OperationType.SUBSTITUTE));
        assertTrue(sedEngine.supportsOperation(SedOperation.OperationType.DELETE));
        assertTrue(sedEngine.supportsOperation(SedOperation.OperationType.PRINT));
        assertFalse(sedEngine.supportsOperation(SedOperation.OperationType.APPEND));
        assertFalse(sedEngine.supportsOperation(SedOperation.OperationType.INSERT));
        assertFalse(sedEngine.supportsOperation(SedOperation.OperationType.CHANGE));
    }

    @Test
    void testBatchOperations() {
        // Test executing multiple operations in sequence
        String content = "hello world\nfoo bar\nhello foo";
        
        SedOperation op1 = SedOperation.builder()
                .operation(SedOperation.OperationType.SUBSTITUTE)
                .pattern("hello")
                .replacement("hi")
                .flags("g")
                .build();
                
        SedOperation op2 = SedOperation.builder()
                .operation(SedOperation.OperationType.SUBSTITUTE)
                .pattern("foo")
                .replacement("baz")
                .flags("g")
                .build();
        
        SedResult result = sedEngine.executeBatch(content, java.util.List.of(op1, op2));
        
        assertTrue(result.isSuccess());
        assertTrue(result.isModified());
        assertEquals("hi world\nbaz bar\nhi baz", result.getModifiedContent());
        assertTrue(result.getLinesModified() > 0);
    }

    @Test
    void testPreviewOperation() {
        // Test preview functionality
        String content = "hello world";
        SedOperation operation = SedOperation.builder()
                .operation(SedOperation.OperationType.SUBSTITUTE)
                .pattern("hello")
                .replacement("hi")
                .build();
        
        SedResult result = sedEngine.previewOperation(content, operation);
        
        assertTrue(result.isSuccess());
        assertTrue(result.isModified());
        assertEquals("hi world", result.getModifiedContent());
        assertEquals(0, result.getExecutionTimeMs()); // Previews don't track execution time
    }
}