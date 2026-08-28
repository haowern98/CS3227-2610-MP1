---
name: phase-verification
description: Verify a CS3227 MP1 implementation phase before committing, opening a pull request, merging, or calling it complete. Use whenever a phase, feature increment, or release candidate is ready for review.
---

# Verify an MP1 Phase

Use evidence, not assumptions. Read `AGENTS.md`, the relevant plan, changed source, tests, and
documentation before starting.

## Required checks

1. Run the full Gradle test suite using Java 25.
2. Invoke `test-ui` when the phase changes user-facing behavior, then record the manual results.
3. Invoke `present-changes-visually` to inspect the proposed change against its base revision.
4. Run `git diff --check` and confirm unrelated files are not staged.
5. Confirm the User Guide, Developer Guide, reflections, and AI interaction log accurately match the
   verified state.
6. Run `check_mp1_structure.sh` before a release or submission. A failed check is a failed delivery
   check; report it rather than treating the phase as complete.
7. Record the commands, platform, and results in the relevant documentation or log before committing.

## Report format

Report each check as passed, failed, or not applicable, with its command or evidence. State remaining
gaps plainly. Never merge or publish a release without explicit user approval.
