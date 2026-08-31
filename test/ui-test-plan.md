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

## UI-002: Manage possessions

- Aim: confirm possession add, edit, search, filtering, and persistence behavior without archival.
- Preconditions: launch the dashboard with an accessible local data directory.
- Actions: confirm both filters begin as `All`, add a named possession, edit its location, search by a
  tag, confirm `Archived` is absent from the status controls, combine category and status filters,
  clear filters, and confirm the dashboard has no archive action. Close and relaunch the app.
- Expected result: the initial dashboard is unfiltered, only the supported statuses are available,
  no archive action appears, and each successful change appears immediately and remains after
  relaunch.
- Observed result: Passed on Windows 11 on 31 August 2026 using `./gradlew.bat run`. The student
  confirmed archive controls and the `Archived` status are absent, the three supported statuses are
  available, add/edit/search/filter/clear-filter behavior works, deletion still works, and data
  remains after relaunch.

## UI-003: Manage lifecycle history

- Aim: confirm that a possession's lifecycle events can be added, changed, removed, ordered, and
  restored from local storage.
- Preconditions: launch the dashboard with at least one active possession and an accessible local
  data directory.
- Actions: select a possession, choose `View Details`, inspect its details and lifecycle history, add
  two dated events, edit one description, confirm newest-date-first ordering, delete one event,
  close the app, and relaunch it.
- Expected result: the detail screen shows the selected possession, successful changes appear at
  once, the newer event is listed first, and the undeleted event remains after relaunch. An invalid
  future date shows a clear error and does not add an event.
- Observed result: Passed on Windows 11 on 29 August 2026 using `./gradlew.bat run`. The student
  confirmed the possession details, add/edit/delete, newest-first ordering, future-date validation,
  and persistence after relaunch. A screenshot of lifecycle history was supplied in the development
  conversation.

## UI-004: Permanently delete a possession

- Aim: confirm that permanent deletion removes the selected possession and its lifecycle history.
- Preconditions: launch the dashboard with two possessions. Add two lifecycle events to the first
  possession and one lifecycle event to the second possession.
- Actions: confirm `Delete Selected` is disabled without a selection. Select the first possession,
  choose `Delete Selected`, inspect the lifecycle-event count, cancel, reopen the confirmation,
  confirm deletion, close the app, and relaunch it.
- Expected result: cancellation preserves all data. Confirmation reports two lifecycle events,
  permanently removes the selected possession and those events, immediately refreshes the dashboard,
  preserves the second possession and its event, and remains effective after relaunch.
- Observed result: Passed on Windows 11 on 31 August 2026 using `./gradlew.bat run`. The student
  confirmed the disabled state, correct count of two lifecycle events, cancellation, immediate
  dashboard removal, preservation of unrelated data, and persistence after relaunch.

## UI-005: View possession details and lifecycle history

- Aim: confirm the detail screen presents possession information and lifecycle history without tabs.
- Preconditions: launch the dashboard with one possession containing notes and one without notes.
  Add at least one lifecycle event to either possession.
- Actions: open each possession's detail screen. Inspect the location, tags, notes area, lifecycle
  heading, action buttons, and event table. Return to the dashboard using `Back to Dashboard`.
- Expected result: details and lifecycle history appear together without `Overview` or `Lifecycle
  History` tabs. Notes appear in a fixed-height, read-only multiline box, wrap without resizing the
  screen, and show `Not recorded` when empty. Lifecycle actions and the event table remain usable,
  and `Back to Dashboard` returns to the dashboard.
- Observed result: Passed on Windows 11 on 31 August 2026 using `./gradlew.bat run`. The student
  confirmed the lifecycle-focused detail screen after checking the listed behavior.

## UI-006: Choose lifecycle-event dates from the calendar

- Aim: confirm lifecycle-event dates can only be selected using the date-picker calendar.
- Preconditions: launch the dashboard with at least one possession and open its detail screen.
- Actions: choose `Add Event`, try to type and paste into the date field, hover over the displayed
  date, click it, then use the calendar button to select a date. Repeat in the `Edit Lifecycle Event`
  dialog.
- Expected result: typing and pasting do not change the date. Clicking either the displayed date or
  calendar button opens the calendar in both dialogs. The displayed date shows an arrow cursor, not
  a text cursor. Selecting a future date and choosing `OK` still shows the existing validation error.
- Observed result: Passed on Windows 11 on 1 September 2026 using `./gradlew.bat run`. The student
  confirmed that both date-selection routes work in both dialogs, the date display has an arrow
  cursor, typing and pasting are blocked, and future-date validation remains in place.
