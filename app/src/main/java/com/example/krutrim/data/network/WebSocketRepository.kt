package com.example.krutrim.data.network

import com.example.krutrim.domain.models.TickerMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WebSocketRepository(private val webSocketManager: WebSocketManager) {
    private val _messages = MutableStateFlow<List<TickerMessage>>(emptyList())
    val messages: StateFlow<List<TickerMessage>> get() = _messages.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error.asStateFlow()

    private var isWebSocketConnected = false

    init {
        CoroutineScope(Dispatchers.IO).launch {
            webSocketManager.messages.collect { messages ->
                if (isWebSocketConnected) {
                    _messages.value = messages
                }
            }
        }

        // Loading initial messages from Room
        loadMessagesFromRoom()
    }

    private fun loadMessagesFromRoom() {
        CoroutineScope(Dispatchers.IO).launch {
            val roomMessages = webSocketManager.loadMessagesFromRoom()
            _messages.value = roomMessages.map { TickerMessage(
                type = it.type,
                sequence = it.sequence,
                productId = it.productId ?: "N/A",
                price = it.price ?: "N/A",
                open24h = it.open24h ?: "N/A",
                volume24h = it.volume24h ?: "N/A",
                low24h = it.low24h ?: "N/A",
                high24h = it.high24h ?: "N/A",
                volume30d = it.volume30d ?: "N/A",
                bestBid = it.bestBid ?: "N/A",
                bestBidSize = it.bestBidSize ?: "N/A",
                bestAsk = it.bestAsk ?: "N/A",
                bestAskSize = it.bestAskSize ?: "N/A",
                side = it.side ?: "N/A",
                time = it.time ?: "N/A",
                tradeId = it.tradeId ?: 0L,
                lastSize = it.lastSize ?: "N/A"
            ) }
        }
    }

    fun startWebSocket() {
        webSocketManager.connect()
        isWebSocketConnected = true
    }

    fun stopWebSocket() {
        webSocketManager.disconnect()
        isWebSocketConnected = false
        loadMessagesFromRoom()
    }
}