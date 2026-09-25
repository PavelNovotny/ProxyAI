package ee.carlrobert.codegpt.settings.tools

import com.intellij.openapi.components.BaseState
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.SimplePersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service

@Service
@State(
    name = "CodeGPT_ToolConfigurationSettings_210",
    storages = [Storage("CodeGPT_ToolConfigurationSettings_210.xml")]
)
class ToolConfigurationSettings :
    SimplePersistentStateComponent<ToolConfigurationSettingsState>(ToolConfigurationSettingsState()) {
    companion object {
        @JvmStatic
        fun getState(): ToolConfigurationSettingsState {
            return service<ToolConfigurationSettings>().state
        }
    }
}

class ToolConfigurationSettingsState : BaseState() {
    var bashPath by string("")
}
