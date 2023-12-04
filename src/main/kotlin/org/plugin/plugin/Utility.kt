package org.plugin.plugin

import Challenge
import ChallengeList
import com.google.gson.Gson
import com.intellij.openapi.wm.impl.welcomeScreen.learnIde.coursesInProgress.mainBackgroundColor
import com.intellij.ui.components.JBScrollPane
import com.intellij.util.ui.JBUI
import okhttp3.Credentials
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.plugin.plugin.data.Message
import org.plugin.plugin.data.RestClient
import org.plugin.plugin.panels.AcceptedRejectedChallengesPanel
import org.plugin.plugin.panels.CurrentQuestsChallengesPanel
import java.awt.*
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.util.prefs.Preferences
import javax.swing.*
import javax.swing.border.CompoundBorder
import javax.swing.border.LineBorder


object Utility {

    val lPreferences: Preferences = Preferences.userRoot().node("org.plugin.plugin.panels")

    var lCurrentQuestsChallengesPanel: CurrentQuestsChallengesPanel? = null

    private var lAcceptedRejectedChallengesPanel: AcceptedRejectedChallengesPanel? = null

    private val lGson = Gson()

    val lPaddingSize = 5
    val lEmptyBorder = BorderFactory.createEmptyBorder(lPaddingSize, lPaddingSize, 0, lPaddingSize)

    fun setCurrentQuestsChallengesPanel(parent: CurrentQuestsChallengesPanel) {
        this.lCurrentQuestsChallengesPanel = parent
    }

    fun setAcceptedRejectedChallengesPanel(parent: AcceptedRejectedChallengesPanel) {
        this.lAcceptedRejectedChallengesPanel = parent
    }

    init {
        println("Utility initialized")
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
            rejectChallenge(challenge, reasonTextArea.getText()) { success, errorMessage ->
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

        val lChallengesPanel = JPanel()
        lChallengesPanel.background = mainBackgroundColor
        lChallengesPanel.setLayout(BoxLayout(lChallengesPanel, BoxLayout.Y_AXIS))
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
        lChallengesPanel.border = lPaddingBorder

        for (index in aInStoredChallenges.indices) {

            val lChallengePanel = JPanel()
            lChallengePanel.setLayout(BorderLayout())
            val lineBorder = LineBorder(Color.GRAY, 1)
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
            lChallengeTitleName.foreground = Color.yellow
            rightPanel.add(lChallengeTitleName)

            val lButtonsPanel = JPanel(FlowLayout(FlowLayout.RIGHT))
            gbc.gridx = 1
            gbc.weightx = 0.2

            lChallengeHeader.add(rightPanel, gbc)

            lChallengePanel.add(lChallengeHeader, BorderLayout.PAGE_START)
            lChallengePanel.add(lButtonsPanel, BorderLayout.PAGE_END)
            lChallengesPanel.add(lChallengePanel)
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
            lChallengesPanel.add(lChallengePanel)
            modalBody.add(lChallengesPanel)
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

            val lResponse =
                RestClient.getInstance().post(getBaseUrl() + Constants.STORE_CHALLENGE, requestBody)

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
            val lResponse =
                RestClient.getInstance().post(getBaseUrl() + Constants.RESTORE_CHALLENGE, requestBody)

            val kindValue = lGson.fromJson(lResponse, Message::class.java).message.kind

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

            val lResponse =
                RestClient.getInstance().post(getBaseUrl()+ Constants.UNSHELVE_CHALLENGE, requestBody)

            val kindValue = lGson.fromJson(lResponse, Message::class.java).message.kind

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

            val lResponse =
                RestClient.getInstance().post(getBaseUrl() + Constants.REJECT_CHALLENGE, requestBody)

            val kindValue = lGson.fromJson(lResponse, Message::class.java).message.kind

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
        return lPreferences.get("token", "") != ""
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
            val lResponse =
                RestClient.getInstance().get(getBaseUrl() + Constants.GET_CURRENT_CHALLENGES, queryParams)
            return Gson().fromJson(lResponse, ChallengeList::class.java).currentChallenges

        }
        return null
    }

    fun getBaseUrl(): String {
        val lURL = lPreferences.get("url", "http://localhost:8080/jenkins")
        return "$lURL/gamekins";
    }

    fun getAuthorizationHeader(): String {
        val token = lPreferences.get("token", "")
        val username = lPreferences.get("username", "")
        return Credentials.basic(username, token)
    }

    fun logout() {
        lPreferences.remove("token")
        lPreferences.remove("username")
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
}