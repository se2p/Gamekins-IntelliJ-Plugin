package org.plugin.plugin

import Challenge
import ChallengeList
import StoredChallengeList
import com.google.gson.Gson
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.table.JBTable
import com.intellij.util.ui.JBUI
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.plugin.plugin.components.IconCellRenderer
import org.plugin.plugin.data.*
import org.plugin.plugin.panels.AcceptedRejectedChallengesPanel
import org.plugin.plugin.panels.CurrentQuestsChallengesPanel
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.util.*
import java.util.prefs.Preferences
import javax.swing.*
import javax.swing.border.CompoundBorder
import javax.swing.border.LineBorder
import javax.swing.table.DefaultTableCellRenderer
import javax.swing.table.DefaultTableModel


object Utility {

     val lPreferences = Preferences.userRoot().node("org.plugin.plugin.panels")

    var lCurrentQuestsChallengesPanel: CurrentQuestsChallengesPanel? = null

    private var lAcceptedRejectedChallengesPanel: AcceptedRejectedChallengesPanel? = null

    private val gson = Gson()

    fun setCurrentQuestsChallengesPanel(parent: CurrentQuestsChallengesPanel) {
        this.lCurrentQuestsChallengesPanel = parent
    }

    fun setAcceptedRejectedChallengesPanel(parent: AcceptedRejectedChallengesPanel) {
        this.lAcceptedRejectedChallengesPanel = parent
    }

    init {
        println("Utility initialized")
    }

    fun createChallengePanel(mainPanel: JPanel) {

        val gbc = GridBagConstraints()
        gbc.insets = JBUI.insets(5, 0)
        gbc.weightx = 1.0
        gbc.weighty = 0.45
        gbc.gridx = 0
        gbc.gridy = 2

        gbc.fill = GridBagConstraints.BOTH
        gbc.gridwidth = 1

        mainPanel.add(AcceptedRejectedChallengesPanel(), gbc)
    }

     fun createRejectModal(challenge: String?) {

        val rejectModal = JDialog()
        rejectModal.setTitle("Reject Challenge")
        rejectModal.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE)
        rejectModal.setSize(300, 250)
        rejectModal.setModalityType(Dialog.ModalityType.APPLICATION_MODAL)
        rejectModal.setLocationRelativeTo(null)
        val modalContent = JPanel()
        modalContent.setLayout(BorderLayout())
        val modalHeader = JPanel()
        val modalTitle = JLabel("Reject Challenge")
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

        formPanel.add(reasonPanel, BorderLayout.NORTH)
        modalBody.add(formPanel, BorderLayout.NORTH)

        val modalFooter = JPanel()
        val closeModalButton = JButton("Close")
        val rejectButton = JButton("Reject")
        modalFooter.add(closeModalButton)
        modalFooter.add(rejectButton)
        closeModalButton.addActionListener { rejectModal.dispose() }

        rejectButton.addActionListener {
            rejectChallenge( challenge, reasonTextArea.getText()) { success, errorMessage ->
                if (success) {
                    rejectModal.dispose()
                    showMessageDialog("Reject successful!")
                    lCurrentQuestsChallengesPanel?.update()
                    lAcceptedRejectedChallengesPanel?.update()

                } else {
                    rejectModal.dispose()
                    showErrorDialog("Reject failed: $errorMessage")
                }
            }
        }

        modalContent.add(modalHeader, BorderLayout.NORTH)
        modalContent.add(modalBody, BorderLayout.CENTER)
        modalContent.add(modalFooter, BorderLayout.SOUTH)
        rejectModal.add(modalContent)
        rejectModal.isVisible = true

    }

    fun openStoredChallengesDialog(aInStoredChallenges: List<Challenge>) {

        val challengesPanel = JPanel()
        challengesPanel.setLayout(BoxLayout(challengesPanel, BoxLayout.Y_AXIS))
        val lPaddingBorder = JBUI.Borders.empty(5)

        val storedChallengesModal = JDialog()

        storedChallengesModal.setTitle("Stored Challenges")
        storedChallengesModal.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE)
        storedChallengesModal.setSize(650, 500)
        storedChallengesModal.setModalityType(Dialog.ModalityType.APPLICATION_MODAL)

        val modalContent = JPanel()
        modalContent.setLayout(BorderLayout())

        val modalHeader = JPanel()
        val modalTitle = JLabel("Stored Challenges")
        modalHeader.add(modalTitle)

        val modalBody = JPanel()
        modalBody.setLayout(BorderLayout())
        challengesPanel.border = lPaddingBorder

        for (index in aInStoredChallenges.indices) {

            val lChallengePanel = JPanel()
            lChallengePanel.setLayout(BorderLayout())
            val lineBorder = LineBorder(JBColor.GRAY, 1)
            lChallengePanel.border = CompoundBorder(lineBorder, lPaddingBorder)
            lChallengePanel.maximumSize = Dimension(Int.MAX_VALUE, 110)

            val leftPanel = JPanel(FlowLayout(FlowLayout.LEFT))
            val lChallengeHeader = JPanel(GridBagLayout())
            val lChallengeTitleLabel = JLabel(
                "<HTML><div WIDTH=" + leftPanel.width + ">" +
                        aInStoredChallenges[index].generalReason + "</div></HTML>"
            )
            lChallengeTitleLabel.alignmentX = JLabel.CENTER_ALIGNMENT

            leftPanel.addComponentListener(object : ComponentAdapter() {
                override fun componentResized(evt: ComponentEvent) {
                    lChallengeTitleLabel.setText(
                        "<HTML><div WIDTH=" + leftPanel.width + ">" +
                                aInStoredChallenges[index].generalReason + "</div></HTML>"
                    )
                }
            })

            val gbc = GridBagConstraints()
            gbc.gridx = 0
            gbc.gridy = 0
            gbc.weightx = 0.8
            gbc.fill = GridBagConstraints.HORIZONTAL

            leftPanel.add(lChallengeTitleLabel)
            lChallengeHeader.add(leftPanel, gbc)

            val lChallengeTitleName = JLabel(aInStoredChallenges[index].name)
            val lExpandButton = JButton("Expand")
            lExpandButton.toolTipText = Constants.CHALLENGE_PANEL_DESCRIPTION

            val rightPanel = JPanel(FlowLayout(FlowLayout.RIGHT))

            lChallengeTitleName.background = JBColor.yellow
            lChallengeTitleName.isOpaque = true
            rightPanel.add(lChallengeTitleName)

            val lButtonsPanel = JPanel(FlowLayout(FlowLayout.RIGHT))
            gbc.gridx = 1
            gbc.weightx = 0.2

            lChallengeHeader.add(rightPanel, gbc)

            lChallengePanel.add(lChallengeHeader, BorderLayout.PAGE_START)
            lChallengePanel.add(lButtonsPanel, BorderLayout.PAGE_END)
            challengesPanel.add(lChallengePanel)
            val unshelveButton = JButton("Unshelve")
            lButtonsPanel.add(unshelveButton)

            unshelveButton.addActionListener {
                unshelveChallenge(
                    aInStoredChallenges[index].generalReason?.replace(Regex("<[^>]++>"), "")
                ) { success, errorMessage ->
                    if (success) {
                        storedChallengesModal.dispose()
                        showMessageDialog("Unshelve successful!")
                        lCurrentQuestsChallengesPanel?.update()
                    } else {
                        storedChallengesModal.dispose()
                        showErrorDialog("Unshelve failed: $errorMessage")
                    }
                }

                lCurrentQuestsChallengesPanel?.update()
            }

            lChallengePanel.add(lChallengeHeader, BorderLayout.PAGE_START)
            lChallengePanel.add(lButtonsPanel, BorderLayout.CENTER)
            challengesPanel.add(lChallengePanel)
            modalBody.add(challengesPanel)
        }

        val modalFooter = JPanel()
        val closeModalButton = JButton("Close")
        modalFooter.add(closeModalButton)

        closeModalButton.addActionListener { storedChallengesModal.dispose() }

        modalContent.add(modalHeader, BorderLayout.NORTH)
        val lScrollPane = JBScrollPane(modalBody)
        lScrollPane.horizontalScrollBarPolicy = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        lScrollPane.verticalScrollBarPolicy = ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS
        modalContent.add(lScrollPane, BorderLayout.CENTER)
        modalContent.add(modalFooter, BorderLayout.SOUTH)

        storedChallengesModal.setLocationRelativeTo(null)
        storedChallengesModal.add(modalContent)
        storedChallengesModal.isVisible = true
    }

    fun createAndShowCompletedQuestsTable(mainPanel: JPanel) {

        val lProjectName = lPreferences.get("projectName", "")
        if (lProjectName != "") {

            val queryParams = mapOf(
                "job" to lProjectName
            )

            val response = RestClient.getInstance().get(Constants.API_BASE_URL + Constants.GET_CURRENT_QUESTS_TASKS, queryParams)
            val questsTasksList = Gson().fromJson(response, QuestsListTasks::class.java).currentQuestTasks

            val lQuestsPanel = JPanel()
            lQuestsPanel.setLayout(BoxLayout(lQuestsPanel, BoxLayout.Y_AXIS))
            lQuestsPanel.border = LineBorder(JBColor.GRAY, 2)

            for (index in questsTasksList.indices) {

                val lQuestPanel = JPanel(GridLayout(2, 1, 0, 5))

                lQuestPanel.border = BorderFactory.createCompoundBorder(
                    LineBorder(JBColor.GRAY, 0),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
                )
                lQuestPanel.maximumSize = Dimension(Int.MAX_VALUE, 70)

                val task: QuestTask = questsTasksList[0]
                val questLabel = JLabel(task.title)
                val headerPanel = JPanel(FlowLayout(FlowLayout.LEFT, 5, 0))

                val spacerLabel = JLabel("")
                spacerLabel.preferredSize = Dimension(10, 0)

                val scoreString = if (task.score > 1) "points" else "point"

                val challengeTitleScore = JLabel(task.score.toString() + "" + scoreString)
                challengeTitleScore.setForeground(JBColor.GREEN)

                val lIndex = index + 1
                val progressBarLabel = JLabel(task.completedPercentage.toString()).apply {
                    foreground = JBColor.BLUE
                    text = "$lIndex. "
                }
                headerPanel.add(progressBarLabel)

                val progressBar = JProgressBar(0, 100)
                progressBar.setValue(task.completedPercentage)
                progressBar.setStringPainted(true)

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
            gbc.weightx = 1.0
            gbc.weighty = 0.45
            gbc.gridx = 0
            gbc.gridy = 1
            gbc.fill = GridBagConstraints.BOTH
            gbc.gridwidth = GridBagConstraints.REMAINDER

            mainPanel.add(scrollPane, gbc)
        }
    }

     fun resizeImageIcon(icon: ImageIcon, width: Int, height: Int): ImageIcon {
        val img = icon.image
        val resizedImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH)
        return ImageIcon(resizedImg)
    }

     fun storeChallenge(aInChallenge: String?, aInCallback: (Boolean, String) -> Unit) {

        val lProjectName = lPreferences.get("projectName", "")
        if (lProjectName != "") {
            val json = """{"job": "$lProjectName", "challengeName": "$aInChallenge"}"""
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = json.toRequestBody(mediaType)

            val lResponse = RestClient.getInstance().post(Constants.API_BASE_URL + Constants.STORE_CHALLENGE, requestBody)

            val gson = Gson()
            val message = gson.fromJson(lResponse, Message::class.java)
            val kindValue = message.message.kind

            if (kindValue == "OK") {
                aInCallback(true, "success")
            } else {
                aInCallback(false, "failure")
            }
        }

    }

    fun restoreChallenge(challenge: String?, aInCallback: (Boolean, String) -> Unit) {

        val lProjectName = lPreferences.get("projectName", "")
        if (lProjectName != "") {
            val json = """{"job": "$lProjectName", "challengeName": "$challenge"}"""
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = json.toRequestBody(mediaType)
            val lResponse = RestClient.getInstance().post(Constants.API_BASE_URL + Constants.RESTORE_CHALLENGE, requestBody)


            val kindValue = gson.fromJson(lResponse, Message::class.java).message.kind

            if (kindValue == "OK") {
                aInCallback(true, "success")
            } else {
                aInCallback(false, "failure")
            }

        }
    }

    private fun unshelveChallenge(aInChallenge: String?, aInCallback: (Boolean, String) -> Unit) {

        val lProjectName = lPreferences.get("projectName", "")
        if (lProjectName != "") {
            val json = """{"job": "$lProjectName", "challengeName": "$aInChallenge"}"""
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = json.toRequestBody(mediaType)

            val lResponse = RestClient.getInstance().post(Constants.API_BASE_URL + Constants.UNSHELVE_CHALLENGE, requestBody)

            val kindValue = gson.fromJson(lResponse, Message::class.java).message.kind

            if (kindValue == "OK") {
                aInCallback(true, "success")
            } else {
                aInCallback(false, "failure")
            }
        }
    }

    private fun rejectChallenge(
        aInChallenge: String?,
        aInReason: String,
        aInCallback: (Boolean, String) -> Unit
    ) {
        val lProjectName = lPreferences.get("projectName", "")
        if (lProjectName != "") {
            val json = """{"job": "$lProjectName", "challengeName": "$aInChallenge", "reason": "$aInReason"}"""
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = json.toRequestBody(mediaType)

            val lResponse = RestClient.getInstance().post(Constants.API_BASE_URL + Constants.REJECT_CHALLENGE, requestBody)

            val kindValue = gson.fromJson(lResponse, Message::class.java).message.kind

            if (kindValue == "OK") {
                aInCallback(true, "success")
            } else {
                aInCallback(false, "failure")
            }
        }
    }

    private fun readToken(): String? {
        return try {
            Files.readString(Path.of(Constants.TOKEN_FILE_PATH))
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    private fun isTokenValid(token: String?): Boolean {
        return token != null && !token.isEmpty()
    }

    fun isAuthenticated(): Boolean {
        val tokenFile = File(Constants.TOKEN_FILE_PATH)
        return tokenFile.exists() && isTokenValid(readToken())
    }

    fun saveToken(aInToken: String) {
        try {
            Files.write(Path.of(Constants.TOKEN_FILE_PATH), aInToken.toByteArray(), StandardOpenOption.CREATE)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun showMessageDialog(message: String) {
        JOptionPane.showMessageDialog(null, message, "Information", JOptionPane.INFORMATION_MESSAGE)
    }

    fun showErrorDialog(errorMessage: String) {
        JOptionPane.showMessageDialog(null, errorMessage, "Error", JOptionPane.ERROR_MESSAGE)
    }

     fun getCurrentChallenges(): List<Challenge>? {

        val lProjectName = lPreferences.get("projectName", "")
        if (lProjectName != "") {

            val queryParams = mapOf(
                "job" to lProjectName
            )
            val lResponse = RestClient.getInstance().get(Constants.API_BASE_URL + Constants.GET_CURRENT_CHALLENGES, queryParams)
            return Gson().fromJson(lResponse, ChallengeList::class.java).currentChallenges

        }
        return null
    }
}