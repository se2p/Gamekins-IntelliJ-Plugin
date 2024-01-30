/*
 * Copyright 2023 IntelliGame contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.plugin.plugin

import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowAnchor
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import org.plugin.plugin.panels.AuthenticationPanel
import org.plugin.plugin.panels.MainPanel
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.JComponent
import javax.swing.JPanel

class MainToolWindow  : ToolWindowFactory {

    companion object {
        fun createPanel(): JComponent {

            val main = JPanel(GridBagLayout())

            if (!Utility.isAuthenticated()) {
                main.add(createAuthenticationPanel(main))
            } else {
                try {
                    main.add(MainPanel(), GridBagConstraints().apply {
                        gridx = 0
                        gridy = 0
                        weightx = 1.0
                        weighty = 1.0
                        fill = GridBagConstraints.BOTH
                    })
                } catch (exception: ExceptionInInitializerError) {
                    Utility.logout()
                    main.add(createAuthenticationPanel(main))
                    var message = "Previous authentication did not work, please login again."
                    if (!exception.message.isNullOrEmpty()) {
                        message += " More information: ${exception.message}"
                    }
                    Utility.showNotification(message, NotificationType.ERROR)
                }
            }

            return main
        }

        private fun createAuthenticationPanel(main: JPanel): AuthenticationPanel {
            val authenticationPanel = AuthenticationPanel()
            authenticationPanel.addAuthenticationListener(object : AuthenticationPanel.AuthenticationListener {

                override fun onAuthenticationResult(successful: Boolean) {
                    if (successful) {
                        main.remove(authenticationPanel)
                        main.add(MainPanel(), GridBagConstraints().apply {
                            gridx = 0
                            gridy = 0
                            weightx = 1.0
                            weighty = 1.0
                            fill = GridBagConstraints.BOTH
                        })
                        main.revalidate()
                        main.repaint()
                        Utility.startWebSocket()
                    }
                }
            })

            return authenticationPanel
        }

        fun rebuildPanel(toolWindow: ToolWindow) {

            val contentManager = toolWindow.contentManager
            contentManager.removeAllContents(true)

            val newPanel = createPanel()
            contentManager.addContent(ContentFactory.getInstance().createContent(newPanel, null, false))
        }
    }

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        toolWindow.contentManager.addContent( ContentFactory.getInstance().createContent(createPanel(), null, false))
    }

    override fun getAnchor(): ToolWindowAnchor {
        return ToolWindowAnchor.RIGHT
    }

    override fun shouldBeAvailable(project: Project) = true

}