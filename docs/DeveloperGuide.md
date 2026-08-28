# Possession Manager Developer Guide

## Phase 1 architecture

The foundation uses Gradle 9.7.1, Java 25, JavaFX 25.0.4, and JUnit Jupiter. The Gradle wrapper
is the supported project entry point; no system Gradle installation is required.

`com.possessionmanager.App` is the JavaFX entry point. It creates the initial application window
and loads the shared stylesheet from `src/main/resources/com/possessionmanager/app.css`.

The next phases will add domain, service, storage, and UI packages. The planned file map and
responsibilities are maintained in `Project_plans.md`.

## Testing

Run the automated test suite with `./gradlew test` on macOS/Linux or `./gradlew.bat test` on
Windows. `ApplicationResourceTest` is a foundation smoke test for runtime-resource packaging.

Manual runtime smoke check recorded on 29 August 2026:

- Platform: Windows 11 x64.
- JDK: Microsoft Build of OpenJDK 25.0.4.1.
- Command: `./gradlew.bat run`.
- Result: JavaFX started without a Gradle error or Java native-access warning. The process remained
  active, as expected while the application window was open, and was then intentionally stopped.

Future GUI flows and error paths must be manually tested and recorded when the corresponding
features exist.

The supplied `check_mp1_structure.sh` script was also run on 29 August 2026. All required source,
documentation, log, and directory checks passed. The only failure was the expected absence of a
release JAR; creating a tested self-contained release is deferred until the final release phase.

## Reuse and AI assistance

This project uses the JavaFX Gradle Plugin, JavaFX, Gradle, and JUnit Jupiter as documented build
libraries. No third-party application code has been copied into the repository.

I used Codex to help plan the application, set up the initial build, draft documentation, and run
verification commands. I reviewed the output and remain responsible for the submitted content and
quality. Substantive interactions are summarized in `logs/`.
