# Documentation and Delivery

These entries summarize the AI-assisted release-readiness work. The student reviewed the resulting
guides, workflow, diagrams, and verification evidence.

## Continuous integration

**Prompt strategy.** The request was for comprehensive CI appropriate to this repository without
copying another student's workflow. The agreed scope was Java 25 verification on Windows, Linux,
and macOS plus documentation-format checks.

**AI contribution and corrections.** Codex reviewed the Gradle tasks and project documentation,
then drafted an original GitHub Actions workflow. The first hosted run exposed that `gradlew` lacked
its executable bit; that repository setting was corrected before rerunning the workflow.

**Verification and judgement.** `actionlint` accepted the workflow, `markdownlint-cli2` reported no
documentation issues, and the local Java 25 command `./gradlew.bat clean test javadoc assemble`
passed on Windows 11. The subsequent hosted run passed the Windows, Linux, macOS, and documentation
jobs. Existing Javadoc warnings remain warnings rather than build failures.

**Reflection takeaway.** Local success was not enough to establish cross-platform readiness. Hosted
CI exposed a repository-metadata problem that normal Windows execution did not reveal.

## User Guide

**Prompt strategy.** The User Guide was requested to follow CS2103 documentation guidance while
remaining specific to Possession Manager. After reviewing an early draft, the student selected an
original task-based structure rather than copying another project's organisation.

**AI contribution and corrections.** Codex audited the views, validation services, storage,
manual test plan, build configuration, and supplied screenshots before drafting the guide. The
student supplied three screenshots and kept them limited to situations where they clarify the UI.

**Verification and judgement.** Documented fields, defaults, enum values, search scope, ordering,
deletion, rollback, and corrupt-data behavior were checked against source. The screenshots matched
the dashboard, detail screen, and deletion confirmation. `clean test`, Markdown linting, local
image reference checks, and standalone HTML rendering all passed on Windows 11 with Java 25.0.4.1.

**Reflection takeaway.** A documentation prompt must request an audit of the current product, not
just fluent prose. Code inspection and rendered-document review were needed to prevent inaccurate
instructions.

## Developer Guide

**Prompt strategy.** The Developer Guide was requested as a top-down CS2103-style explanation of
the final system, not a chronological “phase” report. The student approved the table of contents
before detailed writing and then reviewed the level of detail, diagrams, and repository layout.

**AI contribution and corrections.** Codex traced the actual application, UI, service, model, and
storage boundaries; documented selected implementation decisions; and created a deletion rollback
sequence diagram. The initial architecture presentation was revised after student feedback to use
the intended horizontal UML layout. The guide embeds the reviewed `architecture.png` directly.

**Verification and judgement.** On Windows 11 with Temurin 25.0.4.1,
`./gradlew.bat clean check javadoc assemble` completed with 29 passing tests. Markdown linting
passed for the repository documentation, both UML diagrams were checked for readability, and the
guide's links and local assets were inspected. No new UI test was required because this work did
not change application behavior.

**Reflection takeaway.** The most effective documentation workflow was incremental review: outline,
accurate source audit, concise prose, then rendered diagrams. Engineering judgement remained
necessary to reject excessive detail and ensure diagrams describe the real application.

## Log consolidation verification

On 1 September 2026, the previously separate interaction summaries were consolidated into these
three reflection-ready logs. On Windows 11 with Temurin JDK 25.0.4.1,
`.\gradlew.bat clean check javadoc assemble` passed. `markdownlint-cli2 "logs/**/*.md"` and
`git diff --check` also passed. The Gradle Javadoc task reported 19 existing missing-comment
warnings but did not fail the build.

## Local release-JAR smoke test

On 1 September 2026, a local `release/PossessionManager.jar` was built with the Gradle `releaseJar`
task. The JAR bundles JavaFX and Gson, declares the launcher entry point, and started normally on
Windows 11 with Temurin JDK 25.0.4.1 using `java -jar release\PossessionManager.jar`. The student
visually confirmed that the normal dashboard appeared. The JAR remains local and uncommitted until
the final documentation and release pass.

After that smoke test, the README, User Guide, and UI launch test were updated to use the release
JAR as the primary user launch path. The Developer Guide retains Gradle commands for source
development and includes the separate release-JAR verification command. Markdown linting and local
link checks passed, and the documentation was scanned for stale launch instructions and em dashes.
