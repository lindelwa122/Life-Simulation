---
name: headless-simulation-verification
description: "Verify the Java life simulation in bounded headless mode. Use for launch checks, runtime smoke tests, configuration experiments, snapshots, and simulation regressions."
argument-hint: "Optionally specify generations and steps for the bounded run"
user-invocable: true
---

# Headless Simulation Verification

## When to Use
- Verify a simulation runtime change.
- Check that the packaged JAR starts without a display.
- Reproduce an issue with a short, bounded run.
- Validate snapshot or configuration behavior.

## Procedure
1. Build the application with `mvn clean package` unless an existing package is known to match the source.
2. Create a temporary config outside `simulation-config.json`, such as `/tmp/life-simulation-headless.json`.
3. Set `headless: true`, small `maxPopulation`, small `stepsPerGeneration`, and a low `maxGenerations` value.
4. If possible, set `snapshotInterval: 1` or an appropriate small interval; direct snapshots are governed by the simulation implementation.
5. Run `java -jar target/life-simulation-1.0-SNAPSHOT.jar <temporary-config-path>`.
6. Capture exit status, stdout, stderr, and any new snapshot/output files.
7. Report exact runtime duration and the point of failure, if any.

## Constraints
- Never modify the committed `simulation-config.json` for a temporary run.
- Never use the GUI if a display is unnecessary.
- Do not inspect or delete `target/` or `output/` unless the task explicitly requires generated-artifact cleanup.
- Keep verification bounded so a 10,000-generation default cannot run accidentally.
- For deterministic reproductions, preserve or set the seed mechanism used by the relevant implementation.

## Example Config
```json
{
  "fastforward": true,
  "headless": true,
  "debugBrainScan": false,
  "debugGenomeAudit": false,
  "regionalRepopulation": false,
  "numberOfGenes": 8,
  "mutationEnabled": true,
  "mutationRate": 0.02,
  "mutationAddConnectionRate": 0.01,
  "mutationRemoveConnectionRate": 0.005,
  "snapshotInterval": 1,
  "maxPopulation": 25,
  "stepsPerGeneration": 10,
  "maxGenerations": 3,
  "movementEnergyCost": 0,
  "reproductionEnergyCost": 0,
  "outputDir": "output"
}
```
