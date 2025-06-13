# Integration Test Strategy

## Overview

This document outlines the comprehensive integration testing strategy for the sed-mcp project. Based on the current architecture and README analysis, we need to test end-to-end workflows involving Spring Boot, Spring AI MCP server, and sed operations.

## Current Architecture Analysis

From the README, the system has:
- **Spring Boot MCP Server** with `@Tool` annotated methods
- **Sed Engine** (currently mock, future real implementation)
- **CLI Tool** for manual testing
- **Multiple test layers**: unit tests, integration tests, and MCP protocol tests

## Integration Test Categories

### 1. **Spring Boot Context Integration Tests**
**Purpose**: Verify that the complete Spring application context loads and MCP auto-configuration works.

**Test Scenarios**:
- ✅ Application context loads successfully
- ✅ MCP server auto-configuration activates
- ✅ All `@Tool` annotated methods are registered
- ✅ SedService bean is created and injected properly
- ✅ Security configuration doesn't interfere with MCP operations

### 2. **MCP Protocol Integration Tests**
**Purpose**: Test the complete MCP server protocol compliance and tool execution.

**Test Scenarios**:
- **Tool Discovery**: MCP clients can discover available sed tools
- **Tool Execution**: End-to-end tool calls with proper JSON request/response
- **Error Handling**: MCP protocol error responses for invalid operations
- **Schema Validation**: JSON schema generation for tool parameters
- **Multiple Tool Calls**: Sequential tool execution in same session

### 3. **Sed Operations Integration Tests**
**Purpose**: Test complete sed operation workflows from MCP request to sed result.

**Test Scenarios**:
- **Basic Operations**: Execute, preview, and validate sed operations
- **Complex Patterns**: Multi-line patterns, regex with special characters
- **Error Scenarios**: Invalid patterns, malformed requests
- **Content Types**: Different text formats and encodings
- **Performance**: Large content processing and timeout handling

### 4. **File System Integration Tests** (Future)
**Purpose**: Test file operations when real FileService is implemented.

**Test Scenarios**:
- **File Processing**: Read → Transform → Write workflows
- **Path Security**: Path traversal prevention and validation
- **Permissions**: File access control and error handling
- **Batch Operations**: Multiple file processing
- **Backup/Recovery**: File backup and rollback operations

## Testing Infrastructure

### TestContainers Usage

**Why TestContainers?**
- **Isolated Testing**: Each test runs in clean environment
- **MCP Client Simulation**: Test MCP protocol from client perspective
- **External Tool Testing**: Test with actual MCP Inspector-like clients
- **Container Orchestration**: Multi-service integration testing

**TestContainers Strategy**:

```java
@Testcontainers
class McpServerIntegrationTest {
    
    @Container
    static GenericContainer<?> mcpServer = new GenericContainer<>("openjdk:24-jdk")
        .withExposedPorts(8080)
        .withFileSystemBind("target/sed-mcp.jar", "/app/sed-mcp.jar")
        .withCommand("java", "-jar", "/app/sed-mcp.jar")
        .waitingFor(Wait.forHttp("/actuator/health"));
    
    @Container
    static GenericContainer<?> mcpClient = new GenericContainer<>("node:18")
        .withExposedPorts(3000)
        .withCommand("npx", "@modelcontextprotocol/inspector", "--headless")
        .dependsOn(mcpServer);
}
```

### Test Data Management

**Strategy**:
- **Embedded Test Data**: Small, focused test cases
- **External Test Files**: Complex sed operations and edge cases
- **Generated Content**: Performance testing with large datasets
- **Error Scenarios**: Malformed JSON, invalid patterns, security violations

## Test Implementation Plan

### Phase 1: Foundation Tests (Immediate)

**1.1 Enhanced Context Test**
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
    "spring.ai.mcp.server.enabled=true",
    "spring.ai.mcp.server.type=SYNC",
    "logging.level.org.springframework.ai=DEBUG"
})
class McpServerContextIntegrationTest {
    
    @Autowired
    private ApplicationContext context;
    
    @Test
    void verifyMcpServerConfiguration() {
        // Verify MCP server beans are created
        // Verify tool registration
        // Verify health endpoints
    }
}
```

**1.2 Tool Registration Test**
```java
@Test
void verifyToolsAreRegistered() {
    // Check that sed_execute, sed_preview, sed_validate are registered
    // Verify JSON schema generation for tool parameters
    // Test tool metadata and descriptions
}
```

### Phase 2: MCP Protocol Tests

**2.1 Tool Execution Test**
```java
@Test
void testSedExecuteToolExecution() {
    // Create valid MCP tool call request
    // Execute sed_execute tool
    // Verify response format and content
    // Assert successful operation results
}
```

**2.2 Error Handling Test**
```java
@Test
void testInvalidPatternHandling() {
    // Send invalid regex pattern
    // Verify proper MCP error response
    // Check error messages are descriptive
}
```

### Phase 3: End-to-End Workflow Tests

**3.1 Complete Sed Operation Test**
```java
@Test 
void testCompleteSubstitutionWorkflow() {
    // 1. Validate operation with sed_validate
    // 2. Preview changes with sed_preview  
    // 3. Execute operation with sed_execute
    // 4. Verify results match preview
}
```

**3.2 TestContainers MCP Client Test**
```java
@Testcontainers
class McpClientServerIntegrationTest {
    
    @Test
    void testMcpInspectorConnection() {
        // Start MCP server in container
        // Connect MCP Inspector client
        // Discover and execute tools
        // Verify protocol compliance
    }
}
```

### Phase 4: Performance and Security Tests

**4.1 Performance Test**
```java
@Test
void testLargeContentProcessing() {
    // Generate large text content (1MB+)
    // Execute sed operation
    // Verify performance within acceptable limits
    // Check memory usage
}
```

**4.2 Security Test**
```java
@Test
void testSecurityValidation() {
    // Test path traversal attempts
    // Verify input sanitization
    // Check resource limits
}
```

## Testing Tools and Dependencies

### Additional Dependencies Needed

```xml
<!-- TestContainers -->
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>junit-jupiter</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>testcontainers</artifactId>
    <scope>test</scope>
</dependency>

<!-- WebTestClient for MCP HTTP testing -->
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-webflux</artifactId>
    <scope>test</scope>
</dependency>

<!-- JSON testing utilities -->
<dependency>
    <groupId>com.jayway.jsonpath</groupId>
    <artifactId>json-path</artifactId>
    <scope>test</scope>
</dependency>

<!-- Awaitility for async testing -->
<dependency>
    <groupId>org.awaitility</groupId>
    <artifactId>awaitility</artifactId>
    <scope>test</scope>
</dependency>
```

### Test Profiles

**application-test.yml**:
```yaml
spring:
  ai:
    mcp:
      server:
        enabled: true
        type: SYNC
        capabilities:
          tool: true
          resource: false
          prompt: false
          completion: false
        instructions: "Test sed MCP server for integration testing"
  
  security:
    user:
      name: test
      password: test
      
logging:
  level:
    org.springframework.ai: DEBUG
    dev.klawed.sedmcp: DEBUG
```

## Test Organization Structure

```
src/test/java/
├── dev/klawed/sedmcp/
│   ├── integration/
│   │   ├── context/
│   │   │   ├── McpServerContextIntegrationTest.java
│   │   │   └── ToolRegistrationIntegrationTest.java
│   │   ├── protocol/
│   │   │   ├── McpProtocolComplianceTest.java
│   │   │   ├── ToolExecutionIntegrationTest.java
│   │   │   └── ErrorHandlingIntegrationTest.java
│   │   ├── operations/
│   │   │   ├── SedOperationWorkflowTest.java
│   │   │   ├── ComplexPatternIntegrationTest.java
│   │   │   └── PerformanceIntegrationTest.java
│   │   ├── containers/
│   │   │   ├── McpClientServerIntegrationTest.java
│   │   │   └── McpInspectorIntegrationTest.java
│   │   └── security/
│   │       ├── SecurityIntegrationTest.java
│   │       └── PathValidationIntegrationTest.java
│   └── testdata/
│       ├── sed-operations.json
│       ├── sample-content.txt
│       └── complex-patterns.txt
```

## Test Execution Strategy

### Maven Configuration

**pom.xml additions**:
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-failsafe-plugin</artifactId>
    <version>3.0.0-M9</version>
    <configuration>
        <includes>
            <include>**/*IntegrationTest.java</include>
        </includes>
        <systemPropertyVariables>
            <testcontainers.reuse.enable>true</testcontainers.reuse.enable>
        </systemPropertyVariables>
    </configuration>
    <executions>
        <execution>
            <goals>
                <goal>integration-test</goal>
                <goal>verify</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

### Test Commands

```bash
# Run only unit tests
mvn test

# Run only integration tests  
mvn verify -DskipUTs

# Run all tests
mvn verify

# Run specific integration test category
mvn verify -Dit.test="**/protocol/*IntegrationTest"

# Run with TestContainers in CI
mvn verify -Dtestcontainers.reuse.enable=false
```

## Continuous Integration Strategy

### GitHub Actions Integration

```yaml
name: Integration Tests
on: [push, pull_request]

jobs:
  integration-tests:
    runs-on: ubuntu-latest
    services:
      docker:
        image: docker:24-dind
        
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          java-version: '24'
      - name: Run Integration Tests
        run: mvn verify -Dspring.profiles.active=test
      - name: Upload Test Reports
        uses: actions/upload-artifact@v4
        with:
          name: integration-test-reports
          path: target/failsafe-reports/
```

## Quality Gates

### Coverage Targets
- **Unit Test Coverage**: 80%+ for service classes
- **Integration Test Coverage**: 70%+ for MCP workflows
- **End-to-End Coverage**: 60%+ for complete user scenarios

### Performance Benchmarks
- **Startup Time**: < 10 seconds for test context
- **Tool Execution**: < 1 second for simple operations
- **Large Content**: < 5 seconds for 1MB text processing
- **Memory Usage**: < 512MB heap for test scenarios

## Test Data and Fixtures

### Test Data Strategy
- **Static Test Files**: Version controlled test content
- **Generated Content**: Dynamic test data for performance testing
- **Edge Cases**: Boundary conditions and error scenarios
- **Security Test Cases**: Malicious input patterns

### Sample Test Data

**testdata/sed-operations.json**:
```json
{
  "substitutions": [
    {
      "name": "simple_replace",
      "pattern": "hello",
      "replacement": "hi",
      "flags": "g",
      "input": "hello world hello",
      "expected": "hi world hi"
    }
  ],
  "errors": [
    {
      "name": "invalid_regex",
      "pattern": "[invalid",
      "expectedError": "Unclosed character class"
    }
  ]
}
```

## Monitoring and Reporting

### Test Metrics
- **Execution Time**: Track test duration trends
- **Failure Rates**: Monitor test stability
- **Coverage Reports**: Ensure adequate test coverage
- **Performance Metrics**: Benchmark operation performance

### Integration with Development Workflow
- **Pre-commit Hooks**: Run fast integration tests
- **Pull Request Checks**: Full integration test suite
- **Nightly Builds**: Extended test scenarios including performance tests
- **Release Validation**: Complete test suite including security and edge cases

## Future Enhancements

### When Real Implementation is Available
1. **File System Testing**: Real file I/O operations
2. **Advanced Sed Features**: Complex address ranges, hold space operations
3. **Batch Processing**: Multiple file operations
4. **Configuration Management**: External configuration testing
5. **Metrics Integration**: Monitoring and observability testing

### Advanced Testing Scenarios
1. **Chaos Engineering**: Failure injection and recovery testing
2. **Load Testing**: High-concurrency MCP operations
3. **Security Testing**: Penetration testing for MCP endpoints
4. **Compatibility Testing**: Different MCP client implementations

This comprehensive strategy ensures that the sed-mcp project maintains high quality through all phases of development, from the current mock implementation to future production-ready versions.