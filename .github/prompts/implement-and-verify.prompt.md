---
description: "Implement a focused change and verify it with tests and, when relevant, the simulation"
agent: "agent"
argument-hint: "Describe the change or bug to implement"
---

Work on this request: **${input:task}**

## Workflow
1. Locate the relevant implementation, configuration, and existing tests.
2. State the smallest change you intend to make if the request could affect public behavior.
3. Implement the change using existing project conventions.
4. Add or update focused JUnit 5 tests.
5. Run `mvn test` and relevant targeted tests.
6. If the change affects the simulation runtime, run a bounded verification using a temporary headless configuration rather than modifying the committed default configuration.
7. Report changed files, verification commands, results, and remaining risks.

## Constraints
- Do not alter `target/` or `output/` unless explicitly needed by the verification.
- Preserve Java 21 compatibility and existing package structure.
- Avoid weakening, deleting, or broadly skipping tests.
- Leave unrelated files unchanged.
