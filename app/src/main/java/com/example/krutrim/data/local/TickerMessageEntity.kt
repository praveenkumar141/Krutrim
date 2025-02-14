package com.example.krutrim.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ticker_messages")
data class TickerMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String,
    val sequence: Long,
    val productId: String,
    val price: String,
    val open24h: String,
    val volume24h: String,
    val low24h: String,
    val high24h: String,
    val volume30d: String,
    val bestBid: String,
    val bestBidSize: String,
    val bestAsk: String,
    val bestAskSize: String,
    val side: String,
    val time: String,
    val tradeId: Long,
    val lastSize: String
)