# Initial Development

These are verified summaries of the substantive AI-assisted decisions behind the initial version of
Possession Manager. They are not conversation transcripts.

## Foundation and project scope

**Prompt strategy.** The initial requests focused on establishing a reproducible Java 25, Gradle,
JavaFX, and JUnit baseline before product features were added. This kept environment failures
separate from domain and UI work.

**AI contribution and corrections.** Codex evaluated the build configuration, generated a minimal
JavaFX shell, and proposed resource-packaging checks. The initial test setup omitted the JUnit
Platform launcher; this was corrected after the test configuration failed.

**Verification and judgement.** The stylesheet check was observed failing before the resource was
added and passing afterward. The app was launched on Windows 11 with Microsoft OpenJDK 25.0.4.1,
and the student visually confirmed the title, heading, placeholder text, and stylesheet. The
student chose a focused physical-possession utility rather than expanding the initial scope.

**Reflection takeaway.** AI was useful for getting a standard build baseline in place, but a passing
configuration test and a real launch check were both necessary. In future, dependency requirements
should be checked before drafting tests that depend on them.

## Possession management

**Prompt strategy.** The first feature request asked for one complete vertical slice: immutable
possession records, validation, search and filters, JSON persistence, and a JavaFX dashboard and
dialog. Keeping the prompt bounded avoided speculative relationship and compatibility features.

**AI contribution and corrections.** Codex proposed the model, service, storage, test cases, and
UI structure. The tests were drafted before the production classes, so they initially failed to
compile until the planned classes existed. Manual review also found that the dashboard initially
displayed selected filters, which hid a newly created default possession. The filter controls were
corrected to start and reset unfiltered.

**Verification and judgement.** `./gradlew.bat test` passed on Windows 11 with Microsoft OpenJDK
25.0.4.1 on 29 August 2026. The student manually verified adding, editing, searching, filtering,
clearing filters, permanent removal, and persistence after relaunch. JSON is kept local and UTF-8;
the data format was intentionally kept small.

**Reflection takeaway.** Unit tests validate service behavior but did not reveal the filter-default
problem; a human UI check did. Prompts for UI work should explicitly include the initial state and
reset state of controls.

## Lifecycle history

**Prompt strategy.** The follow-up requested a dated history for each possession while preserving
compatibility with possession-only JSON files. The scope deliberately used fixed event types,
date-only entries, a required description, and optional notes.

**AI contribution and corrections.** Codex proposed immutable lifecycle-event records, service
validation, storage support, tests, the possession detail screen, and the event dialog. The new
test sources initially failed until the planned lifecycle model and service were implemented.

**Verification and judgement.** `./gradlew.bat test` passed on Windows 11 with Microsoft OpenJDK
25.0.4.1 on 29 August 2026. The student manually verified event add, edit, delete, newest-first
ordering, future-date validation, and persistence after relaunch. The student accepted the limits
of no time-of-day and a fixed event-type list to keep the feature understandable and testable.

**Reflection takeaway.** The most useful prompt constrained compatibility and validation rules up
front. Engineering judgement was still needed to decide which real-world detail to omit and to
perform the visual verification that automated tests cannot provide.
