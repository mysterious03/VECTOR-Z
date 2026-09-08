package com.iqoo.vectorz.feature.agent

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
import com.iqoo.vectorz.core.model.AgentExecutionPlan
import com.iqoo.vectorz.core.model.AgentRiskTier
import com.iqoo.vectorz.core.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgentScreen(
    currentPlan: AgentExecutionPlan?,
    onRunCommand: (String) -> Unit,
    onConfirmExecution: () -> Unit,
    onBack: () -> Unit
) {
    var commandInput by remember { mutableStateOf("Fill the passport number in the current field.") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "BOUNDED AGENT",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = VectorZCyan,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Policy-Gated Phone Automation",
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
                    text = "COMMAND INTENT",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = VectorZTextSecondary,
                    letterSpacing = 1.sp
                )
            }

            item {
                OutlinedTextField(
                    value = commandInput,
                    onValueChange = { commandInput = it },
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
                    label = { Text("Agent Command", color = VectorZTextSecondary) }
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { onRunCommand(commandInput) },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = VectorZCyan)
                    ) {
                        Text(
                            text = "GENERATE PLAN",
                            fontWeight = FontWeight.Bold,
                            color = VectorZBlack,
                            letterSpacing = 0.5.sp
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            commandInput = "Transfer ₹5,000 using my UPI PIN"
                            onRunCommand(commandInput)
                        },
                        modifier = Modifier.height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, VectorZRiskRed.copy(alpha = 0.5f))
                    ) {
                        Text("Test Safety Block", color = VectorZRiskRed, fontSize = 11.sp)
                    }
                }
            }

            if (currentPlan != null) {
                item {
                    AgentPlanCard(
                        plan = currentPlan,
                        onConfirm = onConfirmExecution
                    )
                }
            }
        }
    }
}

@Composable
private fun AgentPlanCard(
    plan: AgentExecutionPlan,
    onConfirm: () -> Unit
) {
    val isBlocked = plan.safetyStatus == "BLOCKED_BY_SAFETY_GUARD"
    val borderCol = if (isBlocked) VectorZRiskRed else VectorZCyan

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, borderCol.copy(alpha = 0.6f), RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = VectorZSurface)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "AGENT EXECUTION TRACE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = borderCol,
                    letterSpacing = 1.sp
                )

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = borderCol.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = plan.safetyStatus,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = borderCol,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Step List
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                plan.steps.forEach { step ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(VectorZSurfaceElevated)
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(26.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    when {
                                        step.isBlocked -> VectorZRiskRed.copy(alpha = 0.2f)
                                        step.isCompleted -> VectorZSafetyGreen.copy(alpha = 0.2f)
                                        else -> VectorZCyan.copy(alpha = 0.2f)
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${step.stepIndex}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (step.isBlocked) VectorZRiskRed else if (step.isCompleted) VectorZSafetyGreen else VectorZCyan
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = step.title,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = VectorZTextPrimary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "[${step.riskTier.name}]",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (step.riskTier == AgentRiskTier.PROHIBITED) VectorZRiskRed else VectorZTextSecondary
                                )
                            }
                            Text(
                                text = step.detail,
                                fontSize = 11.sp,
                                color = if (step.isBlocked) VectorZRiskRed else VectorZTextSecondary,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }

            if (!isBlocked && plan.resolvedValueMasked != null) {
                Spacer(modifier = Modifier.height(14.dp))
                Button(
                    onClick = onConfirm,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VectorZSafetyGreen)
                ) {
                    Text(
                        text = "CONFIRM & INJECT ${plan.resolvedValueMasked}",
                        fontWeight = FontWeight.Bold,
                        color = VectorZBlack,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}
