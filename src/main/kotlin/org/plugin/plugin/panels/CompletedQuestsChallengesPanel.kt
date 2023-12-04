package org.plugin.plugin.panels


import com.google.gson.Gson
import com.intellij.openapi.wm.impl.welcomeScreen.learnIde.coursesInProgress.mainBackgroundColor
import com.intellij.ui.components.JBScrollPane
import com.intellij.util.ui.JBUI
import org.plugin.plugin.Constants
import org.plugin.plugin.Utility
import org.plugin.plugin.data.*
import java.awt.*
import javax.swing.*
import javax.swing.border.LineBorder


class CompletedQuestsChallengesPanel : JPanel() {
    init {

        this.layout = GridBagLayout()
        val gbc = GridBagConstraints()
        background = mainBackgroundColor
        gbc.insets = JBUI.insets(15, 15, 0, 15)
        gbc.weightx = 1.0
        gbc.weighty = 0.1
        gbc.gridx = 0
        gbc.gridy = 0
        gbc.fill = GridBagConstraints.HORIZONTAL

        val row1 = JPanel(BorderLayout())

        val lHeader = JLabel("Completed Quests & Challenges")
        lHeader.setFont(Font("Arial", Font.BOLD, 18))
        lHeader.setHorizontalAlignment(SwingConstants.LEFT);
        lHeader.setVerticalAlignment(SwingConstants.CENTER);

        row1.add(lHeader, BorderLayout.CENTER)
        row1.background = mainBackgroundColor
        this.add(row1, gbc)
        createAndShowCompletedQuestsTable(this)
        createChallengePanel(this)
        
    }

    private fun createAndShowCompletedQuestsTable(aInMainPanel: JPanel) {

        val lProjectName = Utility.lPreferences.get("projectName", "")
        if (lProjectName != "") {

            val lQueryParams = mapOf(
                "job" to lProjectName
            )

            val lResponse =
                RestClient.getInstance().get(Utility.getBaseUrl() + Constants.GET_COMPLETED_QUESTS_TASKS, lQueryParams)
            val lQuestsTasksList = Gson().fromJson(lResponse, CompletedQuestsListTasks::class.java).completedQuestTasks

            val lQuestsPanel = JPanel()
            lQuestsPanel.setLayout(BoxLayout(lQuestsPanel, BoxLayout.Y_AXIS))
            lQuestsPanel.background = Color.decode("#dbffe0")


            lQuestsPanel.add(JPanel().apply {
                layout = BorderLayout()
                val label = JLabel("Completed Quests")
                label.setFont(Font("Arial", Font.BOLD, 16))
                label.horizontalAlignment = SwingConstants.LEADING
                label.verticalAlignment = SwingConstants.CENTER
                label.foreground = Color.WHITE
                label.border = BorderFactory.createEmptyBorder(0, 10, 0, 0)
                background = Color.BLACK
                foreground = Color.WHITE
                preferredSize = Dimension(300, 40)
                add(label, BorderLayout.CENTER)
            })
            for (lIndex in lQuestsTasksList.indices) {

                val lQuestPanel = JPanel(GridLayout(2, 1, 0, 5))
                lQuestPanel.background = Color.decode("#dbffe0")

                lQuestPanel.border = BorderFactory.createCompoundBorder(
                    LineBorder(Color.GRAY, 0),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
                )
                lQuestPanel.maximumSize = Dimension(Int.MAX_VALUE, 70)

                val lQuestTask: QuestTask = lQuestsTasksList[lIndex]
                val lQuestLabel = JLabel(lQuestTask.title)
                lQuestLabel.foreground = Color.black
                val lHeaderPanel = JPanel(FlowLayout(FlowLayout.LEFT, 5, 0))
                lHeaderPanel.background = Color.decode("#dbffe0")


                val spacerLabel = JLabel("")
                spacerLabel.preferredSize = Dimension(10, 0)

                val lScoreString = if (lQuestTask.score > 1) "points" else "point"
                val lChallengeTitleScore = JLabel("<html><div style='padding: 3px;'>${lQuestTask.score.toString() + "&nbsp;" + lScoreString}</div></html>").apply {
                    setOpaque(true)
                    setBackground(Color.decode("#28a745"))
                    setForeground(Color.white)
                    setFont(font.deriveFont(Font.BOLD, 12f))
                    setHorizontalAlignment(SwingConstants.CENTER)
                    setVerticalAlignment(SwingConstants.CENTER)
                }


                val lIndex = lIndex + 1
                val progressBarLabel = JLabel(lQuestTask.completedPercentage.toString()).apply {
                    text = "$lIndex. "
                }
                progressBarLabel.foreground = Color.black
                lHeaderPanel.add(progressBarLabel)

                val progressBar = JProgressBar(0, 100).apply {
                    value = lQuestTask.completedPercentage
                    isStringPainted = true
                    foreground = Color.blue
                }

                lHeaderPanel.add(lQuestLabel)
                lHeaderPanel.add(spacerLabel)
                lHeaderPanel.add(lChallengeTitleScore)
                lQuestPanel.add(lHeaderPanel)
                lQuestPanel.add(progressBar)
                lQuestsPanel.add(lQuestPanel)

                val separator = JSeparator(JSeparator.HORIZONTAL)
                if (lIndex != (lQuestsTasksList.size)) {
                    lQuestsPanel.add(separator)
                }
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

            aInMainPanel.add(scrollPane, gbc)
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

