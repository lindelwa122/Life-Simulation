---
name: python-utility-review
description: "Review standalone Python utilities and shell tools in this workspace. Use for exercise.py, CLI input handling, shell portability, Docker scripts, and timelapse helpers."
argument-hint: "Describe the utility bug or behavior to review"
user-invocable: true
---

# Python Utility Review

## Procedure
1. Confirm whether the utility is standalone or invoked from Java, Docker, or a shell script.
2. Inspect imports and runtime assumptions before changing code.
3. Test a small input/output case independently of Maven.
4. Add explicit validation for malformed or unavailable inputs.
5. Preserve existing CLI and stdout behavior unless a change is requested.

## Shell Checklist
- Quote file paths and variables.
- Verify exit propagation and required executables.
- Check macOS and Linux differences for display, FFmpeg, and Docker mounts.

## Constraints
- Do not present Python test results as Java test coverage.
- Avoid generated media or bytecode in source directories.
