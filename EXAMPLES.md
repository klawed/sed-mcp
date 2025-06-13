# Practical Examples Collection

Ready-to-use sed-MCP examples for common text processing tasks. Each example includes the JSON payload and expected results.

## Web Development

### 1. Update API Endpoints (v1 to v2)

**Use case:** Migrating API calls across a codebase

```json
{
  "tool": "sed_execute",
  "arguments": {
    "content": "fetch('/api/v1/users')\nconst url = '/api/v1/orders'\naxios.get('/api/v1/products')",
    "operation": "s",
    "pattern": "/api/v1/",
    "replacement": "/api/v2/",
    "flags": "g"
  }
}
```

**Expected result:**
```javascript
fetch('/api/v2/users')
const url = '/api/v2/orders'
axios.get('/api/v2/products')
```

### 2. Remove HTML Comments

**Use case:** Cleaning up HTML before deployment

```json
{
  "tool": "sed_execute",
  "arguments": {
    "content": "<div class=\"header\">\n  <!-- TODO: Fix navigation -->\n  <h1>Welcome</h1>\n  <!-- Debug: user session -->\n</div>",
    "operation": "d",
    "pattern": "<!--.*-->"
  }
}
```

**Expected result:**
```html
<div class="header">
  <h1>Welcome</h1>
</div>
```

### 3. Extract CSS Classes

**Use case:** Auditing CSS usage

```json
{
  "tool": "sed_execute",
  "arguments": {
    "content": "<div class=\"nav-bar primary\">\n<span class=\"text-bold highlight\">\n<footer class=\"footer-dark\">",
    "operation": "s",
    "pattern": ".*class=\"([^\"]+)\".*",
    "replacement": "$1",
    "flags": "g"
  }
}
```

**Expected result:**
```
nav-bar primary
text-bold highlight
footer-dark
```

## DevOps & Configuration

### 4. Update Environment Variables

**Use case:** Switching between development and staging

```json
{
  "tool": "sed_execute",
  "arguments": {
    "content": "DATABASE_HOST=localhost\nREDIS_HOST=localhost\nAPI_HOST=localhost:3000",
    "operation": "s",
    "pattern": "localhost",
    "replacement": "staging.company.com",
    "flags": "g"
  }
}
```

**Expected result:**
```
DATABASE_HOST=staging.company.com
REDIS_HOST=staging.company.com
API_HOST=staging.company.com:3000
```

### 5. Docker Image Version Updates

**Use case:** Updating Docker Compose to latest versions

```json
{
  "tool": "sed_execute", 
  "arguments": {
    "content": "services:\n  web:\n    image: nginx:1.20.2\n  app:\n    image: node:16.14.0\n  db:\n    image: postgres:13.5",
    "operation": "s",
    "pattern": "(image: [^:]+):[0-9.]+",
    "replacement": "$1:latest",
    "flags": "g"
  }
}
```

**Expected result:**
```yaml
services:
  web:
    image: nginx:latest
  app:
    image: node:latest
  db:
    image: postgres:latest
```

### 6. Log Level Configuration

**Use case:** Changing log levels for deployment

```json
{
  "tool": "sed_execute",
  "arguments": {
    "content": "logging.level.root=DEBUG\nlogging.level.com.company=DEBUG\nlogging.level.org.springframework=DEBUG",
    "operation": "s",
    "pattern": "=DEBUG",
    "replacement": "=INFO",
    "flags": "g"
  }
}
```

**Expected result:**
```
logging.level.root=INFO
logging.level.com.company=INFO
logging.level.org.springframework=INFO
```

## Log Processing

### 7. Extract Error Messages

**Use case:** Finding errors in application logs

```json
{
  "tool": "sed_execute",
  "arguments": {
    "content": "[2025-01-01 10:00:00] INFO: Server started\n[2025-01-01 10:01:00] ERROR: Database connection failed\n[2025-01-01 10:02:00] WARN: High memory usage\n[2025-01-01 10:03:00] ERROR: Authentication failed",
    "operation": "p",
    "pattern": "ERROR:.*"
  }
}
```

**Expected result:**
```
[2025-01-01 10:01:00] ERROR: Database connection failed
[2025-01-01 10:03:00] ERROR: Authentication failed
```

### 8. Remove Debug Logs

**Use case:** Cleaning logs for analysis

```json
{
  "tool": "sed_execute",
  "arguments": {
    "content": "[INFO] Application started\n[DEBUG] Loading configuration\n[ERROR] Connection failed\n[DEBUG] Retrying connection\n[INFO] Connected successfully",
    "operation": "d",
    "pattern": "\\[DEBUG\\].*"
  }
}
```

**Expected result:**
```
[INFO] Application started
[ERROR] Connection failed
[INFO] Connected successfully
```

### 9. Extract IP Addresses from Access Logs

**Use case:** Security analysis of web server logs

```json
{
  "tool": "sed_execute",
  "arguments": {
    "content": "192.168.1.1 - GET /api/users 200\n10.0.0.1 - POST /api/login 401\n192.168.1.1 - GET /dashboard 200\n172.16.0.1 - GET /health 200",
    "operation": "s",
    "pattern": "([0-9.]+) - .*",
    "replacement": "$1",
    "flags": "g"
  }
}
```

**Expected result:**
```
192.168.1.1
10.0.0.1
192.168.1.1
172.16.0.1
```

## Data Processing

### 10. Phone Number Normalization

**Use case:** Standardizing phone number formats

```json
{
  "tool": "sed_execute",
  "arguments": {
    "content": "Contact: (555) 123-4567\nPhone: 555.123.4567\nTel: 5551234567",
    "operation": "s",
    "pattern": "[^0-9]*([0-9]{3})[^0-9]*([0-9]{3})[^0-9]*([0-9]{4})",
    "replacement": "Phone: ($1) $2-$3",
    "flags": "g"
  }
}
```

**Expected result:**
```
Phone: (555) 123-4567
Phone: (555) 123-4567
Phone: (555) 123-4567
```

### 11. Email Domain Extraction

**Use case:** Analyzing email domains for compliance

```json
{
  "tool": "sed_execute",
  "arguments": {
    "content": "user1@gmail.com\nuser2@company.org\nuser3@university.edu\nuser4@gmail.com",
    "operation": "s",
    "pattern": ".*@([^\\s]+)",
    "replacement": "$1",
    "flags": "g"
  }
}
```

**Expected result:**
```
gmail.com
company.org
university.edu
gmail.com
```

### 12. CSV Data Cleaning

**Use case:** Removing incomplete records

```json
{
  "tool": "sed_execute",
  "arguments": {
    "content": "name,email,phone\nJohn Doe,john@email.com,555-1234\n,jane@email.com,\nBob Smith,,555-5678\nAlice,alice@email.com,555-9999",
    "operation": "d",
    "pattern": "^[^,]*,,[^,]*$|^,[^,]*,|^[^,]*,,[^,]*$"
  }
}
```

**Expected result:**
```
name,email,phone
John Doe,john@email.com,555-1234
Alice,alice@email.com,555-9999
```

## Security & Privacy

### 13. Password Redaction

**Use case:** Masking sensitive information in configuration files

```json
{
  "tool": "sed_execute",
  "arguments": {
    "content": "username=admin\npassword=secret123\napi_key=abc123xyz\ndatabase_password=mysecret",
    "operation": "s",
    "pattern": "(password|api_key)=.*",
    "replacement": "$1=[REDACTED]",
    "flags": "gi"
  }
}
```

**Expected result:**
```
username=admin
password=[REDACTED]
api_key=[REDACTED]
database_password=[REDACTED]
```

### 14. User Data Anonymization

**Use case:** Preparing data for testing environments

```json
{
  "tool": "sed_execute",
  "arguments": {
    "content": "User ID: 12345, Name: John Smith, SSN: 123-45-6789\nUser ID: 67890, Name: Jane Doe, SSN: 987-65-4321",
    "operation": "s",
    "pattern": "(Name: )[^,]+",
    "replacement": "$1[ANONYMIZED]",
    "flags": "g"
  }
}
```

**Expected result:**
```
User ID: 12345, Name: [ANONYMIZED], SSN: 123-45-6789
User ID: 67890, Name: [ANONYMIZED], SSN: 987-65-4321
```

### 15. Credit Card Number Masking

**Use case:** Protecting payment information in logs

```json
{
  "tool": "sed_execute",
  "arguments": {
    "content": "Payment processed: 4532-1234-5678-9012\nCard ending in: 4532-1234-5678-9012\nTransaction: 5555-4444-3333-2222",
    "operation": "s",
    "pattern": "([0-9]{4})-([0-9]{4})-([0-9]{4})-([0-9]{4})",
    "replacement": "****-****-****-$4",
    "flags": "g"
  }
}
```

**Expected result:**
```
Payment processed: ****-****-****-9012
Card ending in: ****-****-****-9012
Transaction: ****-****-****-2222
```

## Code Maintenance

### 16. Console.log Cleanup

**Use case:** Removing debug statements before deployment

```json
{
  "tool": "sed_execute",
  "arguments": {
    "content": "function processData() {\n  console.log('Debug: starting process');\n  return data.map(item => {\n    console.log('Processing:', item);\n    return transform(item);\n  });\n}",
    "operation": "d",
    "pattern": ".*console\\.log.*"
  }
}
```

**Expected result:**
```javascript
function processData() {
  return data.map(item => {
    return transform(item);
  });
}
```

### 17. TODO Comment Extraction

**Use case:** Creating task lists from code comments

```json
{
  "tool": "sed_execute",
  "arguments": {
    "content": "// Regular comment\n// TODO: Fix performance issue\nfunction doWork() {\n  // TODO: Add error handling\n  return result;\n}\n// FIXME: Memory leak here",
    "operation": "p",
    "pattern": "// (TODO|FIXME):.*"
  }
}
```

**Expected result:**
```
// TODO: Fix performance issue
// TODO: Add error handling
// FIXME: Memory leak here
```

### 18. Import Statement Updates

**Use case:** Updating module imports during refactoring

```json
{
  "tool": "sed_execute",
  "arguments": {
    "content": "import { utils } from '../utils/oldUtils';\nimport { helper } from '../utils/oldHelper';\nimport { config } from '../config/oldConfig';",
    "operation": "s",
    "pattern": "from '[^']*old([^']*)'",
    "replacement": "from '../shared/new$1'",
    "flags": "g"
  }
}
```

**Expected result:**
```javascript
import { utils } from '../shared/newUtils';
import { helper } from '../shared/newHelper';
import { config } from '../shared/newConfig';
```

## Documentation Processing

### 19. Markdown Link Updates

**Use case:** Updating documentation links after site migration

```json
{
  "tool": "sed_execute",
  "arguments": {
    "content": "[Documentation](http://old-docs.com/guide)\n[API Reference](http://old-docs.com/api)\n[External Link](http://external.com/page)",
    "operation": "s",
    "pattern": "\\](http://old-docs\\.com/([^)]+))",
    "replacement": "](https://new-docs.company.com/$1)",
    "flags": "g"
  }
}
```

**Expected result:**
```markdown
[Documentation](https://new-docs.company.com/guide)
[API Reference](https://new-docs.company.com/api)
[External Link](http://external.com/page)
```

### 20. Version Number Updates

**Use case:** Updating version references across documentation

```json
{
  "tool": "sed_execute",
  "arguments": {
    "content": "Current version: v1.2.3\nDownload v1.2.3 here\nSupports API v1.2.3",
    "operation": "s",
    "pattern": "v1\\.2\\.3",
    "replacement": "v1.3.0",
    "flags": "g"
  }
}
```

**Expected result:**
```
Current version: v1.3.0
Download v1.3.0 here
Supports API v1.3.0
```

## JSON Processing

### 21. JSON Key Extraction

**Use case:** Creating simple key-value lists from JSON

```json
{
  "tool": "sed_execute",
  "arguments": {
    "content": "{\"name\": \"John\", \"age\": 30, \"city\": \"Boston\", \"active\": true}",
    "operation": "s",
    "pattern": "\"([^\"]+)\": \"?([^,}\"]+)\"?",
    "replacement": "$1 = $2",
    "flags": "g"
  }
}
```

**Expected result:**
```
{name = John, age = 30, city = Boston, active = true}
```

### 22. JSON Formatting Cleanup

**Use case:** Removing extra whitespace from minified JSON

```json
{
  "tool": "sed_execute",
  "arguments": {
    "content": "{ \"name\" : \"value\" , \"count\" : 42 , \"active\" : true }",
    "operation": "s",
    "pattern": " *: *",
    "replacement": ":",
    "flags": "g"
  }
}
```

**Expected result:**
```
{ "name":"value" , "count":42 , "active":true }
```

## Tips for Using These Examples

### 1. Always Test First
Use `sed_preview` before `sed_execute`:
```json
{
  "tool": "sed_preview",
  "arguments": {
    "content": "your content here",
    "operation": "s",
    "pattern": "test pattern",
    "replacement": "new value"
  }
}
```

### 2. Validate Complex Patterns
For complex regex, use `sed_validate`:
```json
{
  "tool": "sed_validate",
  "arguments": {
    "operation": "s",
    "pattern": "^(?:[0-9]{1,3}\\.){3}[0-9]{1,3}$",
    "replacement": "IP_ADDRESS"
  }
}
```

### 3. Escape Special Characters
Remember to escape regex special characters:
- `.` becomes `\\.`
- `[` becomes `\\[`
- `(` becomes `\\(`
- `*` becomes `\\*`

### 4. Use Appropriate Flags
- `g` - Global replacement (all matches)
- `i` - Case-insensitive matching
- `m` - Multiline mode
- `s` - Dot matches newlines

## Getting More Examples

1. Check the [Usage Guide](USAGE_GUIDE.md) for detailed explanations
2. Try the [Tutorial](TUTORIAL.md) for step-by-step learning
3. Read [DI-WHY.md](DI-WHY.md) to understand when to use sed vs other tools
4. Experiment with your own patterns using MCP Inspector

---

*These examples are starting points - modify patterns and replacements to fit your specific needs.*
