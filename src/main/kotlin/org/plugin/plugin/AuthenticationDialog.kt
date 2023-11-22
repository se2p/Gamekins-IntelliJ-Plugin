package org.plugin.plugin

import com.cdancy.jenkins.rest.JenkinsClient
import com.intellij.openapi.project.Project
import java.awt.Dimension
import java.awt.GridLayout
import javax.swing.*


class AuthenticationDialog(project: Project?) : JFrame() {

    private var project: Project?

    init {
        this.project = project
        createAndShowGUI()
        size = Dimension(500, 400)
    }

    private fun createAndShowGUI() {
        setTitle("Authentication Dialog")
        setDefaultCloseOperation(EXIT_ON_CLOSE)
        if (Utility.isAuthenticated()) {
            openGamekinsDialog()
            return
        }
        val panel = JPanel(GridLayout(6, 2))
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


        val okButton = JButton("OK")
        val cancelButton = JButton("Cancel")
        okButton.addActionListener {
            val username = usernameField.getText()
            val password = String(passwordField.getPassword())
            val confirmPassword = String(confirmPasswordField.getPassword())
            val URL = URLField.getText()
            val project = projectField.getText()

            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || URL.isEmpty() || project.isEmpty()) {
                JOptionPane.showMessageDialog(
                    this@AuthenticationDialog,
                    "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE
                )
            } else {
                val token = authenticateUser(username, password, URL, project)
                if (password != confirmPassword) {
                    JOptionPane.showMessageDialog(
                        this@AuthenticationDialog,
                        "Incorrect password", "Error", JOptionPane.ERROR_MESSAGE
                    )
                } else if (token != null) {
                    Utility.saveToken(token)
                    dispose()
                    openGamekinsDialog();
                } else {
                    JOptionPane.showMessageDialog(
                        this@AuthenticationDialog,
                        "Authentication failed", "Error", JOptionPane.ERROR_MESSAGE
                    )
                }
            }
        }
        cancelButton.addActionListener {
            dispose()
        }
        panel.add(okButton)
        panel.add(cancelButton)
        contentPane.add(panel)
        pack()
        setLocationRelativeTo(null)
        isVisible = true
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


            client.authValue()

        } catch (e: Exception) {

            null
        }
    }

    private fun openGamekinsDialog() {
       GamekinsDialog(project).show()
    }
}

