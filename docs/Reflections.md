# Reflections on AI-Assisted Software Engineering

This living document records verified reflections as the project develops. The student remains
responsible for every design choice and submitted artifact.

## 1. Narrowing the product concept

**Prompt formulation.** I asked the AI to generate personal-utility ideas that were not generic
to-do applications and then to assess a possession-relationship manager against similar products.

**Assumptions and limitations.** Early suggestions described a broad personal knowledge graph. That
was too close to existing generic graph tools and too broad for a focused individual project.

**Verification and evolution.** I compared the direction with public examples and course constraints,
then narrowed the product to physical possessions only: possession CRUD, lifecycle history, typed
relationships, and manually entered hardware compatibility.

**Engineering judgement.** I rejected automatic compatibility inference, cloud synchronization, and
a graph-canvas editor. They would add complexity without being needed to solve the stated problem.

## 2. Designing typed relationships

**Prompt formulation.** I asked how users could define relationships without small wording
differences such as `stored in` and `stored-in` breaking later queries.

**Assumptions and limitations.** An AI suggestion alone cannot establish a usable data model. It could
also over-generalize the product into a schema builder.

**Verification and evolution.** The chosen design stores stable IDs for possessions and relationship
types. A type is defined once and selected from a controlled list when a relationship is created.
The planned validation rules cover duplicate type names, self-links, directed labels, and type
deletion while in use.

**Engineering judgement.** User-defined relationship types remain, but free-text type entry on each
relationship is excluded. The future implementation must prove these rules with automated tests.

## 3. Choosing the development foundation

**Prompt formulation.** I asked the AI to plan an incremental JavaFX and Gradle foundation aligned
with the assignment's Java 25, testing, documentation, and public-repository requirements.

**Assumptions and limitations.** A build file that looks plausible is not enough; versions and runtime
behavior must be checked on the local platform.

**Verification and evolution.** I verified Gradle's Java 25 compatibility, generated a Gradle wrapper,
ran a test-first resource smoke test, and launched the JavaFX shell on Windows. The first test run
also revealed a missing JUnit Platform launcher; I corrected the configuration and reran the test
until it failed for the intended missing-resource reason before adding that resource.

**Engineering judgement.** The Phase 1 scope stops at a small, runnable shell. Domain abstractions,
storage, and feature dialogs are deferred until they are needed by later increments.

## 4. Implementing the first persistence slice

**Prompt formulation.** I asked the AI to implement only possession CRUD and local JSON persistence,
with test-first checks and no relationship or compatibility features.

**Assumptions and limitations.** A local JSON file is portable in format, but platform portability
also depends on correct path handling and actual testing on each operating system. Automated tests
cannot prove that a JavaFX dialog is understandable or visually correct.

**Verification and evolution.** The possession and storage tests were written before the corresponding
implementation and first failed because the classes did not exist. They then passed after the
minimal model, service, and storage code was added. The application uses Java `Path` APIs and stores
data below the current user's home directory. The Windows UI workflow was manually verified; other
platforms remain unverified.

**Engineering judgement.** I kept this phase to one persisted entity and a single dashboard. I did
not add a generic repository layer, cloud synchronization, or a relationship graph before a feature
requires them.
