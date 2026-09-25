# Tasks

<!-- The single, sole place status lives: done / partial / planned. -->
<!-- Update whenever a task's true status changes. -->

## Implemented

- `ToolConfigurationSettings` — app-level `SimplePersistentStateComponent` with `bashPath` field.
- `ToolConfigurationConfigurable` + form — settings UI with tool selector and Bash PATH text field.
- `BashTool` — `ProcessBuilder.applyCustomPath()` applied at both foreground and background process startup.
- `plugin.xml` — `settings.codegpt.tools` `<applicationConfigurable>` registered.

## Planned / not wired yet

- Other tools' configuration (non-Bash).
- Other environment variables beyond PATH.
- Shell binary selection configuration.
- Per-project tool configuration overrides.

## Acceptance criteria status

- AC1: met — "Tools" configurable registered as `settings.codegpt.tools`.
- AC2: met — tool selector combo box present (Bash only).
- AC3: met — Bash PATH text field in the form.
- AC4: met — `applyCustomPath()` prepends to `ProcessBuilder.environment()` PATH.
- AC5: met — empty `bashPath` leaves environment unchanged.
