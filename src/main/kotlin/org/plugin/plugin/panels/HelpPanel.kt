package org.plugin.plugin.panels

import org.plugin.plugin.Constants
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.BorderFactory
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
        }

        layout = GridBagLayout()
        border = BorderFactory.createEmptyBorder(20, 20, 10, 10)

        val gbc = GridBagConstraints().apply {
            gridx = 0
            gridy = 0
            gridwidth = 1
        }

        add(textPane, gbc)
    }
}
