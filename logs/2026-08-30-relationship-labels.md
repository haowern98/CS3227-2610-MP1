# AI Interaction Log: Relationship Labels

## Purpose

Add reusable relationship labels before implementing actual links between possessions.

## Substantive assistance used

- Proposed a controlled list of built-in labels with a separate custom-label path, rather than
  accepting near-duplicate free-text wording on each future relationship.
- Added model, validation, persistence, manager, and dialog support for reusable labels.
- Revised the JavaFX wording after visual feedback: technical direction terms were removed, the
  built-in chooser and custom dialog were separated, and both possession readings are shown.
- Added JUnit coverage for directional and same-wording example sentences.

## Verification

- The relationship-label model, validation, storage, template, and formatter tests passed on
  Windows 11 with Microsoft OpenJDK 25.0.4.1.
- The student manually verified UI-004 on Windows 11 on 30 August 2026: built-in and custom label
  add/edit/delete, duplicate-name validation, fixed-size dialogs, matching examples, and persistence
  after relaunch all passed.

## Limitations

Labels define reusable wording only. The app does not yet store or display actual links between two
possessions. macOS and Linux runtime testing remains outstanding and requires peer or TA testing
before final submission.
