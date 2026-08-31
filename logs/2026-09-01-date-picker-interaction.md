# Date Picker Interaction

## Prompt summary

The student asked for the complete lifecycle-event date display to open the
calendar and for it to show an arrow cursor instead of a text cursor.

## Implemented change

The existing non-editable `DatePicker` editor now opens the calendar on click
and uses JavaFX's default cursor. The calendar button and future-date
validation remain unchanged.

## Verification

`./gradlew.bat test --rerun-tasks` passed on Windows 11 with Java 25 on
1 September 2026. The student manually passed UI-006 in the Add and Edit
Lifecycle Event dialogs using `./gradlew.bat run`.
