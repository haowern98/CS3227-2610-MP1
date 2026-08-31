# Save-failure rollback

## Prompt summary

The student asked for a code-quality review, then approved a bounded fix for mutations that remained
in memory after JSON saving failed. The approved design required one complete `AppData` snapshot,
restoration of the existing service objects, distinct validation and storage messages, and no change
to successful UI behavior.

## AI assistance

Codex traced the mutation and save paths, proposed snapshot-and-rollback, added failure-first tests,
implemented a shared persistent-change boundary, and updated the relevant documentation draft.

## Verification

- Java: Microsoft OpenJDK 25.0.4.1.
- Automated test: `./gradlew.bat test --rerun-tasks` passed on Windows 11.
- The rollback tests use real `JsonStorage` with a path that cannot be created, rather than a mock.
- Manual UI-007 verification remains pending and is not claimed as passed.
