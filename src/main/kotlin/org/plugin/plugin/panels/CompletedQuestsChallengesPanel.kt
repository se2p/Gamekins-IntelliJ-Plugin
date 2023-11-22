package org.plugin.plugin.panels


import com.intellij.util.ui.JBUI
import org.plugin.plugin.Utility
import java.awt.*
import javax.swing.*


class CompletedQuestsChallengesPanel : JPanel() {
    init {

        this.layout = GridBagLayout()
        val gbc = GridBagConstraints()
        gbc.insets = JBUI.insets(5, 0)
        gbc.weightx = 1.0
        gbc.weighty = 0.1
        gbc.gridx = 0
        gbc.gridy = 0
        gbc.fill = GridBagConstraints.HORIZONTAL
        gbc.gridwidth = GridBagConstraints.REMAINDER;

        val lHeader = JLabel("<html><h1>Completed Quests & Challenges</h1></html>")
        lHeader.setHorizontalAlignment(SwingConstants.CENTER)

        this.add(lHeader, gbc)
        Utility.createAndShowCompletedQuestsTable(this)
        Utility.createChallengePanel(this)
        
    }
}

