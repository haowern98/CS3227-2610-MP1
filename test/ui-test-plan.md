# UI Test Plan

## Recording rules

For each relevant test, record the platform, command, actual result, and screenshot path when one is
captured. A result is not verified until a human has visually checked the relevant behavior.

## UI-001: Launch the application

- Aim: confirm that the JavaFX application starts without a startup error.
- Preconditions: a Java 25 JDK is available and the repository root is the working directory.
- Actions: run `./gradlew.bat run` on Windows or `./gradlew run` on macOS/Linux.
- Expected result: a window titled `Possession Manager` opens and shows the dashboard.
- Observed result: Passed on Windows 11 on 29 August 2026 using `./gradlew.bat run`. The dashboard
  opened without a startup error; a screenshot was supplied in the development conversation.

## UI-002: Manage active possessions

- Aim: confirm possession add, edit, search, filtering, archive, and persistence behavior.
- Preconditions: launch the dashboard with an accessible local data directory.
- Actions: confirm both filters begin as `All`, add a named possession, edit its location, search by a
  tag, combine category and status filters, clear filters, archive the row, close and relaunch.
- Expected result: the initial dashboard is unfiltered, and each successful change appears immediately
  and remains after relaunch. The archived item disappears from active results without being deleted.
- Observed result: Passed on Windows 11 on 29 August 2026 after the filter-default fix. The student
  confirmed add, edit, search, combined filters, clear filters, archive, and persistence after
  relaunch. A screenshot of the unfiltered dashboard was supplied in the development conversation.
