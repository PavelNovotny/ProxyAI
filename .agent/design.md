# Design

<!-- How it works. Technical architecture — components, contracts, data flow. -->
<!-- Write in present tense for what's built today. No status tags here. -->

## Tool configuration

Per-tool configuration is stored at the application level via `ToolConfigurationSettings`, a `SimplePersistentStateComponent<ToolConfigurationSettingsState>` persisted to `CodeGPT_ToolConfigurationSettings_210.xml`. The state holds a single field, `bashPreExecScript`, representing a multi-line setup preamble executed before every Bash tool command (default empty string).

A settings UI page (`ToolConfigurationConfigurable`, displayName "Tools") is registered as an `<applicationConfigurable>` under `settings.codegpt` in `plugin.xml`. The form presents a tool selector (currently a combo box with one entry, "Bash") and, for the Bash tool, a multi-line text area for the pre-execution script. The configurable reads and writes `ToolConfigurationSettings.getState().bashPreExecScript` via the standard `reset`/`apply`/`isModified` lifecycle.

### Bash tool pre-execution script

`BashTool` starts child processes via `ProcessBuilder` in two locations: foreground streaming execution and background execution. Both call `buildShellCommand()` to construct the process invocation. `buildShellCommand()` reads `ToolConfigurationSettings.getState().bashPreExecScript`; when the value is non-empty, it prepends the script to the agent's command, separated by a newline, inside the existing `bash -c` / `sh -c` / `cmd.exe /c` wrapper (e.g. `bash -c "<preExecScript>\n<agentCommand>"` on Unix). When the value is empty, the command runs unchanged. The child process inherits the JVM environment via `ProcessBuilder` defaults — there is no explicit `ProcessBuilder.environment()` mutation.
