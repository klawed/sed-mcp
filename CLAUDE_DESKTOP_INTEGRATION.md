# Claude Desktop Integration Guide

Complete guide for integrating sed-mcp with Claude Desktop for seamless text processing.

## Quick Setup

### 1. Build the MCP Server

```bash
cd /path/to/sed-mcp
mvn clean package
```

This creates: `target/sed-mcp-0.0.1-SNAPSHOT.jar`

### 2. Find Your Claude Desktop Config

**macOS:**
```
~/Library/Application Support/Claude/claude_desktop_config.json
```

**Windows:**
```
%APPDATA%\Claude\claude_desktop_config.json
```

**Linux:**
```
~/.config/Claude/claude_desktop_config.json
```

### 3. Add sed-mcp Configuration

```json
{
  "mcpServers": {
    "sed-mcp": {
      "command": "java",
      "args": [
        "-jar",
        "/absolute/path/to/your/sed-mcp-0.0.1-SNAPSHOT.jar"
      ]
    }
  }
}
```

**Important:** Use the **absolute path** to your JAR file!

### 4. Restart Claude Desktop

Close and reopen Claude Desktop to load the new MCP server.

## Verification

### Check Connection

In Claude Desktop, you should see:
- MCP server status indicator
- Available tools in the tool panel
- No connection errors

### Test Basic Functionality

Try this conversation:
```
Can you help me replace all instances of "hello" with "hi" in this text:
"hello world, hello everyone, hello there"
```

Claude should use the `sed_execute` tool and return:
```
"hi world, hi everyone, hi there"
```

## Common Use Cases with Claude

### 1. **Code Refactoring**

**You:** "I need to update all my API calls from v1 to v2 in this JavaScript file:"
```javascript
fetch('/api/v1/users')
fetch('/api/v1/orders') 
fetch('/api/v1/products')
```

**Claude will:** Use sed to replace `/api/v1/` with `/api/v2/` globally.

### 2. **Log Analysis**

**You:** "Extract only the error messages from this log:"
```
[INFO] Server started
[ERROR] Database connection failed
[DEBUG] Processing request
[ERROR] Invalid user credentials
```

**Claude will:** Use sed with pattern `/\[ERROR\].*/p` to extract error lines.

### 3. **Configuration Updates**

**You:** "Update the database host from localhost to production server in this config:"
```
database.host=localhost
database.port=5432
database.name=myapp
```

**Claude will:** Use sed to replace `localhost` with your specified server address.

### 4. **Data Cleaning**

**You:** "Remove all empty lines and comments from this file:"
```css
/* Main styles */
.header {
  color: blue;
}

/* Footer styles */
.footer {
  color: gray;
}
```

**Claude will:** Use sed to remove comments and clean up the formatting.

## Advanced Configuration

### Environment Variables

You can use environment variables in your configuration:

```json
{
  "mcpServers": {
    "sed-mcp": {
      "command": "java",
      "args": [
        "-jar",
        "${SED_MCP_JAR_PATH}"
      ],
      "env": {
        "JAVA_OPTS": "-Xmx512m",
        "LOGGING_LEVEL": "INFO"
      }
    }
  }
}
```

Set the environment variable:
```bash
export SED_MCP_JAR_PATH="/path/to/sed-mcp-0.0.1-SNAPSHOT.jar"
```

### Custom JVM Options

For better performance or memory management:

```json
{
  "mcpServers": {
    "sed-mcp": {
      "command": "java",
      "args": [
        "-Xmx1g",
        "-XX:+UseG1GC",
        "-Dlogging.level.dev.klawed.sedmcp=DEBUG",
        "-jar",
        "/path/to/sed-mcp-0.0.1-SNAPSHOT.jar"
      ]
    }
  }
}
```

### Multiple Configurations

You can run multiple instances with different settings:

```json
{
  "mcpServers": {
    "sed-mcp-minimal": {
      "command": "java",
      "args": [
        "-Dlogging.level.root=WARN",
        "-jar",
        "/path/to/sed-mcp-0.0.1-SNAPSHOT.jar"
      ]
    },
    "sed-mcp-debug": {
      "command": "java",
      "args": [
        "-Dlogging.level.dev.klawed.sedmcp=DEBUG",
        "-jar",
        "/path/to/sed-mcp-0.0.1-SNAPSHOT.jar"
      ]
    }
  }
}
```

## Conversation Examples

### Example 1: Website Migration

**You:** 
> I'm migrating my website from `old-domain.com` to `new-domain.com`. Can you help me update all the links in this HTML?

```html
<a href="https://old-domain.com/about">About</a>
<a href="https://old-domain.com/contact">Contact</a> 
<img src="https://old-domain.com/images/logo.png">
```

**Claude's Response:**
> I'll help you update all the domain references using sed. Let me replace all instances of `old-domain.com` with `new-domain.com`.

*[Claude uses sed_execute with pattern `old-domain\.com` and replacement `new-domain.com`]*

**Result:**
```html
<a href="https://new-domain.com/about">About</a>
<a href="https://new-domain.com/contact">Contact</a>
<img src="https://new-domain.com/images/logo.png">
```

### Example 2: Configuration Management

**You:**
> I need to update my Docker Compose file to use the latest versions instead of specific version numbers:

```yaml
version: '3.8'
services:
  web:
    image: nginx:1.20.2
  app:
    image: node:16.14.0
  db:
    image: postgres:13.5
```

**Claude's Response:**
> I'll help you update all the image versions to use `latest`. Let me use sed to replace the version numbers.

*[Claude uses sed_execute with pattern `(image: [^:]+):[0-9.]+` and replacement `$1:latest`]*

**Result:**
```yaml
version: '3.8'
services:
  web:
    image: nginx:latest
  app:
    image: node:latest
  db:
    image: postgres:latest
```

### Example 3: Data Privacy

**You:**
> I need to anonymize this user data before sharing it with the development team:

```
User: john.doe@company.com, Phone: 555-123-4567, ID: 12345
User: jane.smith@company.com, Phone: 555-987-6543, ID: 67890
```

**Claude's Response:**
> I'll help you anonymize the sensitive information while keeping the structure intact.

*[Claude uses multiple sed operations to replace emails, phones, and IDs with placeholder values]*

**Result:**
```
User: [EMAIL_REDACTED], Phone: [PHONE_REDACTED], ID: [ID_REDACTED]
User: [EMAIL_REDACTED], Phone: [PHONE_REDACTED], ID: [ID_REDACTED]
```

## Troubleshooting

### Connection Issues

**Problem:** Claude Desktop shows "MCP server connection failed"

**Solutions:**
1. **Check Java installation:**
   ```bash
   java -version
   ```

2. **Test JAR manually:**
   ```bash
   java -jar /path/to/sed-mcp-0.0.1-SNAPSHOT.jar
   ```

3. **Verify absolute path:**
   ```bash
   ls -la /path/to/sed-mcp-0.0.1-SNAPSHOT.jar
   ```

4. **Check file permissions:**
   ```bash
   chmod +x /path/to/sed-mcp-0.0.1-SNAPSHOT.jar
   ```

### Performance Issues

**Problem:** Slow response times

**Solutions:**
1. **Increase JVM memory:**
   ```json
   "args": ["-Xmx2g", "-jar", "path/to/jar"]
   ```

2. **Use G1 garbage collector:**
   ```json
   "args": ["-XX:+UseG1GC", "-jar", "path/to/jar"]
   ```

3. **Reduce logging:**
   ```json
   "args": ["-Dlogging.level.root=WARN", "-jar", "path/to/jar"]
   ```

### Debugging

**Enable debug logging:**
```json
{
  "mcpServers": {
    "sed-mcp": {
      "command": "java",
      "args": [
        "-Dlogging.level.dev.klawed.sedmcp=DEBUG",
        "-jar",
        "/path/to/sed-mcp-0.0.1-SNAPSHOT.jar"
      ]
    }
  }
}
```

**Check Claude Desktop logs:**
- **macOS:** `~/Library/Logs/Claude/`
- **Windows:** `%LOCALAPPDATA%\Claude\Logs\`
- **Linux:** `~/.local/share/Claude/logs/`

## Best Practices

### 1. **Use Descriptive Names**
```json
{
  "mcpServers": {
    "sed-text-processor": {
      "command": "java",
      "args": ["-jar", "/path/to/sed-mcp.jar"]
    }
  }
}
```

### 2. **Set Resource Limits**
```json
{
  "mcpServers": {
    "sed-mcp": {
      "command": "java",
      "args": [
        "-Xmx512m",
        "-XX:MaxMetaspaceSize=128m",
        "-jar",
        "/path/to/sed-mcp.jar"
      ]
    }
  }
}
```

### 3. **Version Management**
Keep your configuration updated with the JAR version:
```json
{
  "mcpServers": {
    "sed-mcp-v0.0.1": {
      "command": "java",
      "args": ["-jar", "/path/to/sed-mcp-0.0.1-SNAPSHOT.jar"]
    }
  }
}
```

### 4. **Environment Separation**
Use different configurations for different environments:

**Development:**
```json
{
  "mcpServers": {
    "sed-mcp-dev": {
      "command": "java",
      "args": [
        "-Dlogging.level.dev.klawed.sedmcp=DEBUG",
        "-jar",
        "/path/to/sed-mcp.jar"
      ]
    }
  }
}
```

**Minimal:**
```json
{
  "mcpServers": {
    "sed-mcp-minimal": {
      "command": "java", 
      "args": [
        "-Dlogging.level.root=ERROR",
        "-Xmx1g",
        "-jar",
        "/path/to/sed-mcp.jar"
      ]
    }
  }
}
```

## Next Steps

1. **Explore Examples:** Try the use cases in [USAGE_GUIDE.md](USAGE_GUIDE.md)
2. **Custom Workflows:** Develop your own text processing workflows
3. **Integration:** Combine with other MCP servers for powerful automation
4. **Feedback:** Share your use cases and improvements

## Support

- **Issues:** [GitHub Issues](https://github.com/klawed/sed-mcp/issues)
- **Documentation:** [Main README](README.md)
- **Examples:** [Usage Guide](USAGE_GUIDE.md)

---

*Enjoy seamless text processing with Claude Desktop and sed-mcp!*
