package org.plugin.plugin.panels

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.openapi.wm.impl.welcomeScreen.learnIde.coursesInProgress.mainBackgroundColor
import com.intellij.ui.components.JBScrollPane
import org.plugin.plugin.MainToolWindow
import org.plugin.plugin.Utility
import java.awt.Font
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.GridLayout
import javax.swing.*


class MainPanel: JPanel() {

    private val lLeaderboardButton = JButton("Leaderboard")
    private val lChallengesButton = JButton("Challenges")
    private val lQuestsButton = JButton("Quests")
    private val lAchievementsButton = JButton("Achievements")
    private val lHelpButton = JButton("Help")
    private val lLogoutButton = JButton("Logout")

    private val lLeaderboardPanel = LeaderboardPanel()
    private val lChallengesPanel = ChallengesPanel()
    private val lQuestsPanel = QuestsPanel()
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

            val toolWindowManager = Utility.project?.let {
                it1 -> ToolWindowManager.getInstance(it1)
            }

            val mainToolWindow = MainToolWindow()
            val myToolWindow: ToolWindow? = toolWindowManager?.getToolWindow("Gamekins")

            if (myToolWindow != null) {
                mainToolWindow.rebuildPanel(myToolWindow)
            }

        }

        for (button in listOf(lLeaderboardButton, lChallengesButton, lQuestsButton, lAchievementsButton, lHelpButton, lLogoutButton)) {
            centerPanel.add(button)
            button.setFont(Font("SansSerif", Font.BOLD, 12))
            button.background = mainBackgroundColor
        }

        lLeaderboardButton.addActionListener { switchToPanel(lLeaderboardPanel) }
        lChallengesButton.addActionListener { switchToPanel(lChallengesPanel) }
        lQuestsButton.addActionListener { switchToPanel(lQuestsPanel) }
        lAchievementsButton.addActionListener { switchToPanel(lAchievementsPanel) }
        lHelpButton.addActionListener { switchToPanel(lHelpPanel) }

        switchToPanel(lChallengesPanel)

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
