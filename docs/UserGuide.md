# Possession Manager User Guide

## Current status

Phase 2 adds a manually verified possession dashboard on Windows 11. Lifecycle, relationship, and
compatibility features are not available yet.

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

The command opens the Possession Manager dashboard.

## Managing possessions

Use **Add Possession** to record a physical item. A name is required; category, location, status,
comma-separated tags, and notes are optional. Select a row and choose **Edit Selected**, double-click
the row, or choose **Archive Selected** to manage an existing item.

Use the search box to match names and tags. Category and status filters work together with search.
Archived items are preserved in the data file but do not appear in active dashboard results.

## Running automated tests

From the repository root, run:

```powershell
.\gradlew.bat test
```

```bash
./gradlew test
```

The suite covers stylesheet packaging, possession validation, CRUD querying and archival, JSON
round trips, missing data files, and corrupt-data backup handling.

## Data and limitations

Data is saved after each successful addition, edit, or archive to
`~/.possession-manager/data.json`, where `~` is the current user's home directory on Windows,
macOS, or Linux. If the file is corrupt, the app preserves it as a timestamped `data.corrupt-*.json`
backup and displays an error. Cross-platform runtime testing is still required.
