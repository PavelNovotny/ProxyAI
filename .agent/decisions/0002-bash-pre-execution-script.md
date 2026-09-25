# 0002 — Tool Configuration: Bash Pre-Execution Script

Status: Done
Date: 2026-09-25

## Context

Slug 0001 (Done) added a `bashPath` setting — a single PATH string prepended to
`ProcessBuilder.environment()` before the Bash tool starts a process. The mechanism
is limited to one environment variable (`PATH`) and operates purely through
`ProcessBuilder.environment()` mutation.

`ToolConfigurationSettingsState` (`ToolConfigurationSettings.kt:25`) holds a single
field:

```kotlin
var bashPath by string("")
```

`BashTool.applyCustomPath()` (`BashTool.kt:583–589`) reads that field and, when
non-empty, sets the `PATH` key in `ProcessBuilder.environment()`:

```kotlin
private fun ProcessBuilder.applyCustomPath() {
    val bashPath = ToolConfigurationSettings.getState().bashPath
    if (bashPath.isNullOrEmpty()) return
    val environment = environment()
    val currentPath = environment["PATH"] ?: ""
    environment["PATH"] = if (currentPath.isEmpty()) bashPath else "$bashPath${File.pathSeparator}$currentPath"
}
```

`buildShellCommand()` (`BashTool.kt:568–579`) constructs the process invocation as
`bash -c "<command>"` (or `sh -c` / `cmd.exe /c` on other platforms). The `<command>`
string is the raw command the agent passes. There is no way to inject setup code that
runs *before* the command — setting env vars, sourcing a file, changing `cd`,
`export`, `umask`, etc.

The settings UI (`ToolConfigurationConfigurable.kt:43–61`) shows a tool selector combo
box ("Bash") and a single `bashPathField` text field.

## Decision

Replace the `bashPath` PATH-only override with a **pre-execution script** — a
multi-line text field whose contents are executed as a setup preamble before every
Bash tool command. On Unix, the pre-execution script is concatenated ahead of the
agent's command inside the same `bash -c` / `sh -c` invocation; on Windows, inside
`cmd.exe /c`.

Concretely:

1. **New settings field** — Replace `bashPath` with `bashPreExecScript` in
   `ToolConfigurationSettingsState` (a `String`, default empty). The old `bashPath`
   field is removed — this slug replaces slug 0001's mechanism, it does not extend it.

2. **`buildShellCommand` becomes the injection point** —
   `BashTool.buildShellCommand()` is modified to read
   `ToolConfigurationSettings.getState().bashPreExecScript`. When non-empty, it is
   prepended to the agent's command, separated by a newline, inside the existing
   `bash -c` / `sh -c` / `cmd.exe /c` wrapper. Example on Unix:

   ```
   bash -c "<preExecScript>\n<agentCommand>"
   ```

   When empty (the default), behavior is unchanged — the command runs as before.

3. **`applyCustomPath()` removed** — The `ProcessBuilder.applyCustomPath()` extension
   function and both call sites (foreground `BashTool.kt:374`, background
   `BashTool.kt:560`) are deleted. The pre-execution script subsumes the PATH case: a
   user who previously set `bashPath` to `/opt/homebrew/bin` now sets the script to
   `export PATH=/opt/homebrew/bin:$PATH`.

4. **Settings UI updated** — `ToolConfigurationConfigurable` and
   `ToolConfigurationComponent` replace the `bashPathField` (`JBTextField`, single
   line) with a multi-line text area for the pre-execution script. The combo box
   stays. The `isModified`/`apply`/`reset` lifecycle reads and writes
   `bashPreExecScript` instead of `bashPath`.

5. **Scope is limited** — No other tools, no per-project overrides, no conditional
   execution logic. The script is always executed (when non-empty) before every
   command, foreground and background.

## Consequences

### Code changes

- Modified: `ToolConfigurationSettings.kt` — `ToolConfigurationSettingsState.bashPath`
  replaced by `bashPreExecScript`.
- Modified: `BashTool.kt` — `buildShellCommand()` injects the pre-execution script;
  `applyCustomPath()` deleted; both call sites removed.
- Modified: `ToolConfigurationConfigurable.kt` — single-line `bashPathField` replaced
  by a multi-line text area bound to `bashPreExecScript`.

### `.agent/` tracked docs

- `.agent/requirements.md` — AC3, AC4, AC5 under R1 need rewriting: the acceptance
  criteria change from "additional PATH string" to "multi-line pre-execution script".
- `.agent/design.md` — the "Bash tool PATH override" section and the
  `ToolConfigurationSettings` description need rewriting to describe the
  pre-execution script mechanism and the removal of `applyCustomPath()`.
- `.agent/tasks.md` — existing "Implemented" entries for `bashPath` / `applyCustomPath`
  need updating to reflect the replacement; new tasks added for `bashPreExecScript` /
  `buildShellCommand` injection / UI text area.

### Migration

`bashPath` values stored in `CodeGPT_ToolConfigurationSettings_210.xml` from slug 0001
will not be read after the field rename. Users who set a `bashPath` value must
manually translate it to a `bashPreExecScript` entry (e.g. `export PATH=...`). No
automatic migration is provided — slug 0001 shipped and was Done, but its scope was
narrow and the feature is being replaced wholesale.
