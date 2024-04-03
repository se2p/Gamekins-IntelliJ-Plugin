package org.gamekins.ide.panels

import com.intellij.openapi.wm.impl.welcomeScreen.learnIde.coursesInProgress.mainBackgroundColor
import com.intellij.util.ui.JBUI
import org.gamekins.ide.Constants
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.JPanel
import javax.swing.JTextPane
import javax.swing.text.html.HTMLEditorKit

class HelpPanel : JPanel() {

    init {
        val textPane = JTextPane().apply {
            isEditable = false
            contentType = "text/html"
            editorKit = HTMLEditorKit()
            text = "<html><body>${Constants.HELP}</body></html>"
            background = mainBackgroundColor
        }
        background = mainBackgroundColor
        layout = GridBagLayout()

        val gbc = GridBagConstraints().apply {
            gridx = 0
            gridy = 0
            gridwidth = 1
        }

        gbc.insets = JBUI.insets(10)

        add(textPane, gbc)
    }
}
