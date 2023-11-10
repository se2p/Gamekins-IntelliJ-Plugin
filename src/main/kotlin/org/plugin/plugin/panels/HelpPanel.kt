package org.plugin.plugin.panels

import com.intellij.ui.JBColor
import org.plugin.plugin.Constants
import java.awt.Font
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.*

val lHelpTextArea = JTextArea(Constants.HELP)

class HelpPanel : JPanel() {

    init {

        this.layout = GridBagLayout()

        val fontSize = 10
        val font = Font("SansSerif", Font.PLAIN, fontSize)

        lHelpTextArea.font = font;
        lHelpTextArea.isEditable = false

        val gbc = GridBagConstraints()
        gbc.gridx = 0
        gbc.gridy = 0
        gbc.gridwidth = 0;
        gbc.gridwidth = 0;

        this.add(JLabel("Help"), gbc)

        gbc.gridy = 1
        gbc.weightx = 0.0
        gbc.weighty = 0.0

        lHelpTextArea.setBackground(JBColor.GRAY)
        
        this.add(lHelpTextArea, gbc)
    }
}

