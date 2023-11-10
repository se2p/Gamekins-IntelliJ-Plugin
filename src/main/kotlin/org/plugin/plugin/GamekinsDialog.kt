package org.plugin.plugin


import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.util.maximumHeight
import org.plugin.plugin.panels.*

import java.awt.*
import javax.swing.*

class GamekinsDialog(project: Project?) : DialogWrapper(project) {

    private val lLeaderboardButton = JButton("Leaderboard")
    private val lCurrentChallengesButton = JButton("Current Quests & Challenges")
    private val lCompletedChallengesButton = JButton("Completed Quests & Challenges")
    private val lStatisticsButton = JButton("Statistics")
    private val lHelpButton = JButton("Help")

    private val lLeaderboardPanel = LeaderboardPanel()
    private val lCurrentChallengesPanel = CurrentQuestsChallengesPanel()
    private val lCompletedChallengesPanel = CompletedQuestsChallengesPanel()
    private val lStatisticsPanel = StatisticsPanel()
    private val lHelpPanel = HelpPanel()

    private val mainPanel = JPanel()
    private val contentPanel = JPanel()

    init {
        init()
        title = "Gamekins"
    }

    override fun createCenterPanel(): JComponent? {

        mainPanel.layout = GridBagLayout()
        contentPanel.layout = GridBagLayout()
        mainPanel.size = Dimension(700, 700)
        mainPanel.maximumSize = Dimension(800, 700)

        val buttonsPanel = JPanel()
        buttonsPanel.layout = BoxLayout(buttonsPanel, BoxLayout.Y_AXIS)
        buttonsPanel.add(lLeaderboardButton)
        buttonsPanel.add(lCurrentChallengesButton)
        buttonsPanel.add(lCompletedChallengesButton)
        buttonsPanel.add(lStatisticsButton)
        buttonsPanel.add(lHelpButton)


        val buttonWidth = 220
        val buttonHeight = 80

        for (button in listOf(lLeaderboardButton, lCurrentChallengesButton, lCompletedChallengesButton, lStatisticsButton, lHelpButton)) {
            button.alignmentX = Component.CENTER_ALIGNMENT
            button.maximumSize = Dimension(buttonWidth, Int.MAX_VALUE);
            button.maximumHeight = buttonHeight
            buttonsPanel.add(button)
            buttonsPanel.add(Box.createVerticalStrut(10)) // Add spacing between buttons
        }

        lLeaderboardButton.addActionListener { switchToPanel(lLeaderboardPanel) }
        lCurrentChallengesButton.addActionListener { switchToPanel(lCurrentChallengesPanel) }
        lCompletedChallengesButton.addActionListener { switchToPanel(lCompletedChallengesPanel) }
        lStatisticsButton.addActionListener { switchToPanel(lStatisticsPanel) }
        lHelpButton.addActionListener { switchToPanel(lHelpPanel) }

        switchToPanel(lCurrentChallengesPanel)

        val gbc = GridBagConstraints()
        gbc.gridx = 0
        gbc.gridy = 0
        gbc.weightx = 0.1
        gbc.weighty = 1.0
        gbc.fill = GridBagConstraints.BOTH
        mainPanel.add(buttonsPanel, gbc)
        gbc.gridx = 1
        gbc.weightx = 0.9

        mainPanel.add(contentPanel, gbc)

        return mainPanel
    }

    private fun switchToPanel(panel: JComponent) {
        val gbc = GridBagConstraints()
        gbc.gridx = 0
        gbc.gridy = 0
        gbc.weightx = 1.0
        gbc.weighty = 1.0
        gbc.fill = GridBagConstraints.BOTH

        contentPanel.removeAll()
        contentPanel.add(panel, gbc)
        contentPanel.revalidate()
        contentPanel.repaint()
    }
}
