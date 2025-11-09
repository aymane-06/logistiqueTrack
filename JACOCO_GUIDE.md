# 📊 JaCoCo Test Coverage Report - Guide

## Overview
JaCoCo (Java Code Coverage) is a tool that measures how much of your code is tested by your unit tests. It helps identify untested code paths and improves code quality.

## What is Code Coverage?

**Code coverage** is the percentage of your code that is executed during tests:
- **Line Coverage**: Percentage of code lines executed
- **Branch Coverage**: Percentage of conditional branches (if/else) executed
- **Method Coverage**: Percentage of methods called during tests

### Example:
```java
public int divide(int a, int b) {
    if (b == 0) {
        return -1;  // Not covered if tests don't check division by zero
    }
    return a / b;   // Covered if tests check normal division
}
```

## How to Generate JaCoCo Report

### Method 1: Using Maven Command
Run this command in your project root:

```bash
./mvnw clean test jacoco:report -Dmaven.test.failure.ignore=true
```

**Explanation:**
- `clean` - Removes previous builds
- `test` - Runs all tests
- `jacoco:report` - Generates the coverage report
- `-Dmaven.test.failure.ignore=true` - Generates report even if tests fail

### Method 2: Using Maven Goals
```bash
./mvnw clean test
```
JaCoCo report is automatically generated during the test phase.

## Where is the Report Located?

After running the command, find the report here:
```
target/site/jacoco/
```

**Key files:**
- **`index.html`** - Main report (open this in your browser)
- **`jacoco.xml`** - Machine-readable format (for CI/CD)
- **`jacoco.csv`** - Spreadsheet format
- **Folders by package** - Detailed coverage per package

## How to View the Report

### Option 1: Open in Browser
1. Run the Maven command above
2. Open: `target/site/jacoco/index.html` in your browser
3. Or navigate to: `file:///path/to/your/project/target/site/jacoco/index.html`

### Option 2: From Command Line
```bash
# Navigate to the report
cd target/site/jacoco
# Open in default browser (Linux/Mac)
open index.html
# Or (Linux with xdg-open)
xdg-open index.html
```

## Understanding the Report

### Color Coding:
- **🟢 Green** - Code is covered (tested)
- **🔴 Red** - Code is not covered (not tested)
- **🟡 Yellow** - Code is partially covered

### Metrics Explained:

| Metric | Meaning |
|--------|---------|
| **Instruction** | Individual bytecode instructions executed |
| **Branch** | Boolean branches (if/else, switch cases) |
| **Line** | Source code lines executed |
| **Method** | Methods called during tests |
| **Class** | Classes with at least one tested method |

### Example Report:
```
Package: com.logitrack.logitrack.services

ProductService
├─ Line Coverage: 75% (15/20 lines)
├─ Branch Coverage: 60% (3/5 branches)
└─ Method Coverage: 80% (4/5 methods)
```

## Coverage Goals

**Recommended targets:**
- **Overall**: 80%+ coverage
- **Critical code**: 90%+ coverage
- **Utils/Helpers**: 70%+ coverage

## How to Improve Coverage

### 1. Identify Uncovered Code
- Look for red lines in the JaCoCo report
- Focus on critical business logic first

### 2. Write Tests
Example of improving coverage:

```java
// Before: No test for error case
public String validateEmail(String email) {
    if (email == null || email.isEmpty()) {
        return "Invalid";
    }
    return "Valid";
}

// After: Tests for both cases
@Test
void testValidateEmailValid() {
    assertEquals("Valid", service.validateEmail("test@example.com"));
}

@Test
void testValidateEmailEmpty() {
    assertEquals("Invalid", service.validateEmail(""));
}

@Test
void testValidateEmailNull() {
    assertEquals("Invalid", service.validateEmail(null));
}
```

### 3. Test Both Branches
Always test conditional paths:
```java
// if/else branches
if (condition) { /* Test this */ }
else { /* And this */ }

// switch cases
switch(value) {
    case 1: /* Test */ break;
    case 2: /* Test */ break;
}
```

## Integration with CI/CD

### In GitHub Actions:
```yaml
- name: Generate JaCoCo Report
  run: ./mvnw clean test jacoco:report

- name: Upload Coverage Report
  uses: codecov/codecov-action@v3
  with:
    files: ./target/site/jacoco/jacoco.xml
```

## Running Tests and Generating Report

### Quick Start - One Command:
```bash
./mvnw clean test jacoco:report -Dmaven.test.failure.ignore=true
```

### Then Open:
1. Open your project folder in file explorer
2. Navigate to: `target/site/jacoco/`
3. Double-click `index.html` to open in browser

## Troubleshooting

### No Report Generated?
- Check if tests are actually running
- Verify JaCoCo plugin is in pom.xml
- Run: `./mvnw clean test` first

### Report Shows 0% Coverage?
- Tests may not be running
- Check test file locations: `src/test/java/`
- Verify test classes end with `Test` or `Tests`

### Report is Outdated?
- Always run `clean` first: `./mvnw clean test jacoco:report`
- This removes old build artifacts

## Example Usage

```bash
# In your LogiTrack project
cd /home/aymane/IdeaProjects/LogiTrack

# Generate coverage report
./mvnw clean test jacoco:report -Dmaven.test.failure.ignore=true

# Open in browser
open target/site/jacoco/index.html
# Or
xdg-open target/site/jacoco/index.html
```

## Key Takeaways

✅ **Run tests with coverage**: `./mvnw clean test jacoco:report`  
✅ **View report in browser**: Open `target/site/jacoco/index.html`  
✅ **Green = Good**: More green lines mean better coverage  
✅ **Aim high**: Target 80%+ coverage for production code  
✅ **Test branches**: Don't forget to test if/else and switch cases  

## Next Steps

1. Generate the report for your LogiTrack project
2. Check which classes have low coverage
3. Write tests for uncovered code paths
4. Re-generate the report to see improvements
5. Integrate into your CI/CD pipeline

---

**Need help?** Check the test files in `src/test/java/` to see existing test examples!
