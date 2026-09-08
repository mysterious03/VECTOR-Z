package com.iqoo.vectorz.feature.truth

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
import com.iqoo.vectorz.core.model.TruthAuditReport
import com.iqoo.vectorz.core.model.TruthStatus
import com.iqoo.vectorz.core.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TruthAuditScreen(
    currentReport: TruthAuditReport?,
    onAuditContent: (String) -> Unit,
    onBack: () -> Unit
) {
    var rawInput by remember {
        mutableStateOf(
            """
            URGENT FORWARD: Government is giving free ₹50,000 electricity relief to all bank account holders. Click here to claim your RBI lottery bonus immediately.
            """.trimIndent()
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "TRUTH & MEDIA AUDIT",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = VectorZCyan,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Atomic Claims • Evidence • Synthetic Media Signals",
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
                    text = "SELECTED MESSAGE / FORWARDED CLAIM TEXT",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = VectorZTextSecondary,
                    letterSpacing = 1.sp
                )
            }

            item {
                OutlinedTextField(
                    value = rawInput,
                    onValueChange = { rawInput = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VectorZCyan,
                        unfocusedBorderColor = VectorZBorder,
                        focusedContainerColor = VectorZSurface,
                        unfocusedContainerColor = VectorZSurface,
                        focusedTextColor = VectorZTextPrimary,
                        unfocusedTextColor = VectorZTextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    placeholder = { Text("Paste forwarded claim, news snippet, or social post...", color = VectorZTextSecondary) }
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { onAuditContent(rawInput) },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = VectorZCyan)
                    ) {
                        Text(
                            text = "RUN TRUTH AUDIT",
                            fontWeight = FontWeight.Bold,
                            color = VectorZBlack,
                            letterSpacing = 0.5.sp
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            rawInput = "UIDAI states that Offline Paperless e-KYC is a secure digitally signed mechanism for identity verification without exposing full biometric data."
                            onAuditContent(rawInput)
                        },
                        modifier = Modifier.height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, VectorZBorder)
                    ) {
                        Text("Verified Preset", color = VectorZTextSecondary, fontSize = 11.sp)
                    }
                }
            }

            if (currentReport != null) {
                item {
                    TruthCard(report = currentReport)
                }
            }
        }
    }
}

@Composable
private fun TruthCard(report: TruthAuditReport) {
    val statusColor = when (report.overallStatus) {
        TruthStatus.SUPPORTED -> VectorZSafetyGreen
        TruthStatus.DISPUTED -> VectorZRiskRed
        TruthStatus.UNVERIFIED -> VectorZWarningAmber
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, statusColor.copy(alpha = 0.6f), RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = VectorZSurface)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = statusColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = report.overallStatus.label,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        letterSpacing = 0.5.sp
                    )
                }

                Text(
                    text = "Confidence: ${(report.confidence * 100).toInt()}%",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = VectorZTextSecondary
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Synthetic Media Indicator Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(VectorZSurfaceElevated)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("AI / Synthetic Media Signals:", fontSize = 11.sp, color = VectorZTextSecondary)
                Text(report.syntheticMediaSignals, fontSize = 11.sp, fontWeight = FontWeight.Bold,
                    color = if (report.syntheticMediaSignals == "ELEVATED") VectorZRiskRed else VectorZSafetyGreen)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "ATOMIC CLAIMS & EVIDENCE",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = VectorZTextSecondary,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                report.atomicClaims.forEach { claim ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(VectorZSurfaceElevated)
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "“${claim.claimText}”",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = VectorZTextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        claim.evidenceSnippets.forEach { ev ->
                            Text(
                                text = "• $ev",
                                fontSize = 11.sp,
                                color = VectorZTextSecondary,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Explanation Box
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = VectorZSurfaceElevated)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "SYNTHESIS & EXPLANATION",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = VectorZCyan,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = report.explanation,
                        fontSize = 12.sp,
                        color = VectorZTextPrimary,
                        lineHeight = 17.sp
                    )
                }
            }
        }
    }
}
