package org.plugin.plugin.data

import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import java.io.IOException
import java.util.prefs.Preferences

class RestClient private constructor() {

    private val client = OkHttpClient()
    private val preferences = Preferences.userRoot().node(PANEL_NODE)

    companion object {
        private const val PANEL_NODE = "org.plugin.plugin.panels"
        private var instance: RestClient? = null

        fun getInstance(): RestClient {
            return instance ?: synchronized(this) {
                instance ?: RestClient().also { instance = it }
            }
        }
    }

    @Throws(IOException::class)
    fun get(url: String, queryParams: Map<String, String>): String? {
        val urlBuilder = url.toHttpUrlOrNull()?.newBuilder()

        queryParams.forEach { (key, value) ->
            urlBuilder?.addQueryParameter(key, value)
        }

        val request = urlBuilder?.build()?.let {
            Request.Builder()
                .url(it)
                .header("Authorization", getAuthorizationHeader())
                .build()
        }

        return request?.let {
            executeRequest(it)
        }
    }

    @Throws(IOException::class)
    fun post(url: String, requestBody: RequestBody): String? {
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .header("Authorization", getAuthorizationHeader())
            .build()

        return executeRequest(request)
    }

    private fun executeRequest(request: Request): String? {
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IOException("HTTP request failed with code: ${response.code}, message: ${response.message}")
            }
            return response.body?.string() ?: throw IOException("Response body is null")
        }
    }

    private fun getAuthorizationHeader(): String {
        val token = preferences.get("token", "")
        val username = preferences.get("username", "")
        return Credentials.basic(username, token)
    }
}
