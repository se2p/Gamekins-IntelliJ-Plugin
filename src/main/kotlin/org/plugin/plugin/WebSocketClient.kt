package org.plugin.plugin

import okhttp3.*
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.util.concurrent.TimeUnit

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
                val textSplit = text.split("]")
                val userSplit = textSplit[0].removePrefix("[").split(",")
                if (userSplit.contains(Utility.lPreferences["username", ""])) {
                    Utility.showNotification(textSplit[1])
                }
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
}
