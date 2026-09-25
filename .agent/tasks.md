# Tasks

<!-- The single, sole place status lives: done / partial / planned. -->
<!-- Update whenever a task's true status changes. -->

## Implemented

- `ToolConfigurationSettings` — app-level `SimplePersistentStateComponent` with `bashPreExecScript` field.
- `ToolConfigurationConfigurable` + form — settings UI with tool selector and Bash pre-execution script text area.
- `BashTool` — `buildShellCommand()` injects the pre-execution script before the agent's command.
- `plugin.xml` — `settings.codegpt.tools` `<applicationConfigurable>` registered.

## Planned / not wired yet

- Other tools' configuration (non-Bash).
- Per-project tool configuration overrides.

## Acceptance criteria status

- AC1: met — "Tools" configurable registered as `settings.codegpt.tools`.
- AC2: met — tool selector combo box present (Bash only).
- AC3: met — Bash pre-execution script multi-line text area in the form.
- AC4: met — `buildShellCommand()` prepends the script inside the `bash -c` / `sh -c` / `cmd.exe /c` wrapper.
- AC5: met — empty `bashPreExecScript` leaves the command unchanged.
