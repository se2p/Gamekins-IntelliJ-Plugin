package org.plugin.plugin.panels

import com.intellij.ui.JBColor
import com.intellij.ui.util.maximumHeight
import java.awt.*
import javax.swing.*
import javax.swing.border.LineBorder


class MainPanel: JPanel() {

    private val lLeaderboardButton = JButton("Leaderboard")
    private val lCurrentChallengesButton = JButton("Current Quests & Challenges")
    private val lCompletedChallengesButton = JButton("Completed Quests & Challenges")
    private val lHelpButton = JButton("Help")

    private val lLeaderboardPanel = LeaderboardPanel()
    private val lCurrentChallengesPanel = CurrentQuestsChallengesPanel()
    private val lCompletedChallengesPanel = CompletedQuestsChallengesPanel()
    private val lHelpPanel = HelpPanel()

    private val contentPanel = JPanel()

    init {

        this.layout = GridBagLayout()
        contentPanel.layout = GridBagLayout()

        val buttonsPanel = JPanel()
        buttonsPanel.layout = BoxLayout(buttonsPanel, BoxLayout.Y_AXIS)
        buttonsPanel.alignmentX = Component.CENTER_ALIGNMENT
        buttonsPanel.alignmentY = Component.CENTER_ALIGNMENT
        buttonsPanel.add(Box.createRigidArea(Dimension(0, 20)))

        buttonsPanel.add(lLeaderboardButton)
        buttonsPanel.add(lCurrentChallengesButton)
        buttonsPanel.add(lCompletedChallengesButton)
        buttonsPanel.add(lHelpButton)



        val buttonWidth = 220
        val buttonHeight = 80

        for (button in listOf(lLeaderboardButton, lCurrentChallengesButton, lCompletedChallengesButton, lHelpButton)) {
            button.alignmentX = Component.CENTER_ALIGNMENT
            button.maximumSize = Dimension(buttonWidth, Int.MAX_VALUE);
            button.maximumHeight = buttonHeight
            buttonsPanel.add(button)
            buttonsPanel.add(Box.createVerticalStrut(10))
            button.setFont(Font("SansSerif", Font.BOLD, 12))
        }

        lLeaderboardButton.addActionListener { switchToPanel(lLeaderboardPanel) }
        lCurrentChallengesButton.addActionListener { switchToPanel(lCurrentChallengesPanel) }
        lCompletedChallengesButton.addActionListener { switchToPanel(lCompletedChallengesPanel) }
        lHelpButton.addActionListener { switchToPanel(lHelpPanel) }

        switchToPanel(lCurrentChallengesPanel)

        val gbc = GridBagConstraints()
        gbc.gridx = 0
        gbc.gridy = 0
        gbc.weightx = 0.1
        gbc.weighty = 1.0
        gbc.fill = GridBagConstraints.BOTH
        this.add(buttonsPanel, gbc)
        gbc.gridx = 1
        gbc.weightx = 0.9

        this.add(contentPanel, gbc)
    }

    private fun switchToPanel(panel: JComponent) {
        val gbc = GridBagConstraints()
        gbc.gridx = 0
        gbc.gridy = 0
        gbc.weightx = 1.0
        gbc.weighty = 1.0
        gbc.fill = GridBagConstraints.BOTH

        contentPanel.removeAll()
        contentPanel.add(panel, gbc, )
        contentPanel.revalidate()
        contentPanel.repaint()
    }
}
