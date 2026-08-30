# Archive removal

## Goal

Remove archival so possession removal has one clear, permanent workflow.

## AI assistance

Codex helped trace archive references across the model, service, JavaFX dashboard, tests, plans, and
guides. It proposed an incremental removal order that keeps each commit within two files.

## Verification and judgment

- Obsolete archive expectations were removed before deleting their service APIs so intermediate
  commits remain buildable.
- A status-contract test failed first because `ARCHIVED` was still exposed, then passed after the
  enum value was removed.
- The full automated suite passed on Windows 11 using `./gradlew.bat test` with the project-local
  Java 25 JDK after the implementation.
- The student manually verified the revised dashboard, status choices, possession management,
  deletion, and persistence after relaunch on Windows 11.
- No migration was added because the student had cleared earlier development data and explicitly
  chose not to support archived records from development builds.

The student reviewed the result and remains responsible for the submitted work.
