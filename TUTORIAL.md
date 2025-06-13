# sed-MCP Tutorial: From Zero to Text Processing Hero

A hands-on tutorial that walks you through sed-MCP capabilities, from basic operations to advanced patterns.

## Prerequisites

- Java 24+ installed
- Maven 3.8+ for building
- Basic understanding of regular expressions (helpful but not required)
- Claude Desktop or MCP Inspector for testing

## Part 1: Building and Testing

### Step 1: Build the Server

```bash
git clone https://github.com/klawed/sed-mcp.git
cd sed-mcp
mvn clean package
```

Expected output:
```
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  XX.XXX s
[INFO] Final Memory: XX MB
[INFO] ------------------------------------------------------------------------
```

### Step 2: Verify with MCP Inspector

```bash
npx @modelcontextprotocol/inspector java -jar target/sed-mcp-0.0.1-SNAPSHOT.jar
```

You should see:
- Connection successful
- Three tools listed: `sed_execute`, `sed_preview`, `sed_validate`
- No error messages

## Part 2: Your First sed Operations

### Example 1: Basic Text Replacement

Let's start simple. We want to change "hello" to "hi" in some text.

**Input:**
```json
{
  "content": "hello world\nhello everyone\ngoodbye world",
  "operation": "s",
  "pattern": "hello",
  "replacement": "hi"
}
```

**What happens:**
- `s` = substitute operation
- `hello` = pattern to find
- `hi` = replacement text
- No flags = only first occurrence per line

**Result:**
```
hi world
hi everyone
goodbye world
```

### Example 2: Global Replacement

What if we want to replace ALL occurrences, not just the first on each line?

**Input:**
```json
{
  "content": "hello hello world\nhello everyone hello",
  "operation": "s",
  "pattern": "hello",
  "replacement": "hi",
  "flags": "g"
}
```

The `g` flag means "global" - replace all matches.

**Result:**
```
hi hi world
hi everyone hi
```

### Example 3: Case-Insensitive Matching

Sometimes text has mixed case:

**Input:**
```json
{
  "content": "Hello WORLD\nhello world\nHELLO world",
  "operation": "s",
  "pattern": "hello",
  "replacement": "hi",
  "flags": "gi"
}
```

The `i` flag makes matching case-insensitive.

**Result:**
```
hi WORLD
hi world
hi world
```

## Part 3: Line Operations

### Deleting Lines

Sometimes you want to remove entire lines that match a pattern.

**Example: Remove Debug Logs**
```json
{
  "content": "[INFO] Server started\n[DEBUG] Loading config\n[ERROR] Connection failed\n[DEBUG] Retrying",
  "operation": "d",
  "pattern": "\\[DEBUG\\]"
}
```

**What's happening:**
- `d` = delete operation
- `\\[DEBUG\\]` = pattern (brackets are escaped because they're special in regex)
- Lines matching this pattern get deleted

**Result:**
```
[INFO] Server started
[ERROR] Connection failed
```

### Extracting Lines

The `p` operation prints (extracts) only lines that match:

**Example: Extract Error Messages**
```json
{
  "content": "[INFO] Server started\n[ERROR] Database timeout\n[WARN] Low memory\n[ERROR] Auth failed",
  "operation": "p",
  "pattern": "\\[ERROR\\]"
}
```

**Result:**
```
[ERROR] Database timeout
[ERROR] Auth failed
```

## Part 4: Real-World Scenarios

### Scenario 1: API Version Migration

You're updating your codebase from API v1 to v2:

**The Problem:**
```javascript
fetch('/api/v1/users')
const endpoint = '/api/v1/orders'
axios.get('/api/v1/products')
```

**The Solution:**
```json
{
  "content": "fetch('/api/v1/users')\nconst endpoint = '/api/v1/orders'\naxios.get('/api/v1/products')",
  "operation": "s",
  "pattern": "/api/v1/",
  "replacement": "/api/v2/",
  "flags": "g"
}
```

**Result:**
```javascript
fetch('/api/v2/users')
const endpoint = '/api/v2/orders'
axios.get('/api/v2/products')
```

### Scenario 2: Configuration Updates

You need to update database connections for deployment:

**The Problem:**
```
database.host=localhost
database.port=5432
cache.host=localhost
```

**The Solution:**
```json
{
  "content": "database.host=localhost\ndatabase.port=5432\ncache.host=localhost",
  "operation": "s",
  "pattern": "localhost",
  "replacement": "prod-server.company.com",
  "flags": "g"
}
```

**Result:**
```
database.host=prod-server.company.com
database.port=5432
cache.host=prod-server.company.com
```

### Scenario 3: Data Anonymization

You need to anonymize user data for testing:

**The Problem:**
```
User: john.doe@company.com, Phone: 555-123-4567
User: jane.smith@company.com, Phone: 555-987-6543
```

**The Solution (Step by Step):**

First, anonymize emails:
```json
{
  "content": "User: john.doe@company.com, Phone: 555-123-4567\nUser: jane.smith@company.com, Phone: 555-987-6543",
  "operation": "s",
  "pattern": "[^@]+@[^,]+",
  "replacement": "[EMAIL]",
  "flags": "g"
}
```

Then anonymize phone numbers:
```json
{
  "content": "User: [EMAIL], Phone: 555-123-4567\nUser: [EMAIL], Phone: 555-987-6543",
  "operation": "s",
  "pattern": "Phone: [0-9-]+",
  "replacement": "Phone: [REDACTED]",
  "flags": "g"
}
```

**Final Result:**
```
User: [EMAIL], Phone: [REDACTED]
User: [EMAIL], Phone: [REDACTED]
```

## Part 5: Advanced Patterns

### Using Groups and Backreferences

Groups let you capture parts of the match and reuse them:

**Example: Reformat Phone Numbers**
```json
{
  "content": "Phone: 5551234567\nPhone: 5559876543",
  "operation": "s",
  "pattern": "Phone: ([0-9]{3})([0-9]{3})([0-9]{4})",
  "replacement": "Phone: ($1) $2-$3",
  "flags": "g"
}
```

**What's happening:**
- `([0-9]{3})` = first group, matches 3 digits
- `([0-9]{3})` = second group, matches 3 digits  
- `([0-9]{4})` = third group, matches 4 digits
- `$1`, `$2`, `$3` = reference the captured groups

**Result:**
```
Phone: (555) 123-4567
Phone: (555) 987-6543
```

### Complex Patterns

**Example: Extract JSON Values**
```json
{
  "content": "{\"name\": \"John\", \"age\": 30, \"city\": \"Boston\"}",
  "operation": "s",
  "pattern": "\"([^\"]+)\": \"([^\"]+)\"",
  "replacement": "$1 = $2",
  "flags": "g"
}
```

**Result:**
```
{name = John, age = 30, city = Boston}
```

## Part 6: Safety and Best Practices

### Always Preview First

Before making changes, use `sed_preview`:

```json
{
  "content": "important production data here",
  "operation": "s",
  "pattern": "risky_pattern",
  "replacement": "new_value"
}
```

This shows you what would change without actually changing it.

### Validate Complex Patterns

Use `sed_validate` to check your regex:

```json
{
  "operation": "s",
  "pattern": "[invalid regex(",
  "replacement": "something"
}
```

This will tell you if your pattern is valid before you try to use it.

### Common Pitfalls

1. **Forgetting to escape special characters:**
   - Wrong: `pattern: "[DEBUG]"`
   - Right: `pattern: "\\[DEBUG\\]"`

2. **Greedy matching catching too much:**
   - Problem: `pattern: ".*"` (matches everything)
   - Better: `pattern: "[^,]*"` (matches until comma)

3. **Case sensitivity issues:**
   - Solution: Add `i` flag for case-insensitive matching

## Part 7: Integration with Claude Desktop

### Setting Up

1. Add to your Claude Desktop config:
```json
{
  "mcpServers": {
    "sed-mcp": {
      "command": "java",
      "args": ["-jar", "/full/path/to/sed-mcp-0.0.1-SNAPSHOT.jar"]
    }
  }
}
```

2. Restart Claude Desktop

### Natural Language Interaction

Once connected, you can have conversations like:

**You:** "I need to update all the database connections in this config file from 'localhost' to 'prod-db.internal'"

**Claude:** Will use sed_execute to make the replacements automatically.

**You:** "Can you show me what would change before applying it?"

**Claude:** Will use sed_preview first to show you the changes.

## Part 8: Advanced Workflows

### Multi-Step Processing

Sometimes you need multiple operations:

1. **Remove comments**
2. **Update URLs**  
3. **Format output**

Claude can chain these operations together using multiple sed calls.

### Combining with Other Tools

sed-MCP works great with:
- File management tools
- Database query tools
- API testing tools
- Code analysis tools

## Troubleshooting

### Common Issues

**"Pattern not found"**
- Check if your pattern is correct
- Try with `sed_validate` first
- Make sure you've escaped special characters

**"No matches"**
- Verify the content contains what you expect
- Check case sensitivity (try adding `i` flag)
- Use `sed_preview` to debug

**"Invalid regex"**
- Use `sed_validate` to check syntax
- Common issues: unescaped brackets, parentheses
- Check regex reference guides

### Getting Help

1. Start with simple patterns and build up
2. Use the preview and validate tools
3. Check the [Usage Guide](USAGE_GUIDE.md) for more examples
4. Read the [DI-WHY](DI-WHY.md) to understand when to use sed vs other approaches

## Next Steps

1. **Practice** with your own text files
2. **Experiment** with different pattern types
3. **Integrate** with your development workflow
4. **Share** your use cases and improvements

## Conclusion

sed-MCP gives you surgical precision for text processing tasks. Start with simple replacements, work up to complex patterns, and soon you'll be handling text transformations that would take much longer with manual editing or general-purpose AI tools.

The key is understanding that sed excels at pattern-based transformations where you know exactly what you want to change. For everything else, let Claude's natural language understanding guide the process.

---

*Remember: sed is a scalpel, not a hammer. Use it when precision matters.*
