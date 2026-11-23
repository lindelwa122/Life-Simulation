---
description: "Use when writing or fixing JUnit 5 tests, assertions, deterministic simulation tests, or Maven test failures."
applyTo: "src/test/java/**/*.java"
---

# JUnit 5 Test Guidelines

- Use descriptive test methods and JUnit assertions.
- Seed randomness or use fixed inputs for deterministic genetic and world tests.
- Test the smallest behavior boundary first; avoid whole-simulation timing assertions.
- For pruning or genome tests, assert invariants rather than exact random connection counts unless seeding makes them stable.
- Inspect Surefire XML/text reports after failures and preserve unrelated failing tests in the final report.
- Never weaken or remove an assertion merely to obtain a passing build.
