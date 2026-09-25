# 0001 — Tool Configuration: Bash PATH

Status: Done
Date: 2026-09-25

## Context

The ProxyAI plugin has a tool system built on the Koog agents framework. Tools are
registered via `BuiltInToolRegistry` (`src/main/kotlin/ee/carlrobert/codegpt/agent/BuiltInToolRegistry.kt`),
instantiated per agent session from a `RegistrationContext`, and each tool extends
`BaseTool<Args, Result>` (`src/main/kotlin/ee/carlrobert/codegpt/agent/tools/BaseTool.kt`).

The `BashTool` (`src/main/kotlin/ee/carlrobert/codegpt/agent/tools/BashTool.kt`) executes
shell commands using `ProcessBuilder` (lines 368–374 for foreground, 551–563 for
background). `ProcessBuilder.environment()` is never called — the child process
inherits the JVM's environment (i.e., the IntelliJ IDE process environment). The shell
binary is selected in `buildShellCommand()` (lines 565–577): `bash -c` on macOS/Linux,
`sh -c` on BSD, `cmd.exe /c` on Windows.

There is no settings class for Bash tool environment or PATH. A grep for
`BashSettings`, `ShellSettings`, `customEnv`, `customPath` returns zero results. The
`CommandRuntimeHelper` (`src/main/kotlin/ee/carlrobert/codegpt/util/CommandRuntimeHelper.kt`)
provides environment/PATH merging utilities, but it is currently used only by MCP server
startup — not by the Bash tool.

The plugin's settings system has two persistence mechanisms:

1. **App-level** (`@Service` + `@State` + `SimplePersistentStateComponent<State>` with
   `BaseState`), e.g. `ConfigurationSettings`
   (`src/main/kotlin/ee/carlrobert/codegpt/settings/configuration/ConfigurationSettings.kt`).
   Stored in XML. UI registered as `<applicationConfigurable>` in `plugin.xml`.

2. **Project-level** (`.proxyai/settings.json` via `ProxyAISettingsService`,
   `src/main/kotlin/ee/carlrobert/codegpt/settings/ProxyAISettingsService.kt`). UI
   registered as `<projectConfigurable>`.

Settings UI pages implement `com.intellij.openapi.options.Configurable` and are
registered in `plugin.xml` (lines 30–106) under the `settings.codegpt` parent. There is
no existing "Tools" configuration page.

## Decision

Add a per-tool configuration system, starting with the Bash tool. A new settings page
under the ProxyAI settings tree lets the user choose a tool and configure it. For the
Bash tool, the configurable parameter is a custom `PATH` string that is prepended to
the inherited environment's `PATH` when the Bash tool starts a process.

Concretely:

1. **New settings class** — `ToolConfigurationSettings`, an app-level
   `SimplePersistentStateComponent` (following the `ConfigurationSettings` pattern).
   State holds a per-tool configuration map. For the initial implementation, the state
   contains a single field: `bashPath` (a `String`, default empty), representing
   additional PATH entries for the Bash tool.

2. **Bash tool uses the custom PATH** — `BashTool.buildShellCommand()` / process
   startup modified: when `bashPath` is non-empty, it is prepended to the inherited
   `PATH` environment variable via `ProcessBuilder.environment()`. When empty (the
   default), behavior is unchanged — the process inherits the JVM environment as it
   does today.

3. **Settings UI** — A new `ToolConfigurationConfigurable` registered in `plugin.xml`
   as an `<applicationConfigurable>` under `settings.codegpt` (parentId
   `settings.codegpt`, id `settings.codegpt.tools`, displayName "Tools"). The form
   shows a tool selector (initially only "Bash") and, for the selected tool, the
   relevant fields. For Bash: a text field for the additional PATH value.

4. **Scope is limited to Bash PATH** — No other tools, no other environment variables,
   no shell binary selection, and no per-project overrides in this slug. Those can be
   addressed in follow-up slugs.

## Consequences

### Code changes

- New: `ToolConfigurationSettings.kt` (settings state class, app-level `BaseState`).
- New: `ToolConfigurationConfigurable` + form class (settings UI).
- Modified: `BashTool.kt` — read `ToolConfigurationSettings` and apply the custom PATH
  to `ProcessBuilder.environment()` before `.start()` (both foreground and background
  paths).
- Modified: `plugin.xml` — register the new `<applicationConfigurable>`.

### `.agent/` tracked docs

- `.agent/requirements.md` — add a requirement for per-tool configuration with Bash PATH
  as the first acceptance criterion.
- `.agent/design.md` — add a section describing the tool configuration settings system
  and how the Bash tool consumes the PATH override.
- `.agent/tasks.md` — add tasks for the settings class, UI, BashTool modification, and
  plugin.xml registration; set status as planned until implemented.
