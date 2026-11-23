---
description: "Use when editing simulation configuration, generation updates, survival criteria, obstacles, or snapshot settings."
applyTo: ["simulation-config.json", "src/main/java/**/SimulationConfig.java"]
---

# Simulation Configuration Guidelines

- Prefer bounded, explicit values for verification; leave the committed default configuration stable unless configuration itself is the request.
- Keep `headless`, `maxPopulation`, `stepsPerGeneration`, and `maxGenerations` consistent with available runtime resources.
- If `generationUpdates` changes survival criteria or obstacles, verify the generation index and coordinate ranges.
- Preserve nullable handling for optional configuration fields.
- Record the purpose of experimental config changes and remove temporary output from the workspace unless requested.
