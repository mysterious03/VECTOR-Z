package com.iqoo.vectorz.feature.payment

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iqoo.vectorz.core.model.PaymentIntent
import com.iqoo.vectorz.core.model.RiskAssessment
import com.iqoo.vectorz.core.model.RiskLevel
import com.iqoo.vectorz.core.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentGuardScreen(
    currentIntent: PaymentIntent?,
    currentAssessment: RiskAssessment?,
    onScanOrAuditQr: (String, String) -> Unit,
    onBack: () -> Unit
) {
    var qrPayload by remember {
        mutableStateOf("upi://pay?pa=refund-helpdesk99@fakeupi&pn=ABC+MART+REFUND&am=2500&tn=Instant+Cashback+Verification")
    }
    var contextClaim by remember {
        mutableStateOf("Customer service said: Scan this QR to RECEIVE your ₹2,500 refund.")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "PAYMENT INTENT GUARD",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = VectorZCyan,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Pre-Transaction Intent Verification",
                            fontSize = 11.sp,
                            color = VectorZTextSecondary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = VectorZBlack)
            )
        },
        containerColor = VectorZBlack
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            item {
                Text(
                    text = "QR CODE / UPI INTENT STRING",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = VectorZTextSecondary,
                    letterSpacing = 1.sp
                )
            }

            item {
                OutlinedTextField(
                    value = qrPayload,
                    onValueChange = { qrPayload = it },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VectorZCyan,
                        unfocusedBorderColor = VectorZBorder,
                        focusedContainerColor = VectorZSurface,
                        unfocusedContainerColor = VectorZSurface,
                        focusedTextColor = VectorZTextPrimary,
                        unfocusedTextColor = VectorZTextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    label = { Text("UPI Intent URI", color = VectorZTextSecondary) }
                )
            }

            item {
                OutlinedTextField(
                    value = contextClaim,
                    onValueChange = { contextClaim = it },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VectorZCyan,
                        unfocusedBorderColor = VectorZBorder,
                        focusedContainerColor = VectorZSurface,
                        unfocusedContainerColor = VectorZSurface,
                        focusedTextColor = VectorZTextPrimary,
                        unfocusedTextColor = VectorZTextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    label = { Text("Surrounding Message / Claim", color = VectorZTextSecondary) }
                )
            }

            item {
                Button(
                    onClick = { onScanOrAuditQr(qrPayload, contextClaim) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VectorZCyan)
                ) {
                    Text(
                        text = "INSPECT PAYMENT INTENT",
                        fontWeight = FontWeight.Bold,
                        color = VectorZBlack,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            if (currentIntent != null && currentAssessment != null) {
                item {
                    PaymentIntentCard(intent = currentIntent, assessment = currentAssessment)
                }
            }
        }
    }
}

@Composable
private fun PaymentIntentCard(
    intent: PaymentIntent,
    assessment: RiskAssessment
) {
    val isRisky = intent.isRiskyInstructionDetected
    val borderCol = if (isRisky) VectorZRiskRed else VectorZSafetyGreen

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, borderCol.copy(alpha = 0.7f), RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = VectorZSurface)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "WHAT YOU ARE ABOUT TO DO",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = VectorZCyan,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Main Transaction Summary
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(VectorZSurfaceElevated)
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Transferring to:",
                        fontSize = 11.sp,
                        color = VectorZTextSecondary
                    )
                    Text(
                        text = intent.payeeName ?: intent.payeeVpa,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = VectorZTextPrimary
                    )
                    Text(
                        text = intent.payeeVpa,
                        fontSize = 12.sp,
                        color = VectorZTextSecondary
                    )
                }

                Text(
                    text = intent.amount?.let { "₹$it" } ?: "User Specified",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isRisky) VectorZRiskRed else VectorZCyan
                )
            }

            if (isRisky) {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = VectorZRiskRed.copy(alpha = 0.15f))
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "🚨", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "CRITICAL CONFLICT: You were told you are RECEIVING money, but scanning this QR will SEND money out of your account.",
                            fontSize = 11.sp,
                            color = VectorZRiskRed,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Policy Rule Footer
            Text(
                text = "🔒 Hard Safety Guard: Vector-Z never authorizes payments or enters your UPI PIN autonomously.",
                fontSize = 10.sp,
                color = VectorZTextSecondary,
                lineHeight = 14.sp
            )
        }
    }
}
