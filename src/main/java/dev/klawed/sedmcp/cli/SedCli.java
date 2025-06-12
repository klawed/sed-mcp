package dev.klawed.sedmcp.cli;

import dev.klawed.sedmcp.model.SedOperation;
import dev.klawed.sedmcp.model.SedResult;
import dev.klawed.sedmcp.service.SedEngine;
import dev.klawed.sedmcp.service.SedEngineException;
import dev.klawed.sedmcp.service.impl.MockSedEngine;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Command line interface for testing sed operations manually.
 * Because sometimes you want to poke at things directly without 
 * going through the whole MCP protocol dance.
 */
public class SedCli {
    
    private final SedEngine engine;
    private final Scanner scanner;
    private boolean running = true;
    
    public SedCli(SedEngine engine) {
        this.engine = engine;
        this.scanner = new Scanner(System.in);
    }
    
    public static void main(String[] args) {
        System.out.println("=== Sed MCP CLI Tool ===");
        System.out.println("Because sometimes you need to test things the hard way.");
        System.out.println();
        
        SedEngine engine = new MockSedEngine();
        SedCli cli = new SedCli(engine);
        cli.run();
    }
    
    public void run() {
        while (running) {
            showMenu();
            String choice = readInput("Choose an option: ");
            handleChoice(choice);
        }
        System.out.println("Thanks for using the Sed CLI. May your regex always compile.");
    }
    
    private void showMenu() {
        System.out.println();
        System.out.println("=== Main Menu ===");
        System.out.println("1. Execute single sed operation");
        System.out.println("2. Execute batch operations");
        System.out.println("3. Preview operation");
        System.out.println("4. Validate operation");
        System.out.println("5. Test engine capabilities");
        System.out.println("6. Show examples");
        System.out.println("0. Quit");
        System.out.println();
    }
    
    private void handleChoice(String choice) {
        try {
            switch (choice.trim()) {
                case "1":
                    executeSingleOperation();
                    break;
                case "2":
                    executeBatchOperations();
                    break;
                case "3":
                    previewOperation();
                    break;
                case "4":
                    validateOperation();
                    break;
                case "5":
                    testCapabilities();
                    break;
                case "6":
                    showExamples();
                    break;
                case "0":
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Try again or learn to read.");
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            System.err.println("Well, that didn't work as expected.");
        }
    }
    
    private void executeSingleOperation() {
        System.out.println("\\n=== Execute Single Operation ===");
        
        String content = readMultilineInput("Enter the text content:");
        SedOperation operation = buildOperation();
        
        try {
            long start = System.currentTimeMillis();
            SedResult result = engine.executeOperation(content, operation);
            long duration = System.currentTimeMillis() - start;
            
            displayResult(result, duration);
        } catch (SedEngineException e) {
            System.err.println("Sed operation failed: " + e.getMessage());
        }
    }
    
    private void executeBatchOperations() {
        System.out.println("\\n=== Execute Batch Operations ===");
        
        String content = readMultilineInput("Enter the text content:");
        List<SedOperation> operations = new ArrayList<>();
        
        String addMore;
        do {
            System.out.println("\\nOperation " + (operations.size() + 1) + ":");
            operations.add(buildOperation());
            addMore = readInput("Add another operation? (y/n): ");
        } while (addMore.toLowerCase().startsWith("y"));
        
        try {
            long start = System.currentTimeMillis();
            SedResult result = engine.executeBatch(content, operations);
            long duration = System.currentTimeMillis() - start;
            
            displayResult(result, duration);
        } catch (SedEngineException e) {
            System.err.println("Batch operation failed: " + e.getMessage());
        }
    }
    
    private void previewOperation() {
        System.out.println("\\n=== Preview Operation ===");
        System.out.println("See what would happen without actually doing it.");
        
        String content = readMultilineInput("Enter the text content:");
        SedOperation operation = buildOperation();
        
        try {
            SedResult result = engine.previewOperation(content, operation);
            System.out.println("\\n--- Preview Results ---");
            displayResult(result, 0);
        } catch (SedEngineException e) {
            System.err.println("Preview failed: " + e.getMessage());
        }
    }
    
    private void validateOperation() {
        System.out.println("\\n=== Validate Operation ===");
        System.out.println("Check if your sed operation makes sense.");
        
        SedOperation operation = buildOperation();
        
        try {
            engine.validateOperation(operation);
            System.out.println("✓ Operation is valid. Congratulations on basic competence.");
        } catch (SedEngineException e) {
            System.err.println("✗ Validation failed: " + e.getMessage());
            System.err.println("Maybe try reading the sed manual sometime.");
        }
    }
    
    private void testCapabilities() {
        System.out.println("\\n=== Engine Capabilities ===");
        
        for (SedOperation.OperationType type : SedOperation.OperationType.values()) {
            boolean supported = engine.supportsOperation(type);
            String status = supported ? "✓" : "✗";
            System.out.printf("%s %s (%s)%n", status, type.name(), type.getCommand());
        }
    }
    
    private void showExamples() {
        System.out.println("\\n=== Sed Operation Examples ===");
        System.out.println("Because apparently everyone needs hand-holding.");
        System.out.println();
        
        System.out.println("1. Substitute (replace text):");
        System.out.println("   Operation: s");
        System.out.println("   Pattern: hello");
        System.out.println("   Replacement: hi");
        System.out.println("   Flags: g (global replacement)");
        System.out.println();
        
        System.out.println("2. Delete lines:");
        System.out.println("   Operation: d");
        System.out.println("   Pattern: error");
        System.out.println("   (Deletes lines containing 'error')");
        System.out.println();
        
        System.out.println("3. Insert text:");
        System.out.println("   Operation: i");
        System.out.println("   Text: # New comment");
        System.out.println("   Address: 1 (insert before line 1)");
        System.out.println();
        
        System.out.println("4. Append text:");
        System.out.println("   Operation: a");
        System.out.println("   Text: END OF FILE");
        System.out.println("   (Appends after last line)");
    }
    
    private SedOperation buildOperation() {
        System.out.println("\\nBuilding sed operation...");
        
        String opType = readInput("Operation type (s/d/p/a/i/c): ").toLowerCase();
        SedOperation.OperationType type;
        
        try {
            type = SedOperation.OperationType.fromCommand(opType);
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid operation type. Using substitute as fallback.");
            type = SedOperation.OperationType.SUBSTITUTE;
        }
        
        SedOperation.Builder builder = SedOperation.builder().operation(type);
        
        switch (type) {
            case SUBSTITUTE:
                String pattern = readInput("Pattern (regex): ");
                String replacement = readInput("Replacement: ");
                String flags = readInput("Flags (g/i/p/etc, or empty): ");
                builder.pattern(pattern).replacement(replacement).flags(flags);
                break;
                
            case DELETE:
            case PRINT:
                String delPattern = readInput("Pattern (or empty for address): ");
                String address = readInput("Address (or empty for pattern): ");
                if (!delPattern.isEmpty()) {
                    builder.pattern(delPattern);
                }
                if (!address.isEmpty()) {
                    builder.address(address);
                }
                break;
                
            case INSERT:
            case APPEND:
            case CHANGE:
                String text = readInput("Text to " + type.name().toLowerCase() + ": ");
                String addr = readInput("Address (optional): ");
                builder.text(text);
                if (!addr.isEmpty()) {
                    builder.address(addr);
                }
                break;
        }
        
        return builder.build();
    }
    
    private String readInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }
    
    private String readMultilineInput(String prompt) {
        System.out.println(prompt);
        System.out.println("(Enter text, end with a line containing only 'END')");
        
        StringBuilder content = new StringBuilder();
        String line;
        
        while (!(line = scanner.nextLine()).equals("END")) {
            if (content.length() > 0) {
                content.append("\\n");
            }
            content.append(line);
        }
        
        return content.toString();
    }
    
    private void displayResult(SedResult result, long actualDuration) {
        System.out.println("\\n=== Results ===");
        System.out.println("Success: " + result.isSuccess());
        System.out.println("Modified: " + result.isModified());
        System.out.println("Lines changed: " + result.getLinesModified());
        
        if (actualDuration > 0) {
            System.out.println("Execution time: " + actualDuration + "ms");
        }
        
        if (result.hasWarnings()) {
            System.out.println("\\nWarnings:");
            result.getWarnings().forEach(w -> System.out.println("  ! " + w));
        }
        
        if (!result.isSuccess() && result.getErrorMessage() != null) {
            System.err.println("\\nError: " + result.getErrorMessage());
        }
        
        if (!result.getChangesApplied().isEmpty()) {
            System.out.println("\\nChanges applied:");
            result.getChangesApplied().forEach(c -> System.out.println("  • " + c));
        }
        
        System.out.println("\\n--- Original Content ---");
        System.out.println(result.getOriginalContent());
        
        System.out.println("\\n--- Modified Content ---");
        System.out.println(result.getModifiedContent());
        
        if (result.isModified()) {
            System.out.println("\\n(Content was changed - you're welcome)");
        } else {
            System.out.println("\\n(No changes made - either perfect input or useless operation)");
        }
    }
}
