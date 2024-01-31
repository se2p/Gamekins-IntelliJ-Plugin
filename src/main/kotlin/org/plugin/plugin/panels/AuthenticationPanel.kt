package org.plugin.plugin.panels

import com.cdancy.jenkins.rest.JenkinsClient
import com.cdancy.jenkins.rest.domain.job.JobInfo
import com.intellij.notification.NotificationType
import com.intellij.openapi.wm.impl.welcomeScreen.learnIde.coursesInProgress.mainBackgroundColor
import com.intellij.util.ui.JBUI
import org.plugin.plugin.Utility
import java.awt.*
import javax.swing.*

class AuthenticationPanel : JPanel() {

    private var authenticationListener: AuthenticationListener? = null

    init {
        this.background = mainBackgroundColor
        createAndShowGUI()
    }

    private fun createAndShowGUI() {
        if (Utility.isAuthenticated()) {
            return
        }

        val frame = JFrame("Login")
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.setSize(300, 200)
        frame.contentPane.layout = BorderLayout()

        val mainPanel = JPanel()
        mainPanel.layout = BoxLayout(mainPanel, BoxLayout.Y_AXIS)

        val panel = JPanel(GridBagLayout())
        val gbc = GridBagConstraints()
        gbc.gridx = 0
        gbc.gridy = 0
        gbc.insets = JBUI.insets(5)

        val usernameField = JTextField(15)
        val passwordField = JPasswordField(15)
        val urlField = JTextField(15)
        val projectField = JTextField(15)

        panel.add(JLabel("Username:"), gbc)
        gbc.gridx = 1
        panel.add(usernameField, gbc)
        gbc.gridx = 0
        gbc.gridy = 1
        panel.add(JLabel("Password:"), gbc)
        gbc.gridx = 1
        panel.add(passwordField, gbc)
        gbc.gridx = 0
        gbc.gridy = 2
        panel.add(JLabel("URL:"), gbc)
        gbc.gridx = 1
        panel.add(urlField, gbc)
        gbc.gridx = 0
        gbc.gridy = 3
        panel.add(JLabel("Project:"), gbc)
        gbc.gridx = 1
        panel.add(projectField, gbc)

        val loginButton = JButton("Login")
        val progressBar = JProgressBar()
        progressBar.preferredSize = Dimension(80, 30)
        progressBar.isIndeterminate = true

        loginButton.addActionListener {
            val username = usernameField.text
            val password = String(passwordField.password)
            val url = urlField.text
            val project = projectField.text

            if (username.isEmpty() || password.isEmpty() || url.isEmpty() || project.isEmpty()) {
                JOptionPane.showMessageDialog(
                    frame,
                    "Please fill in all fields",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                )
            } else {

                panel.add(progressBar, gbc)
                panel.revalidate()
                panel.repaint()

                val jobInfo = authenticateUser(username, password, url, project)

                panel.remove(progressBar)
                panel.revalidate()
                panel.repaint()

                if (jobInfo != null) {
                    onLogin()
                } else {
                    Utility.showNotification("Authentication failed.", NotificationType.ERROR)
                }

            }
        }

        gbc.gridx = 1
        gbc.gridy = 4
        gbc.gridwidth = 2
        gbc.anchor = GridBagConstraints.CENTER
        panel.add(loginButton, gbc)

        val welcomeLabel = JLabel("<html><h2>Welcome to Gamekins</h2></html>")

        mainPanel.add(welcomeLabel)
        mainPanel.add(panel)

        this.add(mainPanel)
    }

    private fun authenticateUser(
        username: String,
        password: String,
        url: String,
        project: String
    ): JobInfo? {

        return try {

            val client = JenkinsClient.builder()
                .endPoint(url)
                .credentials("${username}:${password}")
                .build()

            val token = client.api().userApi().generateNewToken("token").data().tokenValue()
            val preferences = Utility.preferences
            preferences.put("username", username)
            preferences.put("token", token)
            preferences.put("projectName", project)
            preferences.put("url", url)

            var folderPath: String? = null
            var projectName = project
            if (project.contains("/")) {
                projectName = project.substringAfterLast("/")
                folderPath = project.substringBeforeLast("/")
            }

            client.api().jobsApi().jobInfo(folderPath, projectName)


        } catch (e: Exception) {
            null
        }
    }

    private fun onLogin() {
        authenticationListener?.onAuthenticationResult(true)
    }

    fun addAuthenticationListener(listener: AuthenticationListener) {
        authenticationListener = listener
    }

    interface AuthenticationListener {
        fun onAuthenticationResult(successful: Boolean)
    }

}


