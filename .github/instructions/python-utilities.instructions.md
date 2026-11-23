---
description: "Use when reviewing or modifying standalone Python utilities such as exercise.py or timelapse scripts."
applyTo: ["**/*.py", "*.sh"]
---

# Standalone Utility Guidelines

- Python utilities are not part of the Java Maven runtime unless explicitly requested.
- Preserve useful CLI behavior and add clear input validation rather than silently coercing bad values.
- For shell scripts, maintain Docker/Linux/macOS compatibility and quote paths safely.
- Keep generated media and temporary files outside source directories unless the tool intentionally manages an output directory.
- Verify utility changes independently from Maven tests.
