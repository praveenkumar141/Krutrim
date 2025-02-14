package com.example.krutrim.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import com.example.krutrim.presentation.viewmodels.WebSocketViewModel
import org.koin.androidx.compose.koinViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.sp

@Composable
fun StreamingScreen(viewModel: WebSocketViewModel = koinViewModel()) {
    val messages by viewModel.messages.collectAsState()
    val error by viewModel.error.collectAsState()
    val perplexity by viewModel.perplexity.collectAsState()
    var inputText by remember { mutableStateOf(TextFieldValue("")) }

    LaunchedEffect(Unit) {
        viewModel.startWebSocket()
    }

    val latestMessages = remember(messages) {
        messages.takeLast(10).reversed()
    }

    Column(
        Modifier
            .background(Color.Black)
            .padding(16.dp)) {
        if (error != null) {
            Text("Network Error: $error", color = MaterialTheme.colorScheme.error)
        }
        Spacer(modifier = Modifier.height(32.dp))

        Text("CoinBase Trade Stream", fontSize = 22.sp, color = Color.White, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(32.dp))

        // Display the latest 10 messages with the latest on top
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(latestMessages) { msg ->
                println(msg)
                Text(msg, Modifier.padding(8.dp), color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Perplexity: $perplexity",
            modifier = Modifier.padding(top = 16.dp),
            color = Color.White
        )
        Spacer(modifier = Modifier.height(16.dp))

        var isError by remember { mutableStateOf(false) }
        if (isError) {
            Text(
                text = "Enter valid probabilities (between 0 and 1)",
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
            )
        }
        OutlinedTextField(
            value = inputText,
            onValueChange = {
                inputText = it
                val probs = inputText.text.split(",").mapNotNull { it.toDoubleOrNull() }

                isError = probs.isEmpty() || probs.any { it < 0.0 || it > 1.0 }

                if (!isError) {
                    viewModel.calculatePerplexity(probs)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            isError = isError, // Mark error state dynamically
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = if (isError) Color.Red else Color.White,
                unfocusedIndicatorColor = if (isError) Color.Red else Color.Gray,
                cursorColor = Color.White,
                errorCursorColor = Color.Red
            ),
            placeholder = { Text("Type in probability sequence (e.g., 0.1,0.5,0.3)") }
        )
    }
}