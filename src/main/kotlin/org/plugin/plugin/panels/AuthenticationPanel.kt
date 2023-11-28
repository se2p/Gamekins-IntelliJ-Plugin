package org.plugin.plugin.panels

import com.cdancy.jenkins.rest.JenkinsClient
import com.cdancy.jenkins.rest.domain.job.Job
import com.cdancy.jenkins.rest.domain.job.JobInfo
import org.plugin.plugin.Utility
import java.awt.Dimension
import java.awt.GridLayout
import java.util.Objects
import java.util.prefs.Preferences
import javax.swing.*
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType


class AuthenticationPanel() : JPanel() {

    private val preferences = Preferences.userRoot().node("org.plugin.plugin.panels")

    private var authenticationListener: AuthenticationListener? = null
    init {
        createAndShowGUI()
        size = Dimension(800, 600)
    }

    private fun createAndShowGUI() {
        if (Utility.isAuthenticated()) {
            return
        }

        val panel = JPanel(GridLayout(5, 2, 15,5))
        val usernameField = JTextField()
        val passwordField = JPasswordField()
        val URLField = JTextField()
        val projectField = JTextField()
        panel.add(JLabel("Username:"))
        panel.add(usernameField)
        panel.add(JLabel("Password:"))
        panel.add(passwordField)
        panel.add(JLabel("URL:"))
        panel.add(URLField)
        panel.add(JLabel("Project:"))
        panel.add(projectField)


        val okButton = JButton("Login")
        okButton.addActionListener {
            val username = usernameField.getText()
            val password = String(passwordField.getPassword())
            val URL = URLField.getText()
            val project = projectField.getText()


            if (username.isEmpty() || password.isEmpty()  || URL.isEmpty() || project.isEmpty()) {
                JOptionPane.showMessageDialog(
                    this@AuthenticationPanel,
                    "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE
                )
            } else {
                val lJobInfo = authenticateUser(username, password, URL, project)

                 if (lJobInfo != null) {
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
    ): JobInfo? {

        return try {
            val client = JenkinsClient.builder()
                .endPoint(URL)
                .credentials("${username}:${password}")
                .build()


            val lToken = client.api().userApi().generateNewToken("token").data().tokenValue()
            preferences.put("username", username)
            preferences.put("token", lToken)

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


