package dev.klawed.sedmcp.service.impl;

import dev.klawed.sedmcp.service.FileService;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Mock file service for testing. Because dealing with actual files 
 * during tests is a recipe for chaos and flaky builds.
 */
public class MockFileService implements FileService {
    
    private final Map<String, String> files = new HashMap<>();
    private final Map<String, String> encodings = new HashMap<>();
    private boolean shouldFailRead = false;
    private boolean shouldFailWrite = false;
    private String failureMessage = "Mock file operation failed";
    
    public void addFile(String path, String content) {
        files.put(path, content);
    }
    
    public void addFile(String path, String content, String encoding) {
        files.put(path, content);
        encodings.put(path, encoding);
    }
    
    public void setShouldFailRead(boolean shouldFailRead) {
        this.shouldFailRead = shouldFailRead;
    }
    
    public void setShouldFailWrite(boolean shouldFailWrite) {
        this.shouldFailWrite = shouldFailWrite;
    }
    
    public void setFailureMessage(String failureMessage) {
        this.failureMessage = failureMessage;
    }
    
    public void clear() {
        files.clear();
        encodings.clear();
    }
    
    public Map<String, String> getAllFiles() {
        return new HashMap<>(files);
    }
    
    @Override
    public String readFile(Path filePath) throws IOException {
        if (shouldFailRead) {
            throw new IOException(failureMessage);
        }
        
        String content = files.get(filePath.toString());
        if (content == null) {
            throw new IOException("File not found: " + filePath);
        }
        return content;
    }
    
    @Override
    public String readFile(Path filePath, String encoding) throws IOException {
        if (shouldFailRead) {
            throw new IOException(failureMessage);
        }
        
        String content = files.get(filePath.toString());
        if (content == null) {
            throw new IOException("File not found: " + filePath);
        }
        
        // Simulate encoding issues
        String fileEncoding = encodings.get(filePath.toString());
        if (fileEncoding != null && !fileEncoding.equals(encoding)) {
            // Simulate some encoding conversion issues
            return content + " [converted from " + fileEncoding + " to " + encoding + "]";
        }
        
        return content;
    }
    
    @Override
    public void writeFile(Path filePath, String content) throws IOException {
        if (shouldFailWrite) {
            throw new IOException(failureMessage);
        }
        
        files.put(filePath.toString(), content);
    }
    
    @Override
    public void writeFile(Path filePath, String content, String encoding) throws IOException {
        if (shouldFailWrite) {
            throw new IOException(failureMessage);
        }
        
        files.put(filePath.toString(), content);
        encodings.put(filePath.toString(), encoding);
    }
    
    @Override
    public Path createBackup(Path filePath) throws IOException {
        String originalContent = files.get(filePath.toString());
        if (originalContent == null) {
            throw new IOException("Cannot backup non-existent file: " + filePath);
        }
        
        Path backupPath = Path.of(filePath.toString() + ".backup");
        files.put(backupPath.toString(), originalContent);
        return backupPath;
    }
    
    @Override
    public boolean canRead(Path filePath) {
        return files.containsKey(filePath.toString()) && !shouldFailRead;
    }
    
    @Override
    public boolean canWrite(Path filePath) {
        return !shouldFailWrite;
    }
    
    @Override
    public long getFileSize(Path filePath) throws IOException {
        String content = files.get(filePath.toString());
        if (content == null) {
            throw new IOException("File not found: " + filePath);
        }
        return content.length();
    }
    
    @Override
    public void validatePath(Path filePath, Path allowedBasePath) {
        String path = filePath.toString();
        String basePath = allowedBasePath.toString();
        
        // Basic validation simulation
        if (path.contains("..") || path.contains("~")) {
            throw new SecurityException("Suspicious path detected: " + path);
        }
        
        if (!path.startsWith(basePath)) {
            throw new SecurityException("Path outside allowed base: " + path);
        }
    }
}
