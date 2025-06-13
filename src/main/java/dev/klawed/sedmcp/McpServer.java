package dev.klawed.sedmcp;

import dev.klawed.sedmcp.model.SedOperation;
import dev.klawed.sedmcp.model.SedResult;
import dev.klawed.sedmcp.service.impl.RealSedEngine;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Map;
import java.util.List;
import java.util.HashMap;

/**
 * Standalone MCP Server that communicates via JSON-RPC over stdio
 * This implements the actual MCP protocol for sed operations
 */
public class McpServer {
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RealSedEngine sedEngine = new RealSedEngine();
    private final PrintWriter out = new PrintWriter(System.out, true);
    private final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    
    public static void main(String[] args) {
        new McpServer().run();
    }
    
    public void run() {
        try {
            String line;
            while ((line = in.readLine()) != null) {
                handleMessage(line);
            }
        } catch (Exception e) {
            sendError(-32603, "Internal error: " + e.getMessage(), null);
        }
    }
    
    private void handleMessage(String message) {
        try {
            JsonNode request = objectMapper.readTree(message);
            String method = request.get("method").asText();
            JsonNode params = request.get("params");
            Object id = request.has("id") ? request.get("id") : null;
            
            switch (method) {
                case "initialize":
                    handleInitialize(id);
                    break;
                case "tools/list":
                    handleToolsList(id);
                    break;
                case "tools/call":
                    handleToolCall(params, id);
                    break;
                default:
                    sendError(-32601, "Method not found", id);
            }
        } catch (Exception e) {
            sendError(-32700, "Parse error", null);
        }
    }
    
    private void handleInitialize(Object id) {
        Map<String, Object> result = new HashMap<>();
        result.put("protocolVersion", "2024-11-05");
        result.put("capabilities", Map.of(
            "tools", Map.of()
        ));
        result.put("serverInfo", Map.of(
            "name", "sed-mcp",
            "version", "0.0.1"
        ));
        
        sendResponse(result, id);
    }
    
    private void handleToolsList(Object id) {
        List<Map<String, Object>> tools = List.of(
            Map.of(
                "name", "sed_execute",
                "description", "Execute a sed operation on text content",
                "inputSchema", Map.of(
                    "type", "object",
                    "properties", Map.of(
                        "content", Map.of("type", "string", "description", "Text content to process"),
                        "operation", Map.of("type", "string", "description", "Sed operation (s, d, p)"),
                        "pattern", Map.of("type", "string", "description", "Regex pattern"),
                        "replacement", Map.of("type", "string", "description", "Replacement text"),
                        "flags", Map.of("type", "string", "description", "Operation flags (g, i, m)")
                    ),
                    "required", List.of("content", "operation", "pattern")
                )
            ),
            Map.of(
                "name", "sed_preview",
                "description", "Preview a sed operation without modifying content",
                "inputSchema", Map.of(
                    "type", "object",
                    "properties", Map.of(
                        "content", Map.of("type", "string", "description", "Text content to process"),
                        "operation", Map.of("type", "string", "description", "Sed operation (s, d, p)"),
                        "pattern", Map.of("type", "string", "description", "Regex pattern"),
                        "replacement", Map.of("type", "string", "description", "Replacement text"),
                        "flags", Map.of("type", "string", "description", "Operation flags (g, i, m)")
                    ),
                    "required", List.of("content", "operation", "pattern")
                )
            ),
            Map.of(
                "name", "sed_validate",
                "description", "Validate sed operation syntax",
                "inputSchema", Map.of(
                    "type", "object",
                    "properties", Map.of(
                        "operation", Map.of("type", "string", "description", "Sed operation (s, d, p)"),
                        "pattern", Map.of("type", "string", "description", "Regex pattern"),
                        "replacement", Map.of("type", "string", "description", "Replacement text"),
                        "flags", Map.of("type", "string", "description", "Operation flags (g, i, m)")
                    ),
                    "required", List.of("operation", "pattern")
                )
            )
        );
        
        sendResponse(Map.of("tools", tools), id);
    }
    
    private void handleToolCall(JsonNode params, Object id) {
        try {
            String toolName = params.get("name").asText();
            JsonNode arguments = params.get("arguments");
            
            switch (toolName) {
                case "sed_execute":
                    handleSedExecute(arguments, id);
                    break;
                case "sed_preview":
                    handleSedPreview(arguments, id);
                    break;
                case "sed_validate":
                    handleSedValidate(arguments, id);
                    break;
                default:
                    sendError(-32602, "Unknown tool: " + toolName, id);
            }
        } catch (Exception e) {
            sendError(-32603, "Tool execution error: " + e.getMessage(), id);
        }
    }
    
    private void handleSedExecute(JsonNode args, Object id) {
        try {
            SedOperation operation = buildOperation(args);
            String content = args.get("content").asText();
            
            SedResult result = sedEngine.executeOperation(content, operation);
            
            Map<String, Object> response = Map.of(
                "content", List.of(Map.of(
                    "type", "text",
                    "text", formatSedResult(result)
                ))
            );
            
            sendResponse(response, id);
        } catch (Exception e) {
            sendError(-32603, "Sed execution failed: " + e.getMessage(), id);
        }
    }
    
    private void handleSedPreview(JsonNode args, Object id) {
        try {
            SedOperation operation = buildOperation(args);
            String content = args.get("content").asText();
            
            SedResult result = sedEngine.previewOperation(content, operation);
            
            Map<String, Object> response = Map.of(
                "content", List.of(Map.of(
                    "type", "text",
                    "text", "PREVIEW: " + formatSedResult(result)
                ))
            );
            
            sendResponse(response, id);
        } catch (Exception e) {
            sendError(-32603, "Sed preview failed: " + e.getMessage(), id);
        }
    }
    
    private void handleSedValidate(JsonNode args, Object id) {
        try {
            SedOperation operation = buildOperation(args);
            sedEngine.validateOperation(operation);
            
            Map<String, Object> response = Map.of(
                "content", List.of(Map.of(
                    "type", "text",
                    "text", "✅ Operation is valid: " + operation.getOperationType()
                ))
            );
            
            sendResponse(response, id);
        } catch (Exception e) {
            Map<String, Object> response = Map.of(
                "content", List.of(Map.of(
                    "type", "text",
                    "text", "❌ Validation failed: " + e.getMessage()
                ))
            );
            
            sendResponse(response, id);
        }
    }
    
    private SedOperation buildOperation(JsonNode args) {
        String operationStr = args.get("operation").asText();
        SedOperation.OperationType type = SedOperation.OperationType.fromCommand(operationStr);
        
        SedOperation.Builder builder = SedOperation.builder().operation(type);
        
        if (args.has("pattern")) {
            builder.pattern(args.get("pattern").asText());
        }
        if (args.has("replacement")) {
            builder.replacement(args.get("replacement").asText());
        }
        if (args.has("flags")) {
            builder.flags(args.get("flags").asText());
        }
        
        return builder.build();
    }
    
    private String formatSedResult(SedResult result) {
        if (!result.isSuccess()) {
            return "❌ Error: " + result.getError();
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("✅ Success!\n");
        sb.append("Modified: ").append(result.isModified()).append("\n");
        sb.append("Lines modified: ").append(result.getLinesModified()).append("\n");
        sb.append("Execution time: ").append(result.getExecutionTimeMs()).append("ms\n\n");
        sb.append("Result:\n").append(result.getModifiedContent());
        
        if (!result.getChangesApplied().isEmpty()) {
            sb.append("\n\nChanges applied:\n");
            result.getChangesApplied().forEach(change -> sb.append("- ").append(change).append("\n"));
        }
        
        return sb.toString();
    }
    
    private void sendResponse(Object result, Object id) {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("jsonrpc", "2.0");
            response.put("result", result);
            if (id != null) {
                response.put("id", id);
            }
            
            out.println(objectMapper.writeValueAsString(response));
        } catch (Exception e) {
            sendError(-32603, "Response serialization error", id);
        }
    }
    
    private void sendError(int code, String message, Object id) {
        try {
            Map<String, Object> error = Map.of(
                "code", code,
                "message", message
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("jsonrpc", "2.0");
            response.put("error", error);
            if (id != null) {
                response.put("id", id);
            }
            
            out.println(objectMapper.writeValueAsString(response));
        } catch (Exception e) {
            // Last resort - print simple error
            out.println("{\"jsonrpc\":\"2.0\",\"error\":{\"code\":-32603,\"message\":\"Internal error\"}}");
        }
    }
}