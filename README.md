# Setup and Development Guide

Because apparently we need yet another guide to get this thing running. Here's how to set up the Sed MCP Server for development and testing.

## Prerequisites

Before you start complaining about things not working, make sure you have:

- **Java 24** or later (because why use something stable when you can use the bleeding edge)
- **Maven 3.8+** (for all your dependency nightmare needs)
- **Node.js 18+** (if you want to use the MCP Inspector tool)
- **Git** (obviously)

## Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/klawed/javaone-mcp.git
cd javaone-mcp
```

### 2. Update Maven Dependencies

The `pom.xml` needs some additional dependencies for our sed functionality. Add these to your dependencies section:

```xml
<!-- JUnit 5 for testing -->
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter-engine</artifactId>
    <version>5.10.0</version>
    <scope>test</scope>
</dependency>

<!-- Commons IO for file operations -->
<dependency>
    <groupId>commons-io</groupId>
    <artifactId>commons-io</artifactId>
    <version>2.11.0</version>
</dependency>

<!-- Commons Lang for utilities -->
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
    <version>3.12.0</version>
</dependency>
```

### 3. Build the Project

```bash
mvn clean compile
```

If this fails, check your Java version and Maven configuration. If it still fails, blame the cosmic rays.

### 4. Run Unit Tests

```bash
mvn test
```

This will run all the unit tests we've defined for the sed operations, models, and mocks.

### 5. Run Integration Tests

```bash
mvn test -Dtest="*IntegrationTest"
```

Watch as our integration tests verify that all the pieces actually work together (shocking, I know).

## Testing with CLI Tool

### Manual Testing

Run the CLI tool to manually test sed operations:

```bash
mvn compile exec:java -Dexec.mainClass="dev.klawed.sedmcp.cli.SedCli"
```

This will start an interactive CLI where you can:
- Test individual sed operations
- Run batch operations
- Preview changes before applying them
- Validate operation syntax

The CLI is useful for understanding how the sed operations work before integrating with MCP.

## MCP Inspector Setup

The [MCP Inspector](https://github.com/modelcontextprotocol/inspector) is actually useful for testing MCP servers without dealing with client integration headaches.

### Install MCP Inspector

```bash
npm install -g @modelcontextprotocol/inspector
```

### Build the MCP Server JAR

```bash
mvn clean package
```

This creates `target/javaone-mcp-0.0.2.jar` (or whatever version number we're pretending to use).

### Test with MCP Inspector

Get the absolute path to your JAR file:

```bash
# On Linux/macOS
FULL_PATH=$(pwd)/target/javaone-mcp-0.0.2.jar
echo $FULL_PATH

# On Windows PowerShell
$FULL_PATH="$(Get-Location)\target\javaone-mcp-0.0.2.jar"
echo $FULL_PATH
```

Run the inspector:

```bash
npx @modelcontextprotocol/inspector java -jar $FULL_PATH
```

### Using MCP Inspector

1. **Connection Verification**: The inspector should connect to your server and show it in the connection pane
2. **Tools Tab**: Navigate to see available tools (currently just the JavaOne presentation tools, but soon to be sed tools)
3. **Test Tools**: Click on tools to test them and see their responses
4. **Monitor Logs**: Check the notifications pane for any errors or debug information

## Development Workflow

### Adding New Features

1. **Write the interface first** - Define what the feature should do
2. **Create a mock implementation** - For testing without dependencies
3. **Write unit tests** - Test the interface and mock
4. **Write integration tests** - Test how it works with other components
5. **Implement the real thing** - Only after you know what you're building
6. **Test with CLI tool** - Manual verification
7. **Test with MCP Inspector** - Protocol verification

### Running Different Test Categories

```bash
# Unit tests only
mvn test -Dtest="*Test" -Dtest="!*IntegrationTest"

# Integration tests only  
mvn test -Dtest="*IntegrationTest"

# All tests
mvn test

# Specific test class
mvn test -Dtest="SedOperationTest"

# Specific test method
mvn test -Dtest="SedOperationTest#testSubstituteOperationBuilder"
```

### Code Coverage

Check test coverage (because apparently we need metrics for everything):

```bash
mvn jacoco:prepare-agent test jacoco:report
```

Open `target/site/jacoco/index.html` in your browser to see coverage reports.

## Project Structure

```
src/
├── main/java/dev/klawed/sedmcp/
│   ├── model/           # Data models (SedOperation, SedResult)
│   ├── service/         # Interface definitions
│   ├── service/impl/    # Implementations (including mocks)
│   └── cli/            # Command line interface
└── test/java/dev/klawed/sedmcp/
    ├── model/          # Unit tests for models
    ├── service/impl/   # Unit tests for implementations
    └── integration/    # Integration tests
```

## Configuration

### MCP Server Configuration

When this thing is actually implemented, you'll configure it in Claude Desktop like this:

```json
{
  "sed-mcp-server": {
    "command": "java",
    "args": [
      "-jar",
      "/full/path/to/your/javaone-mcp-0.0.2.jar"
    ]
  }
}
```

### Environment Variables

Set these if you want to customize behavior:

```bash
# Maximum file size for processing (default: 10MB)
export SED_MAX_FILE_SIZE=10485760

# Enable debug logging
export SED_DEBUG=true

# Base directory for file operations
export SED_BASE_DIR=/tmp/sed-workspace
```

## Troubleshooting

### Common Issues

**"ClassNotFoundException"**: Your classpath is wrong. Use the fat JAR created by `mvn package`.

**"UnsupportedOperationException"**: You're trying to use a sed operation that isn't implemented yet. Check the engine's `supportsOperation()` method.

**"SecurityException"**: Path validation failed. Make sure you're not trying to access files outside the allowed directories.

**"MockSedEngine used in production"**: Well, that's embarrassing. Make sure you're using the real implementation, not the mock.

### Debug Mode

Enable debug logging to see what's happening:

```bash
java -Dorg.slf4j.simpleLogger.defaultLogLevel=debug -jar target/javaone-mcp-0.0.2.jar
```

### Testing Connection Issues

If MCP Inspector can't connect:

1. Check that the JAR file exists and is executable
2. Verify Java is in your PATH
3. Look for error messages in the inspector console
4. Try running the JAR directly to see if it starts properly

### Memory Issues

For large files or complex operations:

```bash
java -Xmx2g -jar target/javaone-mcp-0.0.2.jar
```

## Testing Strategies

### Unit Testing

Our unit tests focus on individual components:

- **Model tests**: Verify data validation and serialization
- **Service tests**: Test business logic in isolation
- **Mock tests**: Ensure test infrastructure works

### Integration Testing

Integration tests verify end-to-end workflows:

- **File processing**: Read → Transform → Write
- **Error handling**: How failures propagate through the system
- **Security**: Path validation and access controls
- **Performance**: Timing and resource usage

### Manual Testing with CLI

The CLI tool lets you test operations interactively:

```bash
# Example session
Choose an option: 1
Enter the text content:
Hello world
This is a test
END

Operation type (s/d/p/a/i/c): s
Pattern (regex): Hello
Replacement: Hi
Flags (g/i/p/etc, or empty): g
```

### MCP Protocol Testing

Use MCP Inspector to verify:

- Tool registration and discovery
- Request/response handling
- Error propagation
- Schema validation

## Performance Considerations

### Memory Usage

- The mock implementations store everything in memory
- Real implementations should stream large files
- Set appropriate JVM heap size for your use case

### Processing Speed

- Pattern compilation is expensive - cache compiled patterns
- File I/O can be slow - use appropriate buffer sizes
- Complex regex patterns can be catastrophically slow

### Scalability

Current architecture is single-threaded and synchronous. For high-throughput scenarios, consider:

- Async processing with CompletableFuture
- Thread pools for parallel operations
- Streaming APIs for large files

## Security Notes

### File Access Controls

The system includes basic path validation:

```java
fileService.validatePath(userPath, allowedBasePath);
```

This prevents:
- Path traversal attacks (`../../../etc/passwd`)
- Access outside allowed directories
- Symlink attacks (in real implementation)

### Input Validation

All sed operations are validated before execution:

- Pattern syntax checking
- Operation type validation
- Parameter presence verification

### Resource Limits

Consider implementing:
- Maximum file size limits
- Regex complexity limits
- Processing time timeouts
- Memory usage monitoring

## Next Steps

### Immediate Priorities

1. **Implement real SedEngine** - Replace mock with actual sed operations
2. **Add real FileService** - Replace mock with actual file I/O
3. **Create MCP tool wrappers** - Bridge between MCP protocol and sed operations
4. **Update main Application** - Wire everything together

### Future Enhancements

1. **Advanced sed features** - Address ranges, hold space, etc.
2. **Batch file processing** - Process multiple files in one operation
3. **Undo/redo support** - Maintain operation history
4. **Configuration management** - External config files
5. **Metrics and monitoring** - Operation statistics and health checks

## Contributing

When adding new features:

1. **Follow the interface-first approach** - Define contracts before implementation
2. **Write tests first** - Both unit and integration tests
3. **Update documentation** - Keep this guide current
4. **Test with CLI and MCP Inspector** - Verify everything works
5. **Consider security implications** - Don't create new attack vectors

## References

- [Model Context Protocol Specification](https://modelcontextprotocol.github.io/spec/)
- [MCP Inspector Documentation](https://modelcontextprotocol.github.io/docs/tutorials/inspector)
- [Sed Manual](https://www.gnu.org/software/sed/manual/sed.html) (for when you need to remember how sed actually works)
- [Java Pattern Documentation](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/regex/Pattern.html)

Remember: This is development infrastructure, not the final implementation. The mocks and CLI tool exist to make development easier, not to replace the real components.

Now stop reading documentation and go write some code.
