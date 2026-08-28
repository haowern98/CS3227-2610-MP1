# UI Test Plan

## Recording rules

For each relevant test, record the platform, command, actual result, and screenshot path when one is
captured. A result is not verified until a human has visually checked the relevant behavior.

## UI-001: Launch the application

- Aim: confirm that the JavaFX application starts without a startup error.
- Preconditions: a Java 25 JDK is available and the repository root is the working directory.
- Actions: run `./gradlew.bat run` on Windows or `./gradlew run` on macOS/Linux.
- Expected result: a window titled `Possession Manager` opens and shows the dashboard.
- Observed result: Phase 1's foundation-screen check passed on Windows 11 on 29 August 2026. The
  dashboard version is not yet manually reviewed.

## UI-002: Manage active possessions

- Aim: confirm possession add, edit, search, filtering, archive, and persistence behavior.
- Preconditions: launch the dashboard with an accessible local data directory.
- Actions: add a named possession, edit its location, search by a tag, combine category and status
  filters, archive the row, close and relaunch the application.
- Expected result: each successful change appears immediately and remains after relaunch. The
  archived item disappears from active results without being deleted from the saved data.
- Observed result: Not run. Manual UI review is pending.
