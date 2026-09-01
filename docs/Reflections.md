# Reflections on AI-Assisted Software Engineering

I used Codex as an assistant for analysis, implementation drafts, tests, documentation, and review.
I reviewed the output, made the final decisions, and remain responsible for the submitted work.
The interaction summaries in [`logs/`](../logs/) provide supporting evidence for these reflections.

## 1. Keeping data safe when saving fails

### Prompt: save-failure recovery

> Check how the app changes data and saves it to JSON. If saving fails, the app must return to
> exactly the same state as before the user action. This includes possessions and their lifecycle
> events. Keep the current UI behaviour unchanged. Use one complete `AppData` snapshot, restore the
> existing service objects instead of replacing them, give clear validation and storage error
> messages, and add focused tests without building a large new test system.

### Why this prompt needed precise constraints

The problem was not simply “handle a save error.” The existing app changed in-memory data before
saving. If saving failed, a later successful action could silently persist the earlier failed
change. Possession deletion was especially risky because it also deletes related lifecycle events.
I named the required invariant, the complete rollback unit, the service-reference constraint, and
the limit on new testing infrastructure so the proposed solution would remain small and safe.

### Save-failure assumptions, corrections, and verification

The main risk was treating rollback as replacing the service objects. That looks simple, but views
and `LifecycleEventService` already hold references to the existing services. I required the
contents of those services to be restored instead. I also clarified that a validation failure must
keep its useful validation message, while a storage failure should explain that no changes were
kept.

The result was checked with focused `PersistentChange` tests using real `JsonStorage` and a path
that cannot be created, rather than a mock that only imitates a failure. The full Java 25 Gradle
test suite passed on Windows 11. The rollback-specific manual UI test remains pending; I do not
claim that it was manually verified.

### Save-failure judgement and next time

AI helped find a shared transaction boundary, but it could not decide which state relationships
were safe to replace. I had to preserve object identity, require one snapshot containing both data
collections, and keep the UI's successful behaviour unchanged. Next time, I would ask for the
rollback invariants and failure test cases before asking for any implementation. That would make the
review criteria clear from the start.

## 2. Adding GitHub CI without copying another project

### Prompt: original cross-platform CI

> Add GitHub Actions checks for this Java 25 Gradle project. Run the important build and test
> commands on Windows, Linux, and macOS. Also check Markdown formatting. First inspect this
> repository so the workflow matches the project. Make it comprehensive but simple, and do not copy
> another student's workflow.

### Why this prompt required repository inspection

I wanted CI to confirm the same checks that matter locally, while also finding platform-specific
problems. I included the instruction to inspect the repository first because a generic Java
workflow could miss this project's Gradle wrapper, Java 25 toolchain, documentation files, or
Windows command syntax. I also wanted an original workflow, not a disguised copy of a peer's work.

### CI assumptions, corrections, and verification

The initial assumption was that a working local build meant the wrapper was ready for every hosted
runner. The first GitHub run proved that wrong: `gradlew` did not have its executable bit, which
Windows did not reveal. I corrected the file mode, then reran the hosted workflow.

`actionlint` accepted the workflow, Markdown linting passed, and the local command
`.\gradlew.bat clean test javadoc assemble` passed with Java 25 on Windows 11. The next hosted run
passed its Windows, Linux, macOS, and documentation jobs. The Javadoc task still reports existing
missing-comment warnings, so CI treats them as visible warnings rather than pretending they are
fixed.

### CI judgement and next time

Prompting was less useful than the actual hosted run here. The assistant could draft a correct-looking
YAML file, but it could not prove executable permissions or platform behaviour from my Windows
machine. I chose a small matrix and the existing Gradle commands instead of adding code-coverage
gates or complicated release automation. Next time, I would push the basic workflow earlier so
cross-platform differences are discovered before more features depend on the build.

## 3. Writing documentation that matches the real application

### Prompt: accurate, original documentation

> Read the current code, tests, UI behaviour, storage code, and screenshots before writing the
> documentation. Create an original CS2103-style guide that explains only features that really
> exist. Use screenshots only when they help. Clearly separate completed checks from tests that are
> still pending, and do not copy another project's wording or structure.

### Why this prompt required product evidence

Documentation can sound convincing while being wrong. I wanted the assistant to treat the source,
tests, UI test plan, and screenshots as evidence before writing. The prompt also prevented two
common problems: copying another student's guide structure and presenting a planned feature or
unverified test as complete.

### Documentation assumptions, corrections, and verification

Early documentation drafts were too wordy and some architecture presentations did not match the
intended UML layout. I reviewed the structure, asked for a task-based User Guide and a top-down
Developer Guide, and replaced the architecture image with the reviewed PNG used directly by the
guide. I also removed stale planning material and updated the README when an audit showed it still
described an early project state.

I checked documented fields, defaults, filtering, event ordering, deletion, rollback, and corrupt
data handling against the source. The screenshots were checked against the current screens.
Markdown linting passed, local image links were checked, and the guides were rendered for review.
The documentation describes UI-007 as pending rather than claiming a manual save-failure test that
has not been performed.

### Documentation judgement and next time

AI was useful for organising and drafting, but it was not a replacement for product knowledge or
visual review. I had to decide what to omit, reject overly detailed sections, choose only three
screenshots, and correct diagrams until they explained the actual design. Next time, I would create
a short evidence checklist before documentation drafting: current source paths, supported inputs,
manual-test status, and the exact screenshots to use. This would reduce revision cycles.

## Overall lessons

The best results came from prompts that stated the problem, the non-negotiable constraints, the
expected evidence, and the boundaries of the change. AI was less reliable for visual correctness,
cross-platform details, and deciding whether an abstraction was worth its complexity. Tests, hosted
CI, manual JavaFX checks, source inspection, and my own review were necessary to turn drafts into
work I could defend.
