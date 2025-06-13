package dev.klawed.sedmcp;

import dev.klawed.sedmcp.model.SedOperation;
import dev.klawed.sedmcp.model.SedResult;
import dev.klawed.sedmcp.service.SedEngine;
import dev.klawed.sedmcp.service.impl.RealSedEngine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.List;

/**
 * Main Spring Boot application for the sed MCP server.
 * This is the fallback Spring Boot application - the actual MCP server is in McpServer.java
 */
@SpringBootApplication
public class Application {
    
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

/**
 * Service that provides sed operations as a Spring Bean.
 * Note: This is not the main MCP server - see McpServer.java for the actual MCP implementation
 */
@Service
class SedService {
    
    private static final Logger logger = LoggerFactory.getLogger(SedService.class);
    private final SedEngine engine;
    
    public SedService() {
        this.engine = new RealSedEngine();
    }
    
    @PostConstruct
    public void init() {
        logger.info("Sed MCP Service initialized with RealSedEngine");
        logger.info("Note: The main MCP server implementation is in McpServer.java");
        logger.info("Supported operations: SUBSTITUTE, DELETE, PRINT");
    }
    
    public SedResult executeSedOperation(String content, SedOperation operation) {
        try {
            logger.info("Executing sed operation: {}", operation.getOperationType());
            return engine.executeOperation(content, operation);
        } catch (Exception e) {
            logger.error("Error executing sed operation", e);
            return SedResult.builder()
                .success(false)
                .originalContent(content)
                .modifiedContent(content)
                .errorMessage(e.getMessage())
                .build();
        }
    }
    
    public SedResult previewSedOperation(String content, SedOperation operation) {
        try {
            logger.info("Previewing sed operation: {}", operation.getOperationType());
            return engine.previewOperation(content, operation);
        } catch (Exception e) {
            logger.error("Error previewing sed operation", e);
            return SedResult.builder()
                .success(false)
                .originalContent(content)
                .modifiedContent(content)
                .errorMessage(e.getMessage())
                .build();
        }
    }
    
    public boolean validateSedOperation(SedOperation operation) {
        try {
            logger.info("Validating sed operation: {}", operation.getOperationType());
            engine.validateOperation(operation);
            return true;
        } catch (Exception e) {
            logger.warn("Validation failed for sed operation: {}", operation.getOperationType(), e);
            return false;
        }
    }
}
