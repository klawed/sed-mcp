# SED-MCP Usage Guide & Examples

A comprehensive guide to using the sed Model Context Protocol (MCP) server with real-world examples and use cases.

## Quick Start

### Installation & Setup

1. **Build the MCP server:**
   ```bash
   mvn clean package
   ```

2. **Test with MCP Inspector:**
   ```bash
   npx @modelcontextprotocol/inspector java -jar target/sed-mcp-0.0.1-SNAPSHOT.jar
   ```

3. **Configure with Claude Desktop:**
   ```json
   {
     "mcpServers": {
       "sed-mcp": {
         "command": "java",
         "args": ["-jar", "/path/to/your/sed-mcp-0.0.1-SNAPSHOT.jar"]
       }
     }
   }
   ```

## Available Tools

### 1. `sed_execute` - Execute sed operations
Execute sed operations on text content with full processing.

### 2. `sed_preview` - Preview without changes
Test sed operations safely without modifying content.

### 3. `sed_validate` - Validate syntax
Check if your sed operation syntax is valid before execution.

## Operation Types

### Substitution (`s`)
Replace text patterns with new content.

**Syntax:** `s/pattern/replacement/flags`

**Flags:**
- `g` - Global (replace all occurrences)
- `i` - Case-insensitive
- `m` - Multiline mode
- `s` - Dot matches newlines

### Deletion (`d`)
Remove lines matching a pattern.

**Syntax:** `/pattern/d`

### Print (`p`)
Extract lines matching a pattern.

**Syntax:** `/pattern/p`

## Real-World Examples

### Text Processing

#### 1. **Update Configuration Files**
```json
{
  "content": "server.port=8080\ndatabase.url=localhost:5432\napi.version=v1",
  "operation": "s",
  "pattern": "localhost:5432",
  "replacement": "prod-db.company.com:5432",
  "flags": "g"
}
```
**Result:** Updates database URL in configuration

#### 2. **Clean Log Files**
```json
{
  "content": "[DEBUG] Starting application\n[INFO] Server started\n[DEBUG] Processing request\n[ERROR] Connection failed",
  "operation": "d",
  "pattern": "\\[DEBUG\\].*"
}
```
**Result:** Removes all debug log entries

#### 3. **Extract Error Messages**
```json
{
  "content": "[INFO] App started\n[ERROR] Database connection failed\n[INFO] Processing request\n[ERROR] Invalid user token",
  "operation": "p",
  "pattern": "\\[ERROR\\].*"
}
```
**Result:** Returns only error log lines

### Web Development

#### 4. **Update API Endpoints**
```json
{
  "content": "fetch('/api/v1/users')\nfetch('/api/v1/orders')\nfetch('/api/v1/products')",
  "operation": "s",
  "pattern": "/api/v1/",
  "replacement": "/api/v2/",
  "flags": "g"
}
```
**Result:** Updates all API endpoints to v2

#### 5. **Clean HTML Comments**
```json
{
  "content": "<div>Content</div>\n<!-- TODO: Fix this -->\n<p>More content</p>\n<!-- Debug info -->",
  "operation": "d",
  "pattern": "<!--.*-->"
}
```
**Result:** Removes HTML comments

#### 6. **Extract CSS Classes**
```json
{
  "content": "<div class=\"nav-bar primary\">\n<span class=\"text-bold\">\n<div class=\"footer secondary\">",
  "operation": "p",
  "pattern": "class=\"[^\"]*\""
}
```
**Result:** Extracts all CSS class definitions

### DevOps & System Administration

#### 7. **Update Docker Compose Versions**
```json
{
  "content": "version: '3.7'\nservices:\n  web:\n    image: nginx:1.20\n  db:\n    image: postgres:13",
  "operation": "s",
  "pattern": "image: ([^:]+):[0-9.]+",
  "replacement": "image: $1:latest",
  "flags": "g"
}
```
**Result:** Updates all Docker images to latest versions

#### 8. **Filter Server Logs by IP**
```json
{
  "content": "192.168.1.1 - GET /api/users\n10.0.0.1 - POST /api/login\n192.168.1.1 - GET /api/dashboard\n172.16.0.1 - GET /health",
  "operation": "p",
  "pattern": "192\\.168\\.1\\.1.*"
}
```
**Result:** Shows only logs from specific IP address

#### 9. **Remove Sensitive Data**
```json
{
  "content": "user: john\npassword: secret123\nemail: john@company.com\napi_key: abc123xyz",
  "operation": "s",
  "pattern": "(password|api_key): .*",
  "replacement": "$1: [REDACTED]",
  "flags": "gi"
}
```
**Result:** Masks sensitive information

### Data Processing

#### 10. **CSV Data Cleaning**
```json
{
  "content": "name,email,phone\nJohn Doe,john@email.com,555-1234\n,jane@email.com,\nBob Smith,,555-5678",
  "operation": "d",
  "pattern": "^[^,]*,,|^,[^,]*,|,,.*$"
}
```
**Result:** Removes rows with missing critical data

#### 11. **Normalize Phone Numbers**
```json
{
  "content": "Phone: (555) 123-4567\nPhone: 555.123.4567\nPhone: 555-123-4567",
  "operation": "s",
  "pattern": "Phone: [\\(\\.]?([0-9]{3})[\\)\\.]?[-\\. ]?([0-9]{3})[-\\.]?([0-9]{4})",
  "replacement": "Phone: ($1) $2-$3",
  "flags": "g"
}
```
**Result:** Standardizes phone number format

#### 12. **Extract Email Domains**
```json
{
  "content": "user1@gmail.com\nuser2@company.org\nuser3@university.edu\nuser4@gmail.com",
  "operation": "s",
  "pattern": ".*@([^\\s]+)",
  "replacement": "$1",
  "flags": "g"
}
```
**Result:** Extracts just the domain parts of email addresses

### Mobile Development

#### 13. **Update App Version Strings**
```json
{
  "content": "version = \"1.2.3\"\nbuild = \"1.2.3-SNAPSHOT\"\napi_version = \"1.2.3\"",
  "operation": "s",
  "pattern": "\"1\\.2\\.3[^\"]*\"",
  "replacement": "\"1.3.0\"",
  "flags": "g"
}
```
**Result:** Updates version numbers across configuration

#### 14. **Clean Debug Prints**
```json
{
  "content": "console.log('Debug: user loaded');\nreturn userData;\nconsole.log('Debug: processing complete');",
  "operation": "d",
  "pattern": "console\\.log\\('Debug:.*'\\);"
}
```
**Result:** Removes debug console statements

### Security & Compliance

#### 15. **Anonymize User Data**
```json
{
  "content": "User ID: 12345, Name: John Smith, SSN: 123-45-6789\nUser ID: 67890, Name: Jane Doe, SSN: 987-65-4321",
  "operation": "s",
  "pattern": "(Name: )[^,]+",
  "replacement": "$1[ANONYMIZED]",
  "flags": "g"
}
```
**Result:** Anonymizes personal information

#### 16. **Validate Email Format**
```json
{
  "content": "valid@email.com\ninvalid-email\nanother@valid.org\nbad@email",
  "operation": "p",
  "pattern": "^[^@]+@[^@]+\\.[^@]+$"
}
```
**Result:** Returns only valid email addresses

### Content Management

#### 17. **Update Markdown Links**
```json
{
  "content": "[Link](http://old-site.com/page)\n[Another](http://old-site.com/docs)\n[External](http://other-site.com)",
  "operation": "s",
  "pattern": "\\](http://old-site\\.com/([^)]+))",
  "replacement": "](https://new-site.com/$1)",
  "flags": "g"
}
```
**Result:** Updates internal links to new domain

#### 18. **Extract Image Alt Text**
```json
{
  "content": "<img src=\"logo.png\" alt=\"Company Logo\">\n<img src=\"banner.jpg\" alt=\"Welcome Banner\">\n<img src=\"icon.svg\">",
  "operation": "p",
  "pattern": "alt=\"[^\"]+\""
}
```
**Result:** Finds all images with alt text

## Advanced Patterns

### Complex Regex Examples

#### 19. **Extract JSON Values**
```json
{
  "content": "{\"name\": \"John\", \"age\": 30, \"city\": \"New York\"}",
  "operation": "s",
  "pattern": "\"([^\"]+)\": \"([^\"]+)\"",
  "replacement": "$1 = $2",
  "flags": "g"
}
```
**Result:** Converts JSON to key=value format

#### 20. **Multi-line Code Block Processing**
```json
{
  "content": "```javascript\nconsole.log('hello');\n```\n\nSome text\n\n```python\nprint('world')\n```",
  "operation": "p",
  "pattern": "```[^`]*```",
  "flags": "s"
}
```
**Result:** Extracts all code blocks

## Best Practices

### 1. **Always Preview First**
Use `sed_preview` to test your operations before applying them:
```json
{
  "content": "your text here",
  "operation": "s",
  "pattern": "risky_pattern",
  "replacement": "new_value"
}
```

### 2. **Validate Complex Patterns**
Use `sed_validate` for complex regex patterns:
```json
{
  "operation": "s",
  "pattern": "^(?:[0-9]{1,3}\\.){3}[0-9]{1,3}$",
  "replacement": "IP_ADDRESS"
}
```

### 3. **Escape Special Characters**
Remember to escape regex special characters:
- `.` becomes `\\.`
- `*` becomes `\\*`
- `[` becomes `\\[`
- `(` becomes `\\(`

### 4. **Use Appropriate Flags**
- `g` for global replacement
- `i` for case-insensitive matching
- `m` for multiline mode
- `s` for dot-matches-all mode

## Performance Tips

1. **Specific Patterns**: Use specific patterns rather than broad ones
2. **Anchoring**: Use `^` and `$` for line anchoring when possible
3. **Non-greedy Matching**: Use `*?` instead of `*` when appropriate
4. **Character Classes**: Use `[0-9]` instead of `\\d` for better compatibility

## Common Pitfalls

1. **Forgetting to Escape**: Always escape special regex characters
2. **Greedy Matching**: Be careful with `.*` patterns
3. **Case Sensitivity**: Remember to use `i` flag when needed
4. **Line Ending Differences**: Consider different line ending formats

## Integration Examples

### With curl
```bash
echo '{"content":"hello world","operation":"s","pattern":"world","replacement":"universe"}' | \
curl -X POST -H "Content-Type: application/json" -d @- http://localhost:8080/sed/execute
```

### With Python
```python
import subprocess
import json

def sed_execute(content, operation, pattern, replacement="", flags=""):
    payload = {
        "content": content,
        "operation": operation,
        "pattern": pattern,
        "replacement": replacement,
        "flags": flags
    }
    
    # This would connect to your MCP server
    # Implementation depends on your MCP client setup
    pass
```

## Next Steps

1. **Learn More**: Study the [sed manual](https://www.gnu.org/software/sed/manual/sed.html)
2. **Practice**: Try the examples with your own data
3. **Integrate**: Add to your Claude Desktop configuration
4. **Extend**: Consider contributing new features

## Getting Help

- Check the [main README](README.md) for setup instructions
- Review the [architecture documentation](ARCHITECTURE.md)
- Test with MCP Inspector for debugging
- Open issues for bugs or feature requests

---

*Happy text processing with sed-mcp!*
