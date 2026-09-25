package ee.carlrobert.codegpt.settings.tools

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.components.JBTextArea
import com.intellij.util.ui.FormBuilder
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JScrollPane

class ToolConfigurationConfigurable : Configurable {

    private var component: ToolConfigurationComponent? = null

    override fun getDisplayName(): String = "Tools"

    override fun getPreferredFocusedComponent(): JComponent? =
        component?.preferredFocusedComponent

    override fun createComponent(): JComponent {
        val comp = ToolConfigurationComponent()
        component = comp
        return comp.panel
    }

    override fun isModified(): Boolean {
        val state = ToolConfigurationSettings.getState()
        return component?.bashPreExecScript != state.bashPreExecScript
    }

    override fun apply() {
        ToolConfigurationSettings.getState().bashPreExecScript = component?.bashPreExecScript ?: ""
    }

    override fun reset() {
        component?.bashPreExecScript = ToolConfigurationSettings.getState().bashPreExecScript ?: ""
    }

    override fun disposeUIResources() {
        component = null
    }
}

private class ToolConfigurationComponent {

    private val toolSelector = ComboBox(arrayOf("Bash"))
    private val bashPreExecScriptArea = JBTextArea(8, 40)

    val panel: JPanel = FormBuilder.createFormBuilder()
        .addLabeledComponent("Tool:", toolSelector)
        .addLabeledComponent("Bash pre-execution script:", JScrollPane(bashPreExecScriptArea))
        .addComponentFillVertically(JPanel(), 0)
        .panel

    val preferredFocusedComponent: JComponent get() = bashPreExecScriptArea

    var bashPreExecScript: String
        get() = bashPreExecScriptArea.text.trim()
        set(value) {
            bashPreExecScriptArea.text = value
        }
}
