---
description: "Use when modifying Java source, simulation classes, genes, neurons, creature state, or world behavior."
applyTo: "src/main/java/**/*.java"
---

# Java Source Guidelines

- Target Java 21; preserve existing package boundaries and public APIs.
- Keep brain/genome logic under `genes` and `neurons`; creature values belong in `mbcs`; environment behavior belongs in `world`.
- Prefer existing types such as `Coords`, `World`, and `Creature` over duplicate domain models.
- Make random or evolutionary behavior explicit and reproducible when tests or debugging depend on it.
- Keep Swing/GUI code isolated from headless simulation logic.
- Avoid retaining mutable static state unless the class already uses it as an intentional simulation-wide registry.
