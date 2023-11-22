package org.plugin.plugin.panels

import com.cdancy.jenkins.rest.JenkinsClient
import org.plugin.plugin.Utility
import java.awt.Dimension
import java.awt.GridLayout
import java.util.prefs.Preferences
import javax.swing.*


class AuthenticationPanel() : JPanel() {

    private val preferences = Preferences.userRoot().node("org.plugin.plugin.panels")

    private var authenticationListener: AuthenticationListener? = null
    init {
        createAndShowGUI()
        size = Dimension(600, 500)
    }

    private fun createAndShowGUI() {
        if (Utility.isAuthenticated()) {
            return
        }

        val panel = JPanel(GridLayout(6, 2, 10,5))
        val usernameField = JTextField()
        val passwordField = JPasswordField()
        val confirmPasswordField = JPasswordField()
        val URLField = JTextField()
        val projectField = JTextField()
        panel.add(JLabel("Username:"))
        panel.add(usernameField)
        panel.add(JLabel("Password:"))
        panel.add(passwordField)
        panel.add(JLabel("Confirm Password:"))
        panel.add(confirmPasswordField)
        panel.add(JLabel("URL:"))
        panel.add(URLField)
        panel.add(JLabel("Project:"))
        panel.add(projectField)


        val okButton = JButton("Login")
        val cancelButton = JButton("Cancel")
        okButton.addActionListener {
            val username = usernameField.getText()
            val password = String(passwordField.getPassword())
            val confirmPassword = String(confirmPasswordField.getPassword())
            val URL = URLField.getText()
            val project = projectField.getText()

            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || URL.isEmpty() || project.isEmpty()) {
                JOptionPane.showMessageDialog(
                    this@AuthenticationPanel,
                    "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE
                )
            } else {
                val token = authenticateUser(username, password, URL, project)
                if (password != confirmPassword) {
                    JOptionPane.showMessageDialog(
                        this@AuthenticationPanel,
                        "Incorrect password", "Error", JOptionPane.ERROR_MESSAGE
                    )
                } else if (token != null) {
                    Utility.saveToken(token)
                    onLogin()
                } else {
                    JOptionPane.showMessageDialog(
                        this@AuthenticationPanel,
                        "Authentication failed", "Error", JOptionPane.ERROR_MESSAGE
                    )
                }
            }
        }

        panel.add(okButton)
        this.add(panel)
    }

    private fun authenticateUser(
        username: String,
        password: String,
        URL: String,
        project: String
    ): String? {

        return try {
            val client = JenkinsClient.builder()
                .endPoint(URL)
                .credentials("${username}:${password}")
                .build()

            client.api().jobsApi().jobInfo(null, project)

            val lToken = client.api().userApi().generateNewToken("token").data().tokenValue()
            preferences.put("username", username)
            preferences.put("token", lToken)

            client.authValue();


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


