# Developer Guide finalization

## Prompt summary

The student requested a Developer Guide that follows the CS2103 documentation guidance and
describes the final Possession Manager system rather than presenting a chronological development
or phase history. The first approved step was to establish the complete section hierarchy and
linked table of contents before writing detailed content or creating diagrams.

## AI assistance

Codex proposed a top-down structure covering setup, architecture, component design, selected
implementation details, design considerations, testing, manual testing, and acknowledgements. The
student reviewed and approved that structure before it was added to `docs/DeveloperGuide.md`.

## Current scope

This work completes the Developer Guide from the approved outline. It covers the current design,
selected implementation details, design decisions, testing approach, manual testing instructions,
acknowledgements, and two UML diagrams.

## Introduction and setup

Codex checked the application entry point, Gradle build, wrapper configuration, CI workflow, and
User Guide before drafting the introduction and development setup. The documented commands use the
repository's Gradle wrapper and distinguish Windows from macOS and Linux. No system Gradle
installation is required.

## Architecture

Codex traced the dependencies from `App` through the JavaFX views, services, model records,
storage classes, and local JSON file. The architecture section presents those components at a
high level and records the startup flow. A PlantUML component diagram was created from the current
source; lower-level classes and calls were omitted for readability and will be covered by later
design diagrams.

The student reviewed the architecture diagram against the intended layered UML presentation. The
final version uses the application's actual Application, UI, Service, Model, and Storage components,
removes decorative component glyphs, and uses a horizontal runtime flow so that arrows do not
obstruct text.

## Design, implementation, and testing

Codex checked the JavaFX views, model records, services, storage implementation, JUnit suites, CI
workflow, User Guide, and UI test plan before drafting the remaining sections. The guide focuses on
the actual boundaries and non-trivial behavior of the current application rather than repeating
class-level details or presenting a phase history.

The manual testing section distinguishes instructions from completed evidence. In particular, the
save-failure recovery case remains marked as pending in `test/ui-test-plan.md` and is not presented
as manually verified.

## Sequence diagram

The possession-deletion sequence diagram was generated from `DashboardView` and
`PersistentChange`. It shows the `Runnable` callback, lifecycle-event deletion before possession
deletion, complete snapshots, save success, and restore-before-rethrow behavior. The failure branch
shows the error before `refreshTable()`, matching the `catch` and `finally` order in the UI.

## Verification

Verification was performed on Windows 11 using Temurin JDK 25.0.4.1. The command
`.\gradlew.bat clean check javadoc assemble` completed successfully with 29 passing tests; Javadoc
reported 19 existing missing-comment warnings. `markdownlint-cli2` checked 20 repository Markdown
files and reported no issues. PlantUML accepted both diagram sources, referenced local files were
present, and both rendered PNGs were visually checked for readability. No new UI test was needed
because application behavior was not changed.
