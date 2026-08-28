# Possession Manager User Guide

## Current status

Phase 1 provides the JavaFX application foundation. Possession, lifecycle, relationship, and
compatibility features will be documented here when they are implemented and verified.

## Prerequisites

- A Java 25 JDK. Confirm that `java --version` reports version 25.
- An internet connection for the first Gradle run, which downloads build dependencies.

## Launching the application

From the repository root, run one of the following commands:

```powershell
.\gradlew.bat run
```

```bash
./gradlew run
```

The command opens a window titled `Possession Manager` with a temporary foundation screen.

## Running automated tests

From the repository root, run:

```powershell
.\gradlew.bat test
```

```bash
./gradlew test
```

The Phase 1 smoke test verifies that the application's shared stylesheet is included in the
runtime resources.

## Data and limitations

Phase 1 does not create or persist any user data. It is not yet possible to add, edit, or search
possessions.
