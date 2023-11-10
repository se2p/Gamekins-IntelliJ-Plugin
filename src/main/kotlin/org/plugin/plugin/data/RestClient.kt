package org.plugin.plugin.data

import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull

class RestClient {
    private val client = OkHttpClient()

    fun get(url: String, queryParams: Map<String, String>): String? {
        val urlBuilder = url.toHttpUrlOrNull()?.newBuilder()

        for ((key, value) in queryParams) {
            urlBuilder?.addQueryParameter(key, value)
        }

        val request = urlBuilder?.build()?.let {
            Request.Builder()
                .url(it)
                .header("Authorization", Credentials.basic("anqawe", "*******"))
                .build()
        }

        if (request != null) {
            client.newCall(request).execute().use { response: Response ->
                if (!response.isSuccessful) {
                    throw Exception("HTTP request failed with code: ${response.code}, message: ${response.message}")
                }
                return response.body?.string() ?: throw Exception("Response body is null")
            }
        }

        return null;
    }

    fun post(url: String, requestBody: RequestBody): String? {
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .header("Authorization", Credentials.basic("anqawe", "11e6a9234b7cb67cd387ccbfc3647d67cc"))
            .build()

        client.newCall(request).execute().use { response: Response ->
            if (!response.isSuccessful) {
                throw Exception("HTTP request failed with code: ${response.code}, message: ${response.message}")
            }
            return response.body?.string() ?: throw Exception("Response body is null")
        }
    }
}
