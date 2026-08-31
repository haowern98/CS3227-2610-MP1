# Possession Manager User Guide

Possession Manager is a local desktop application for recording physical belongings, where they
are kept, their current status, and important events throughout their lifecycle. It is useful for
items such as electronics, books, travel equipment, and hobby supplies.

## Quick start

### Requirements

- A Java 25 JDK. Run `java --version` and confirm that the first line reports version 25.
- An internet connection for the first Gradle run so the required dependencies can be downloaded.
- A checkout of this repository. The current development version does not yet include a packaged
  release JAR.

### Launch from source

Open a terminal in the repository root and run the command for your operating system.

Windows:

```powershell
.\gradlew.bat run
```

macOS or Linux:

```bash
./gradlew run
```

The application opens on the dashboard. Closing the application window exits the program; every
successful change has already been saved.

## Interface tour

![Dashboard containing six example possessions](images/dashboard-overview.png)

The dashboard has four main areas:

1. The search box finds possessions by name or tag.
2. The category and status controls narrow the displayed possessions. **Clear Filters** restores
   the complete list.
3. **Add Possession** opens the form for recording a new item.
4. The table displays each possession's name, category, location, status, and tags. Select a row to
   enable **View Details**, **Edit Selected**, and **Delete Selected**.

Possessions are displayed alphabetically by name. The count above the table reflects the currently
displayed results.

## Five-minute walkthrough

This walkthrough introduces the main workflow without deleting any data.

1. Select **Add Possession**.
2. Enter `Laptop` as the name, choose **Electronics**, enter `Desk Drawer` as the location, leave the
   status as **In use**, and enter `work, portable, warranty` as the tags.
3. Add a short note such as `Includes charger and protective sleeve.`, then select **OK**.
4. Enter `portable` in the search box and confirm that the Laptop remains visible.
5. Select **Clear Filters**, select the Laptop row, and choose **View Details**.
6. Select **Add Event**, choose **Purchase**, select a date that is not in the future, enter
   `Purchased for coursework` as the description, and select **OK**.
7. Return with **Back to Dashboard**, close the application, and launch it again. The Laptop and its
   event should still be present.

## Managing possessions

### Add a possession

Select **Add Possession**, complete the form, and select **OK**.

| Field | Requirement and behavior |
| --- | --- |
| Name | Required. Leading and trailing spaces are removed. |
| Category | Defaults to **Other**. Available values are Electronics, Accessories, Hobby, Books, Travel, and Other. |
| Location | Optional free text describing where the item is kept. |
| Status | Defaults to **In use**. Available values are In use, Lent out, and Retired. |
| Tags | Optional comma-separated search labels, for example `travel, fragile, warranty`. |
| Notes | Optional multiline details such as condition, included accessories, or storage reminders. |

Empty tags and extra spaces around comma-separated tags are ignored. Tags are displayed using the
capitalization entered by the user.

### Edit a possession

Select a possession and choose **Edit Selected**. Change any field and select **OK**. The item keeps
its lifecycle history after its details are edited.

### Search and filter

The dashboard updates as text is entered in the search box. Search is case-insensitive and matches
partial text in possession names and tags. It does not search locations, notes, or lifecycle-event
text.

Category and status filters can be combined with search. For example, search for `travel`, choose
**Electronics**, and choose **In use** to show only active electronics carrying that tag. Select
**Clear Filters** to clear the search text and both filters.

### View possession details

Select a possession and choose **View Details**, or double-click its table row. The detail screen
shows the category, status, location, tags, notes, and complete lifecycle history. Blank location,
tags, or notes values are displayed as `Not recorded`.

![Laptop details and lifecycle history](images/possession-details.png)

Select **Back to Dashboard** to return to the main table.

### Permanently delete a possession

Select a possession and choose **Delete Selected**. The confirmation states how many lifecycle
events will also be removed.

![Confirmation before deleting a possession and its lifecycle events](images/delete-possession-confirmation.png)

Select **Cancel** to keep the possession. Selecting **Delete** permanently removes the possession
and all of its lifecycle events. This operation cannot be undone.

## Managing lifecycle history

Lifecycle events record when something important happened to a possession. Open the possession's
detail screen to manage them.

### Add an event

Select **Add Event**, complete the form, and select **OK**.

| Field | Requirement and behavior |
| --- | --- |
| Event Type | Defaults to **Added**. Available values are Purchase, Added, Maintenance, Repair, Loan, Return, Upgrade, Retired, and Other. |
| Date | Defaults to today. Select a date using the displayed date or calendar button. Typing and pasting are disabled. Future dates are rejected. |
| Description | Required short explanation of the event. |
| Notes | Optional additional details. |

Events are displayed from newest date to oldest date.

### Edit or delete an event

Select an event and choose **Edit Selected** to change it. Choose **Delete Selected** and confirm to
remove only that event from the possession's history. Cancelling either dialog keeps the existing
data unchanged.

## Saving and recovery

Possession Manager stores all possessions and lifecycle events in one local JSON file. The file is
located at:

- Windows: `%USERPROFILE%\.possession-manager\data.json`
- macOS and Linux: `~/.possession-manager/data.json`

The application saves after every successful addition, edit, or deletion. If saving fails, an error
ending with `No changes were kept.` is displayed, and the attempted change is discarded. A later
successful save therefore cannot accidentally include the failed change.

If the application cannot load an existing data file, it attempts to preserve that file beside the
original as `data.corrupt-<timestamp>.json`, reports the problem, and starts with empty data. Do not
edit the JSON file while the application is running.

## Troubleshooting

### The application does not start

1. Run `java --version` and confirm that Java 25 is active.
2. Run the platform-appropriate Gradle command from the repository root.
3. Confirm that an internet connection is available if Gradle is downloading dependencies for the
   first time.

### A possession is not visible

Select **Clear Filters**. If it remains missing, search using part of its name. Remember that search
does not inspect location, notes, or lifecycle-event text.

### An event date is rejected

Choose today or an earlier date from the calendar. Lifecycle history cannot contain future events.

### A change cannot be saved

Read the error message and confirm that the current user can write to the `.possession-manager`
directory. The displayed data should remain unchanged when saving fails.

## Current limitations

- The current development version must be launched through Gradle; a packaged release JAR has not
  yet been produced.
- Data is local to one operating-system user profile. There is no cloud sync, account system,
  import, or export feature.
- Categories, statuses, and lifecycle-event types are fixed to the values listed in this guide.
- Permanent deletion has no undo operation.
- Development data from an earlier version using the removed `Archived` status is not migrated.
- The GUI has been manually verified on Windows 11. Automated builds and tests pass on Windows,
  Linux, and macOS, but manual GUI verification on Linux and macOS remains outstanding.
