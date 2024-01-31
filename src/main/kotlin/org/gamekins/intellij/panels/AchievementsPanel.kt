package org.gamekins.intellij.panels

import com.google.gson.Gson
import com.intellij.openapi.wm.impl.welcomeScreen.learnIde.coursesInProgress.mainBackgroundColor
import com.intellij.ui.JBColor
import com.intellij.ui.util.maximumHeight
import com.intellij.ui.util.maximumWidth
import com.intellij.ui.util.minimumWidth
import org.gamekins.intellij.Constants
import org.gamekins.intellij.Utility
import org.gamekins.intellij.data.*
import java.awt.*
import javax.swing.*

class AchievementsPanel : JPanel() {

    init {
        this.background = mainBackgroundColor
        initializeAchievements()
    }

    private fun initializeAchievements() {
        val projectName = Utility.preferences["projectName", ""] ?: return
        val completedAchievements = fetchCompletedAchievements(projectName)
        val unsolvedAchievements = fetchUnsolvedAchievements(projectName)
        val badgeAchievements = fetchBadgeAchievements(projectName)
        val progressAchievements = fetchProgressAchievements(projectName)

        this.layout = BoxLayout(this, BoxLayout.Y_AXIS)

        this.add(Box.createRigidArea(Dimension(0, 10)))

        val labelBadge = JLabel("Badge Achievements")
        labelBadge.font = Font("Arial", Font.BOLD, 18)
        labelBadge.alignmentX = Component.CENTER_ALIGNMENT
        this.add(labelBadge)

        this.add(Box.createRigidArea(Dimension(0, 10)))

        badgeAchievements.forEach { achievement ->
            val panel = JPanel()
            createBadgeAchievement(panel, achievement)
            this.add(panel)
            this.add(Box.createRigidArea(Dimension(0, 10)))
        }

        this.add(Box.createRigidArea(Dimension(0, 10)))

        val labelProgress = JLabel("Progress Achievements")
        labelProgress.font = Font("Arial", Font.BOLD, 18)
        labelProgress.alignmentX = Component.CENTER_ALIGNMENT
        this.add(labelProgress)

        this.add(Box.createRigidArea(Dimension(0, 10)))

        progressAchievements.forEach { achievement ->
            val panel = JPanel()
            createProgressAchievement(panel, achievement)
            this.add(panel)
            this.add(Box.createRigidArea(Dimension(0, 10)))
        }

        this.add(Box.createRigidArea(Dimension(0, 10)))

        if (completedAchievements.isNotEmpty()) {
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

    private fun fetchBadgeAchievements(projectName: String): List<BadgeAchievement> {
        val queryParams = mapOf("job" to projectName)
        val response =
            RestClient.getInstance().get(Utility.getBaseUrl() + Constants.GET_BADGE_ACHIEVEMENTS, queryParams)
        return Gson().fromJson(response, BadgeAchievementsList::class.java).badgeAchievements
    }

    private fun fetchProgressAchievements(projectName: String): List<ProgressAchievement> {
        val queryParams = mapOf("job" to projectName)
        val response =
            RestClient.getInstance().get(Utility.getBaseUrl() + Constants.GET_PROGRESS_ACHIEVEMENTS, queryParams)
        return Gson().fromJson(response, ProgressAchievementsList::class.java).progressAchievements
    }

    private fun createAchievement(panel: JPanel, achievement: Achievement) {
        val gbl = GridBagLayout()
        val gbc = GridBagConstraints()
        gbl.setConstraints(panel, gbc)
        panel.background = mainBackgroundColor
        panel.layout = gbl
        panel.border = BorderFactory.createLineBorder(JBColor.BLACK)
        panel.maximumHeight = 70
        panel.maximumWidth = 650

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
        panel.add(Box.createRigidArea(Dimension(10, 10)))
        gbc.gridx = 1
        panel.add(JLabel(resizedIcon), gbc)

        gbc.gridx = 2
        gbc.gridy = 0
        gbc.gridheight = 2
        panel.add(Box.createRigidArea(Dimension(10, 10)))

        gbc.gridx = 3
        gbc.gridy = 0
        gbc.gridheight = 1
        gbc.weightx = 1.0
        val title = JLabel("<HTML><div WIDTH=350 style=\"text-align: left\">${achievement.title}</div></HTML>", SwingConstants.LEFT)
        title.font = Font("Arial", Font.BOLD, 13)
        panel.add(title, gbc)

        gbc.gridx = 3
        gbc.gridy = 1
        gbc.gridheight = 1
        gbc.weightx = 1.0
        panel.add(JLabel("<HTML><div WIDTH=350 style=\"text-align: left\">${achievement.description}</div></HTML>"), gbc)

        gbc.gridx = 4
        gbc.gridy = 0
        gbc.gridheight = 2
        panel.add(Box.createRigidArea(Dimension(10, 10)))

        gbc.gridx = 5
        gbc.gridy = 0
        gbc.gridheight = 2
        panel.add(JLabel(achievement.solvedTimeString), gbc)
    }

    private fun createBadgeAchievement(panel: JPanel, achievement: BadgeAchievement) {
        val gbl = GridBagLayout()
        val gbc = GridBagConstraints()
        gbl.setConstraints(panel, gbc)
        panel.background = mainBackgroundColor
        panel.layout = gbl
        panel.border = BorderFactory.createLineBorder(JBColor.BLACK)
        panel.maximumHeight = 75
        panel.maximumWidth = 650

        gbc.gridx = 0
        gbc.gridy = 0
        gbc.gridheight = 3
        panel.add(Box.createRigidArea(Dimension(10, 10)), gbc)

        gbc.gridx = 1
        gbc.gridheight = 1
        gbc.weightx = 1.0
        gbc.gridwidth = 10
        val title = JLabel("<HTML><div WIDTH=630 style=\"text-align: left\">${achievement.title}</div></HTML>", SwingConstants.LEFT)
        title.font = Font("Arial", Font.BOLD, 13)
        panel.add(title, gbc)

        gbc.gridy = 1
        panel.add(JLabel("<HTML><div WIDTH=630 style=\"text-align: left\">${achievement.description}</div></HTML>"), gbc)

        gbc.gridy = 2
        gbc.weightx = 0.0
        gbc.gridheight = 1
        gbc.gridwidth = 1
        var x = 1
        var gridx = 1
        achievement.badgePaths.forEach {
            val width = (630 - (achievement.badgePaths.size * 50)) / (achievement.badgePaths.size)
            gbc.gridx = gridx
            val icon = if (achievement.badgeCounts[x - 1] == 0) {
                val path = it.replace("colour", "blackwhite").replace("-blackwhite.png", ".png")
                val imageUrl = "/achievements/" + path.substring(path.indexOf("blackwhite/"))
                ImageIcon(this::class.java.getResource(imageUrl))
            } else {
                val imageUrl = "/achievements/" + it.substring(it.indexOf("colour/"))
                ImageIcon(this::class.java.getResource(imageUrl))
            }
            val resizedIcon = Utility.resizeImageIcon(icon, 50, 50)
            gbc.gridy = 2
            gbc.weightx = 0.0
            gbc.gridheight = 1
            panel.add(JLabel(resizedIcon), gbc)
            gridx = gridx.inc()
            gbc.gridx = gridx
            panel.add(Box.createRigidArea(Dimension(width, 10)), gbc)

            x = x.inc()
            gridx = gridx.inc()
        }

        gbc.gridy = 3
        panel.add(Box.createRigidArea(Dimension(10, 5)), gbc)
    }

    fun createProgressAchievement(panel: JPanel, achievement: ProgressAchievement) {
        val gbl = GridBagLayout()
        val gbc = GridBagConstraints()
        gbl.setConstraints(panel, gbc)
        panel.background = mainBackgroundColor
        panel.layout = gbl
        panel.border = BorderFactory.createLineBorder(JBColor.BLACK)
        panel.maximumHeight = 75
        panel.maximumWidth = 650

        gbc.gridx = 0
        gbc.gridy = 0
        gbc.gridheight = 4
        panel.add(Box.createRigidArea(Dimension(10, 10)), gbc)

        gbc.gridx = 1
        gbc.gridy = 0
        gbc.gridheight = 4
        val icon = if (achievement.progress < achievement.milestones[0]) {
            val path = achievement.badgePath.replace("colour", "blackwhite").replace("-blackwhite.png", ".png")
            val imageUrl = "/achievements/" + path.substring(path.indexOf("blackwhite/"))
            ImageIcon(this::class.java.getResource(imageUrl))
        } else {
            val imageUrl = "/achievements/" + achievement.badgePath.substring(achievement.badgePath.indexOf("colour/"))
            ImageIcon(this::class.java.getResource(imageUrl))
        }
        val resizedIcon = Utility.resizeImageIcon(icon, 50, 50)
        val imageLabel = JLabel(resizedIcon)
        imageLabel.minimumWidth = 50
        panel.add(imageLabel, gbc)

        gbc.gridx = 2
        gbc.gridheight = 4
        panel.add(Box.createRigidArea(Dimension(10, 10)), gbc)

        gbc.gridx = 3
        gbc.gridheight = 1
        gbc.gridwidth = 12
        val title = JLabel("<HTML><div WIDTH=580 style=\"text-align: left\">${achievement.title}</div></HTML>", SwingConstants.LEFT)
        title.font = Font("Arial", Font.BOLD, 13)
        panel.add(title, gbc)

        var gridx = 4
        gbc.gridwidth = 1
        gbc.weightx = 0.0
        achievement.milestones.forEachIndexed { index, milestone ->

            gbc.gridx = gridx
            gbc.gridy = 1
            gbc.gridheight = 1
            val previous = if (index == 0) 0 else achievement.milestones[index - 1]
            val progressBar = JProgressBar(previous, milestone).apply {
                value = achievement.progress
                string = if (achievement.progress in previous..milestone) {
                    "${achievement.progress}${achievement.unit}"
                } else {
                    ""
                }
                isStringPainted = true
                minimumWidth = (580 / achievement.milestones.size) - 10
            }
            panel.add(progressBar, gbc)

            gbc.gridy = 2
            val progressNumber = JLabel("${milestone}${achievement.unit}")
            progressNumber.font = Font("Arial", Font.ITALIC, 13)
            panel.add(progressNumber, gbc)

            gridx = gridx.inc()
            gbc.gridx = gridx
            gbc.gridy = 1
            gbc.gridheight = 1
            panel.add(Box.createRigidArea(Dimension(10, 10)), gbc)
            gridx = gridx.inc()
        }

        gbc.gridx = 3
        gbc.gridy = 3
        gbc.gridwidth = 12
        panel.add(JLabel("<HTML><div WIDTH=580 style=\"text-align: left\">${achievement.description}</div></HTML>"), gbc)
    }
}