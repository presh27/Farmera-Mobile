package com.example.network

import com.example.model.ApiCart
import com.squareup.moshi.Moshi
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.BufferedReader
import java.io.InputStreamReader

class CartSseManager(
    private val okHttpClient: OkHttpClient = ApiClient.sseHttpClient,
    private val moshi: Moshi = ApiClient.moshi,
    private val onCartUpdated: (ApiCart) -> Unit
) {
    private var sseJob: Job? = null
    private var currentToken: String? = null
    private val cartAdapter = moshi.adapter(ApiCart::class.java)

    /**
     * Starts listening for real-time cart update events for the authenticated session.
     * Enforces exactly one active listener per session.
     */
    fun startListening(coroutineScope: CoroutineScope, token: String) {
        if (token.isBlank()) return
        if (sseJob?.isActive == true && currentToken == token) {
            return
        }

        stopListening()
        currentToken = token

        sseJob = coroutineScope.launch(Dispatchers.IO) {
            var retryDelayMs = 2000L
            while (isActive) {
                var response: Response? = null
                try {
                    val url = "${ApiConfig.BASE_URL.trimEnd('/')}/${ApiConfig.CART_EVENTS}?token=$token"
                    val request = Request.Builder()
                        .url(url)
                        .header("Authorization", "Bearer $token")
                        .header("Accept", "text/event-stream")
                        .header("Cache-Control", "no-cache")
                        .build()

                    response = okHttpClient.newCall(request).execute()
                    if (!response.isSuccessful) {
                        if (response.code == 401) {
                            // Unauthorized - stop listener without looping
                            break
                        }
                        delay(retryDelayMs)
                        retryDelayMs = (retryDelayMs * 2).coerceAtMost(30000L)
                        continue
                    }

                    // Reset retry backoff upon successful connection
                    retryDelayMs = 2000L

                    val body = response.body
                    if (body != null) {
                        val inputStream = body.byteStream()
                        val reader = BufferedReader(InputStreamReader(inputStream, Charsets.UTF_8))

                        var currentEvent: String? = null
                        val dataBuffer = StringBuilder()

                        while (isActive) {
                            val line = reader.readLine() ?: break // null indicates stream closed
                            if (line.isEmpty()) {
                                // End of SSE message block
                                if (currentEvent == "cart-updated" && dataBuffer.isNotEmpty()) {
                                    val json = dataBuffer.toString().trim()
                                    try {
                                        val apiCart = cartAdapter.fromJson(json)
                                        if (apiCart != null) {
                                            onCartUpdated(apiCart)
                                        }
                                    } catch (_: Exception) {
                                        // Ignore parse errors on malformed payloads
                                    }
                                }
                                currentEvent = null
                                dataBuffer.setLength(0)
                            } else if (line.startsWith("event:")) {
                                currentEvent = line.substring(6).trim()
                            } else if (line.startsWith("data:")) {
                                if (dataBuffer.isNotEmpty()) {
                                    dataBuffer.append("\n")
                                }
                                dataBuffer.append(line.substring(5).trim())
                            }
                        }
                    }
                } catch (_: CancellationException) {
                    break
                } catch (_: Exception) {
                    // Temporary network interruption - reconnect after backoff
                } finally {
                    try {
                        response?.close()
                    } catch (_: Exception) {}
                }

                if (isActive) {
                    delay(retryDelayMs)
                    retryDelayMs = (retryDelayMs * 2).coerceAtMost(30000L)
                }
            }
        }
    }

    /**
     * Closes the active Server-Sent Events stream and cancels reconnection attempts.
     */
    fun stopListening() {
        sseJob?.cancel()
        sseJob = null
        currentToken = null
    }

    fun isListening(): Boolean = sseJob?.isActive == true
}
