# Possession Manager User Guide

## Current status

Phase 4 adds manually verified relationship-label management on Windows 11. Possession-to-possession
links and compatibility assessments are not available yet.

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
comma-separated tags, and notes are optional. Select a row and choose **Edit Selected** or
**Archive Selected** to manage an existing item. Choose **View Details** or double-click a row to
open its detail screen.

Use the search box to match names and tags. Category and status filters work together with search.
Archived items are preserved in the data file but do not appear in active dashboard results.

## Recording lifecycle history

Open a possession's detail screen and select **Lifecycle History**. Use **Add Event** to record a
purchase, maintenance activity, loan, return, repair, upgrade, retirement, or other dated event.
Each event needs an event type, date, and description; notes are optional. Dates after today are
rejected to avoid accidental future history.

Select an event to edit or delete it. Events appear newest first. All successful event changes are
saved with the possession data and remain after the application is relaunched.

## Managing relationship labels

Choose **Manage Relationship Labels** on the dashboard to create reusable wording for future links
between possessions. Select a built-in label such as Storage, Charging, Compatibility, Part /
accessory, or Use together. The chooser explains both readings; for example, Storage reads:

```text
Item A is stored in Item B, Item B contains Item A
```

Choose **Custom relationship…** and then **OK** to open the custom-label dialog. Enter a label name,
the phrase after `Item A is`, and the phrase used when viewing Item B. Select **Use the same wording
when viewing either item** for wording such as `compatible with`. Label names must be unique without
regard to capitalization or surrounding spaces.

Select a row to edit or delete a label. Label definitions are saved locally and remain after relaunch.
They define wording only; creating links between individual possessions is not available yet.

## Running automated tests

From the repository root, run:

```powershell
.\gradlew.bat test
```

```bash
./gradlew test
```

The suite covers stylesheet packaging, possession validation, CRUD querying and archival,
lifecycle-event validation and ordering, JSON round trips, missing data files, and corrupt-data
backup handling, relationship-label validation, and relationship-example wording.

## Data and limitations

Data is saved after each successful addition, edit, archive, or lifecycle-event change to
`~/.possession-manager/data.json`, where `~` is the current user's home directory on Windows,
macOS, or Linux. If the file is corrupt, the app preserves it as a timestamped `data.corrupt-*.json`
backup and displays an error. Cross-platform runtime testing is still required.

Relationship labels are included in the same data file. The app has not yet implemented actual links
or compatibility assessments between possessions.
