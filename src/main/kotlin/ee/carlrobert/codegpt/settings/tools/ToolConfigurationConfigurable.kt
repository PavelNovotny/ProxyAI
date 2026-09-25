package ee.carlrobert.codegpt.settings.tools

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import javax.swing.JComponent
import javax.swing.JPanel

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
        return component?.bashPath != state.bashPath
    }

    override fun apply() {
        ToolConfigurationSettings.getState().bashPath = component?.bashPath ?: ""
    }

    override fun reset() {
        component?.bashPath = ToolConfigurationSettings.getState().bashPath ?: ""
    }

    override fun disposeUIResources() {
        component = null
    }
}

private class ToolConfigurationComponent {

    private val toolSelector = ComboBox(arrayOf("Bash"))
    private val bashPathField = JBTextField(40)

    val panel: JPanel = FormBuilder.createFormBuilder()
        .addLabeledComponent("Tool:", toolSelector)
        .addLabeledComponent("Bash additional PATH:", bashPathField)
        .addComponentFillVertically(JPanel(), 0)
        .panel

    val preferredFocusedComponent: JComponent get() = bashPathField

    var bashPath: String
        get() = bashPathField.text.trim()
        set(value) {
            bashPathField.text = value
        }
}
