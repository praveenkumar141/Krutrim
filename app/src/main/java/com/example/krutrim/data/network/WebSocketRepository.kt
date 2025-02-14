package com.example.krutrim.data.network

import com.example.krutrim.domain.models.TickerMessage
import kotlinx.coroutines.flow.StateFlow

class WebSocketRepository(private val webSocketManager: WebSocketManager) {
    val messages: StateFlow<List<TickerMessage>> = webSocketManager.messages
    val error: StateFlow<String?> = webSocketManager.error

    fun startWebSocket() {
        webSocketManager.connect()
    }

    fun stopWebSocket() {
        webSocketManager.disconnect()
    }
}
