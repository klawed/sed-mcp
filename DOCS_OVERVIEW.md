# Documentation Overview

Comprehensive documentation and examples for sed-MCP server.

## Quick Navigation

### Getting Started
- **[README.md](README.md)** - Main project overview and setup
- **[TUTORIAL.md](TUTORIAL.md)** - Step-by-step learning guide from basics to advanced
- **[CLAUDE_DESKTOP_INTEGRATION.md](CLAUDE_DESKTOP_INTEGRATION.md)** - Complete Claude Desktop setup guide

### Understanding sed-MCP
- **[DI-WHY.md](DI-WHY.md)** - Philosophical exploration of why sed-MCP exists and when to use it
- **[ARCHITECTURE.md](ARCHITECTURE.md)** - Technical architecture and design decisions

### Practical Usage
- **[USAGE_GUIDE.md](USAGE_GUIDE.md)** - Comprehensive usage guide with 20 real-world examples
- **[EXAMPLES.md](EXAMPLES.md)** - Ready-to-use JSON examples for common tasks
- **[COMPILATION_FIXES_COMPLETE.md](COMPILATION_FIXES_COMPLETE.md)** - Technical summary of fixes applied

## Documentation Structure

### For New Users
1. Start with [README.md](README.md) for project overview
2. Follow [TUTORIAL.md](TUTORIAL.md) for hands-on learning
3. Set up with [CLAUDE_DESKTOP_INTEGRATION.md](CLAUDE_DESKTOP_INTEGRATION.md)
4. Try examples from [EXAMPLES.md](EXAMPLES.md)

### For Developers
1. Review [ARCHITECTURE.md](ARCHITECTURE.md) for technical details
2. Understand the philosophy in [DI-WHY.md](DI-WHY.md)
3. Reference [USAGE_GUIDE.md](USAGE_GUIDE.md) for advanced patterns
4. Check [COMPILATION_FIXES_COMPLETE.md](COMPILATION_FIXES_COMPLETE.md) for implementation notes

### For Daily Usage
- **Quick reference**: [EXAMPLES.md](EXAMPLES.md)
- **Troubleshooting**: [CLAUDE_DESKTOP_INTEGRATION.md](CLAUDE_DESKTOP_INTEGRATION.md)
- **Advanced patterns**: [USAGE_GUIDE.md](USAGE_GUIDE.md)

## What's Included

### Core Documentation
- **Setup guides** for multiple environments
- **Step-by-step tutorials** from beginner to advanced
- **Real-world examples** with expected outputs
- **Best practices** and common pitfalls
- **Troubleshooting guides** for common issues

### Example Collections
- **22 practical examples** covering web development, DevOps, security, and data processing
- **JSON payloads** ready for copy-paste testing
- **Expected results** for verification
- **Use case explanations** for context

### Integration Guides
- **Claude Desktop** complete setup and configuration
- **MCP Inspector** testing and debugging
- **Environment variables** and JVM tuning
- **Multiple server configurations**

## Philosophy

This documentation follows a few key principles:

1. **Practical over theoretical** - Every example solves a real problem
2. **Progressive complexity** - Start simple, build to advanced
3. **Multiple learning styles** - Visual examples, step-by-step tutorials, and reference materials
4. **Honest about limitations** - Clear about when sed-MCP is and isn't the right tool

## Contributing to Documentation

When adding new documentation:

1. **Follow the existing style** - Minimal emojis, clear headings, practical examples
2. **Include working examples** - Test all JSON payloads before committing
3. **Cross-reference related docs** - Link to relevant sections in other files
4. **Update this overview** - Add new documents to the navigation structure

## Quick Test

Want to verify everything works? Try this simple test:

1. Build the server: `mvn clean package`
2. Start MCP Inspector: `npx @modelcontextprotocol/inspector java -jar target/sed-mcp-0.0.1-SNAPSHOT.jar`
3. Test with this payload:
```json
{
  "tool": "sed_execute",
  "arguments": {
    "content": "hello world",
    "operation": "s",
    "pattern": "world",
    "replacement": "sed-MCP",
    "flags": "g"
  }
}
```
4. Expected result: `"hello sed-MCP"`

If that works, you're ready to explore the full documentation!

---

*This documentation collection represents hours of real-world testing and practical application. Use it, modify it, and contribute back.*
