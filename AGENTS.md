# CS3227 Mini Project 1 — Repository Instructions

## Project Scope

- Build an **individual** Java desktop personal-utility application for CS3227 MP1.
- Use **Java SE 25** as the default Java version.
- The app must be meaningfully different from the CS2103/T Duke individual project and team project: do **not** build a generic to-do manager or a generic chat interface for managing tasks, events, deadlines, completion status, and task search.
- Keep the product focused: prefer one coherent domain and a small set of polished, testable features over a broad feature set.
- Do not use external services, paid APIs, or network-dependent features unless the user explicitly requests them.
- The app must function properly on Windows, Linux, and macOS. Record the platform and verification result for cross-platform tests; obtain peer or TA testing where a platform is unavailable locally.

## Planning and Change Control

- Do not write application code, scaffold the app, add dependencies, or make implementation changes until the user explicitly approves the proposed app concept and implementation plan.
- Before modifying the repository, inspect this file and any more-specific `AGENTS.md` files in subdirectories.
- Before changing existing code, inspect the relevant source, tests, build configuration, documentation, and current Git status. Preserve unrelated user changes.
- Use `apply_patch` for direct file edits. Do not use destructive Git operations such as `git reset --hard` or overwrite unrelated work.
- Keep changes small, explain their purpose, and verify them in proportion to their risk.
- Stage files in logical groups by purpose. Do not stage unrelated changes together.
- Keep commit purposes separate, using conventional subjects such as `feat:`, `test:`, `docs:`, `refactor:`, and `chore:`. Do not mix feature, test, documentation, refactoring, and housekeeping changes in one commit when they can be committed separately.

## Engineering Expectations

- Use standard Java conventions and keep classes cohesive with clear responsibilities.
- Separate UI, application/domain logic, persistence, and input validation where doing so improves clarity and testability.
- Add automated JUnit tests for domain logic, parsing, calculations, validation, filtering, and persistence that can be tested without the GUI.
- Manually test GUI flows and error messages; do not claim completion without recording the verification performed.
- Handle invalid input, missing/corrupt data files, duplicate records where uniqueness matters, invalid dates/amounts, and other anticipated user errors with clear messages.
- Do not copy code or designs from other projects without recording the source and acknowledging it in the Developer Guide.

## Project AI Workflows

- Before committing a non-trivial implementation change, use the project-local
  `present-changes-visually` skill to inspect the proposed diff.
- After a change to a JavaFX screen, dialog, navigation flow, visible validation, or UI-visible
  persistence behavior, update `test/ui-test-plan.md` if needed and use the project-local `test-ui`
  skill. Record manual results; do not claim visual verification without a human check.
- Before closing a phase, opening a pull request, merging, or claiming a milestone is complete, use
  the project-local `phase-verification` skill.

## Code Quality and Java Style

- Maximize readability. Keep methods short; review and split a method that grows beyond roughly 30 lines when doing so improves clarity.
- Avoid nesting beyond three levels. Prefer guard clauses and early returns so the happy path remains prominent.
- Avoid magic literals, complicated boolean expressions, clever code, premature optimization, dead code, empty `catch` blocks, duplicated logic, and misleading names.
- Keep each method at one clear level of abstraction. Group related statements and arrange them to read as a logical story.
- Use meaningful English names: classes and enums are PascalCase nouns; methods are camelCase verbs; variables are camelCase; booleans use forms such as `isValid` or `hasData`; collections use plural names; constants use `UPPER_SNAKE_CASE`.
- Put every class in a logical Java package. Use explicit imports rather than wildcard imports.
- Use four spaces for indentation, K&R braces, braces around every conditional/loop body, and a maximum 120-character line length (prefer 110 or fewer). Wrap lines for readability.
- Use one blank line between logical units. Keep variables initialized and scoped as locally as possible.

## Comments and Javadocs

- Write all comments and Javadocs in English using American spelling. Avoid local slang.
- Write descriptive Javadocs for every class and every public method, except simple getters/setters, test-only code, and overrides whose inherited documentation applies unchanged.
- Javadocs describe the contract: what a class or method does, its observable result, meaningful parameters, return value, and exceptions. Do not narrate implementation steps.
- Start a method Javadoc with a concise summary such as `Adds ...`, `Returns ...`, or `Calculates ...`. Use standard `/** ... */` formatting, aligned `*` prefixes, punctuation, and no blank line between the Javadoc and its declaration.
- Use `//` comments sparingly for non-obvious intent, rationale, or logical sections. Explain WHAT or WHY, not HOW; do not restate code that is already self-explanatory.
- Include an explicit `// Fallthrough` comment whenever a traditional `switch` case deliberately falls through.

## Required Submission Contents

Maintain these artifacts so that they accurately match the latest application state:

- `src/`: all source code used in the project.
- `release/`: the latest generated executable JAR, including required libraries such as JavaFX so that peer testers can run it without manually installing project dependencies.
- `docs/UserGuide.md`: setup, all current user-facing features, example usage, and testing instructions.
- `docs/DeveloperGuide.md`: architecture/design, development and testing process, and acknowledgements for reused ideas, code, and documentation.
- `docs/Reflections.md`: reflections on AI-assisted software engineering, including at least three interesting prompts; explain the prompt formulation, assumptions/errors, verification, evolution, limits, and engineering judgement.
- `logs/`: concise, verified summaries of substantive prompts and AI interactions during development. Never include secrets, access tokens, personal data, or unreviewed claims.
- A public GitHub repository named `CS3227-2610-MP1`, with the latest intended submission on `master` before the deadline.

## Responsible AI Use

- AI assistance is permitted only when transparently declared in the submission. The student remains responsible for all submitted content and quality.
- Treat AI output as an untrusted draft: inspect it, understand it, test it, and correct it before use.
- Log substantive AI interactions as work proceeds; do not fabricate logs or reflections after the fact.
- Record AI usage clearly in the final submission using a declaration such as: “I used [tool] to [specific uses]. I am responsible for the content and quality of the submitted work.”
- Comply with the NUS Plagiarism Policy, NUS Code of Student Conduct (Academic Integrity), and the course instructions. Do not present unverified or copied material as original work.

## Documentation Quality

- Keep documentation concise, accurate, and synchronized with the application.
- Include exact commands, examples, file paths, and expected behaviour only after verifying them.
- Update the User Guide, Developer Guide, reflection, and relevant prompt-log summary in the same change set as a feature when practical.
- Make the User Guide fit for purpose: give a new user verified prerequisites, exact setup and launch steps, every important feature with realistic examples, persistence/data-file behaviour, known limitations, and screenshots when they materially clarify a GUI action.
- Validate the rendered documentation, not only the repository Markdown preview. If GitHub Pages is used, inspect the deployed page for correctness.
- Keep the Developer Guide aligned with the released app. Explain the actual architecture, key design decisions, testing and relevant engineering process, and acknowledge all reused ideas, code, documentation, and libraries.

## Delivery Checks

Before calling a milestone complete, confirm:

1. The app builds and automated tests pass.
2. Relevant GUI and error paths were manually checked.
3. User and developer documentation accurately describe the current app.
4. AI prompt logs and reflections have been updated and verified.
5. Reused material and AI assistance are acknowledged.
