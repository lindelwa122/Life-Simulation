---
name: genetic-experiment
description: "Set up reproducible genome and neural evolution experiments. Use for mutation tuning, pruning, connection graphs, seeded regressions, and evolutionary behavior comparisons."
argument-hint: "Describe the genetic hypothesis or parameter to experiment with"
user-invocable: true
---

# Genetic Experiment

## Procedure
1. Identify the exact gene, genome, mutation, or neuron behavior under test.
2. Define a graph invariant or fitness observation before changing parameters.
3. Preserve existing public APIs unless the experiment intentionally changes them.
4. Add one seeded unit test or fixed-fixture test to establish reproducibility.
5. Use a bounded headless run for runtime behavior only after unit behavior passes.
6. Compare before/after behavior using the same seed and configuration.

## Metrics
- Genome size and reachable connection count.
- Mutation add/remove effects.
- Sensory-to-internal-to-output connectivity.
- Creature survival or population outcomes across fixed generations.

## Constraints
- Never use exact unseeded random counts as assertions.
- Do not let temporary experiment config replace the committed default.
- Report both successful invariants and nondeterministic observations.
