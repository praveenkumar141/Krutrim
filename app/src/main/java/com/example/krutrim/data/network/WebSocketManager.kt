package com.example.krutrim.data.network

import kotlinx.coroutines.flow.*
import okhttp3.*

class WebSocketManager(private val url: String) {
    private val client = OkHttpClient()
    private var webSocket: WebSocket? = null
    private val _messages = MutableStateFlow<List<String>>(emptyList())
    val messages: StateFlow<List<String>> get() = _messages.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

    fun connect() {
        val request = Request.Builder().url(url).build()
        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                val subscribeMessage = """
                    {
                      "type": "subscribe",
                      "product_ids": ["ETH-USD", "ETH-EUR"],
                      "channels": [
                        "level2",
                        "heartbeat",
                        {
                          "name": "ticker",
                          "product_ids": ["ETH-BTC", "ETH-USD"]
                        }
                      ]
                    }
                """.trimIndent()
                webSocket.send(subscribeMessage)
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                _messages.value = _messages.value + text
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                _error.value = "Error: ${t.localizedMessage}"
            }
        })
    }

    fun disconnect() {
        webSocket?.close(1000, "Closing WebSocket")
        webSocket = null
    }
}
