---
name: snapshot-diagnostics
description: "Diagnose life-simulation snapshot and output behavior. Use for missing frames, invalid images, output directories, snapshot intervals, and timelapse generation."
argument-hint: "Describe the snapshot or timelapse symptom"
user-invocable: true
---

# Snapshot Diagnostics

## Procedure
1. Inspect `Simulation.saveSnapshot` behavior and `snapshotInterval` configuration references.
2. Check the configured `outputDir` and whether the headless run creates it.
3. Run a bounded headless simulation to generate a small sample set.
4. Validate image files, frame count, dimensions, and sequential names.
5. If timelapse output is relevant, verify FFmpeg availability and `generate_timelapse.sh` arguments.

## Verification
- Confirm the process reaches at least one snapshot round.
- Compare configured and actual snapshot count.
- Distinguish missing snapshots from unreadable or corrupt image files.

## Constraints
- Do not delete existing generated media as cleanup.
- Keep test runs limited and use an isolated temporary output directory when supported.
