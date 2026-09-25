# Requirements

<!-- What and why. Intent, goals, acceptance criteria. -->
<!-- Edit only when the *intended* behavior changes, never when implementation status changes. -->

## R1: Per-tool configuration

The plugin provides a settings page where users can configure individual agent tools.

### Acceptance criteria

- **AC1**: A "Tools" settings page exists under the ProxyAI settings tree, registered as an application-level configurable.
- **AC2**: The Tools page allows selecting a tool (initially "Bash" only).
- **AC3**: For the Bash tool, the user can enter a multi-line pre-execution script.
- **AC4**: When the Bash tool starts a process (foreground or background) and the pre-execution script is non-empty, it is executed as a setup preamble before the agent's command inside the same `bash -c` / `sh -c` / `cmd.exe /c` invocation.
- **AC5**: When the pre-execution script is empty (the default), the command runs unchanged — the process inherits the JVM environment as before.
