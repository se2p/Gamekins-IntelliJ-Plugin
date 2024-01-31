package org.plugin.plugin

import Challenge
import ChallengeList
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.intellij.ide.DataManager
import com.intellij.notification.NotificationAction
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.openapi.wm.impl.welcomeScreen.learnIde.coursesInProgress.mainBackgroundColor
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.content.ContentFactory
import okhttp3.Credentials
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.plugin.plugin.data.Message
import org.plugin.plugin.data.RestClient
import org.plugin.plugin.panels.AcceptedRejectedChallengesPanel
import org.plugin.plugin.panels.ChallengesPanel
import java.awt.*
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.util.prefs.Preferences
import javax.swing.*

object Utility {

    val preferences: Preferences = Preferences.userRoot().node("org.plugin.plugin.panels")

    var challengesPanel: ChallengesPanel? = null

    private var acceptedRejectedChallengesPanel: AcceptedRejectedChallengesPanel? = null

    private val gson = Gson()

    var project: Project? = null

    fun setAcceptedRejectedChallengesPanel(parent: AcceptedRejectedChallengesPanel) {
        this.acceptedRejectedChallengesPanel = parent
    }

    fun setCurrentProject(project: Project) {
        this.project = project
    }

    fun createRejectModal(challenge: String?, challengesPanel: ChallengesPanel) {

        val rejectModal = JDialog()
        rejectModal.title = "Reject Challenge"
        rejectModal.defaultCloseOperation = JDialog.DISPOSE_ON_CLOSE
        rejectModal.setSize(300, 250)
        rejectModal.modalityType = Dialog.ModalityType.APPLICATION_MODAL
        rejectModal.setLocationRelativeTo(null)
        val modalContent = JPanel()
        modalContent.layout = BorderLayout()
        val modalHeader = JPanel()
        val modalTitle = JLabel("Reject Challenge")
        modalHeader.add(modalTitle)
        val modalBody = JPanel()
        modalBody.layout = BorderLayout()
        val formPanel = JPanel()
        formPanel.layout = BorderLayout()
        val reasonPanel = JPanel()
        reasonPanel.layout = BorderLayout()
        val reasonLabel = JLabel("Reason:")
        val reasonTextArea = JTextArea(5, 20)
        reasonTextArea.lineWrap = true
        reasonTextArea.wrapStyleWord = true
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
            rejectChallenge(challenge, reasonTextArea.text) { success, errorMessage ->
                if (success) {
                    rejectModal.dispose()
                    showMessageDialog("Reject successful!")
                    challengesPanel.removeAll()
                    challengesPanel.initializePanel()

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

    fun openStoredChallengesDialog(aInStoredChallenges: List<Challenge>, challengesPanel: ChallengesPanel) {

        val storedChallengesModal = JDialog()

        storedChallengesModal.title = "Stored Challenges"
        storedChallengesModal.defaultCloseOperation = JDialog.DISPOSE_ON_CLOSE
        storedChallengesModal.setSize(700, 500)
        storedChallengesModal.modalityType = Dialog.ModalityType.APPLICATION_MODAL

        val modalContent = JPanel()
        modalContent.layout = BorderLayout()

        val modalBody = JPanel()
        modalBody.layout = BoxLayout(modalBody, BoxLayout.Y_AXIS)
        modalBody.background = mainBackgroundColor

        for (index in aInStoredChallenges.indices) {

            val challenge = aInStoredChallenges[index]
            val challengePanel = JPanel()
            challengePanel.background = mainBackgroundColor
            challengePanel.layout = BorderLayout()
            challengePanel.maximumSize = Dimension(Int.MAX_VALUE, 100)

            val leftPanel = JPanel(FlowLayout(FlowLayout.LEFT))
            val challengeHeader = JPanel(GridBagLayout())
            challengeHeader.background = mainBackgroundColor
            leftPanel.background = mainBackgroundColor
            val challengeTitleLabel = JLabel(
                "<HTML><div WIDTH=550>" + challenge.generalReason + "</div></HTML>"
            )

            val gbc = GridBagConstraints()
            gbc.gridx = 0
            gbc.gridy = 0
            gbc.weightx = 0.6
            gbc.fill = GridBagConstraints.BOTH

            leftPanel.add(challengeTitleLabel)
            challengeHeader.add(leftPanel, gbc)


            val challengeTitleName =
                JLabel("<html><div style='padding: 3px;'>${challenge.name}</div></html>").apply {
                    isOpaque = true
                    background = Color.decode("#ffc107")
                    foreground = Color.decode("#212529")
                    font = font.deriveFont(Font.BOLD, 12f)
                    horizontalAlignment = SwingConstants.CENTER
                    verticalAlignment = SwingConstants.CENTER
                }
            val expandButton = JButton("Expand")
            expandButton.toolTipText = Constants.CHALLENGE_PANEL_DESCRIPTION

            val rightPanel = JPanel().apply {
                border = BorderFactory.createEmptyBorder(10, 0, 0, 10)
                layout = BoxLayout(this, BoxLayout.X_AXIS)
                background = mainBackgroundColor
                add(challengeTitleName)
            }

            val buttonsPanel = JPanel(FlowLayout(FlowLayout.RIGHT))
            buttonsPanel.background = mainBackgroundColor
            gbc.gridx = 1
            gbc.weightx = 0.4

            challengeHeader.add(rightPanel, gbc)

            val unshelveButton = JButton("Unshelve")
            unshelveButton.background = mainBackgroundColor
            unshelveButton.foreground = JBColor.RED
            unshelveButton.isContentAreaFilled = false
            unshelveButton.isOpaque = true
            unshelveButton.font = Font("Arial", Font.BOLD, 13)
            buttonsPanel.add(unshelveButton)

            unshelveButton.addActionListener {
                unshelveChallenge(
                    challenge.generalReason?.replace(Regex("<[^>]++>"), "")
                ) { success, errorMessage ->
                    if (success) {
                        storedChallengesModal.dispose()
                        showMessageDialog("Unshelve successful!")
                        challengesPanel.removeAll()
                        challengesPanel.initializePanel()
                    } else {
                        storedChallengesModal.dispose()
                        showErrorDialog("Unshelve failed: $errorMessage")
                    }
                }
            }


            challengePanel.add(challengeHeader, BorderLayout.PAGE_START)
            challengePanel.add(buttonsPanel, BorderLayout.PAGE_END)
            modalBody.add(challengePanel)
        }

        val modalFooter = JPanel()
        val closeModalButton = JButton("Close")
        modalFooter.add(closeModalButton)

        closeModalButton.addActionListener { storedChallengesModal.dispose() }

        val scrollPane = JBScrollPane(modalBody)
        scrollPane.horizontalScrollBarPolicy = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
        scrollPane.verticalScrollBarPolicy = ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS
        modalContent.add(scrollPane, BorderLayout.CENTER)
        modalContent.add(modalFooter, BorderLayout.SOUTH)

        storedChallengesModal.setLocationRelativeTo(null)
        storedChallengesModal.add(modalContent)
        storedChallengesModal.isVisible = true
    }

    fun resizeImageIcon(icon: ImageIcon, width: Int, height: Int): ImageIcon {
        val img = icon.image
        val resizedImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH)
        return ImageIcon(resizedImg)
    }

    fun storeChallenge(aInChallenge: String?, aInCallback: (Boolean, String) -> Unit) {

        val projectName = preferences["projectName", ""]
        if (projectName != "") {
            val json = """{"job": "$projectName", "challengeName": "$aInChallenge"}"""
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = json.toRequestBody(mediaType)

            val response =
                RestClient.getInstance().post(getBaseUrl() + Constants.STORE_CHALLENGE, requestBody)

            val gson = Gson()
            val message = gson.fromJson(response, Message::class.java)
            val kindValue = message.message.kind

            if (kindValue == "OK") {
                aInCallback(true, "success")
            } else {
                aInCallback(false, "failure")
            }
        }

    }

    fun restoreChallenge(challenge: String?, aInCallback: (Boolean, String) -> Unit) {

        val projectName = preferences["projectName", ""]
        if (projectName != "") {
            val json = """{"job": "$projectName", "challengeName": "$challenge"}"""
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = json.toRequestBody(mediaType)
            val response =
                RestClient.getInstance().post(getBaseUrl() + Constants.RESTORE_CHALLENGE, requestBody)

            val kindValue = gson.fromJson(response, Message::class.java).message.kind

            if (kindValue == "OK") {
                aInCallback(true, "success")
            } else {
                aInCallback(false, "failure")
            }

        }
    }

    private fun unshelveChallenge(aInChallenge: String?, aInCallback: (Boolean, String) -> Unit) {

        val projectName = preferences["projectName", ""]
        if (projectName != "") {
            val json = """{"job": "$projectName", "challengeName": "$aInChallenge"}"""
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = json.toRequestBody(mediaType)

            val response =
                RestClient.getInstance().post(getBaseUrl() + Constants.UNSHELVE_CHALLENGE, requestBody)

            val kindValue = gson.fromJson(response, Message::class.java).message.kind

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
        val projectName = preferences["projectName", ""]
        if (projectName != "") {
            val json = """{"job": "$projectName", "challengeName": "$aInChallenge", "reason": "$aInReason"}"""
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = json.toRequestBody(mediaType)

            val response =
                RestClient.getInstance().post(getBaseUrl() + Constants.REJECT_CHALLENGE, requestBody)

            val kindValue = gson.fromJson(response, Message::class.java).message.kind

            if (kindValue == "OK") {
                aInCallback(true, "success")
            } else {
                aInCallback(false, "failure")
            }
        }
    }

    fun isAuthenticated(): Boolean {
        return preferences["token", ""] != ""
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
        if (isAuthenticated()) {
            val projectName = preferences["projectName", ""]
            if (projectName != "") {

                val queryParams = mapOf(
                    "job" to projectName
                )
                val response =
                    RestClient.getInstance().get(getBaseUrl() + Constants.GET_CURRENT_CHALLENGES, queryParams)
                return Gson().fromJson(response, ChallengeList::class.java).currentChallenges
            }
        }
        return null
    }

    fun getBaseUrl(): String {
        val url = preferences["url", "http://localhost:8080/jenkins"].removeSuffix("/")
        return "$url/gamekins"
    }

    fun getDomain(): String {

        val baseUrl = getBaseUrl()
        val startIndex = baseUrl.indexOf("//") + 2
        var endIndex = baseUrl.indexOf(':', startIndex)

        if (endIndex == -1) {
            endIndex = baseUrl.indexOf('/', startIndex)
        }

        return baseUrl.substring(startIndex, if (endIndex != -1) endIndex else baseUrl.length)
    }

    fun getAuthorizationHeader(): String {
        val token = preferences["token", ""]
        val username = preferences["username", ""]
        return Credentials.basic(username, token)
    }

    fun logout() {
        preferences.remove("token")
        preferences.remove("username")
        RestClient
    }

    fun startWebSocket() {
        if (isAuthenticated()) {
            try {
                RestClient.getInstance().post(
                    getBaseUrl()
                            + Constants.START_SOCKET, ByteArray(0).toRequestBody(null, 0, 0)
                )
                val client = WebSocketClient()
                client.startWebSocket()
            } catch (e: Exception) {
                RestClient.getInstance().post(
                    getBaseUrl()
                            + Constants.START_SOCKET, ByteArray(0).toRequestBody(null, 0, 0)
                )
                val client = WebSocketClient()
                client.startWebSocket()
            }
        }
    }

    fun getStoredChallengesLimit(): Int? {

        val projectName = preferences["projectName", ""]
        if (projectName != "") {
            val queryParams = mapOf(
                "job" to projectName
            )
            val response =
                RestClient.getInstance().get(getBaseUrl() + Constants.STORE_CHALLENGE_LIMIT, queryParams)
            val jsonObject: JsonObject = JsonParser.parseString(response.toString()).asJsonObject

            return jsonObject["limit"].asInt
        }
        return null
    }

    fun showNotification(message: String, type: NotificationType = NotificationType.INFORMATION) {

        NotificationGroupManager.getInstance().getNotificationGroup("Custom Notification Group")
            .createNotification(
                message,
                type
            )
            .addAction(
                NotificationAction.createSimple("Show more information"
                ) {
                    val myProject = DataManager.getInstance().dataContextFromFocus.resultSync
                        .getData(PlatformDataKeys.PROJECT)
                    val toolWindow = ToolWindowManager.getInstance(myProject!!).getToolWindow("Gamekins")!!
                    refreshWindow()
                    toolWindow.show()
                }
            )
            .notify(null)
    }

    private fun refreshWindow() {
        val project = DataManager.getInstance().dataContextFromFocus.resultSync
            .getData(PlatformDataKeys.PROJECT)
        val toolWindow = ToolWindowManager.getInstance(project!!).getToolWindow("Gamekins")!!
        SwingUtilities.invokeLater {
            toolWindow.contentManager.removeAllContents(true)
            val content = ContentFactory.getInstance()
                .createContent(MainToolWindow.createPanel(), null, false)
            toolWindow.contentManager.addContent(content)
        }
    }
}