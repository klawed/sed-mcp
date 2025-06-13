package dev.klawed.sedmcp.service.impl;

import dev.klawed.sedmcp.model.SedOperation;
import dev.klawed.sedmcp.model.SedResult;
import dev.klawed.sedmcp.service.SedEngineException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive unit tests for RealSedEngine.
 * Tests all supported operations: substitute, delete, and print.
 */
class RealSedEngineTest {
    
    private RealSedEngine engine;
    
    @BeforeEach
    void setUp() {
        engine = new RealSedEngine();
    }
    
    @Nested
    @DisplayName("Substitute Operations")
    class SubstituteOperations {
        
        @Test
        @DisplayName("Basic substitution without flags")
        void testBasicSubstitution() {
            // Given
            String content = "hello world hello";
            SedOperation operation = SedOperation.builder()
                    .operation(SedOperation.OperationType.SUBSTITUTE)
                    .pattern("hello")
                    .replacement("hi")
                    .build();
            
            // When
            SedResult result = engine.executeOperation(content, operation);
            
            // Then
            assertTrue(result.isSuccess());
            assertEquals("hi world hello", result.getModifiedContent());
            assertTrue(result.isModified());
            assertEquals(1, result.getLinesModified());
            assertEquals(1, result.getChangesApplied().size());
            assertTrue(result.getChangesApplied().get(0).contains("hello"));
            assertTrue(result.getChangesApplied().get(0).contains("hi"));
        }
        
        @Test
        @DisplayName("Global substitution with g flag")
        void testGlobalSubstitution() {
            // Given
            String content = "hello world hello universe hello";
            SedOperation operation = SedOperation.builder()
                    .operation(SedOperation.OperationType.SUBSTITUTE)
                    .pattern("hello")
                    .replacement("hi")
                    .flags("g")
                    .build();
            
            // When
            SedResult result = engine.executeOperation(content, operation);
            
            // Then
            assertTrue(result.isSuccess());
            assertEquals("hi world hi universe hi", result.getModifiedContent());
            assertTrue(result.isModified());
            assertEquals(3, result.getLinesModified());
            assertEquals(3, result.getChangesApplied().size());
        }
        
        @Test
        @DisplayName("Case insensitive substitution with i flag")
        void testCaseInsensitiveSubstitution() {
            // Given
            String content = "Hello HELLO hello";
            SedOperation operation = SedOperation.builder()
                    .operation(SedOperation.OperationType.SUBSTITUTE)
                    .pattern("hello")
                    .replacement("hi")
                    .flags("gi")
                    .build();
            
            // When
            SedResult result = engine.executeOperation(content, operation);
            
            // Then
            assertTrue(result.isSuccess());
            assertEquals("hi hi hi", result.getModifiedContent());
            assertTrue(result.isModified());
            assertEquals(3, result.getLinesModified());
        }
        
        @Test
        @DisplayName("Regex pattern substitution")
        void testRegexPatternSubstitution() {
            // Given
            String content = "The price is $123.45 and $67.89";
            SedOperation operation = SedOperation.builder()
                    .operation(SedOperation.OperationType.SUBSTITUTE)
                    .pattern("\\$\\d+\\.\\d+")
                    .replacement("***")
                    .flags("g")
                    .build();
            
            // When
            SedResult result = engine.executeOperation(content, operation);
            
            // Then
            assertTrue(result.isSuccess());
            assertEquals("The price is *** and ***", result.getModifiedContent());
            assertTrue(result.isModified());
            assertEquals(2, result.getLinesModified());
        }
        
        @Test
        @DisplayName("No match substitution")
        void testNoMatchSubstitution() {
            // Given
            String content = "hello world";
            SedOperation operation = SedOperation.builder()
                    .operation(SedOperation.OperationType.SUBSTITUTE)
                    .pattern("goodbye")
                    .replacement("farewell")
                    .build();
            
            // When
            SedResult result = engine.executeOperation(content, operation);
            
            // Then
            assertTrue(result.isSuccess());
            assertEquals("hello world", result.getModifiedContent());
            assertFalse(result.isModified());
            assertEquals(0, result.getLinesModified());
            assertTrue(result.getChangesApplied().isEmpty());
        }
        
        @Test
        @DisplayName("Empty replacement")
        void testEmptyReplacement() {
            // Given
            String content = "remove this word from text";
            SedOperation operation = SedOperation.builder()
                    .operation(SedOperation.OperationType.SUBSTITUTE)
                    .pattern("this ")
                    .replacement("")
                    .build();
            
            // When
            SedResult result = engine.executeOperation(content, operation);
            
            // Then
            assertTrue(result.isSuccess());
            assertEquals("remove word from text", result.getModifiedContent());
            assertTrue(result.isModified());
            assertEquals(1, result.getLinesModified());
        }
    }
    
    @Nested
    @DisplayName("Delete Operations")
    class DeleteOperations {
        
        @Test
        @DisplayName("Delete lines matching pattern")
        void testDeleteLinesMatchingPattern() {
            // Given
            String content = "keep this line\ndelete this line\nkeep this too\ndelete this also";
            SedOperation operation = SedOperation.builder()
                    .operation(SedOperation.OperationType.DELETE)
                    .pattern("delete")
                    .build();
            
            // When
            SedResult result = engine.executeOperation(content, operation);
            
            // Then
            assertTrue(result.isSuccess());
            assertEquals("keep this line\nkeep this too", result.getModifiedContent());
            assertTrue(result.isModified());
            assertEquals(2, result.getLinesModified());
            assertEquals(2, result.getChangesApplied().size());
        }
        
        @Test
        @DisplayName("Delete with regex pattern")
        void testDeleteWithRegexPattern() {
            // Given
            String content = "line1\nERROR: something bad\nline3\nWARNING: something else\nline5";
            SedOperation operation = SedOperation.builder()
                    .operation(SedOperation.OperationType.DELETE)
                    .pattern("^(ERROR|WARNING):")
                    .build();
            
            // When
            SedResult result = engine.executeOperation(content, operation);
            
            // Then
            assertTrue(result.isSuccess());
            assertEquals("line1\nline3\nline5", result.getModifiedContent());
            assertTrue(result.isModified());
            assertEquals(2, result.getLinesModified());
        }
        
        @Test
        @DisplayName("Delete no matching lines")
        void testDeleteNoMatchingLines() {
            // Given
            String content = "keep all\nof these lines\nnothing to delete";
            SedOperation operation = SedOperation.builder()
                    .operation(SedOperation.OperationType.DELETE)
                    .pattern("remove")
                    .build();
            
            // When
            SedResult result = engine.executeOperation(content, operation);
            
            // Then
            assertTrue(result.isSuccess());
            assertEquals(content, result.getModifiedContent());
            assertFalse(result.isModified());
            assertEquals(0, result.getLinesModified());
            assertTrue(result.getChangesApplied().isEmpty());
        }
        
        @Test
        @DisplayName("Delete all lines")
        void testDeleteAllLines() {
            // Given
            String content = "delete\ndelete\ndelete";
            SedOperation operation = SedOperation.builder()
                    .operation(SedOperation.OperationType.DELETE)
                    .pattern("delete")
                    .build();
            
            // When
            SedResult result = engine.executeOperation(content, operation);
            
            // Then
            assertTrue(result.isSuccess());
            assertEquals("", result.getModifiedContent());
            assertTrue(result.isModified());
            assertEquals(3, result.getLinesModified());
        }
    }
    
    @Nested
    @DisplayName("Print Operations")
    class PrintOperations {
        
        @Test
        @DisplayName("Print lines matching pattern")
        void testPrintLinesMatchingPattern() {
            // Given
            String content = "ignore this\nprint this line\nignore that\nprint this too";
            SedOperation operation = SedOperation.builder()
                    .operation(SedOperation.OperationType.PRINT)
                    .pattern("print")
                    .build();
            
            // When
            SedResult result = engine.executeOperation(content, operation);
            
            // Then
            assertTrue(result.isSuccess());
            assertEquals("print this line\nprint this too", result.getModifiedContent());
            assertTrue(result.isModified()); // Print always modifies by filtering
            assertEquals(2, result.getLinesModified());
            assertEquals(2, result.getChangesApplied().size());
        }
        
        @Test
        @DisplayName("Print with regex pattern")
        void testPrintWithRegexPattern() {
            // Given
            String content = "user1@email.com\nnot an email\nuser2@test.org\nalso not email\nuser3@domain.net";
            SedOperation operation = SedOperation.builder()
                    .operation(SedOperation.OperationType.PRINT)
                    .pattern("\\w+@\\w+\\.(com|org|net)")
                    .build();
            
            // When
            SedResult result = engine.executeOperation(content, operation);
            
            // Then
            assertTrue(result.isSuccess());
            assertEquals("user1@email.com\nuser2@test.org\nuser3@domain.net", result.getModifiedContent());
            assertTrue(result.isModified());
            assertEquals(3, result.getLinesModified());
        }
        
        @Test
        @DisplayName("Print no matching lines")
        void testPrintNoMatchingLines() {
            // Given
            String content = "line1\nline2\nline3";
            SedOperation operation = SedOperation.builder()
                    .operation(SedOperation.OperationType.PRINT)
                    .pattern("nomatch")
                    .build();
            
            // When
            SedResult result = engine.executeOperation(content, operation);
            
            // Then
            assertTrue(result.isSuccess());
            assertEquals("", result.getModifiedContent());
            assertTrue(result.isModified());
            assertEquals(0, result.getLinesModified());
            assertTrue(result.getChangesApplied().isEmpty());
        }
    }
    
    @Nested
    @DisplayName("Batch Operations")
    class BatchOperations {
        
        @Test
        @DisplayName("Execute multiple operations in sequence")
        void testExecuteBatchOperations() {
            // Given
            String content = "Hello World\nThis is a test\nHello Universe";
            List<SedOperation> operations = List.of(
                SedOperation.builder()
                    .operation(SedOperation.OperationType.SUBSTITUTE)
                    .pattern("Hello")
                    .replacement("Hi")
                    .flags("g")
                    .build(),
                SedOperation.builder()
                    .operation(SedOperation.OperationType.SUBSTITUTE)
                    .pattern("test")
                    .replacement("example")
                    .build()
            );
            
            // When
            SedResult result = engine.executeBatch(content, operations);
            
            // Then
            assertTrue(result.isSuccess());
            assertEquals("Hi World\nThis is a example\nHi Universe", result.getModifiedContent());
            assertTrue(result.isModified());
            assertEquals(3, result.getLinesModified()); // 2 Hello replacements + 1 test replacement
            assertEquals(3, result.getChangesApplied().size());
        }
        
        @Test
        @DisplayName("Batch operation fails on invalid operation")
        void testBatchOperationFailsOnInvalidOperation() {
            // Given
            String content = "test content";
            List<SedOperation> operations = List.of(
                SedOperation.builder()
                    .operation(SedOperation.OperationType.SUBSTITUTE)
                    .pattern("test")
                    .replacement("working")
                    .build(),
                SedOperation.builder()
                    .operation(SedOperation.OperationType.SUBSTITUTE)
                    .pattern("[invalid")  // Invalid regex
                    .replacement("broken")
                    .build()
            );
            
            // When
            SedResult result = engine.executeBatch(content, operations);
            
            // Then
            assertFalse(result.isSuccess());
            assertNotNull(result.getError());
            assertTrue(result.getError().contains("Batch operation failed"));
        }
    }
    
    @Nested
    @DisplayName("Preview Operations")
    class PreviewOperations {
        
        @Test
        @DisplayName("Preview shows same result as execute")
        void testPreviewShowsSameResultAsExecute() {
            // Given
            String content = "hello world hello";
            SedOperation operation = SedOperation.builder()
                    .operation(SedOperation.OperationType.SUBSTITUTE)
                    .pattern("hello")
                    .replacement("hi")
                    .flags("g")
                    .build();
            
            // When
            SedResult executeResult = engine.executeOperation(content, operation);
            SedResult previewResult = engine.previewOperation(content, operation);
            
            // Then
            assertEquals(executeResult.getModifiedContent(), previewResult.getModifiedContent());
            assertEquals(executeResult.isModified(), previewResult.isModified());
            assertEquals(executeResult.getLinesModified(), previewResult.getLinesModified());
            assertEquals(0, previewResult.getExecutionTimeMs()); // Preview doesn't track time
        }
    }
    
    @Nested
    @DisplayName("Validation Tests")
    class ValidationTests {
        
        @Test
        @DisplayName("Validate null operation throws exception")
        void testValidateNullOperation() {
            assertThrows(SedEngineException.class, () -> engine.validateOperation(null));
        }
        
        @Test
        @DisplayName("Validate operation with null type throws exception")
        void testValidateOperationWithNullType() {
            SedOperation operation = SedOperation.builder().build();
            assertThrows(SedEngineException.class, () -> engine.validateOperation(operation));
        }
        
        @Test
        @DisplayName("Validate substitute operation without pattern throws exception")
        void testValidateSubstituteWithoutPattern() {
            SedOperation operation = SedOperation.builder()
                    .operation(SedOperation.OperationType.SUBSTITUTE)
                    .replacement("test")
                    .build();
            assertThrows(SedEngineException.class, () -> engine.validateOperation(operation));
        }
        
        @Test
        @DisplayName("Validate substitute operation without replacement throws exception")
        void testValidateSubstituteWithoutReplacement() {
            SedOperation operation = SedOperation.builder()
                    .operation(SedOperation.OperationType.SUBSTITUTE)
                    .pattern("test")
                    .build();
            assertThrows(SedEngineException.class, () -> engine.validateOperation(operation));
        }
        
        @Test
        @DisplayName("Validate substitute operation with invalid regex throws exception")
        void testValidateSubstituteWithInvalidRegex() {
            SedOperation operation = SedOperation.builder()
                    .operation(SedOperation.OperationType.SUBSTITUTE)
                    .pattern("[invalid")
                    .replacement("test")
                    .build();
            assertThrows(SedEngineException.class, () -> engine.validateOperation(operation));
        }
        
        @Test
        @DisplayName("Validate delete operation without pattern throws exception")
        void testValidateDeleteWithoutPattern() {
            SedOperation operation = SedOperation.builder()
                    .operation(SedOperation.OperationType.DELETE)
                    .build();
            assertThrows(SedEngineException.class, () -> engine.validateOperation(operation));
        }
        
        @Test
        @DisplayName("Validate valid operations passes")
        void testValidateValidOperations() {
            // Substitute
            SedOperation substitute = SedOperation.builder()
                    .operation(SedOperation.OperationType.SUBSTITUTE)
                    .pattern("test")
                    .replacement("replacement")
                    .build();
            assertDoesNotThrow(() -> engine.validateOperation(substitute));
            
            // Delete
            SedOperation delete = SedOperation.builder()
                    .operation(SedOperation.OperationType.DELETE)
                    .pattern("test")
                    .build();
            assertDoesNotThrow(() -> engine.validateOperation(delete));
            
            // Print
            SedOperation print = SedOperation.builder()
                    .operation(SedOperation.OperationType.PRINT)
                    .pattern("test")
                    .build();
            assertDoesNotThrow(() -> engine.validateOperation(print));
        }
    }
    
    @Nested
    @DisplayName("Support Tests")
    class SupportTests {
        
        @Test
        @DisplayName("Supports implemented operations")
        void testSupportsImplementedOperations() {
            assertTrue(engine.supportsOperation(SedOperation.OperationType.SUBSTITUTE));
            assertTrue(engine.supportsOperation(SedOperation.OperationType.DELETE));
            assertTrue(engine.supportsOperation(SedOperation.OperationType.PRINT));
        }
        
        @Test
        @DisplayName("Does not support unimplemented operations")
        void testDoesNotSupportUnimplementedOperations() {
            // These operations are defined in the enum but not implemented yet
            assertFalse(engine.supportsOperation(SedOperation.OperationType.APPEND));
            assertFalse(engine.supportsOperation(SedOperation.OperationType.INSERT));
            assertFalse(engine.supportsOperation(SedOperation.OperationType.CHANGE));
        }
    }
    
    @Nested
    @DisplayName("Error Handling")
    class ErrorHandling {
        
        @Test
        @DisplayName("Invalid regex pattern in execution returns error result")
        void testInvalidRegexInExecution() {
            // Given
            String content = "test content";
            SedOperation operation = SedOperation.builder()
                    .operation(SedOperation.OperationType.SUBSTITUTE)
                    .pattern("[invalid")
                    .replacement("test")
                    .build();
            
            // When
            SedResult result = engine.executeOperation(content, operation);
            
            // Then
            assertFalse(result.isSuccess());
            assertNotNull(result.getError());
            assertTrue(result.getError().contains("Invalid regex pattern"));
            assertEquals(content, result.getModifiedContent()); // Original content preserved
        }
        
        @Test
        @DisplayName("Execution time is recorded for successful operations")
        void testExecutionTimeRecorded() {
            // Given
            String content = "hello world";
            SedOperation operation = SedOperation.builder()
                    .operation(SedOperation.OperationType.SUBSTITUTE)
                    .pattern("hello")
                    .replacement("hi")
                    .build();
            
            // When
            SedResult result = engine.executeOperation(content, operation);
            
            // Then
            assertTrue(result.isSuccess());
            assertTrue(result.getExecutionTimeMs() >= 0);
        }
    }
}