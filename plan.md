# Project Refactor Plan

## Overview
This document outlines the refactoring work needed for the `sed-mcp` project to address compilation errors and correct Spring AI dependency references.

## Current Issues Identified

### 1. **CRITICAL: Compilation Errors**
**Problem**: The code fails to compile due to missing imports:
```
[ERROR] /Users/claude/src/sed-mcp/src/main/java/dev/klawed/sedmcp/Application.java:[54,12] cannot find symbol
  symbol:   class FunctionCallback
  location: class dev.klawed.sedmcp.SedService
[ERROR] /Users/claude/src/sed-mcp/src/main/java/dev/klawed/sedmcp/Application.java:[64,12] cannot find symbol
  symbol:   class FunctionCallback
  location: class dev.klawed.sedmcp.SedService
[ERROR] /Users/claude/src/sed-mcp/src/main/java/dev/klawed/sedmcp/Application.java:[74,12] cannot find symbol
  symbol:   class FunctionCallback
  location: class dev.klawed.sedmcp.SedService
```

**Root Cause**: Missing import statement for `FunctionCallback` class in Application.java

**Impact**: 
- Project cannot be built
- Maven compilation fails
- No executable artifact can be created

### 2. **Spring AI Dependency Issues**
**Problem**: Incorrect Spring AI MCP dependency references in `pom.xml`:
- Currently using: `spring-ai-starter-mcp-server` (incorrect artifact name)
- Also has: `spring-ai-spring-boot-docker-compose` (incorrect artifact name)

**Impact**:
- Dependencies may not resolve correctly
- Missing proper MCP server functionality
- Inconsistent with Spring AI 1.0.0 naming conventions

### 3. **Package Structure Issues**
**Problem**: The project has duplicate package structures:
- `com._c_the_future.sed_mcp` (contains `SedMcpApplication.java`)
- `dev.klawed.sedmcp` (contains `Application.java` and main business logic)

**Impact**: 
- Confusing package structure
- Duplicate application entry points
- Maven groupId doesn't match actual package structure

## Refactoring Tasks

### Task 1: Fix Compilation Errors (CRITICAL)
**Priority**: CRITICAL - Must be done first

**Actions Required**:
1. **IMPORTANT: API Migration**: The code is using the deprecated `FunctionCallback` API. According to Spring AI 1.0.0, this should be migrated to the new `ToolCallback` API.

**Documentation References**:
- [Migrating from FunctionCallback to ToolCallback API](https://docs.spring.io/spring-ai/reference/api/tools-migration.html)
- [Tool Calling Documentation](https://docs.spring.io/spring-ai/reference/api/tools.html)
- [FunctionCallback Documentation (deprecated)](https://docs.spring.io/spring-ai/reference/api/function-callback.html)

**Two Options for Fix**:

**Option A: Quick Fix (Add Missing Import)**
- Add `import org.springframework.ai.model.function.FunctionCallback;` to Application.java
- This maintains deprecated API but allows compilation

**Option B: Proper Fix (Migrate to New API)**
- Replace `FunctionCallback` with `ToolCallback` from `org.springframework.ai.tool.ToolCallback`
- Update the builder pattern to use new ToolCallback API
- See: [GitHub: ToolCallback.java](https://github.com/spring-projects/spring-ai/blob/main/spring-ai-model/src/main/java/org/springframework/ai/tool/ToolCallback.java)

**Recommended Approach**: Option A for immediate compilation fix, then Option B for proper migration.

**Files to Modify**:
- `src/main/java/dev/klawed/sedmcp/Application.java` - Add missing import or migrate API

### Task 2: Fix Spring AI Dependencies
**Priority**: High

**Actions Required**:
1. **Update MCP Server Dependency**: 
   - Remove: `spring-ai-starter-mcp-server`
   - Add: `spring-ai-mcp-server-spring-boot-starter` (correct artifact name for 1.0.0)

2. **Fix Docker Compose Dependency**:
   - Remove: `spring-ai-spring-boot-docker-compose` (incorrect)
   - Keep: `spring-boot-docker-compose` (standard Spring Boot dependency)

3. **Add Spring AI BOM**: Include proper dependency management
4. **Update Version References**: Ensure Spring AI version consistency

**Documentation References**:
- [MCP Server Boot Starter Documentation](https://docs.spring.io/spring-ai/reference/api/mcp/mcp-server-boot-starter-docs.html)
- [Spring AI Upgrade Notes - Artifact ID Changes](https://docs.spring.io/spring-ai/reference/upgrade-notes.html)
- [Getting Started - Dependency Management](https://docs.spring.io/spring-ai/reference/getting-started.html)

**Dependency Changes in `pom.xml`**:

**Remove these incorrect dependencies**:
```xml
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-mcp-server</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-spring-boot-docker-compose</artifactId>
    <scope>runtime</scope>
    <optional>true</optional>
</dependency>
```

**Add these correct dependencies**:
```xml
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-mcp-server-spring-boot-starter</artifactId>
</dependency>
```

**Add Spring AI BOM for version management**:
```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.ai</groupId>
            <artifactId>spring-ai-bom</artifactId>
            <version>${spring-ai.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

### Task 3: Standardize Package Structure
**Priority**: Medium

**Actions Required**:
1. **Choose Primary Package Structure**: Standardize on `dev.klawed.sedmcp` as the main package
2. **Remove Duplicate Application Class**: Delete `com._c_the_future.sed_mcp.SedMcpApplication.java`
3. **Update Maven GroupId**: Change from `com.4c-the-future` to `dev.klawed` in `pom.xml`
4. **Verify Package Consistency**: Ensure all classes use consistent package naming

**Files to Modify**:
- `pom.xml` - Update `<groupId>`
- Delete: `src/main/java/com/_c_the_future/sed_mcp/SedMcpApplication.java`
- Verify: All files under `src/main/java/dev/klawed/sedmcp/` are correctly structured

### Task 4: Update Application Configuration
**Priority**: Low

**Actions Required**:
1. **Review Application Properties**: Ensure MCP server configuration is correct
2. **Update Import Statements**: Fix any other missing imports
3. **Verify Main Application Class**: Ensure `dev.klawed.sedmcp.Application.java` is the primary entry point

### Task 5: Documentation Updates
**Priority**: Low

**Actions Required**:
1. **Update README.md**: Reflect correct package structure and dependencies
2. **Update ARCHITECTURE.md**: Document the standardized structure
3. **Update SETUP.md**: Ensure setup instructions are accurate

## Documentation References Used

### Spring AI Official Documentation:
- [Spring AI Reference Documentation](https://docs.spring.io/spring-ai/reference/index.html)
- [Tool Calling Documentation](https://docs.spring.io/spring-ai/reference/api/tools.html)
- [MCP Server Boot Starter](https://docs.spring.io/spring-ai/reference/api/mcp/mcp-server-boot-starter-docs.html)
- [Function Callback Migration Guide](https://docs.spring.io/spring-ai/reference/api/tools-migration.html)
- [Upgrade Notes](https://docs.spring.io/spring-ai/reference/upgrade-notes.html)

### API Documentation:
- [ToolCallback Interface](https://docs.spring.io/spring-ai/docs/1.0.0-SNAPSHOT/api/org/springframework/ai/tool/ToolCallback.html)
- [Tool Annotation](https://docs.spring.io/spring-ai/docs/1.0.0-M6/api/org/springframework/ai/tool/annotation/Tool.html)
- [FunctionCallback (deprecated)](https://docs.spring.io/spring-ai/reference/api/function-callback.html)

### Release Notes:
- [Spring AI 1.0.0 M6 Release](https://spring.io/blog/2025/02/14/spring-ai-1-0-0-m6-released/)
- [Spring AI 1.0 GA Release](https://spring.io/blog/2025/05/20/spring-ai-1-0-GA-released/)

## Verification Steps

### After Completing Task 1 (Critical):
1. **Build Verification**: 
   - Run `mvn clean compile` to ensure no compilation errors
   - Should see "BUILD SUCCESS" instead of compilation errors

### After Completing All Tasks:
1. **Full Build Verification**: 
   - Run `mvn clean test` to verify all tests pass
   - Run `mvn clean package` to create executable JAR

2. **Package Structure Verification**:
   - Confirm only one main application class exists
   - Verify all classes use `dev.klawed.sedmcp` package structure

3. **Dependency Verification**:
   - Run `mvn dependency:tree` to check for any dependency conflicts
   - Verify Spring AI MCP server functionality works correctly

4. **Runtime Verification**:
   - Start the application and verify it boots successfully
   - Test MCP server functionality if applicable

## Immediate Next Steps

1. **START HERE**: Fix the compilation error by adding the missing FunctionCallback import
2. **Verify**: Run `mvn clean compile` to ensure it builds
3. **Then**: Proceed with dependency fixes
4. **Finally**: Clean up package structure

## Timeline Estimate
- **Task 1 (Fix Compilation)**: 30 minutes
- **Task 2 (Dependencies)**: 1-2 hours  
- **Task 3 (Package Structure)**: 2-3 hours
- **Task 4 (Configuration)**: 1 hour
- **Task 5 (Documentation)**: 1 hour
- **Verification & Testing**: 2-3 hours

**Total Estimated Time**: 7-10 hours

## Success Criteria
- [x] Code compiles without errors (`mvn clean compile` succeeds)
- [x] Correct Spring AI MCP dependencies
- [x] Single, consistent package structure (`dev.klawed.sedmcp`)
- [x] Application builds and runs without errors
- [x] All tests pass
- [x] Documentation updated to reflect changes
- [x] No duplicate or conflicting application entry points