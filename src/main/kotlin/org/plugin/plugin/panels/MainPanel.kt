package org.plugin.plugin.panels

import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.wm.impl.welcomeScreen.learnIde.coursesInProgress.mainBackgroundColor
import com.intellij.ui.components.JBScrollPane
import com.intellij.util.ui.JBUI
import org.plugin.plugin.Utility
import java.awt.Font
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.GridLayout
import javax.swing.*


class MainPanel: JPanel() {

    private val lLeaderboardButton = JButton("Leaderboard")
    private val lCurrentChallengesButton = JButton("Current")
    private val lCompletedChallengesButton = JButton("Completed")
    private val lAchievementsButton = JButton("Achievements")
    private val lHelpButton = JButton("Help")
    private val lLogoutButton = JButton("Logout")

    private val lLeaderboardPanel = LeaderboardPanel()
    private val lCurrentChallengesPanel = CurrentQuestsChallengesPanel()
    private val lCompletedChallengesPanel = CompletedQuestsChallengesPanel()
    private val lAchievementsPanel = AchievementsPanel()
    private val lHelpPanel = HelpPanel()

    private val contentPanel = JPanel()

    init {

        this.layout = GridBagLayout()
        this.setBackground(mainBackgroundColor)
        contentPanel.layout = GridBagLayout()
        contentPanel.setBackground(mainBackgroundColor)

        val buttonsPanel = JPanel()
        buttonsPanel.border = null
        buttonsPanel.layout = BoxLayout(buttonsPanel, BoxLayout.X_AXIS)


        val centerPanel = JPanel(GridLayout(1, 0, 10, 0))
        centerPanel.background = mainBackgroundColor
        centerPanel.border = null

        centerPanel.setAlignmentX(CENTER_ALIGNMENT)
        centerPanel.setAlignmentY(CENTER_ALIGNMENT)

        lLogoutButton.addActionListener {
            Utility.logout()
        }

        for (button in listOf(lLeaderboardButton, lCurrentChallengesButton, lCompletedChallengesButton, lAchievementsButton, lHelpButton, lLogoutButton)) {
            centerPanel.add(button)
            button.setFont(Font("SansSerif", Font.BOLD, 12))
            button.background = mainBackgroundColor
        }

        lLeaderboardButton.addActionListener { switchToPanel(lLeaderboardPanel) }
        lCurrentChallengesButton.addActionListener { switchToPanel(lCurrentChallengesPanel) }
        lCompletedChallengesButton.addActionListener { switchToPanel(lCompletedChallengesPanel) }
        lAchievementsButton.addActionListener { switchToPanel(lAchievementsPanel) }
        lHelpButton.addActionListener { switchToPanel(lHelpPanel) }

        switchToPanel(lCurrentChallengesPanel)

        val contentPanelConstraints = GridBagConstraints()
        contentPanelConstraints.gridx = 0
        contentPanelConstraints.weightx = 1.0
        contentPanelConstraints.weighty = 0.95
        contentPanelConstraints.fill = GridBagConstraints.BOTH

        val lScrollPane = JBScrollPane(contentPanel)
        this.add(lScrollPane, contentPanelConstraints)


        buttonsPanel.add(Box.createVerticalGlue())
        buttonsPanel.add(centerPanel)
        buttonsPanel.add(Box.createVerticalGlue())


        val lScrollPanel = JBScrollPane(buttonsPanel)
        lScrollPanel.verticalScrollBarPolicy = ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER
        lScrollPanel.horizontalScrollBarPolicy = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED

        val bottomPanelConstraints = GridBagConstraints()
        bottomPanelConstraints.gridx = 0
        bottomPanelConstraints.weightx = 1.0
        bottomPanelConstraints.weighty = 0.05
        bottomPanelConstraints.anchor = GridBagConstraints.PAGE_END
        bottomPanelConstraints.fill = GridBagConstraints.BOTH

        this.add(lScrollPanel, bottomPanelConstraints)

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
