package com.example.krutrim.data.network

import com.example.krutrim.data.local.TickerMessageDao
import com.example.krutrim.data.local.TickerMessageEntity
import com.example.krutrim.domain.models.TickerMessage
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.flow.*
import okhttp3.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class WebSocketManager(
    private val url: String,
    private val tickerMessageDao: TickerMessageDao,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    private val client = OkHttpClient()
    private var webSocket: WebSocket? = null
    private val _messages = MutableStateFlow<List<TickerMessage>>(emptyList())
    val messages: StateFlow<List<TickerMessage>> get() = _messages.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error.asStateFlow()

    private val _connectionStatus = MutableStateFlow<ConnectionStatus>(ConnectionStatus.Disconnected)
    val connectionStatus: StateFlow<ConnectionStatus> get() = _connectionStatus.asStateFlow()

    private var isConnected = false

    fun connect() {
        val request = Request.Builder().url(url).build()
        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                isConnected = true
                _connectionStatus.value = ConnectionStatus.Connected
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
                val tickerMessage = parseTickerMessage(text)
                if (tickerMessage != null) {
                    _messages.value += tickerMessage
                    coroutineScope.launch {
                        tickerMessageDao.insert(
                            TickerMessageEntity(
                                type = tickerMessage.type,
                                sequence = tickerMessage.sequence,
                                productId = tickerMessage.productId,
                                price = tickerMessage.price,
                                open24h = tickerMessage.open24h,
                                volume24h = tickerMessage.volume24h,
                                low24h = tickerMessage.low24h,
                                high24h = tickerMessage.high24h,
                                volume30d = tickerMessage.volume30d,
                                bestBid = tickerMessage.bestBid,
                                bestBidSize = tickerMessage.bestBidSize,
                                bestAsk = tickerMessage.bestAsk,
                                bestAskSize = tickerMessage.bestAskSize,
                                side = tickerMessage.side,
                                time = tickerMessage.time,
                                tradeId = tickerMessage.tradeId,
                                lastSize = tickerMessage.lastSize
                            )
                        )
                    }
                } else {
                    println("Received invalid message: $text")
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                isConnected = false
                _connectionStatus.value = ConnectionStatus.Disconnected
                _error.value = "Error: ${t.localizedMessage} - ${response?.code}"
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                isConnected = false
                _connectionStatus.value = ConnectionStatus.Disconnected
            }
        })
    }

    fun disconnect() {
        webSocket?.close(1000, "Closing WebSocket")
        webSocket = null
        isConnected = false
        _connectionStatus.value = ConnectionStatus.Disconnected
    }

    private fun parseTickerMessage(jsonString: String): TickerMessage? {
        val gson = Gson()
        return try {
            gson.fromJson(jsonString, TickerMessage::class.java)
        } catch (e: JsonSyntaxException) {
            println("Error parsing JSON: ${e.message}")
            null
        }
    }

    suspend fun loadMessagesFromRoom(): List<TickerMessageEntity> {
        return tickerMessageDao.getLatestMessages()
    }
}

sealed class ConnectionStatus {
    data object Connected : ConnectionStatus()
    data object Disconnected : ConnectionStatus()
}