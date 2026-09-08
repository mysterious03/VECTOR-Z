package com.iqoo.vectorz.feature.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dangerous
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iqoo.vectorz.core.theme.*
import com.iqoo.vectorz.service.notification.NotificationGuardEngine
import com.iqoo.vectorz.service.notification.NotificationScamAlert
import com.iqoo.vectorz.service.notification.ScamCategory

@Composable
fun NotificationGuardScreen(
    onNavigateBack: () -> Unit
) {
    val engine = remember { NotificationGuardEngine() }
    var testSmsInput by remember {
        mutableStateOf("Dear consumer, your electricity will be disconnected tonight at 9:30 PM. Please call officer at 9876543210 or install update: http://power-update.org/bill.apk")
    }
    var currentAlert by remember {
        mutableStateOf<NotificationScamAlert?>(null)
    }

    LaunchedEffect(Unit) {
        currentAlert = engine.inspectNotification("com.google.android.apps.messaging", "URGENT ELECTRICITY BOARD", testSmsInput)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Notification Scam Interceptor",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Real-time SMS & messaging trap defense",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
            Icon(
                imageVector = Icons.Default.NotificationsActive,
                contentDescription = null,
                tint = MonsterOrange
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Live Analyzer Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CardBorder, RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(containerColor = CardDark)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "SIMULATE INCOMING NOTIFICATION",
                    color = TextTertiary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = testSmsInput,
                    onValueChange = {
                        testSmsInput = it
                        currentAlert = engine.inspectNotification("com.google.android.apps.messaging", "SMS Alert", it)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = MonsterOrange,
                        unfocusedBorderColor = CardBorder
                    ),
                    textStyle = LocalTextStyle.current.copy(fontSize = 13.sp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            testSmsInput = "Dear user, your electricity will be disconnected tonight at 9:30 PM. Call officer 9811223344 immediately: http://bit.ly/power.apk"
                            currentAlert = engine.inspectNotification("com.google.android.apps.messaging", "POWER BOARD", testSmsInput)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CardBorder),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Power Cut SMS", fontSize = 11.sp, color = TextPrimary)
                    }
                    Button(
                        onClick = {
                            testSmsInput = "Your SBI YONO account has been suspended due to PAN expiry. Click to verify: http://sbi-kyc-fix.top"
                            currentAlert = engine.inspectNotification("com.google.android.apps.messaging", "SBI ALERT", testSmsInput)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CardBorder),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("PAN Freeze SMS", fontSize = 11.sp, color = TextPrimary)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Intercepted Threat Result Card
        currentAlert?.let { alert ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        if (alert.threatCategory != ScamCategory.CLEAN_NOTIFICATION) AlertRed else EmeraldGreen,
                        RoundedCornerShape(12.dp)
                    ),
                colors = CardDefaults.cardColors(containerColor = CardDark)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (alert.threatCategory != ScamCategory.CLEAN_NOTIFICATION) Icons.Default.Dangerous else Icons.Default.Shield,
                                contentDescription = null,
                                tint = if (alert.threatCategory != ScamCategory.CLEAN_NOTIFICATION) AlertRed else EmeraldGreen
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = alert.threatCategory.name.replace("_", " "),
                                color = if (alert.threatCategory != ScamCategory.CLEAN_NOTIFICATION) AlertRed else EmeraldGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        Text(
                            text = "Risk: ${(alert.riskScore * 100).toInt()}%",
                            color = if (alert.threatCategory != ScamCategory.CLEAN_NOTIFICATION) AlertRed else EmeraldGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = alert.explanation,
                        color = TextPrimary,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )

                    alert.blockedApkUrl?.let { apk ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            color = AlertRed.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "BLOCKED ROGUE APK: $apk",
                                color = AlertRed,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
