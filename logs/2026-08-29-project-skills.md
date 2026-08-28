# AI Interaction Log: Project Skills

## Purpose

Add reusable, project-local AI workflows recommended by the CS2103/T Duke-trimmed-for-CS3227
guidance, adapted for this JavaFX MP1.

## Installed workflows

- `present-changes-visually` generates a reviewable HTML diff before non-trivial commits.
- `test-ui` maintains and executes the JavaFX manual UI-test workflow.
- `phase-verification` combines tests, UI checks, diff inspection, documentation checks, and the
  submission structure check before phase completion.

## Adaptation and review

The upstream visual-diff skill referenced an unavailable commit-message skill. That reference was
replaced with this repository's `AGENTS.md` commit conventions. The console-oriented course example
for `test-ui` was adapted to record manual JavaFX visual verification instead of falsely automating
GUI inspection.
