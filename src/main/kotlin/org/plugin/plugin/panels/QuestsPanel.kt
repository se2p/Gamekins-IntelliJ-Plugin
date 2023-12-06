package org.plugin.plugin.panels

import com.google.gson.Gson
import com.intellij.openapi.wm.impl.welcomeScreen.learnIde.coursesInProgress.mainBackgroundColor
import com.intellij.ui.JBColor
import com.intellij.ui.util.maximumHeight
import org.plugin.plugin.Constants
import org.plugin.plugin.Utility
import org.plugin.plugin.data.CompletedQuestsListTasks
import org.plugin.plugin.data.QuestTask
import org.plugin.plugin.data.QuestsListTasks
import org.plugin.plugin.data.RestClient
import java.awt.*
import javax.swing.*
import javax.swing.border.LineBorder

class QuestsPanel: JPanel() {

    init {
        this.background = mainBackgroundColor
        initializePanel()
    }

    fun initializePanel() {
        this.layout = BoxLayout(this, BoxLayout.Y_AXIS)
        val lProjectName = Utility.lPreferences.get("projectName", "")

        this.add(JPanel().apply {
            layout = BorderLayout()
            val label = JLabel("Current Quests")
            label.font = Font("Arial", Font.BOLD, 16)
            label.horizontalAlignment = SwingConstants.LEADING
            label.verticalAlignment = SwingConstants.CENTER
            label.foreground = JBColor.WHITE
            label.border = BorderFactory.createEmptyBorder(0, 10, 0, 0)
            background = JBColor.BLACK
            foreground = JBColor.WHITE
            preferredSize = Dimension(300, 40)
            maximumHeight = 40
            add(label, BorderLayout.CENTER)
        })

        this.add(Box.createRigidArea(Dimension(0, 10)))

        val questsTasksList = fetchQuestsTasks(lProjectName)

        questsTasksList.forEachIndexed { index, task ->
            val lQuestPanel = createSingleQuestPanel(task, index)
            lQuestPanel.background = mainBackgroundColor
            lQuestPanel.maximumHeight = 100
            this.add(lQuestPanel)
        }

        this.add(JPanel().apply {
            layout = BorderLayout()
            val label = JLabel("Completed Quests")
            label.font = Font("Arial", Font.BOLD, 16)
            label.horizontalAlignment = SwingConstants.LEADING
            label.verticalAlignment = SwingConstants.CENTER
            label.foreground = JBColor.WHITE
            label.border = BorderFactory.createEmptyBorder(0, 10, 0, 0)
            background = JBColor.BLACK
            foreground = JBColor.WHITE
            preferredSize = Dimension(300, 40)
            maximumHeight = 40
            add(label, BorderLayout.CENTER)
        })

        val lQueryParams = mapOf(
            "job" to lProjectName
        )
        val lResponse =
            RestClient.getInstance().get(Utility.getBaseUrl() + Constants.GET_COMPLETED_QUESTS_TASKS, lQueryParams)
        val lQuestsTasksList = Gson().fromJson(lResponse, CompletedQuestsListTasks::class.java).completedQuestTasks

        for (lIndex in lQuestsTasksList.indices) {

            val lQuestPanel = JPanel(GridLayout(2, 1, 0, 5))
            lQuestPanel.background = Color.decode("#dbffe0")

            lQuestPanel.border = BorderFactory.createCompoundBorder(
                LineBorder(JBColor.GRAY, 0),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
            )
            lQuestPanel.maximumHeight = 70

            val lQuestTask: QuestTask = lQuestsTasksList[lIndex]
            val lQuestLabel = JLabel(lQuestTask.title)
            lQuestLabel.foreground = JBColor.BLACK
            val lHeaderPanel = JPanel(FlowLayout(FlowLayout.LEFT, 5, 0))
            lHeaderPanel.background = Color.decode("#dbffe0")


            val spacerLabel = JLabel("")
            spacerLabel.preferredSize = Dimension(10, 0)

            val lScoreString = if (lQuestTask.score > 1) "points" else "point"
            val lChallengeTitleScore = JLabel("<html><div style='padding: 3px;'>${lQuestTask.score.toString() + "&nbsp;" + lScoreString}</div></html>").apply {
                isOpaque = true
                background = Color.decode("#28a745")
                foreground = JBColor.WHITE
                font = font.deriveFont(Font.BOLD, 12f)
                horizontalAlignment = SwingConstants.CENTER
                verticalAlignment = SwingConstants.CENTER
            }


            val progressBarLabel = JLabel(lQuestTask.completedPercentage.toString()).apply {
                text = "${lIndex + 1}. "
            }
            progressBarLabel.foreground = JBColor.BLACK
            lHeaderPanel.add(progressBarLabel)

            val progressBar = JProgressBar(0, 100).apply {
                value = lQuestTask.completedPercentage
                isStringPainted = true
                foreground = JBColor.BLUE
            }

            lHeaderPanel.add(lQuestLabel)
            lHeaderPanel.add(spacerLabel)
            lHeaderPanel.add(lChallengeTitleScore)
            lQuestPanel.add(lHeaderPanel)
            lQuestPanel.add(progressBar)
            this.add(lQuestPanel)
        }
    }

    private fun createSingleQuestPanel(task: QuestTask, index: Int): JPanel {
        val lQuestPanel = JPanel(GridLayout(2, 1, 0, 5)).apply {
            border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
            maximumSize = Dimension(Int.MAX_VALUE, 70)
        }
        lQuestPanel.background = mainBackgroundColor
        val questLabel = JLabel(task.title)
        val headerPanel = JPanel(FlowLayout(FlowLayout.LEFT, 5, 0))
        headerPanel.background = mainBackgroundColor

        val spacerLabel = JLabel("")
        spacerLabel.preferredSize = Dimension(0, 0)

        val lScoreString = if (task.score > 1) "points" else "point"
        val lChallengeTitleScore = JLabel("${task.score} $lScoreString").apply {
            isOpaque = true
            background = Color.decode("#28a745")
            foreground = JBColor.WHITE
            font = font.deriveFont(Font.BOLD, 10f)
            horizontalAlignment = SwingConstants.CENTER
            verticalAlignment = SwingConstants.CENTER
            preferredSize = Dimension(60, 20)
        }

        val lIndex = index + 1
        val progressBarLabel = JLabel(task.completedPercentage.toString()).apply {
            text = "$lIndex. "
        }
        headerPanel.add(progressBarLabel)
        val progressBar = JProgressBar(0, 100).apply {
            value = task.completedPercentage
            isStringPainted = true
        }

        headerPanel.add(questLabel)
        headerPanel.add(spacerLabel)
        headerPanel.add(lChallengeTitleScore)
        lQuestPanel.add(headerPanel)
        lQuestPanel.add(progressBar)

        return lQuestPanel
    }

    private fun fetchQuestsTasks(projectName: String): List<QuestTask> {
        val queryParams = mapOf("job" to projectName)
        val response =
            RestClient.getInstance().get(Utility.getBaseUrl() + Constants.GET_CURRENT_QUESTS_TASKS, queryParams)
        return Gson().fromJson(response, QuestsListTasks::class.java).currentQuestTasks
    }
}