# Sed MCP Server

A Model Context Protocol (MCP) server that provides powerful text editing capabilities using sed (stream editor) operations. This server allows AI models to perform sophisticated text transformations through standardized MCP tools.

## Overview

This project transforms a basic MCP server into a comprehensive text editing tool that provides:
- sed-based text transformation operations
- File reading and writing capabilities  
- Pattern matching and replacement operations
- Text validation and preview functionality
- Batch processing capabilities

## Prerequisites

Before getting started, ensure you have:

- **Java 24** or later
- **Maven 3.8+**
- **Node.js 18+** (for MCP Inspector tool)
- **Git**

## Quick Start

### 1. Clone and Build

```bash
git clone https://github.com/klawed/javaone-mcp.git
cd javaone-mcp
mvn clean compile
```

### 2. Run Tests

```bash
mvn test
```

This runs all unit tests for sed operations, models, and mock implementations.

### 3. Run Integration Tests

```bash
mvn test -Dtest="*IntegrationTest"
```

Verifies that all components work together correctly.

### 4. Build Executable JAR

```bash
mvn clean package
```

Creates `target/sed-mcp-server-0.1.0.jar` with all dependencies included.

## Manual Testing with CLI Tool

Test sed operations interactively without MCP protocol overhead:

```bash
mvn compile exec:java -Dexec.mainClass="dev.klawed.sedmcp.cli.SedCli"
```

The CLI tool provides:
- Single sed operation testing
- Batch operation processing
- Preview mode (see changes before applying)
- Operation syntax validation
- Examples and help

## MCP Inspector Setup

The [MCP Inspector](https://github.com/modelcontextprotocol/inspector) is useful for testing MCP servers during development.

### Install MCP Inspector

```bash
npm install -g @modelcontextprotocol/inspector
```

### Test with MCP Inspector

Get the absolute path to your JAR file:

```bash
# On Linux/macOS
FULL_PATH=$(pwd)/target/sed-mcp-server-0.1.0.jar
echo $FULL_PATH

# On Windows PowerShell
$FULL_PATH="$(Get-Location)\target\sed-mcp-server-0.1.0.jar"
echo $FULL_PATH
```

Run the inspector:

```bash
npx @modelcontextprotocol/inspector java -jar $FULL_PATH
```

### Using MCP Inspector

1. **Connection Verification**: Verify the server connects in the connection pane
2. **Tools Tab**: View available MCP tools
3. **Test Operations**: Execute tools and examine responses
4. **Monitor Logs**: Check notifications pane for errors or debug info

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

## Available Sed Operations

The system supports major sed operation types:

### Substitute (s)
Replace text using pattern matching:
```bash
Operation: s
Pattern: old_text
Replacement: new_text
Flags: g (global replacement)
```

### Delete (d)
Remove lines matching patterns:
```bash
Operation: d
Pattern: error_pattern
```

### Insert (i)
Add text before specified lines:
```bash
Operation: i
Text: # New comment
Address: 1 (before line 1)
```

### Append (a)
Add text after specified lines:
```bash
Operation: a
Text: END OF FILE
```

Additional operations: Change (c) and Print (p)

## Development Workflow

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

Generate test coverage reports:

```bash
mvn jacoco:prepare-agent test jacoco:report
```

Open `target/site/jacoco/index.html` to view coverage details.

### Adding New Features

1. **Define interfaces first** - Establish clear contracts
2. **Create mock implementations** - Enable testing without dependencies
3. **Write comprehensive tests** - Both unit and integration tests
4. **Implement real functionality** - Build on validated interfaces
5. **Test with CLI tool** - Manual verification
6. **Test with MCP Inspector** - Protocol-level verification

## Configuration

### MCP Server Integration

Configure in Claude Desktop:

```json
{
  "sed-mcp-server": {
    "command": "java",
    "args": [
      "-jar",
      "/full/path/to/sed-mcp-server-0.1.0.jar"
    ]
  }
}
```

### Environment Variables

Customize behavior with environment variables:

```bash
# Maximum file size for processing (default: 10MB)
export SED_MAX_FILE_SIZE=10485760

# Enable debug logging
export SED_DEBUG=true

# Base directory for file operations
export SED_BASE_DIR=/tmp/sed-workspace
```

## Architecture

The system uses a clean, interface-driven architecture:

- **SedEngine**: Core text processing operations
- **FileService**: File I/O with security validation
- **Models**: SedOperation and SedResult with builder patterns
- **Mocks**: Full test implementations for development

Key design principles:
- Interface-first development for testability
- Comprehensive error handling and reporting
- Security-first file operations
- Performance monitoring and metrics

## Troubleshooting

### Common Issues

**Build fails**: Verify Java 24+ and Maven 3.8+ are installed
**Tests fail**: Check that all dependencies are resolved with `mvn dependency:resolve`
**MCP Inspector connection issues**: Ensure JAR file exists and Java is in PATH

### Debug Mode

Enable detailed logging:

```bash
java -Dorg.slf4j.simpleLogger.defaultLogLevel=debug -jar target/sed-mcp-server-0.1.0.jar
```

### Memory Configuration

For large file processing:

```bash
java -Xmx2g -jar target/sed-mcp-server-0.1.0.jar
```

## Implementation Status

### Current State
- ✅ Core interfaces and models
- ✅ Mock implementations for testing
- ✅ Comprehensive unit and integration tests
- ✅ CLI tool for manual testing
- ✅ Build configuration and dependencies

### Next Steps
- Real SedEngine implementation with regex processing
- Real FileService with secure file operations
- MCP tool wrappers for protocol integration
- Main application server implementation

## Contributing

When contributing:

1. Follow interface-first development approach
2. Write tests before implementation
3. Update documentation for any API changes
4. Test with both CLI tool and MCP Inspector
5. Consider security implications for file operations

## Security Considerations

The system includes built-in security measures:
- Path validation to prevent directory traversal
- File access controls and sandboxing
- Input validation for all sed operations
- Resource limits for processing

## Performance

Current architecture focuses on correctness and testability. Performance optimizations include:
- Pattern compilation caching
- Streaming for large files
- Memory-efficient line processing
- Execution time monitoring

## References

- [Model Context Protocol Specification](https://modelcontextprotocol.github.io/spec/)
- [MCP Inspector Documentation](https://modelcontextprotocol.github.io/docs/tutorials/inspector)
- [Sed Manual](https://www.gnu.org/software/sed/manual/sed.html)

## License

This project is open source and available under the MIT License.
