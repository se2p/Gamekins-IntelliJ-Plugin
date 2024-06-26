package org.gamekins.ide.data

import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import org.gamekins.ide.Utility
import java.io.IOException

class RestClient private constructor() {

    private val client = OkHttpClient()

    companion object {
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
                .header("Authorization", Utility.getAuthorizationHeader())
                .build()
        }

        return request?.let {
            executeRequest(it)
        }
    }

    @Throws(IOException::class)
    fun post(url: String, requestBody: RequestBody): String {
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .header("Authorization", Utility.getAuthorizationHeader())
            .build()

        return executeRequest(request)
    }

    private fun executeRequest(request: Request): String {
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IOException("HTTP request failed with code: ${response.code}, message: ${response.message}")
            }
            return response.body?.string() ?: throw IOException("Response body is null")
        }
    }


}
