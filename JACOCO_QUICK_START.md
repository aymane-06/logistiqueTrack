# 🚀 JaCoCo Quick Reference

## One Command to Generate Report
```bash
./mvnw clean test jacoco:report -Dmaven.test.failure.ignore=true
```

## View the Report
```
target/site/jacoco/index.html
```
Just open this file in your browser!

## What You'll See

### Overall Coverage
Shows percentage of code covered by tests:
- **Line Coverage %** - Lines executed during tests
- **Branch Coverage %** - if/else paths tested
- **Method Coverage %** - Methods called

### By Package
Detailed breakdown of each package:
- `com.logitrack.logitrack.controllers`
- `com.logitrack.logitrack.services`
- etc.

### Color Code
- 🟢 **Green** = Covered (Good!)
- 🔴 **Red** = Not covered (Need tests)
- 🟡 **Yellow** = Partially covered

## Coverage Targets
- **80%+** - Excellent (aim for this)
- **60-79%** - Good
- **40-59%** - Fair
- **<40%** - Need improvement

## How to Improve
1. **Find red lines** in the report (uncovered code)
2. **Write tests** for those code paths
3. **Re-run** the report to see improvement
4. **Repeat** until satisfied with coverage

## Files Generated
- `index.html` - Main interactive report
- `jacoco.csv` - Export to Excel
- `jacoco.xml` - For CI/CD tools
- Folders - Details per package/class

---
For detailed information, see `JACOCO_GUIDE.md`
