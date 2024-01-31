package org.gamekins.intellij.panels

import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.openapi.wm.impl.welcomeScreen.learnIde.coursesInProgress.mainBackgroundColor
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.util.minimumHeight
import org.gamekins.intellij.MainToolWindow
import org.gamekins.intellij.Utility
import java.awt.Font
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.GridLayout
import javax.swing.*


class MainPanel: JPanel() {

    private val leaderboardButton = JButton("Leaderboard")
    private val challengesButton = JButton("Challenges")
    private val questsButton = JButton("Quests")
    private val achievementsButton = JButton("Achievements")
    private val helpButton = JButton("Help")
    private val logoutButton = JButton("Logout")

    private val leaderboardPanel = LeaderboardPanel()
    private val challengesPanel = ChallengesPanel()
    private val questsPanel = QuestsPanel()
    private val achievementsPanel = AchievementsPanel()
    private val helpPanel = HelpPanel()

    private val contentPanel = JPanel()

    init {

        this.layout = GridBagLayout()
        this.background = mainBackgroundColor
        contentPanel.layout = GridBagLayout()
        contentPanel.background = mainBackgroundColor

        val buttonsPanel = JPanel()
        buttonsPanel.border = null
        buttonsPanel.layout = BoxLayout(buttonsPanel, BoxLayout.X_AXIS)


        val centerPanel = JPanel(GridLayout(1, 0, 10, 0))
        centerPanel.background = mainBackgroundColor
        centerPanel.border = null

        centerPanel.alignmentX = CENTER_ALIGNMENT
        centerPanel.alignmentY = CENTER_ALIGNMENT

        logoutButton.addActionListener {
            Utility.logout()

            val toolWindowManager = Utility.project?.let {
                it1 -> ToolWindowManager.getInstance(it1)
            }

            val myToolWindow: ToolWindow? = toolWindowManager?.getToolWindow("Gamekins")

            if (myToolWindow != null) {
                MainToolWindow().rebuildPanel(myToolWindow)
            }
        }

        for (button in listOf(leaderboardButton, challengesButton, questsButton, achievementsButton, helpButton, logoutButton)) {
            centerPanel.add(button)
            button.font = Font("SansSerif", Font.BOLD, 12)
            button.background = mainBackgroundColor
            button.minimumHeight = 30
        }

        leaderboardButton.addActionListener { switchToPanel(leaderboardPanel) }
        challengesButton.addActionListener { switchToPanel(challengesPanel) }
        questsButton.addActionListener { switchToPanel(questsPanel) }
        achievementsButton.addActionListener { switchToPanel(achievementsPanel) }
        helpButton.addActionListener { switchToPanel(helpPanel) }

        switchToPanel(challengesPanel)

        val contentPanelConstraints = GridBagConstraints()
        contentPanelConstraints.gridx = 0
        contentPanelConstraints.weightx = 1.0
        contentPanelConstraints.weighty = 0.95
        contentPanelConstraints.fill = GridBagConstraints.BOTH

        val scrollPane = JBScrollPane(contentPanel)
        this.add(scrollPane, contentPanelConstraints)


        buttonsPanel.add(Box.createVerticalGlue())
        buttonsPanel.add(centerPanel)
        buttonsPanel.add(Box.createVerticalGlue())


        val scrollPanel = JBScrollPane(buttonsPanel)
        scrollPanel.verticalScrollBarPolicy = ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER
        scrollPanel.horizontalScrollBarPolicy = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED

        val bottomPanelConstraints = GridBagConstraints()
        bottomPanelConstraints.gridx = 0
        bottomPanelConstraints.weightx = 1.0
        bottomPanelConstraints.weighty = 0.05
        bottomPanelConstraints.anchor = GridBagConstraints.PAGE_END
        bottomPanelConstraints.fill = GridBagConstraints.BOTH

        this.add(scrollPanel, bottomPanelConstraints)
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
