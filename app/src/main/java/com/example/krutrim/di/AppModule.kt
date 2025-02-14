package com.example.krutrim.di

import android.util.Log
import com.example.krutrim.data.local.AppDatabase
import com.example.krutrim.data.network.WebSocketManager
import com.example.krutrim.data.network.WebSocketRepository
import com.example.krutrim.presentation.viewmodels.WebSocketViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single {
        try {
            Log.d("KoinModule", "Providing AppDatabase")
            AppDatabase.getDatabase(get())
        } catch (e: Exception) {
            Log.e("KoinModule", "Error providing AppDatabase", e)
            throw e
        }
    }

    single {
        try {
            Log.d("KoinModule", "Providing TickerMessageDao")
            get<AppDatabase>().tickerMessageDao()
        } catch (e: Exception) {
            Log.e("KoinModule", "Error providing TickerMessageDao", e)
            throw e
        }
    }

    single {
        try {
            Log.d("KoinModule", "Providing WebSocketManager")
            WebSocketManager("wss://ws-feed.exchange.coinbase.com", get())
        } catch (e: Exception) {
            Log.e("KoinModule", "Error providing WebSocketManager", e)
            throw e
        }
    }

    single {
        try {
            Log.d("KoinModule", "Providing WebSocketRepository")
            WebSocketRepository(get())
        } catch (e: Exception) {
            Log.e("KoinModule", "Error providing WebSocketRepository", e)
            throw e
        }
    }

    viewModel {
        try {
            Log.d("KoinModule", "Providing WebSocketViewModel")
            WebSocketViewModel(get())
        } catch (e: Exception) {
            Log.e("KoinModule", "Error providing WebSocketViewModel", e)
            throw e
        }
    }
}
