# Possession Manager User Guide

## Current status

The app provides a manually verified lifecycle-history screen on Windows 11. Relationship and
compatibility features are outside the simplified product scope.

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
comma-separated tags, and notes are optional. Select a row and choose **Edit Selected** to change an
existing item. Choose **View Details** or double-click a row to open its detail screen. Choose
**Delete Selected** to permanently remove an item after reviewing the confirmation message.
Permanent deletion also removes that item's lifecycle events and cannot be undone.

Use the search box to match names and tags. Category and status filters work together with search.
The available statuses are **In use**, **Lent out**, and **Retired**.

## Recording lifecycle history

Open a possession's detail screen to see its location, tags, notes, and lifecycle history together.
Use **Add Event** to record a purchase, maintenance activity, loan, return, repair, upgrade,
retirement, or other dated event. Each event needs an event type, date, and description; notes are
optional. Click the displayed date or calendar button to choose a date; manual entry is disabled.
Dates after today are rejected to avoid accidental future history.

Select an event to edit or delete it. Events appear newest first. All successful event changes are
saved with the possession data and remain after the application is relaunched.

## Running automated tests

From the repository root, run:

```powershell
.\gradlew.bat test
```

```bash
./gradlew test
```

The suite covers stylesheet packaging, possession validation, querying and deletion,
lifecycle-event validation, ordering and deletion, JSON round trips, missing data files, and
corrupt-data backup handling.

## Data and limitations

Data is saved after each successful addition, edit, deletion, or lifecycle-event change to
`~/.possession-manager/data.json`, where `~` is the current user's home directory on Windows,
macOS, or Linux. If the file is corrupt, the app preserves it as a timestamped `data.corrupt-*.json`
backup and displays an error. If a change cannot be saved, the app discards that change and keeps
the previous data. Cross-platform runtime testing is still required.

Development data created by an earlier version with an `Archived` status is not migrated.
