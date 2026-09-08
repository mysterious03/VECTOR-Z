package com.iqoo.vectorz.feature.messaging

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iqoo.vectorz.core.theme.*
import com.iqoo.vectorz.domain.model.ChatMessage
import com.iqoo.vectorz.domain.model.DltHeaderStatus
import com.iqoo.vectorz.domain.model.MessageSafetyVerdict

/**
 * Real WhatsApp Messaging Conversation Thread Simulator with Trojan Infiltration Intercept.
 */
@Composable
fun WhatsAppChatScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var messages by remember {
        mutableStateOf(
            listOf(
                ChatMessage(
                    senderAddress = "+91 98765 43210",
                    senderDisplayName = "Electricity Helpdesk",
                    bodyText = "Dear Consumer, your power supply #8912401 will be disconnected tonight at 9:30 PM. Download the official update app: power-pay-refund.apk",
                    dltHeaderStatus = DltHeaderStatus.SPOOFED_DLT_SENDER,
                    safetyVerdict = MessageSafetyVerdict.MALICIOUS_TROJAN_APK
                )
            )
        )
    }

    var inputText by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0B141A))
            .padding(12.dp)
    ) {
        // WhatsApp Top Header Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "←",
                fontSize = 24.sp,
                color = Color.White,
                modifier = Modifier.clickable { onBack() }
            )
            Spacer(modifier = Modifier.width(10.dp))
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF25D366)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "⚡", fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(text = "Electricity Helpdesk", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
                Text(text = "+91 98765 43210 • Spoofed Sender", fontSize = 11.sp, color = VectorZRed)
            }
        }

        // Vector-Z DLT Header Warning
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF330810)),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
        ) {
            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(text = "🚨", fontSize = 20.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(text = "DLT HEADER SPOOF DETECTED", fontSize = 10.sp, fontWeight = FontWeight.Black, color = VectorZRed)
                    Text(text = "Unregistered sender claiming to be utility board. Quarantining APK download.", fontSize = 11.sp, color = Color.White)
                }
            }
        }

        // Messages Stream
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(messages) { msg ->
                ChatBubble(message = msg)
            }
        }

        // Message Input Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = { Text("Message...", color = Color.Gray, fontSize = 13.sp) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color(0xFF1F2C34),
                    unfocusedContainerColor = Color(0xFF1F2C34)
                ),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    if (inputText.isNotBlank()) {
                        val userMsg = ChatMessage(
                            senderAddress = "Self",
                            senderDisplayName = "Me",
                            bodyText = inputText,
                            isOutgoing = true
                        )
                        messages = messages + userMsg
                        inputText = ""
                    }
                },
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00A884)),
                modifier = Modifier.size(48.dp)
            ) {
                Text(text = "➤", color = Color.White, fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val bg = if (message.isOutgoing) Color(0xFF005C4B) else Color(0xFF202C33)
    val align = if (message.isOutgoing) Alignment.End else Alignment.Start

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = align
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(bg)
                .padding(12.dp)
        ) {
            Column {
                Text(text = message.bodyText, fontSize = 13.sp, color = Color.White)
                if (message.safetyVerdict == MessageSafetyVerdict.MALICIOUS_TROJAN_APK) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF4A1010)),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "📦", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(text = "power-pay-refund.apk", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Text(text = "⚠️ Quarantined (Trojan.SmsSniffer)", fontSize = 10.sp, color = VectorZRed)
                            }
                        }
                    }
                }
            }
        }
    }
}
