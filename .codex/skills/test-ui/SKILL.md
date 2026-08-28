---
name: test-ui
description: Test JavaFX user-facing behavior using the repository's documented UI test plan. Use after changing a JavaFX screen, dialog, navigation flow, user-facing validation, or persistence behavior visible in the UI, and before claiming that such behavior works.
---

# Test JavaFX UI

JavaFX visual behavior needs manual inspection in addition to automated tests. This workflow keeps
the manual check repeatable and records evidence without claiming that an unseen window was checked.

## Workflow

1. Read `AGENTS.md` and `test/ui-test-plan.md`.
2. Add or update test cases when the changed behavior needs coverage. Each case needs an ID, aim,
   preconditions, actions, expected result, and platform-specific observed result.
3. Run the automated suite with the Gradle wrapper for the current platform.
4. Launch the application with the Gradle wrapper. Follow each relevant test case manually.
5. Record the command, platform, actual result, and any screenshot path in the test plan. Record a
   failure exactly; do not revise the expected result to make it pass.
6. Stop at the first failed case and report the actual and expected behavior. Do not claim the UI is
   verified until the relevant cases pass.

## Commands

Use `./gradlew test` on macOS/Linux or `./gradlew.bat test` on Windows. Use the matching `run`
task to open the application. A Java 25 JDK is required.

## Scope

This skill complements JUnit tests. It does not replace tests for domain logic, storage, validation,
or filtering that can run without the GUI.
