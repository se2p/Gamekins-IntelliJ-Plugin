package org.plugin.plugin

import com.intellij.ide.DataManager
import com.intellij.notification.NotificationAction
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.ui.content.ContentFactory
import okhttp3.*
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.util.concurrent.TimeUnit
import javax.swing.SwingUtilities

class WebSocketClient {

    fun startWebSocket() {

        val request =
            Request.Builder()
            .url("ws://${Utility.getDomain()}:8443/jenkins/send")
            .header("Authorization", Utility.getAuthorizationHeader())
            .build()

        val client = OkHttpClient.Builder()
            .connectTimeout(3, TimeUnit.SECONDS)
            .build()

        val webSocketListener = object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                println("Connected to server.")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                println("Received message from server: $text")
                showNotification(text)
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                println("Closing connection: $code / $reason")
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                println("Connection failure: ${t.message}")
            }
        }

        client.newWebSocket(request, webSocketListener)
    }

    companion object {
        fun refreshWindow() {
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

    fun showNotification(message: String) {

            NotificationGroupManager.getInstance().getNotificationGroup("Custom Notification Group")
                .createNotification(
                    message,
                    NotificationType.INFORMATION
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

}
