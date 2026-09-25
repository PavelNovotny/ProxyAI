# Requirements

<!-- What and why. Intent, goals, acceptance criteria. -->
<!-- Edit only when the *intended* behavior changes, never when implementation status changes. -->

## R1: Per-tool configuration

The plugin provides a settings page where users can configure individual agent tools.

### Acceptance criteria

- **AC1**: A "Tools" settings page exists under the ProxyAI settings tree, registered as an application-level configurable.
- **AC2**: The Tools page allows selecting a tool (initially "Bash" only).
- **AC3**: For the Bash tool, the user can enter an additional PATH string.
- **AC4**: When the Bash tool starts a process (foreground or background) and the additional PATH is non-empty, it is prepended to the inherited PATH environment variable.
- **AC5**: When the additional PATH is empty (the default), the process inherits the JVM environment unchanged.
