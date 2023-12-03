package org.plugin.plugin.panels

import com.cdancy.jenkins.rest.JenkinsClient
import com.cdancy.jenkins.rest.domain.job.JobInfo
import com.intellij.openapi.wm.impl.welcomeScreen.learnIde.coursesInProgress.mainBackgroundColor
import com.intellij.util.ui.JBUI
import org.plugin.plugin.Utility
import java.awt.*
import java.util.prefs.Preferences
import javax.swing.*
import javax.swing.border.EmptyBorder

class AuthenticationPanel : JPanel() {


    private val PANEL_WIDTH = 300
    private val PANEL_HEIGHT = 200

    private val preferences = Preferences.userRoot().node("org.plugin.plugin.panels")

    private var authenticationListener: AuthenticationListener? = null

    init {
        this.setBackground(mainBackgroundColor)
        createAndShowGUI()
    }

    fun createAndShowGUI() {
        if (Utility.isAuthenticated()) {
            return
        }

        val frame = JFrame("Login")
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.setSize(PANEL_WIDTH, PANEL_HEIGHT)
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
        val URLField = JTextField(15)
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
        panel.add(URLField, gbc)
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
            val URL = URLField.text
            val project = projectField.text

            if (username.isEmpty() || password.isEmpty() || URL.isEmpty() || project.isEmpty()) {
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

                val lJobInfo = authenticateUser(username, password, URL, project)

                panel.remove(progressBar)
                panel.revalidate()
                panel.repaint()

                if (lJobInfo != null) {
                    onLogin()
                } else {
                    JOptionPane.showMessageDialog(
                        frame,
                        "Authentication failed",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    )
                }

            }
        }

        gbc.gridx = 1
        gbc.gridy = 4
        gbc.gridwidth = 2
        gbc.anchor = GridBagConstraints.CENTER
        panel.add(loginButton, gbc)

        val welcomeLabel = JLabel("<html><h2>Gamekins!</h2></html>")

        mainPanel.add(welcomeLabel)
        mainPanel.add(panel)

        this.add(mainPanel)
    }

    private fun authenticateUser(
        username: String,
        password: String,
        URL: String,
        project: String
    ): JobInfo? {

        return try {

            val client = JenkinsClient.builder()
                .endPoint(URL)
                .credentials("${username}:${password}")
                .build()

            val lToken = client.api().userApi().generateNewToken("token").data().tokenValue()
            preferences.put("username", username)
            preferences.put("token", lToken)
            preferences.put("projectName", project)
            preferences.put("url", URL)

            client.api().jobsApi().jobInfo(null, project)


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


