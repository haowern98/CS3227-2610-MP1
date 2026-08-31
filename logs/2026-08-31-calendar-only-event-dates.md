# AI Interaction Log: Calendar-Only Event Dates

## Context

The lifecycle-event dialog allows dates to be typed or selected from its JavaFX calendar. The
student wanted selection limited to the calendar without changing future-date validation.

## Assistance

Codex identified the existing `DatePicker` setting as the smallest change and kept validation in the
service unchanged. After the student approved the change, Codex made the picker non-editable and
added UI-006 for add and edit dialogs.

The Java 25 suite passed on Windows 11 using `./gradlew.bat test --rerun-tasks`. The student confirmed
UI-006 using `./gradlew.bat run`: typing and pasting were blocked, calendar selection worked, and the
existing future-date error remained.
