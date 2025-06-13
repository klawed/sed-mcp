package dev.klawed.sedmcp;

import dev.klawed.sedmcp.model.SedOperation;
import dev.klawed.sedmcp.model.SedResult;
import dev.klawed.sedmcp.service.SedEngine;
import dev.klawed.sedmcp.service.impl.MockSedEngine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

// Add the missing import for FunctionCallback
import org.springframework.ai.model.function.FunctionCallback;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

/**
 * Main Spring Boot application for the sed MCP server.
 * Uses Spring AI MCP to expose sed operations as MCP tools.
 */
@SpringBootApplication
public class Application {
    
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

/**
 * Service that exposes sed operations as MCP tools.
 */
@Service
class SedService {
    
    private static final Logger logger = LoggerFactory.getLogger(SedService.class);
    private final SedEngine engine;
    
    public SedService() {
        this.engine = new MockSedEngine();
    }
    
    @PostConstruct
    public void init() {
        logger.info("Sed MCP Service initialized with MockSedEngine");
        logger.info("Ready to perform sed operations via MCP protocol");
    }
    
    // For now, we'll implement tool registration via function callbacks
    // This will be replaced with @Tool annotations when Spring AI MCP starter is properly configured
    
    @Bean
    public FunctionCallback sedExecuteFunction() {
        return FunctionCallback.builder()
                .name("sed_execute")
                .description("Execute a sed operation on text content")
                .inputType(SedExecuteRequest.class)
                .function(this::executeSedOperation)
                .build();
    }
    
    @Bean
    public FunctionCallback sedPreviewFunction() {
        return FunctionCallback.builder()
                .name("sed_preview")
                .description("Preview a sed operation without modifying the original")
                .inputType(SedExecuteRequest.class)
                .function(this::previewSedOperation)
                .build();
    }
    
    @Bean
    public FunctionCallback sedValidateFunction() {
        return FunctionCallback.builder()
                .name("sed_validate")
                .description("Validate a sed operation syntax")
                .inputType(SedValidateRequest.class)
                .function(this::validateSedOperation)
                .build();
    }
    
    public SedExecuteResponse executeSedOperation(SedExecuteRequest request) {
        try {
            logger.info("Executing sed operation: {}", request.operation());
            
            SedOperation operation = buildOperation(request);
            SedResult result = engine.executeOperation(request.content(), operation);
            
            return new SedExecuteResponse(
                result.isSuccess(),
                result.getOriginalContent(),
                result.getModifiedContent(),
                result.isModified(),
                result.getLinesModified(),
                result.getChangesApplied(),
                result.getWarnings(),
                result.getExecutionTimeMs(),
                null
            );
            
        } catch (Exception e) {
            logger.error("Error executing sed operation", e);
            return new SedExecuteResponse(
                false, request.content(), request.content(), false, 0,
                List.of(), List.of(), 0, e.getMessage()
            );
        }
    }
    
    public SedExecuteResponse previewSedOperation(SedExecuteRequest request) {
        try {
            logger.info("Previewing sed operation: {}", request.operation());
            
            SedOperation operation = buildOperation(request);
            SedResult result = engine.previewOperation(request.content(), operation);
            
            return new SedExecuteResponse(
                result.isSuccess(),
                result.getOriginalContent(),
                result.getModifiedContent(),
                result.isModified(),
                result.getLinesModified(),
                result.getChangesApplied(),
                result.getWarnings(),
                0, // Preview doesn't track execution time
                null
            );
            
        } catch (Exception e) {
            logger.error("Error previewing sed operation", e);
            return new SedExecuteResponse(
                false, request.content(), request.content(), false, 0,
                List.of(), List.of(), 0, e.getMessage()
            );
        }
    }
    
    public SedValidateResponse validateSedOperation(SedValidateRequest request) {
        try {
            logger.info("Validating sed operation: {}", request.operation());
            
            SedOperation operation = buildOperationForValidation(request);
            engine.validateOperation(operation);
            
            return new SedValidateResponse(
                true,
                request.operation(),
                "Operation is valid",
                null
            );
            
        } catch (Exception e) {
            logger.warn("Validation failed for sed operation: {}", request.operation(), e);
            return new SedValidateResponse(
                false,
                request.operation(),
                null,
                e.getMessage()
            );
        }
    }
    
    private SedOperation buildOperation(SedExecuteRequest request) {
        SedOperation.OperationType type = SedOperation.OperationType.fromCommand(request.operation());
        SedOperation.Builder builder = SedOperation.builder().operation(type);
        
        if (request.pattern() != null) {
            builder.pattern(request.pattern());
        }
        if (request.replacement() != null) {
            builder.replacement(request.replacement());
        }
        if (request.flags() != null) {
            builder.flags(request.flags());
        }
        if (request.text() != null) {
            builder.text(request.text());
        }
        if (request.address() != null) {
            builder.address(request.address());
        }
        
        return builder.build();
    }
    
    private SedOperation buildOperationForValidation(SedValidateRequest request) {
        SedOperation.OperationType type = SedOperation.OperationType.fromCommand(request.operation());
        SedOperation.Builder builder = SedOperation.builder().operation(type);
        
        if (request.pattern() != null) {
            builder.pattern(request.pattern());
        }
        if (request.replacement() != null) {
            builder.replacement(request.replacement());
        }
        if (request.flags() != null) {
            builder.flags(request.flags());
        }
        if (request.text() != null) {
            builder.text(request.text());
        }
        if (request.address() != null) {
            builder.address(request.address());
        }
        
        return builder.build();
    }
}

// Request/Response records for MCP tool calls
record SedExecuteRequest(
    String content,
    String operation,
    String pattern,
    String replacement,
    String flags,
    String text,
    String address
) {}

record SedValidateRequest(
    String operation,
    String pattern,
    String replacement,
    String flags,
    String text,
    String address
) {}

record SedExecuteResponse(
    boolean success,
    String originalContent,
    String modifiedContent,
    boolean modified,
    int linesModified,
    List<String> changesApplied,
    List<String> warnings,
    long executionTimeMs,
    String error
) {}

record SedValidateResponse(
    boolean valid,
    String operation,
    String message,
    String error
) {}