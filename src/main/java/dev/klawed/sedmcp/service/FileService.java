package dev.klawed.sedmcp.service;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Interface for file operations because apparently reading and writing files 
 * needs an abstraction layer too. At least it makes testing easier.
 */
public interface FileService {
    
    /**
     * Read the entire content of a file as a string.
     * 
     * @param filePath Path to the file
     * @return File content as string
     * @throws IOException if the file decides to be difficult
     */
    String readFile(Path filePath) throws IOException;
    
    /**
     * Read file with specific encoding because UTF-8 isn't always enough.
     * 
     * @param filePath Path to the file
     * @param encoding Character encoding to use
     * @return File content as string
     * @throws IOException if encoding or file reading fails
     */
    String readFile(Path filePath, String encoding) throws IOException;
    
    /**
     * Write content to a file, overwriting if it exists.
     * 
     * @param filePath Path where to write
     * @param content Content to write
     * @throws IOException if writing fails
     */
    void writeFile(Path filePath, String content) throws IOException;
    
    /**
     * Write content with specific encoding.
     * 
     * @param filePath Path where to write
     * @param content Content to write
     * @param encoding Character encoding to use
     * @throws IOException if writing fails
     */
    void writeFile(Path filePath, String content, String encoding) throws IOException;
    
    /**
     * Create a backup of an existing file before modifying it.
     * Because sometimes you want an undo button.
     * 
     * @param filePath Path to the file to backup
     * @return Path to the backup file
     * @throws IOException if backup creation fails
     */
    Path createBackup(Path filePath) throws IOException;
    
    /**
     * Check if a file exists and is readable.
     * 
     * @param filePath Path to check
     * @return true if file exists and can be read
     */
    boolean canRead(Path filePath);
    
    /**
     * Check if a file can be written to (or created if it doesn't exist).
     * 
     * @param filePath Path to check
     * @return true if file can be written
     */
    boolean canWrite(Path filePath);
    
    /**
     * Get file size in bytes.
     * 
     * @param filePath Path to the file
     * @return Size in bytes
     * @throws IOException if file doesn't exist or can't be read
     */
    long getFileSize(Path filePath) throws IOException;
    
    /**
     * Validate that a path is safe to use (no path traversal attacks).
     * 
     * @param filePath Path to validate
     * @param allowedBasePath Base path that must contain the file
     * @throws SecurityException if path is suspicious
     */
    void validatePath(Path filePath, Path allowedBasePath);
}
