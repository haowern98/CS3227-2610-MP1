# UI Test Plan

## Recording rules

For each relevant test, record the platform, command, actual result, and screenshot path when one is
captured. A result is not verified until a human has visually checked the relevant behavior.

## UI-001: Launch the application

- Aim: confirm that the JavaFX application starts without a startup error.
- Preconditions: a Java 25 JDK is available and the repository root is the working directory.
- Actions: run `./gradlew.bat run` on Windows or `./gradlew run` on macOS/Linux.
- Expected result: a window titled `Possession Manager` opens and shows the foundation screen.
- Observed result: Passed on Windows 11 on 29 August 2026 using `./gradlew.bat run`. The window title,
  `Possession Manager` heading, placeholder text, and stylesheet were visually confirmed from the
  student's screenshot. No Gradle error or Java native-access warning occurred.
