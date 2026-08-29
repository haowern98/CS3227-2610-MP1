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

## UI-003: Manage lifecycle history

- Aim: confirm that a possession's lifecycle events can be added, changed, removed, ordered, and
  restored from local storage.
- Preconditions: launch the dashboard with at least one active possession and an accessible local
  data directory.
- Actions: select a possession, choose `View Details`, inspect the Overview tab, open `Lifecycle
  History`, add two dated events, edit one description, confirm newest-date-first ordering, delete
  one event, close the app, and relaunch it.
- Expected result: the detail screen shows the selected possession, successful changes appear at
  once, the newer event is listed first, and the undeleted event remains after relaunch. An invalid
  future date shows a clear error and does not add an event.
- Observed result: Passed on Windows 11 on 29 August 2026 using `./gradlew.bat run`. The student
  confirmed the Overview and Lifecycle History screens, add/edit/delete, newest-first ordering,
  future-date validation, and persistence after relaunch. A screenshot of lifecycle history was
  supplied in the development conversation.

## UI-004: Manage saved relationships

- Aim: confirm that predefined and custom saved relationship wording can be added, edited, deleted,
  and restored from local storage.
- Preconditions: launch the dashboard with an accessible local data directory.
- Actions: choose `Manage Saved Relationships`, add a Storage template with `stored in / contains`,
  add a custom relationship with its own name and wording, edit one relationship, try to add a
  duplicate name, delete a relationship, close the app, and relaunch it.
- Expected result: the table uses plain-language relationship wording, predefined choices populate
  the expected phrases, custom wording remains editable, validation prevents duplicate names,
  successful changes appear immediately, and undeleted relationships remain after relaunch.
- Observed result: Not run. Manual review pending on Windows 11.
