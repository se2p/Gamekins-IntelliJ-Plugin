package org.plugin.plugin.panels

import com.google.gson.Gson
import com.intellij.openapi.wm.impl.welcomeScreen.learnIde.coursesInProgress.mainBackgroundColor
import com.intellij.ui.JBColor
import org.plugin.plugin.Constants
import org.plugin.plugin.Utility
import org.plugin.plugin.data.Achievement
import org.plugin.plugin.data.CompletedAchievementsList
import org.plugin.plugin.data.RestClient
import org.plugin.plugin.data.UnsolvedAchievementsList
import java.awt.*
import java.util.prefs.Preferences
import javax.swing.*

class AchievementsPanel : JPanel() {

    val lPreferences = Preferences.userRoot().node("org.plugin.plugin.panels")

    init {
        this.background = mainBackgroundColor
        initializeAchievements()
    }

    private fun initializeAchievements() {
        val lProjectName = lPreferences.get("projectName", "") ?: return
        val completedAchievements = fetchCompletedAchievements(lProjectName)
        val unsolvedAchievements = fetchUnsolvedAchievements(lProjectName)

        this.layout = BoxLayout(this, BoxLayout.Y_AXIS)

        this.add(Box.createRigidArea(Dimension(0, 10)))

        val label1 = JLabel("Completed Achievements")
        label1.font = Font("Arial", Font.BOLD, 18)
        label1.alignmentX = Component.CENTER_ALIGNMENT
        this.add(label1)

        this.add(Box.createRigidArea(Dimension(0, 10)))

        completedAchievements.forEach { achievement ->
            val panel = JPanel()
            createAchievement(panel, achievement)
            this.add(panel)
            this.add(Box.createRigidArea(Dimension(0, 10)))
        }

        val label2 = JLabel("Unsolved Achievements")
        label2.font = Font("Arial", Font.BOLD, 18)
        label2.alignmentX = Component.CENTER_ALIGNMENT
        this.add(label2)

        this.add(Box.createRigidArea(Dimension(0, 10)))

        unsolvedAchievements.filter { !it.secret }.forEach { achievement ->
            val panel = JPanel()
            createAchievement(panel, achievement)
            this.add(panel)
            this.add(Box.createRigidArea(Dimension(0, 10)))
        }
    }

    private fun fetchCompletedAchievements(projectName: String): List<Achievement> {
        val queryParams = mapOf("job" to projectName)
        val response =
            RestClient.getInstance().get(Utility.getBaseUrl() + Constants.GET_COMPLETED_ACHIEVEMENTS, queryParams)
        return Gson().fromJson(response, CompletedAchievementsList::class.java).completedAchievements
    }

    private fun fetchUnsolvedAchievements(projectName: String): List<Achievement> {
        val queryParams = mapOf("job" to projectName)
        val response =
            RestClient.getInstance().get(Utility.getBaseUrl() + Constants.GET_UNSOLVED_ACHIEVEMENTS, queryParams)
        return Gson().fromJson(response, UnsolvedAchievementsList::class.java).unsolvedAchievements
    }

    private fun createAchievement(panel: JPanel, achievement: Achievement) {
        val gbl = GridBagLayout()
        val gbc = GridBagConstraints()
        gbl.setConstraints(panel, gbc)
        panel.background = mainBackgroundColor
        panel.layout = gbl
        panel.border = BorderFactory.createLineBorder(JBColor.BLACK)

        gbc.gridx = 0
        gbc.gridy = 0
        gbc.gridheight = 2
        val icon = if (achievement.solvedTimeString == "Not solved") {
            val imageUrl = "/achievements/" + achievement.unsolvedBadgePath.substring(achievement.unsolvedBadgePath.indexOf("blackwhite/"))
            ImageIcon(this::class.java.getResource(imageUrl))
        } else {
            val imageUrl = "/achievements/" + achievement.badgePath.substring(achievement.badgePath.indexOf("colour/"))
            ImageIcon(this::class.java.getResource(imageUrl))
        }
        val resizedIcon = Utility.resizeImageIcon(icon, 50, 50)
        panel.add(JLabel(resizedIcon), gbc)

        gbc.gridx = 1
        gbc.gridy = 0
        gbc.gridheight = 2
        panel.add(Box.createRigidArea(Dimension(10, 10)))

        gbc.gridx = 2
        gbc.gridy = 0
        gbc.gridheight = 1
        val title = JLabel("<HTML><div WIDTH=305 style=\"text-align: left\">${achievement.title}</div></HTML>", SwingConstants.LEFT)
        title.font = Font("Arial", Font.BOLD, 13)
        panel.add(title, gbc)

        gbc.gridx = 2
        gbc.gridy = 1
        gbc.gridheight = 1
        panel.add(JLabel("<HTML><div WIDTH=350>${achievement.description}</div></HTML>"), gbc)

        gbc.gridx = 3
        gbc.gridy = 0
        gbc.gridheight = 2
        panel.add(Box.createRigidArea(Dimension(10, 10)))

        gbc.gridx = 4
        gbc.gridy = 0
        gbc.gridheight = 2
        panel.add(JLabel(achievement.solvedTimeString), gbc)
    }
}