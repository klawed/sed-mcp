# SED-MCP: A Model Context Protocol Server for sed Operations

A Spring Boot application that provides sed (stream editor) capabilities through the Model Context Protocol (MCP), enabling AI assistants to perform text transformations using familiar Unix sed commands.

## Prerequisites

Before you start, make sure you have:

- **Java 24** or later (because we're using the bleeding edge)
- **Maven 3.8+** (for all your dependency management needs)
- **Node.js 18+** (if you want to use the MCP Inspector tool)
- **Git** (obviously)

## Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/klawed/sed-mcp.git
cd sed-mcp
```

### 2. Build the Project

```bash
mvn clean compile
```

If this fails, check your Java version and Maven configuration. If it still fails, blame the cosmic rays.

### 3. Run Unit Tests

```bash
mvn test
```

This will run all the unit tests we've defined for the sed operations, models, and engine implementations.

### 4. Create Executable JAR

```bash
mvn clean package
```

This creates `target/sed-mcp-0.0.1-SNAPSHOT.jar` with all dependencies included.

## Core Features

### Implemented sed Operations

- **Substitution (`s/pattern/replacement/flags`)**
  - Basic substitution (first match only)
  - Global substitution with `g` flag
  - Case-insensitive substitution with `i` flag
  - Multiline mode with `m` flag
  - Dot-matches-all with `s` flag

- **Deletion (`/pattern/d`)**
  - Delete lines matching regex pattern
  - Track number of lines deleted

- **Print (`/pattern/p`)**
  - Extract lines matching regex pattern
  - Returns only matching lines (filtering)

### Advanced Features

- **Batch Operations**: Execute multiple sed operations in sequence
- **Preview Mode**: Test operations without committing changes
- **Comprehensive Error Handling**: Proper validation and error reporting
- **Performance Tracking**: Execution time measurement

## MCP Server Usage

### Building the MCP Server

```bash
mvn clean package
```

### MCP Inspector Setup

The [MCP Inspector](https://github.com/modelcontextprotocol/inspector) is useful for testing MCP servers without dealing with client integration headaches.

#### Install MCP Inspector

```bash
npm install -g @modelcontextprotocol/inspector
```

#### Test with MCP Inspector

Get the absolute path to your JAR file:

```bash
# On Linux/macOS
FULL_PATH=$(pwd)/target/sed-mcp-0.0.1-SNAPSHOT.jar
echo $FULL_PATH

# On Windows PowerShell
$FULL_PATH="$(Get-Location)\target\sed-mcp-0.0.1-SNAPSHOT.jar"
echo $FULL_PATH
```

Run the inspector:

```bash
npx @modelcontextprotocol/inspector java -jar $FULL_PATH
```

#### Using MCP Inspector

1. **Connection Verification**: The inspector should connect to your server and show it in the connection pane
2. **Tools Tab**: Navigate to see available tools (`sed_execute`, `sed_preview`, `sed_validate`)
3. **Test Tools**: Click on tools to test them and see their responses
4. **Monitor Logs**: Check the notifications pane for any errors or debug information

## Available MCP Tools

### `sed_execute`
Execute a sed operation on text content.

**Parameters:**
- `content` (string): The text content to process
- `operation` (string): The sed operation type (`s`, `d`, `p`)
- `pattern` (string): The regex pattern to match
- `replacement` (string): Replacement text (for substitution)
- `flags` (string): Operation flags (`g`, `i`, `m`, `s`)

### `sed_preview`
Preview a sed operation without modifying the original content.

**Parameters:** Same as `sed_execute`

### `sed_validate`
Validate a sed operation syntax without executing it.

**Parameters:** Same as `sed_execute` (but content not required)

## Configuration

### MCP Server Configuration

Configure in Claude Desktop like this:

```json
{
  "sed-mcp-server": {
    "command": "java",
    "args": [
      "-jar",
      "/full/path/to/your/sed-mcp-0.0.1-SNAPSHOT.jar"
    ]
  }
}
```

### Environment Variables

Set these if you want to customize behavior:

```bash
# Enable debug logging
export LOGGING_LEVEL_ROOT=DEBUG

# Set server port (if needed for debugging)
export SERVER_PORT=8080
```

## Development Workflow

### Project Structure

```
src/
├── main/java/dev/klawed/sedmcp/
│   ├── model/           # Data models (SedOperation, SedResult)
│   ├── service/         # Interface definitions
│   ├── service/impl/    # Implementations (including RealSedEngine)
│   ├── cli/             # Command line interface (basic testing)
│   └── Application.java # Main Spring Boot app with MCP tools
└── test/java/dev/klawed/sedmcp/
    ├── model/           # Unit tests for models
    └── service/impl/    # Unit tests for implementations
```

### Adding New Features

1. **Write the interface first** - Define what the feature should do
2. **Write unit tests** - Test the interface and implementation
3. **Implement the real thing** - Only after you know what you're building
4. **Test with MCP Inspector** - Protocol verification
5. **Update documentation** - Keep this guide current

### Running Different Test Categories

```bash
# Unit tests only
mvn test

# Specific test class
mvn test -Dtest="RealSedEngineTest"

# Specific test method
mvn test -Dtest="RealSedEngineTest#testSubstituteOperation"
```

### Code Coverage

Check test coverage:

```bash
mvn jacoco:prepare-agent test jacoco:report
```

Open `target/site/jacoco/index.html` in your browser to see coverage reports.

## Testing with Examples

### Basic Substitution
```json
{
  "content": "Hello world\nHello universe",
  "operation": "s",
  "pattern": "Hello",
  "replacement": "Hi",
  "flags": "g"
}
```

### Line Deletion
```json
{
  "content": "line 1\nline 2\nline 3",
  "operation": "d",
  "pattern": "line 2"
}
```

### Pattern Matching
```json
{
  "content": "apple\nbanana\napricot",
  "operation": "p",
  "pattern": "ap.*"
}
```

## Troubleshooting

### Common Issues

**"ClassNotFoundException"**: Your classpath is wrong. Use the fat JAR created by `mvn package`.

**"UnsupportedOperationException"**: You're trying to use a sed operation that isn't implemented yet. Check the engine's `supportsOperation()` method.

**"SecurityException"**: Path validation failed. Make sure you're not trying to access files outside the allowed directories.

### Debug Mode

Enable debug logging to see what's happening:

```bash
java -Dlogging.level.dev.klawed.sedmcp=DEBUG -jar target/sed-mcp-0.0.1-SNAPSHOT.jar
```

### Testing Connection Issues

If MCP Inspector can't connect:

1. Check that the JAR file exists and is executable
2. Verify Java is in your PATH
3. Look for error messages in the inspector console
4. Try running the JAR directly to see if it starts properly

## Security Notes

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

## Performance Considerations

### Memory Usage

- The current implementation processes content in memory
- Set appropriate JVM heap size for your use case: `-Xmx2g`

### Processing Speed

- Pattern compilation is expensive - patterns are compiled fresh each time
- Complex regex patterns can be catastrophically slow
- Current architecture is single-threaded and synchronous

## Current Limitations

1. **Limited sed operations**: Only substitute, delete, and print are implemented
2. **No address ranges**: Line number ranges (e.g., `1,5s/old/new/`) not supported
3. **No advanced sed features**: Hold space, branching, etc. not implemented
4. **Single-threaded**: No parallel processing for large content
5. **Memory-bound**: Large files must fit in memory

## Next Steps

### Immediate Priorities

1. **Add more sed operations** - Append, insert, change operations
2. **Implement address ranges** - Support line number ranges
3. **Add file I/O operations** - Read from and write to files
4. **Enhance error handling** - Better error messages and recovery

### Future Enhancements

1. **Advanced sed features** - Hold space, branching, labels
2. **Batch file processing** - Process multiple files in one operation
3. **Configuration management** - External config files
4. **Metrics and monitoring** - Operation statistics and health checks
5. **Performance optimization** - Streaming for large files, pattern caching

## Contributing

When adding new features:

1. **Follow the interface-first approach** - Define contracts before implementation
2. **Write tests first** - Both unit and integration tests
3. **Update documentation** - Keep this guide current
4. **Test with MCP Inspector** - Verify everything works
5. **Consider security implications** - Don't create new attack vectors

## References

- [Model Context Protocol Specification](https://modelcontextprotocol.github.io/spec/)
- [MCP Inspector Documentation](https://modelcontextprotocol.github.io/docs/tutorials/inspector)
- [Sed Manual](https://www.gnu.org/software/sed/manual/sed.html) (for when you need to remember how sed actually works)
- [Java Pattern Documentation](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/regex/Pattern.html)

Remember: This is a working sed MCP server implementation. The core functionality is implemented and tested, ready for integration with MCP clients like Claude Desktop.

Now stop reading documentation and go test some sed operations.