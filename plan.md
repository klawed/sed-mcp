# Project Refactor Plan

## Overview
This document outlines the refactoring work needed for the `sed-mcp` project to address package naming inconsistencies and correct Spring AI dependency references.

## Current Issues Identified

### 1. Package Structure Issues
**Problem**: The project has two separate package structures that appear to be conflicting:
- `com._c_the_future.sed_mcp` (contains `SedMcpApplication.java`)
- `dev.klawed.sedmcp` (contains `Application.java` and main business logic)

**Impact**: 
- Confusing package structure
- Duplicate application entry points
- Maven groupId doesn't match actual package structure
- Package naming conventions are inconsistent

### 2. Spring AI Dependency Issues
**Problem**: Incorrect Spring AI MCP dependency references in `pom.xml`:
- Currently using: `spring-ai-starter-mcp-server` (incorrect)
- Also has: `spring-ai-spring-boot-docker-compose` (incorrect)

**Impact**:
- Dependencies may not resolve correctly
- Missing proper MCP server functionality
- Inconsistent with Spring AI 1.0.0 naming conventions

## Refactoring Tasks

### Task 1: Standardize Package Structure
**Priority**: High

**Actions Required**:
1. **Choose Primary Package Structure**: Standardize on `dev.klawed.sedmcp` as the main package
2. **Remove Duplicate Application Class**: Delete `com._c_the_future.sed_mcp.SedMcpApplication.java`
3. **Update Maven GroupId**: Change from `com.4c-the-future` to `dev.klawed` in `pom.xml`
4. **Verify Package Consistency**: Ensure all classes use consistent package naming

**Files to Modify**:
- `pom.xml` - Update `<groupId>`
- Delete: `src/main/java/com/_c_the_future/sed_mcp/SedMcpApplication.java`
- Verify: All files under `src/main/java/dev/klawed/sedmcp/` are correctly structured

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

### Task 3: Update Application Configuration
**Priority**: Medium

**Actions Required**:
1. **Review Application Properties**: Ensure MCP server configuration is correct
2. **Update Import Statements**: Fix any imports that reference old packages
3. **Verify Main Application Class**: Ensure `dev.klawed.sedmcp.Application.java` is the primary entry point

### Task 4: Documentation Updates
**Priority**: Low

**Actions Required**:
1. **Update README.md**: Reflect correct package structure and dependencies
2. **Update ARCHITECTURE.md**: Document the standardized structure
3. **Update SETUP.md**: Ensure setup instructions are accurate

## Verification Steps

### After Completing Refactor:
1. **Build Verification**: 
   - Run `mvn clean compile` to ensure no compilation errors
   - Run `mvn clean test` to verify all tests pass

2. **Package Structure Verification**:
   - Confirm only one main application class exists
   - Verify all classes use `dev.klawed.sedmcp` package structure

3. **Dependency Verification**:
   - Run `mvn dependency:tree` to check for any dependency conflicts
   - Verify Spring AI MCP server functionality works correctly

4. **Runtime Verification**:
   - Start the application and verify it boots successfully
   - Test MCP server functionality if applicable

## Risks and Considerations

### Potential Risks:
1. **Breaking Changes**: Package name changes may affect existing configurations
2. **Import Dependencies**: Other projects depending on this may need updates
3. **Configuration Files**: May need to update any package-specific configurations

### Mitigation Strategies:
1. **Thorough Testing**: Test all functionality after changes
2. **Gradual Implementation**: Make changes in small, testable increments
3. **Documentation**: Keep detailed notes of what was changed for rollback if needed

## Timeline Estimate
- **Task 1 (Package Structure)**: 2-3 hours
- **Task 2 (Dependencies)**: 1-2 hours  
- **Task 3 (Configuration)**: 1 hour
- **Task 4 (Documentation)**: 1 hour
- **Verification & Testing**: 2-3 hours

**Total Estimated Time**: 7-10 hours

## Success Criteria
- [x] Single, consistent package structure (`dev.klawed.sedmcp`)
- [x] Correct Spring AI MCP dependencies
- [x] Application builds and runs without errors
- [x] All tests pass
- [x] Documentation updated to reflect changes
- [x] No duplicate or conflicting application entry points