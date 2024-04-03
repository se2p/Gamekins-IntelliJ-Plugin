package org.gamekins.ide

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
import org.gamekins.ide.data.*
import org.gamekins.ide.panels.AcceptedRejectedChallengesPanel
import org.gamekins.ide.panels.ChallengesPanel
import java.awt.*
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.util.prefs.Preferences
import javax.swing.*

object Utility {

    val preferences: Preferences = Preferences.userRoot().node("org.gamekins.ide")

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

    fun openStoredChallengesDialog(storedChallenges: List<Challenge>, challengesPanel: ChallengesPanel) {

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

        for (index in storedChallenges.indices) {

            val challenge = storedChallenges[index]
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
            unshelveButton.foreground = JBColor.DARK_GRAY
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

            if (canSend()) {
                val sendButton = JButton("Send to:")
                sendButton.background = mainBackgroundColor
                sendButton.foreground = JBColor.DARK_GRAY
                sendButton.isContentAreaFilled = false
                sendButton.isOpaque = true
                sendButton.font = Font("Arial", Font.BOLD, 13)
                buttonsPanel.add(sendButton)

                sendButton.addActionListener {
                    val sendToModal = JDialog()

                    sendToModal.title = "Send to:"
                    sendToModal.defaultCloseOperation = JDialog.DISPOSE_ON_CLOSE
                    sendToModal.setSize(200, 500)
                    sendToModal.modalityType = Dialog.ModalityType.APPLICATION_MODAL

                    val body = JPanel()
                    body.layout = BoxLayout(body, BoxLayout.Y_AXIS)
                    body.background = mainBackgroundColor

                    val queryParams = mapOf(
                        "job" to preferences["projectName", ""],
                        "username" to preferences["username", ""]
                    )

                    val response = RestClient.getInstance().get(
                        getBaseUrl() + Constants.GET_USER_FOR_SENDING, queryParams)
                    val userList = Gson().fromJson(response, UserList::class.java).users

                    userList.forEach {user ->
                        val button = JButton(user.userName)
                        button.background = mainBackgroundColor
                        button.foreground = JBColor.DARK_GRAY
                        button.isContentAreaFilled = false
                        button.isOpaque = true
                        button.font = Font("Arial", Font.BOLD, 13)
                        body.add(button)

                        button.addActionListener {
                            val json = """{"job": "${preferences["projectName", ""]}", "challengeName": "${challenge.generalReason!!.replace(Regex("<[^>]++>"), "")}", "username": "${preferences["username", ""]}", "sendTo": "${user.userName}"}"""
                            val mediaType = "application/json; charset=utf-8".toMediaType()
                            val requestBody = json.toRequestBody(mediaType)

                            val sendResponse = RestClient.getInstance().post(
                                getBaseUrl() + Constants.SEND_CHALLENGE, requestBody)
                            val message = gson.fromJson(sendResponse, Message::class.java)

                            if (message.message.kind == "OK") {
                                showMessageDialog("Sending successful!")
                                challengesPanel.removeAll()
                                challengesPanel.initializePanel()
                            } else {
                                showErrorDialog("Sending failed: ${message.message.message}")
                            }
                        }
                    }

                    sendToModal.setLocationRelativeTo(null)
                    sendToModal.add(body)
                    sendToModal.isVisible = true
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

    fun storeChallenge(challenge: String?, callback: (Boolean, String) -> Unit) {

        val projectName = preferences["projectName", ""]
        if (projectName != "") {
            val json = """{"job": "$projectName", "challengeName": "$challenge"}"""
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = json.toRequestBody(mediaType)

            val response =
                RestClient.getInstance().post(getBaseUrl() + Constants.STORE_CHALLENGE, requestBody)

            val gson = Gson()
            val message = gson.fromJson(response, Message::class.java)
            val kindValue = message.message.kind

            if (kindValue == "OK") {
                callback(true, "success")
            } else {
                callback(false, "failure")
            }
        }

    }

    fun restoreChallenge(challenge: String?, callback: (Boolean, String) -> Unit) {

        val projectName = preferences["projectName", ""]
        if (projectName != "") {
            val json = """{"job": "$projectName", "challengeName": "$challenge"}"""
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = json.toRequestBody(mediaType)
            val response =
                RestClient.getInstance().post(getBaseUrl() + Constants.RESTORE_CHALLENGE, requestBody)

            val kindValue = gson.fromJson(response, Message::class.java).message.kind

            if (kindValue == "OK") {
                callback(true, "success")
            } else {
                callback(false, "failure")
            }

        }
    }

    private fun unshelveChallenge(challenge: String?, callback: (Boolean, String) -> Unit) {

        val projectName = preferences["projectName", ""]
        if (projectName != "") {
            val json = """{"job": "$projectName", "challengeName": "$challenge"}"""
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = json.toRequestBody(mediaType)

            val response =
                RestClient.getInstance().post(getBaseUrl() + Constants.UNSHELVE_CHALLENGE, requestBody)

            val kindValue = gson.fromJson(response, Message::class.java).message.kind

            if (kindValue == "OK") {
                callback(true, "success")
            } else {
                callback(false, "failure")
            }
        }
    }

    private fun canSend(): Boolean {
        val projectName = preferences["projectName", ""]
        if (projectName != "") {
            val queryParams = mapOf(
                    "job" to projectName
                )
            val response =
                RestClient.getInstance().get(getBaseUrl() + Constants.GET_CAN_SEND, queryParams)
            val jsonObject: JsonObject = JsonParser.parseString(response.toString()).asJsonObject

            return jsonObject["canSend"].asBoolean
        }

        return false
    }

    private fun rejectChallenge(
        challenge: String?,
        reason: String,
        callback: (Boolean, String) -> Unit
    ) {
        val projectName = preferences["projectName", ""]
        if (projectName != "") {
            val json = """{"job": "$projectName", "challengeName": "$challenge", "reason": "$reason"}"""
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = json.toRequestBody(mediaType)

            val response =
                RestClient.getInstance().post(getBaseUrl() + Constants.REJECT_CHALLENGE, requestBody)

            val kindValue = gson.fromJson(response, Message::class.java).message.kind

            if (kindValue == "OK") {
                callback(true, "success")
            } else {
                callback(false, "failure")
            }
        }
    }

    fun isAuthenticated(): Boolean {
        return preferences["token", ""] != ""
    }

    fun saveToken(token: String) {
        try {
            Files.write(Path.of(Constants.TOKEN_FILE_PATH), token.toByteArray(), StandardOpenOption.CREATE)
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
                .createContent(MainToolWindow().createPanel(), null, false)
            toolWindow.contentManager.addContent(content)
        }
    }
}