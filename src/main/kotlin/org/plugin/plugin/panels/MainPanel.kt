package org.plugin.plugin.panels

import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.util.maximumHeight
import com.intellij.util.ui.JBUI
import org.plugin.plugin.Utility
import java.awt.*
import javax.swing.*


class MainPanel: JPanel() {

    private val lLeaderboardButton = JButton("Leaderboard")
    private val lCurrentChallengesButton = JButton("Current Quests & Challenges")
    private val lCompletedChallengesButton = JButton("Completed Quests & Challenges")
    private val lHelpButton = JButton("Help")
    private val lLogoutButton = JButton("Log out")

    private val lLeaderboardPanel = LeaderboardPanel()
    private val lCurrentChallengesPanel = CurrentQuestsChallengesPanel()
    private val lCompletedChallengesPanel = CompletedQuestsChallengesPanel()
    private val lHelpPanel = HelpPanel()

    private val contentPanel = JPanel()

    init {

        this.layout = GridBagLayout()
        contentPanel.layout = GridBagLayout()
        this.maximumSize = Dimension(500, 700)

        val buttonsPanel = JPanel()
        buttonsPanel.layout = BoxLayout(buttonsPanel, BoxLayout.X_AXIS)
        buttonsPanel.alignmentX = Component.CENTER_ALIGNMENT
        buttonsPanel.alignmentY = Component.CENTER_ALIGNMENT
        buttonsPanel.add(Box.createRigidArea(Dimension(0, 20)))

        lLogoutButton.addActionListener {
            Utility.logout()
        }

        val buttonWidth = 220
        val buttonHeight = 80

        for (button in listOf(lLeaderboardButton, lCurrentChallengesButton, lCompletedChallengesButton, lHelpButton, lLogoutButton)) {
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

        val contentPanelConstraints = GridBagConstraints()
        contentPanelConstraints.gridx = 0
        contentPanelConstraints.weightx = 1.0
        contentPanelConstraints.weighty = 1.0
        contentPanelConstraints.fill = GridBagConstraints.BOTH

        val lScrollPane = JBScrollPane(contentPanel)
        this.add(lScrollPane, contentPanelConstraints)

        val bottomPanel = JPanel()
        bottomPanel.layout = BorderLayout()

        bottomPanel.add(buttonsPanel, BorderLayout.CENTER)

        val bottomPanelConstraints = GridBagConstraints()
        bottomPanelConstraints.insets = JBUI.insets(5)
        bottomPanelConstraints.gridx = 0
        bottomPanelConstraints.weightx = 1.0
        bottomPanelConstraints.anchor = GridBagConstraints.PAGE_END
        bottomPanelConstraints.fill = GridBagConstraints.HORIZONTAL

        this.add(bottomPanel, bottomPanelConstraints)

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
