# Possession Manager Developer Guide

## Architecture

The foundation uses Gradle 9.7.1, Java 25, JavaFX 25.0.4, and JUnit Jupiter. The Gradle wrapper
is the supported project entry point; no system Gradle installation is required.

`com.possessionmanager.App` is the JavaFX entry point. It loads local data, creates shared services,
navigates between the dashboard and possession detail screen, and loads the shared stylesheet from
`src/main/resources/com/possessionmanager/app.css`.

Phase 4 has four small layers:

- `model` contains immutable possession, lifecycle-event, and relationship-label data, their fixed
  enums and built-in templates, and the persisted `AppData` snapshot.
- `service.PossessionService` validates and normalizes input, owns records by stable UUID, and
  provides active-list, search, filter, edit, and archive operations.
- `service.LifecycleEventService` validates dated event input, verifies that each event refers to an
  existing possession, and lists a possession's events newest first.
- `service.RelationshipTypeService` validates unique reusable labels and owns their add, edit,
  deletion, and lookup operations. It stores both possession readings so a label can be displayed
  consistently without exposing graph terminology in the UI.
- `storage.JsonStorage` reads and writes one UTF-8 JSON file under the user's home directory. It
  writes through a temporary file, preserves a corrupt file before reporting a load failure, and
  validates possession-event references and relationship labels when loading or saving.
- `ui.DashboardView`, `ui.PossessionDetailView`, `ui.RelationshipTypeManagerView`, and their
  dialogs use the services and storage without embedding domain validation in table controls.

The label manager has two fixed-size dialogs: a compact built-in chooser and a separate custom-label
dialog. Both use one formatter to show the two possession readings. This avoids changing dialog size
after opening and prevents the chooser, custom preview, and manager table from contradicting one
another. Actual possession-to-possession links remain a later phase.

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

Relationship-label model, validation, persistence, template, and two-reading formatter tests passed
on Windows 11. The student manually verified built-in and custom label add/edit/delete,
duplicate-name validation, fixed-size dialogs, matching examples, and persistence after relaunch on
30 August 2026. macOS and Linux runtime testing remains outstanding.

The supplied `check_mp1_structure.sh` script was also run on 29 August 2026. All required source,
documentation, log, and directory checks passed. The only failure was the expected absence of a
release JAR; creating a tested self-contained release is deferred until the final release phase.

## Reuse and AI assistance

This project uses the JavaFX Gradle Plugin, JavaFX, Gradle, JUnit Jupiter, and Gson 2.13.2 for
JSON serialization. The project-local `present-changes-visually` workflow is adapted from the
SE-EDU skill at https://github.com/se-edu/skill-present-changes-visually. No third-party
application code has been copied into the repository.

I used Codex to help plan the application, set up the initial build, draft documentation, run
verification commands, and refine the relationship-label UI after visual feedback. I reviewed the
output and remain responsible for the submitted content and quality. Substantive interactions are
summarized in `logs/`.
