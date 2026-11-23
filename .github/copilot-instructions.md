# Life Simulation Project Guidelines

## Stack
- Java 21 Maven project.
- Build entrypoint: `pom.xml`; simulation entrypoint: `io.github.lindelwa122.simulation.Simulation`.
- Runtime configuration: `simulation-config.json`.
- Tests use JUnit 5 under `src/test/java`.
- Python files are standalone utilities unless the task explicitly changes them.

## Build and verify
- Run `mvn test` for unit tests.
- Run `mvn clean package` before considering the project built.
- Use `mvn test -Dtest=<TestClass>` for a targeted test.
- Use a headless/configured run for simulation verification rather than the GUI when no display is needed.
- Inspect test reports and relevant stdout/stderr instead of relying only on Maven exit status.

## Architecture
- Keep genetic and neural behavior in `src/main/java/io/github/lindelwa122/genes` and `neurons`.
- Keep creature state in `mbcs` and world/environment behavior in `world`.
- Keep simulation orchestration in `simulation`; keep shared helpers in `utilities`.
- Preserve package boundaries and existing public APIs unless a task intentionally changes them.

## Change discipline
- Read the relevant implementation and tests before editing.
- Make the smallest coherent change that satisfies the request.
- Add or update JUnit 5 tests for behavioral changes and bug fixes.
- For simulation changes, consider determinism and seed behavior; do not add nondeterministic assertions to unit tests.
- Do not mutate `simulation-config.json` unless the request is about simulation configuration.
- Avoid destructive or generated-file changes in `target/` and `output/` unless explicitly requested.
- Preserve headless behavior and snapshot/output configuration.

## Response discipline
- Summarize changed files and verification performed.
- Report test or launch failures with exact test names and relevant diagnostics.
- If verification cannot run, state the reason and the command the user should run.
