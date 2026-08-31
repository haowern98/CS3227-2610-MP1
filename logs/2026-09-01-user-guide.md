# User Guide finalization

## Prompt summary

The student requested a complete User Guide based on the CS2103 documentation guidance. The guide
needed to cover every current feature precisely, use the supplied screenshots selectively, and
state current persistence, launch, and platform limitations without copying another project.

## AI assistance

Codex audited the JavaFX views, validation services, storage implementation, UI test plan, build
configuration, release directory, and supplied screenshots before drafting the guide.

## Verification

- The documented fields, defaults, enum values, search scope, event ordering, deletion behavior,
  save rollback, and corrupt-file handling were checked against the current source.
- The three PNG screenshots were inspected and matched the current dashboard, detail screen, and
  deletion confirmation.
- `./gradlew.bat clean test` passed with Temurin 25.0.4.1 on Windows 11.
- `markdownlint-cli2` 0.23.2 reported no issues across the configured project documentation.
- `markdown-link-check` verified all three local image references in `docs/UserGuide.md`.
