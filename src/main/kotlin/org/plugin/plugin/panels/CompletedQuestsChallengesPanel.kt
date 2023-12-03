package org.plugin.plugin.panels


import com.google.gson.Gson
import com.intellij.openapi.wm.impl.welcomeScreen.learnIde.coursesInProgress.mainBackgroundColor
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBScrollPane
import com.intellij.util.ui.JBUI
import org.plugin.plugin.Constants
import org.plugin.plugin.Utility
import org.plugin.plugin.data.*
import org.plugin.plugin.data.QuestsList
import java.awt.*
import javax.swing.*
import javax.swing.border.LineBorder


class CompletedQuestsChallengesPanel : JPanel() {
    init {

        this.layout = GridBagLayout()
        val gbc = GridBagConstraints()
        background = mainBackgroundColor
        gbc.insets = JBUI.insets(5,5,0,5)
        gbc.weightx = 1.0
        gbc.weighty = 0.1
        gbc.gridx = 0
        gbc.gridy = 0
        gbc.fill = GridBagConstraints.HORIZONTAL

        val lHeader = JLabel("Completed Quests & Challenges")
        lHeader.setFont(Font("Arial", Font.BOLD, 18))
        lHeader.setHorizontalAlignment(SwingConstants.CENTER);
        lHeader.setVerticalAlignment(SwingConstants.CENTER);

        this.add(lHeader, gbc)
        createAndShowCompletedQuestsTable(this)
        createChallengePanel(this)
        
    }

    private fun createAndShowCompletedQuestsTable(mainPanel: JPanel) {

        val lProjectName = Utility.lPreferences.get("projectName", "")
        if (lProjectName != "") {

            val queryParams = mapOf(
                "job" to lProjectName
            )

            val response =
                RestClient.getInstance().get(Utility.getBaseUrl() + Constants.GET_COMPLETED_QUESTS_TASKS, queryParams)
            val questsTasksList = Gson().fromJson(response, CompletedQuestsListTasks::class.java).completedQuestTasks

            val lQuestsPanel = JPanel()
            lQuestsPanel.setLayout(BoxLayout(lQuestsPanel, BoxLayout.Y_AXIS))
            lQuestsPanel.background = mainBackgroundColor

            for (index in questsTasksList.indices) {

                val lQuestPanel = JPanel(GridLayout(2, 1, 0, 5))
                lQuestPanel.background = mainBackgroundColor

                lQuestPanel.border = BorderFactory.createCompoundBorder(
                    LineBorder(JBColor.GRAY, 0),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
                )
                lQuestPanel.maximumSize = Dimension(Int.MAX_VALUE, 70)

                val task: QuestTask = questsTasksList[index]
                val questLabel = JLabel(task.title)
                val headerPanel = JPanel(FlowLayout(FlowLayout.LEFT, 5, 0))
                headerPanel.background = mainBackgroundColor

                val spacerLabel = JLabel("")
                spacerLabel.preferredSize = Dimension(10, 0)

                val scoreString = if (task.score > 1) "points" else "point"

                val challengeTitleScore = JLabel(task.score.toString() + "" + scoreString)
                challengeTitleScore.setForeground(JBColor.GREEN)

                val lIndex = index + 1
                val progressBarLabel = JLabel(task.completedPercentage.toString()).apply {
                    text = "$lIndex. "
                }
                headerPanel.add(progressBarLabel)

                val progressBar = JProgressBar(0, 100).apply {
                    value = task.completedPercentage
                    isStringPainted = true
                    foreground = Color.blue
                }

                headerPanel.add(questLabel)
                headerPanel.add(spacerLabel)
                headerPanel.add(challengeTitleScore)
                lQuestPanel.add(headerPanel)
                lQuestPanel.add(progressBar)
                lQuestsPanel.add(lQuestPanel)
            }

            val scrollPane = JBScrollPane(lQuestsPanel)
            scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS)

            val gbc = GridBagConstraints()
            gbc.insets = JBUI.insets(10)
            gbc.weightx = 1.0
            gbc.weighty = 0.45
            gbc.gridx = 0
            gbc.gridy = 1
            gbc.fill = GridBagConstraints.BOTH
            gbc.gridwidth = GridBagConstraints.REMAINDER

            mainPanel.add(scrollPane, gbc)
        }
    }

    private fun createChallengePanel(mainPanel: JPanel) {

        val gbc = GridBagConstraints()
        gbc.insets = JBUI.insets(10)
        gbc.weightx = 1.0
        gbc.weighty = 0.45
        gbc.gridx = 0
        gbc.gridy = 2

        gbc.fill = GridBagConstraints.HORIZONTAL

        mainPanel.add(AcceptedRejectedChallengesPanel(), gbc)
    }

}

