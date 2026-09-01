# Refinement and Reliability

These entries record the later AI-assisted refinements to interaction, integrity, and persistence.

## Simplifying possession workflows

**Prompt strategy.** Refinement requests focused on removing unnecessary concepts and making the
existing possession workflow easier to understand: archive removal, permanent deletion, and a
single-screen possession detail view.

**AI contribution and corrections.** Codex traced the archive feature through the model, service,
UI, tests, and guides before removal. It also proposed a cascade-deletion path that removes a
possession's lifecycle events before the possession, and a simpler detail view without tabs. The
student decided not to provide an archive-data migration because earlier development data had been
cleared; this avoided adding unsupported compatibility behavior.

**Verification and judgement.** A status-contract test failed while `ARCHIVED` was still exposed,
then passed after removal. Deletion tests initially failed before the required service methods were
implemented. The full Gradle suite passed on Windows 11, and the student manually checked deletion
confirmation and cancellation, unrelated-data preservation, detail readability, and persistence
after relaunch.

**Reflection takeaway.** Asking for the smallest coherent workflow produced a better result than
adding another state or migration path. Tests caught stale contracts; manual review determined
whether the simplified layout was actually clearer.

## Date and detail-view interaction

**Prompt strategy.** The interaction prompts described concrete user-visible outcomes: calendar
selection only, a calendar opening when the full date display is clicked, and detail tabs aligned
with the page header without affecting other controls.

**AI contribution and corrections.** Codex located the existing JavaFX `DatePicker` and used its
non-editable setting rather than adding new date handling. It scoped the tab styling to the detail
view. The student requested a later interaction refinement so the non-editable date display opens
the calendar and retains the default cursor.

**Verification and judgement.** The Gradle tests passed after each change on Windows 11. The
student manually passed UI-005 and UI-006 using `./gradlew.bat run`: the calendar accepts selection,
typing and pasting are blocked, the date display opens the picker, future-date validation remains,
and the tab styling does not affect unrelated controls.

**Reflection takeaway.** Precise descriptions of a GUI outcome are more effective than vague
requests to “improve” a control. CSS and JavaFX changes still need a human screenshot or direct
visual check.

## Save-failure recovery

**Prompt strategy.** A code-quality review identified that failed JSON saves left mutations in
memory. The approved fix required a complete `AppData` snapshot, restoration of the contents of
the existing services rather than replacement service instances, and different handling for
validation versus storage failures.

**AI contribution and corrections.** Codex traced all mutation-and-save paths, proposed the shared
`PersistentChange` boundary, added failure-first tests, and applied rollback after any runtime
failure. The design constraint about retaining existing service instances was important because UI
views and the lifecycle service hold references to them.

**Verification and judgement.** `./gradlew.bat test --rerun-tasks` passed on Windows 11 with
Microsoft OpenJDK 25.0.4.1. The rollback tests use real `JsonStorage` with an unwritable path
rather than a mock. Manual UI-007, which forces a save failure in the running application, remains
pending and is not claimed as passed.

**Reflection takeaway.** AI helped identify a reusable transactional boundary, but the student had
to define the rollback invariant and reject a superficially simple but unsafe service-replacement
approach. A testable failure path and a pending manual test should be recorded honestly.
