# Design

<!-- How it works. Technical architecture — components, contracts, data flow. -->
<!-- Write in present tense for what's built today. No status tags here. -->

## Tool configuration

Per-tool configuration is stored at the application level via `ToolConfigurationSettings`, a `SimplePersistentStateComponent<ToolConfigurationSettingsState>` persisted to `CodeGPT_ToolConfigurationSettings_210.xml`. The state holds a single field, `bashPath`, representing additional PATH entries for the Bash tool (default empty string).

A settings UI page (`ToolConfigurationConfigurable`, displayName "Tools") is registered as an `<applicationConfigurable>` under `settings.codegpt` in `plugin.xml`. The form presents a tool selector (currently a combo box with one entry, "Bash") and, for the Bash tool, a text field for the additional PATH value. The configurable reads and writes `ToolConfigurationSettings.getState().bashPath` via the standard `reset`/`apply`/`isModified` lifecycle.

### Bash tool PATH override

`BashTool` starts child processes via `ProcessBuilder` in two locations: foreground streaming execution and background execution. Both call the `ProcessBuilder.applyCustomPath()` extension function before `.start()`. `applyCustomPath()` reads `ToolConfigurationSettings.getState().bashPath`; when the value is non-empty, it prepends it to the `PATH` entry in `ProcessBuilder.environment()` using the platform path separator. When the value is empty, the environment is left unchanged and the process inherits the JVM environment as before.
