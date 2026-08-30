# Personal Possession Ecosystem Manager - File Plan

> Planning document only. This file does not approve implementation and does not create application code, Gradle configuration, or project directories.

## Agreed Scope

The application is a local JavaFX desktop manager for **physical possessions**. It supports:

- possession CRUD, categorization, status, tags, and notes;
- lifecycle-event history for each possession;
- user-defined, typed relationships between possessions;
- manual compatibility requirements and assessments for hardware;
- search, filters, detail views, and local persistence.

The application will not be a generic entity database, graph editor, cloud service, or automatic hardware-specification checker.

## Core Design Decisions

- A relationship stores stable possession and relationship-type IDs. It never relies on free-typed relationship text.
- A relationship type owns its forward label, inverse label, and whether it is directed or symmetric.
- The relationship form chooses a valid displayed label such as `stored in` or `contains`; it does not expose a raw direction setting.
- Compatibility information is manually entered. The application will not query hardware catalogues or infer electrical compatibility.
- Permanent deletion requires confirmation and removes the possession's lifecycle history.

## Git Workflow

- Stage files in logical groups by purpose; never stage unrelated changes together.
- Split each coherent increment into separate commits where applicable: `feat:` for user-visible implementation, `test:` for automated-test additions or changes, `docs:` for guides/logs/reflections, `refactor:` for behaviour-preserving restructuring, and `chore:` for build, tooling, or housekeeping work.
- Each commit must be small, independently understandable, and followed by the relevant verification.

## Build and Repository Files

| Planned file | Responsibility |
| --- | --- |
| `settings.gradle` | Declares the Gradle project name. |
| `build.gradle` | Configures Java 25, JavaFX, Gson JSON persistence, JUnit 5, test/run tasks, and release packaging. |
| `.gitignore` | Excludes Gradle, build, IDE, and local application-data files. |
| `README.md` | Provides a short product description and verified setup/launch instructions. |
| `gradlew`, `gradlew.bat`, `gradle/wrapper/*` | Gradle wrapper files generated during project setup. |

## Production Source Files

All Java files will be under `src/main/java/<base-package>/`. The final base package will be selected once and used consistently.

### Domain Model

| Planned file | Key responsibilities and public methods |
| --- | --- |
| `model/Possession.java` | Stores ID, name, category, status, tags, notes, and timestamps. Methods: `create(...)`, `updateDetails(...)`. |
| `model/PossessionCategory.java` | Defines the fixed categories `ELECTRONICS`, `ACCESSORIES`, `HOBBY`, `BOOKS`, `TRAVEL`, and `OTHER`. |
| `model/PossessionStatus.java` | Defines `IN_USE`, `LENT_OUT`, and `RETIRED`. |
| `model/LifecycleEvent.java` | Stores event ID, possession ID, date, type, description, and notes. Methods: `create(...)`, `update(...)`. |
| `model/LifecycleEventType.java` | Defines `PURCHASE`, `ADDED`, `MAINTENANCE`, `REPAIR`, `LOAN`, `RETURN`, `UPGRADE`, `RETIRED`, and `OTHER`. |
| `model/RelationshipType.java` | Stores ID, forward label, inverse label, and kind. Methods: `create(...)`, `rename(...)`, `updateLabels(...)`. |
| `model/RelationshipKind.java` | Defines `DIRECTED` and `SYMMETRIC`. |
| `model/PossessionRelationship.java` | Stores relationship ID, canonical source possession ID, relationship type ID, target possession ID, and note. Methods: `create(...)`, `updateNote(...)`. |
| `model/CompatibilityEntry.java` | Stores ID, possession ID, requirement name, assessment status, and notes. Methods: `create(...)`, `update(...)`. |
| `model/CompatibilityStatus.java` | Defines `COMPATIBLE`, `INCOMPATIBLE`, and `UNKNOWN`. |
| `model/AppData.java` | Holds the complete persisted collections of possessions, events, types, relationships, and compatibility entries. |

### Application Services

| Planned file | Key responsibilities and public methods |
| --- | --- |
| `service/PossessionService.java` | Possession CRUD and querying. Methods: `addPossession(...)`, `updatePossession(...)`, `deletePossession(...)`, `findById(...)`, `search(...)`, `filterByCategory(...)`, `filterByStatus(...)`, `listAll()`. |
| `service/LifecycleEventService.java` | Lifecycle-event CRUD. Methods: `addEvent(...)`, `updateEvent(...)`, `deleteEvent(...)`, `listForPossession(...)`. |
| `service/RelationshipTypeService.java` | Relationship-type CRUD and validation. Methods: `addType(...)`, `updateType(...)`, `deleteType(...)`, `listTypes()`, `findType(...)`. |
| `service/RelationshipService.java` | Relationship CRUD and relation-label resolution. Methods: `addRelationship(...)`, `updateRelationship(...)`, `deleteRelationship(...)`, `listForPossession(...)`, `hasRelationships(...)`. |
| `service/CompatibilityService.java` | Manual compatibility-entry CRUD. Methods: `addEntry(...)`, `updateEntry(...)`, `deleteEntry(...)`, `listForPossession(...)`. |
| `service/PossessionSummaryService.java` | Creates display-ready overview data. Methods: `createSummary(...)`, `getImmediateStorage(...)`, `getRelationshipCount(...)`, `getRecentEvents(...)`. |
| `service/RelationshipView.java` | Display record containing relationship ID, visible label, direction indicator, related possession, and note. |
| `service/PossessionSummary.java` | Display record containing possession statistics for dashboard/detail views. |
| `service/ValidationException.java` | Represents a clear user-facing domain validation failure. |

### Persistence

| Planned file | Key responsibilities and public methods |
| --- | --- |
| `storage/AppDataFile.java` | Resolves the cross-platform data location. Method: `getDataFilePath()`. |
| `storage/JsonStorage.java` | Reads and writes one local JSON data file. Methods: `load()`, `save(AppData)`, `createBackupOfCorruptFile()`. |
| `storage/StorageException.java` | Represents a load/save failure that the UI can display safely. |

### JavaFX Application and Controllers

| Planned file | Key responsibilities and public methods |
| --- | --- |
| `App.java` | Application entry point. Methods: `main(...)`, `start(...)`, `stop()`. It initializes services, loads data, opens the main window, and saves on a clean exit. |
| `ui/AppServices.java` | Holds the shared services supplied to JavaFX controllers. |
| `ui/ViewNavigator.java` | Switches primary views. Methods: `showDashboard()`, `showPossessionDetail(UUID)`, `showError(...)`. |
| `ui/DashboardController.java` | Controls the dashboard. Methods: `initialize()`, `refreshTable()`, `onSearchChanged()`, `onCategorySelected()`, `onQuickFilterSelected()`, `onAddPossession()`, `onPossessionSelected()`. |
| `ui/PossessionDetailController.java` | Controls the detail view and tabs. Methods: `setPossession(UUID)`, `refreshOverview()`, `refreshLifecycleEvents()`, `refreshRelationships()`, `refreshCompatibility()`, `onEditPossession()`, `onDeletePossession()`. |
| `ui/PossessionDialogController.java` | Controls add/edit possession input. Methods: `setPossessionForEdit(...)`, `onSave()`, `onCancel()`. |
| `ui/LifecycleEventDialogController.java` | Controls add/edit lifecycle-event input. Methods: `setEventForEdit(...)`, `onSave()`, `onCancel()`. |
| `ui/RelationshipDialogController.java` | Controls add/edit relationship input. Methods: `setPossession(...)`, `setRelationshipForEdit(...)`, `refreshRelationshipLabels()`, `onSave()`, `onCancel()`. |
| `ui/RelationshipTypeDialogController.java` | Controls add/edit relationship-type input. Methods: `setTypeForEdit(...)`, `onRelationshipKindChanged()`, `onSave()`, `onCancel()`. |
| `ui/RelationshipTypeManagerController.java` | Controls the relationship-type list. Methods: `refreshTypes()`, `onAddType()`, `onEditType()`, `onDeleteType()`. |
| `ui/CompatibilityController.java` | Controls compatibility-table actions. Methods: `setPossession(...)`, `refreshEntries()`, `onAddEntry()`, `onEditEntry()`, `onDeleteEntry()`. |
| `ui/CompatibilityDialogController.java` | Controls add/edit compatibility input. Methods: `setEntryForEdit(...)`, `onSave()`, `onCancel()`. |

## Resources

All resources will be under `src/main/resources/<base-package>/`.

| Planned file | Responsibility |
| --- | --- |
| `ui/dashboard.fxml` | Main dashboard: category list, quick filters, search, and possession table. |
| `ui/possession-detail.fxml` | Detail overview and Lifecycle History, Relationships, and Compatibility tabs. |
| `ui/possession-dialog.fxml` | Add/edit possession dialog. |
| `ui/lifecycle-event-dialog.fxml` | Add/edit lifecycle-event dialog. |
| `ui/relationship-dialog.fxml` | Add/edit relationship dialog. |
| `ui/relationship-type-dialog.fxml` | Add/edit relationship-type dialog. |
| `ui/relationship-type-manager.fxml` | Relationship-type list and actions. |
| `ui/compatibility-dialog.fxml` | Add/edit manual compatibility-entry dialog. |
| `ui/app.css` | Shared JavaFX styling, including status badges, tables, spacing, and error states. |

## Automated Tests

All tests will be under `src/test/java/<base-package>/`.

| Planned file | Required coverage |
| --- | --- |
| `service/PossessionServiceTest.java` | Creation, editing, deletion, searching, and filters. |
| `service/LifecycleEventServiceTest.java` | Possession ownership and chronological event ordering. |
| `service/RelationshipTypeServiceTest.java` | Case-insensitive duplicate prevention, type-kind rules, and deletion protection. |
| `service/RelationshipServiceTest.java` | Self-link prevention, duplicate prevention, correct inverse display labels, and deletion. |
| `service/CompatibilityServiceTest.java` | Compatibility-entry CRUD and possession ownership validation. |
| `storage/JsonStorageTest.java` | Save/load round trips and safe corrupt-data handling. |

## Documentation and Submission Artifacts

| Planned file or folder | Responsibility |
| --- | --- |
| `docs/UserGuide.md` | Verified setup, launch, features, examples, persistence behaviour, limitations, and screenshots. |
| `docs/DeveloperGuide.md` | Actual architecture, design decisions, testing strategy, development process, and acknowledgements. |
| `docs/Reflections.md` | AI-assisted software-engineering reflection with at least three analysed prompts. |
| `docs/images/` | Only screenshots and diagrams used by project documentation. |
| `logs/` | Verified summaries of substantive AI interactions as development proceeds. |
| `release/` | Latest tested executable JAR containing required JavaFX libraries. |

## Validation Rules to Implement and Test

1. A relationship refers only to existing possessions and an existing relationship type.
2. A possession cannot be related to itself.
3. Relationship-type names are unique ignoring case and surrounding whitespace.
4. A directed relationship type has distinct forward and inverse labels; a symmetric type has one shared label.
5. Renaming a relationship type updates its displayed labels everywhere.
6. A relationship type in use cannot be deleted until its relationships are deleted or migrated.
7. Deleting a possession requires confirmation and removes its lifecycle history.
8. Lifecycle events and compatibility entries must belong to an existing possession.
9. Invalid dates, empty required names, and corrupt saved data produce clear errors without overwriting valid data.

## Deliberate Exclusions

- graph database or graph-canvas editor;
- arbitrary user-defined schemas and fields;
- cloud sync, login, accounts, or network APIs;
- automatic hardware catalogue lookups or electrical-specification inference;
- generic task, deadline, or chat-management features.
