package org.plugin.plugin

import Challenge
import ChallengeList
import StoredChallengeList
import com.google.gson.Gson
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.table.JBTable
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.plugin.plugin.data.*
import org.plugin.plugin.data.QuestTask
import org.plugin.plugin.panels.AcceptedRejectedChallengesPanel
import java.awt.BorderLayout
import java.awt.Dialog
import java.awt.FlowLayout
import java.awt.Font
import java.awt.event.ActionEvent
import java.util.*
import javax.swing.*
import javax.swing.border.LineBorder
import javax.swing.table.DefaultTableModel


object Utility {

    init {
        println("Utility initialized")
    }

    fun createChallengePanel(mainPanel: JPanel) {
        mainPanel.add(AcceptedRejectedChallengesPanel())
    }

    fun createQuests(mainPanel: JPanel) {

        mainPanel.setLayout(BoxLayout(mainPanel, BoxLayout.Y_AXIS))
        val currentQuestTasks: MutableList<QuestTask> = ArrayList<QuestTask>()

        currentQuestTasks.add(QuestTask("Quest 1", 80, 1))
        currentQuestTasks.add(QuestTask("Quest 2", 60, 2))
        currentQuestTasks.add(QuestTask("Quest 3", 40, 1))

        for (index in currentQuestTasks.indices) {
            val task: QuestTask = currentQuestTasks[index]
            val taskPanel = JPanel()
            taskPanel.setLayout(BoxLayout(taskPanel, BoxLayout.Y_AXIS))

            val questLabel = JLabel((index + 1).toString() + ". " + task.name)
            val progressBarLabel = JLabel(task.completedPercentage.toString())
            progressBarLabel.setForeground(JBColor.BLUE)
            val progressBar = JProgressBar(0, 100)
            progressBar.setValue(task.completedPercentage)
            progressBar.setStringPainted(true)

            taskPanel.add(questLabel)
            taskPanel.add(progressBar)
            mainPanel.add(taskPanel)
        }
    }

    fun createChallenges(mainPanel: JPanel) {

        val descriptionLabel = JLabel(Constants.CHALLENGE_PANEL_DESCRIPTION)
        mainPanel.add(descriptionLabel, BorderLayout.CENTER)

        val accordionPanel = JPanel()
        accordionPanel.setLayout(BoxLayout(accordionPanel, BoxLayout.Y_AXIS))

        val cardHeaderPanel = JPanel()
        cardHeaderPanel.setLayout(BoxLayout(cardHeaderPanel, BoxLayout.Y_AXIS))

        val challengesPanel = JPanel()
        challengesPanel.setLayout(BoxLayout(challengesPanel, BoxLayout.Y_AXIS))

        val queryParams = mapOf(
            "job" to Constants.TEST_JOB
        )

        try {
            val response = RestClient().get(Constants.API_BASE_URL + Constants.GET_CURRENT_CHALLENGES, queryParams)
            val challengeList = Gson().fromJson(response, ChallengeList::class.java).currentChallenges

            for (index in 0..challengeList.size) {

                val challengePanel = JPanel()
                challengePanel.border = LineBorder(JBColor.BLUE, 1)
                challengePanel.setLayout(BorderLayout())

                val challengeHeader = JPanel()
                challengeHeader.setLayout(FlowLayout(FlowLayout.LEFT))
                val challengeTitleLabel = JLabel(challengeList[index].generalReason)

                val scoreString = if (challengeList[index].score!! > 1) "points" else "point"
                val challengeTitleScore = JLabel(challengeList[index].score.toString() + "" + scoreString)
                val challengeTitleName = JLabel(challengeList[index].name)
                val expandButton = JButton("Expand")

                challengeHeader.add(challengeTitleLabel)
                if (challengeList[index].score!! > 0)
                    challengeHeader.add(challengeTitleScore)
                challengeHeader.add(challengeTitleName)
                challengeTitleName.background = JBColor.yellow

                challengeHeader.add(expandButton)

                val buttonsPanel = JPanel()
                buttonsPanel.setLayout(FlowLayout(FlowLayout.RIGHT))
                val storeButton = JButton("Store")
                val rejectButton = JButton("Reject")
                buttonsPanel.add(storeButton)
                buttonsPanel.add(rejectButton)

                val extraContentPanel = JPanel()
                extraContentPanel.setLayout(BorderLayout())
                extraContentPanel.isVisible = false

                val challengeSnippetLabel: JLabel
                val challengeHighlightedFileContentLabel: JLabel


                if (challengeList[index].snippet != "") {
                    challengeSnippetLabel = JLabel(challengeList[index].snippet.toString())
                    extraContentPanel.add(challengeSnippetLabel, BorderLayout.PAGE_START)
                }

                if (challengeList[index].highlightedFileContent != "") {
                    challengeHighlightedFileContentLabel =
                        JLabel(challengeList[index].highlightedFileContent.toString())
                    val separator = JSeparator(JSeparator.HORIZONTAL)
                    extraContentPanel.add(separator, BorderLayout.CENTER)
                    extraContentPanel.add(challengeHighlightedFileContentLabel, BorderLayout.PAGE_END)
                }

                expandButton.addActionListener {
                    extraContentPanel.isVisible = !extraContentPanel.isVisible
                    challengePanel.revalidate()
                    challengePanel.repaint()
                }

                storeButton.addActionListener {
                    storeChallenge(Constants.TEST_JOB, challengeList[index].name)
                }

                rejectButton.addActionListener {
                    createRejectModal()
                }

                challengePanel.add(challengeHeader, BorderLayout.PAGE_START)
                challengePanel.add(extraContentPanel, BorderLayout.CENTER)
                challengePanel.add(buttonsPanel, BorderLayout.PAGE_END)
                challengesPanel.add(challengePanel)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        cardHeaderPanel.add(challengesPanel)
        val scrollPane = JBScrollPane(cardHeaderPanel)
        accordionPanel.add(scrollPane)
        mainPanel.add(accordionPanel)
    }

    private fun createRejectModal() {

        val rejectModal = JDialog()
        rejectModal.setTitle("Reject Current Challenge")
        rejectModal.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE)
        rejectModal.setSize(400, 300)
        rejectModal.setModalityType(Dialog.ModalityType.APPLICATION_MODAL)
        rejectModal.setLocationRelativeTo(null)
        val modalContent = JPanel()
        modalContent.setLayout(BorderLayout())
        val modalHeader = JPanel()
        val modalTitle = JLabel("Reject Current Challenge")
        modalHeader.add(modalTitle)
        val modalBody = JPanel()
        modalBody.setLayout(BorderLayout())
        val formPanel = JPanel()
        formPanel.setLayout(BorderLayout())
        val reasonPanel = JPanel()
        reasonPanel.setLayout(BorderLayout())
        val reasonLabel = JLabel("Reason:")
        val reasonTextArea = JTextArea(5, 20)
        reasonTextArea.setLineWrap(true)
        reasonTextArea.setWrapStyleWord(true)
        reasonPanel.add(reasonLabel, BorderLayout.NORTH)
        reasonPanel.add(JScrollPane(reasonTextArea), BorderLayout.CENTER)
        val errorPanel = JPanel()
        errorPanel.setLayout(BorderLayout())
        val errorLabel = JLabel("Error Message:")
        val errorMessage = JLabel("This is where error messages will appear.")
        errorPanel.add(errorLabel, BorderLayout.NORTH)
        errorPanel.add(errorMessage, BorderLayout.CENTER)
        formPanel.add(reasonPanel, BorderLayout.NORTH)
        formPanel.add(errorPanel, BorderLayout.CENTER)
        modalBody.add(formPanel, BorderLayout.NORTH)
        val modalFooter = JPanel()
        val closeModalButton = JButton("Close")
        val rejectButton = JButton("Reject")
        modalFooter.add(closeModalButton)
        modalFooter.add(rejectButton)
        closeModalButton.addActionListener { rejectModal.dispose() }

        rejectButton.addActionListener { // Implement the logic for rejecting the challenge here
            errorMessage.setText("Challenge rejected successfully.")
        }

        modalContent.add(modalHeader, BorderLayout.NORTH)
        modalContent.add(modalBody, BorderLayout.CENTER)
        modalContent.add(modalFooter, BorderLayout.SOUTH)
        rejectModal.add(modalContent)
        rejectModal.isVisible = true

    }

    fun createStoredButton(mainPanel: JPanel) {

        val queryParams = mapOf(
            "job" to Constants.TEST_JOB
        )

        val response = RestClient().get(Constants.API_BASE_URL + Constants.GET_STORED_CHALLENGES, queryParams)
        val challengeList = Gson().fromJson(response, StoredChallengeList::class.java).storedChallenges

        val storedChallengesLimit = 2
        val storedChallengesCount = challengeList.size

        if (storedChallengesLimit > 0 || storedChallengesCount > 0) {
            val storedChallengesButton = JButton("Stored Challenges ($storedChallengesCount/$storedChallengesLimit)")
            storedChallengesButton.setFont(Font("Arial", Font.PLAIN, 14))
            storedChallengesButton.addActionListener { e: ActionEvent? -> openStoredChallengesDialog(challengeList) }
            mainPanel.add(storedChallengesButton, BorderLayout.CENTER)
        }

    }

    private fun openStoredChallengesDialog(aInStoredChallenges: List<Challenge>) {

        val accordionPanel = JPanel()
        accordionPanel.setLayout(BoxLayout(accordionPanel, BoxLayout.Y_AXIS))

        val cardHeaderPanel = JPanel()
        cardHeaderPanel.setLayout(BoxLayout(cardHeaderPanel, BoxLayout.Y_AXIS))

        val challengesPanel = JPanel()
        challengesPanel.setLayout(BoxLayout(challengesPanel, BoxLayout.Y_AXIS))

        val storedChallengesModal = JDialog()

        storedChallengesModal.setTitle("Stored Challenges")
        storedChallengesModal.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE)
        storedChallengesModal.setSize(800, 600)
        storedChallengesModal.setModalityType(Dialog.ModalityType.APPLICATION_MODAL)

        val modalContent = JPanel()
        modalContent.setLayout(BorderLayout())

        val modalHeader = JPanel()
        val modalTitle = JLabel("Stored Challenges")
        modalHeader.add(modalTitle)

        val modalBody = JPanel()
        modalBody.setLayout(BorderLayout())

        for (index in 0 until aInStoredChallenges.size) {

            val challengePanel = JPanel()
            challengePanel.border = LineBorder(JBColor.BLUE, 1)
            challengePanel.setLayout(BorderLayout())

            val challengeHeader = JPanel()
            challengeHeader.setLayout(FlowLayout(FlowLayout.LEFT))
            val challengeTitleLabel = JLabel(aInStoredChallenges[index].generalReason)

            val challengeTitleName = JLabel(aInStoredChallenges[index].name)
            challengeTitleName.setBackground(JBColor.yellow)

            challengeHeader.add(challengeTitleLabel)
            challengeHeader.add(challengeTitleName)

            val buttonsPanel = JPanel()
            buttonsPanel.setLayout(FlowLayout(FlowLayout.RIGHT))
            val unshelveButton = JButton("Unshelve")
            buttonsPanel.add(unshelveButton)

            unshelveButton.addActionListener {
                unshelveChallenge(Constants.TEST_JOB, aInStoredChallenges[index].name)
            }

            challengePanel.add(challengeHeader, BorderLayout.PAGE_START)
            challengePanel.add(buttonsPanel, BorderLayout.CENTER)
            challengesPanel.add(challengePanel)
            modalBody.add(challengesPanel)
        }

        val modalFooter = JPanel()
        val closeModalButton = JButton("Close")
        modalFooter.add(closeModalButton)

        closeModalButton.addActionListener { storedChallengesModal.dispose() }

        modalContent.add(modalHeader, BorderLayout.NORTH)
        modalContent.add(modalBody, BorderLayout.CENTER)
        modalContent.add(modalFooter, BorderLayout.SOUTH)

        storedChallengesModal.setLocationRelativeTo(null)


        storedChallengesModal.add(modalContent)
        storedChallengesModal.isVisible = true
    }

    fun createAndShowCompletedQuestsTable(mainPanel: JPanel) {

        mainPanel.setLayout(BoxLayout(mainPanel, BoxLayout.Y_AXIS))

        val completedQuestTasks: MutableList<QuestTask> = ArrayList<QuestTask>()

        completedQuestTasks.add(QuestTask("Quest 1", 100, 1))
        completedQuestTasks.add(QuestTask("Quest 2", 100, 2))
        completedQuestTasks.add(QuestTask("Quest 3", 100, 1))

        for (index in completedQuestTasks.indices) {

            val task: QuestTask = completedQuestTasks[index]
            val taskPanel = JPanel()

            taskPanel.layout = BoxLayout(taskPanel, BoxLayout.Y_AXIS)

            var questLabel: JLabel
            questLabel = JLabel((index + 1).toString() + ". " + task.name)

            if (task.score > 1) {
                questLabel = JLabel(" " + task.score + " points")
            } else if (task.score == 1) {
                questLabel = JLabel(" " + task.score + " point")
            }

            val progressBarLabel = JLabel(task.completedPercentage.toString())
            progressBarLabel.foreground = JBColor.BLUE

            val progressBar = JProgressBar(0, 100)
            progressBar.value = task.completedPercentage
            progressBar.isStringPainted = true

            taskPanel.add(questLabel)
            taskPanel.add(progressBar)

            mainPanel.add(taskPanel)
        }
    }

    fun userTablePanel(mainPanel: JPanel) {

        val queryParams = mapOf(
            "job" to Constants.TEST_JOB
        )

        val response = RestClient().get(Constants.API_BASE_URL + Constants.GET_USERS, queryParams)
        val userList = Gson().fromJson(response, UserList::class.java).users

        val columnNames = arrayOf(
            "#",
            "",
            "Participant",
            "Team",
            "Completed Challenges",
            "Completed Quests",
            "Completed Achievements",
            "Score"
        )
        val columns: Vector<String> = Vector()
        for (columnName in columnNames) {
            columns.add(columnName)
        }
        val tableModel = DefaultTableModel(columns, 0)
        val label = ImageIcon(javaClass.getResource("/avatars/001-actress.png"))

        for ((index, userDetails) in userList.withIndex()) {
            tableModel.addRow(arrayOf<Any>(index, "icon", userDetails.userName, userDetails.teamName, userDetails.completedChallenges, userDetails.completedQuests, userDetails.completedAchievements, userDetails.score))
        }

        val table = JBTable(tableModel)
        table.tableHeader.setFont(Font("SansSerif", Font.BOLD, 12))
        val scrollPane = JBScrollPane(table)

        //table.columnModel.getColumn(1).setCellRenderer(AvatarTableCellRenderer())
        mainPanel.add(scrollPane, BorderLayout.NORTH)

    }

    fun teamTablePanel(mainPanel: JPanel) {

        val columnNames =
            arrayOf("#", "Team", "Completed Challenges", "Completed Quests", "Completed Achievements", "Score")
        val columns: Vector<String> = Vector()
        for (columnName in columnNames) {
            columns.add(columnName)
        }
        val tableModel = DefaultTableModel(columns, 0)

        val queryParams = mapOf(
            "job" to Constants.TEST_JOB
        )

        val response = RestClient().get(Constants.API_BASE_URL + Constants.GET_TEAMS, queryParams)
        val teamList = Gson().fromJson(response, TeamList::class.java).teams

        for ((index, team) in teamList.withIndex()) {
            tableModel.addRow(arrayOf<Any>(index, team.teamName, team.completedChallenges, team.completedQuests, team.completedAchievements, team.score),)
        }

        val table = JBTable(tableModel)
        table.tableHeader.setFont(Font("SansSerif", Font.BOLD, 12))
        val scrollPane =JBScrollPane(table)
        mainPanel.add(scrollPane, BorderLayout.CENTER)
    }

    private fun storeChallenge(job: String, challenge: String?): String? {

        val json = """{"job": "$job", "challengeName": "$challenge"}"""
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = json.toRequestBody(mediaType)

        return RestClient().post(Constants.API_BASE_URL + Constants.STORE_CHALLENGE, requestBody)

    }

    private fun unshelveChallenge(job: String, challenge: String?): String? {

        val json = """{"job": "$job", "challengeName": "$challenge"}"""
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = json.toRequestBody(mediaType)

        return RestClient().post(Constants.API_BASE_URL + Constants.STORE_CHALLENGE, requestBody)

    }
}