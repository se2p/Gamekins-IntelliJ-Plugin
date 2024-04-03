package org.gamekins.ide.panels

import com.google.gson.Gson
import com.intellij.openapi.wm.impl.welcomeScreen.learnIde.coursesInProgress.mainBackgroundColor
import com.intellij.ui.JBColor
import com.intellij.ui.util.maximumHeight
import org.gamekins.ide.Constants
import org.gamekins.ide.Utility
import org.gamekins.ide.data.CompletedQuestTasksList
import org.gamekins.ide.data.QuestTask
import org.gamekins.ide.data.QuestTasksList
import org.gamekins.ide.data.RestClient
import java.awt.*
import javax.swing.*
import javax.swing.border.LineBorder

class QuestsPanel: JPanel() {

    init {
        this.background = mainBackgroundColor
        initializePanel()
    }

    private fun initializePanel() {
        this.layout = BoxLayout(this, BoxLayout.Y_AXIS)
        val projectName = Utility.preferences.get("projectName", "")

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

        val questsTasksList = fetchQuestsTasks(projectName)

        questsTasksList.forEachIndexed { index, task ->
            val questPanel = createSingleQuestPanel(task, index)
            questPanel.background = mainBackgroundColor
            questPanel.maximumHeight = 100
            this.add(questPanel)
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

        val queryParams = mapOf(
            "job" to projectName
        )
        val response =
            RestClient.getInstance().get(Utility.getBaseUrl() + Constants.GET_COMPLETED_QUESTS_TASKS, queryParams)
        val questsTasksList1 = Gson().fromJson(response, CompletedQuestTasksList::class.java).completedQuestTasks

        for (index in questsTasksList1.indices) {

            val questPanel = JPanel(GridLayout(2, 1, 0, 5))
            questPanel.background = Color.decode("#dbffe0")

            questPanel.border = BorderFactory.createCompoundBorder(
                LineBorder(JBColor.GRAY, 0),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
            )
            questPanel.maximumHeight = 70

            val questTask: QuestTask = questsTasksList1[index]
            val questLabel = JLabel(questTask.title)
            questLabel.foreground = JBColor.BLACK
            val headerPanel = JPanel(FlowLayout(FlowLayout.LEFT, 5, 0))
            headerPanel.background = Color.decode("#dbffe0")


            val spacerLabel = JLabel("")
            spacerLabel.preferredSize = Dimension(10, 0)

            val scoreString = if (questTask.score > 1) "points" else "point"
            val challengeTitleScore = JLabel("<html><div style='padding: 3px;'>${questTask.score.toString() + "&nbsp;" + scoreString}</div></html>").apply {
                isOpaque = true
                background = Color.decode("#28a745")
                foreground = JBColor.WHITE
                font = font.deriveFont(Font.BOLD, 12f)
                horizontalAlignment = SwingConstants.CENTER
                verticalAlignment = SwingConstants.CENTER
            }

            headerPanel.add(questLabel)
            headerPanel.add(spacerLabel)
            headerPanel.add(challengeTitleScore)
            questPanel.add(headerPanel)
            this.add(questPanel)
        }
    }

    private fun createSingleQuestPanel(task: QuestTask, index: Int): JPanel {
        val questPanel = JPanel(GridLayout(2, 1, 0, 5)).apply {
            border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
            maximumSize = Dimension(Int.MAX_VALUE, 70)
        }
        questPanel.background = mainBackgroundColor
        val questLabel = JLabel(task.title)
        val headerPanel = JPanel(FlowLayout(FlowLayout.LEFT, 5, 0))
        headerPanel.background = mainBackgroundColor

        val spacerLabel = JLabel("")
        spacerLabel.preferredSize = Dimension(0, 0)

        val scoreString = if (task.score > 1) "points" else "point"
        val challengeTitleScore = JLabel("${task.score} $scoreString").apply {
            isOpaque = true
            background = Color.decode("#28a745")
            foreground = JBColor.WHITE
            font = font.deriveFont(Font.BOLD, 10f)
            horizontalAlignment = SwingConstants.CENTER
            verticalAlignment = SwingConstants.CENTER
            preferredSize = Dimension(60, 20)
        }

        val progressBarLabel = JLabel(task.completedPercentage.toString()).apply {
            text = "${index + 1}. "
        }
        headerPanel.add(progressBarLabel)
        val progressBar = JProgressBar(0, 100).apply {
            value = task.completedPercentage
            isStringPainted = true
        }

        headerPanel.add(questLabel)
        headerPanel.add(spacerLabel)
        headerPanel.add(challengeTitleScore)
        questPanel.add(headerPanel)
        questPanel.add(progressBar)

        return questPanel
    }

    private fun fetchQuestsTasks(projectName: String): List<QuestTask> {
        val queryParams = mapOf("job" to projectName)
        val response =
            RestClient.getInstance().get(Utility.getBaseUrl() + Constants.GET_CURRENT_QUESTS_TASKS, queryParams)
        return Gson().fromJson(response, QuestTasksList::class.java).currentQuestTasks
    }
}