# Possession Manager Developer Guide

## Architecture

The foundation uses Gradle 9.7.1, Java 25, JavaFX 25.0.4, and JUnit Jupiter. The Gradle wrapper
is the supported project entry point; no system Gradle installation is required.

`com.possessionmanager.App` is the JavaFX entry point. It loads local data, creates shared services,
navigates between the dashboard and possession detail screen, and loads the shared stylesheet from
`src/main/resources/com/possessionmanager/app.css`.

Phase 3 has four small layers:

- `model` contains immutable possession and lifecycle-event data, their fixed enums, and the
  persisted `AppData` snapshot.
- `service.PossessionService` validates and normalizes input, owns records by stable UUID, and
  provides active-list, search, filter, edit, archive, and permanent-delete operations.
- `service.LifecycleEventService` validates dated event input, verifies that each event refers to an
  existing possession, lists a possession's events newest first, and deletes owned events before
  their possession is removed.
- `storage.JsonStorage` reads and writes one UTF-8 JSON file under the user's home directory. It
  writes through a temporary file, preserves a corrupt file before reporting a load failure, and
  validates possession-event references when loading or saving.
- `ui.DashboardView`, `ui.PossessionDetailView`, and their dialogs use the services and storage
  without embedding domain validation in table controls.

The planned file map and future-phase responsibilities are maintained in `Project_plans.md`.

## Testing

Run the automated test suite with `./gradlew test` on macOS/Linux or `./gradlew.bat test` on
Windows. `ApplicationResourceTest` is a foundation smoke test for runtime-resource packaging.

Manual runtime smoke check recorded on 29 August 2026:

- Platform: Windows 11 x64.
- JDK: Microsoft Build of OpenJDK 25.0.4.1.
- Command: `./gradlew.bat run`.
- Result: JavaFX started without a Gradle error or Java native-access warning. The process remained
  active, as expected while the application window was open, and was then intentionally stopped.

The full automated suite passed on Windows 11 on 29 August 2026 using `./gradlew.bat test` with
Microsoft OpenJDK 25.0.4.1. The student also manually verified the dashboard, dialog, filtering,
archive confirmation, lifecycle add/edit/delete, newest-first event ordering, future-date error,
and persistence after relaunch on Windows 11. macOS and Linux runtime testing remains outstanding.

Permanent possession deletion was manually verified on Windows 11 on 31 August 2026. The check
covered confirmation text and lifecycle-event count, cancellation, immediate dashboard refresh,
preservation of unrelated data, and persistence after relaunch.

The supplied `check_mp1_structure.sh` script was also run on 29 August 2026. All required source,
documentation, log, and directory checks passed. The only failure was the expected absence of a
release JAR; creating a tested self-contained release is deferred until the final release phase.

## Reuse and AI assistance

This project uses the JavaFX Gradle Plugin, JavaFX, Gradle, JUnit Jupiter, and Gson 2.13.2 for
JSON serialization. The project-local `present-changes-visually` workflow is adapted from the
SE-EDU skill at https://github.com/se-edu/skill-present-changes-visually. No third-party
application code has been copied into the repository.

I used Codex to help plan the application, set up the initial build, draft documentation, and run
verification commands. I reviewed the output and remain responsible for the submitted content and
quality. Substantive interactions are summarized in `logs/`.
