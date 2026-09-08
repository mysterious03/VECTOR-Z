package com.iqoo.vectorz.feature.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.iqoo.vectorz.core.database.AppPolicyEntity
import com.iqoo.vectorz.core.database.AuditEventEntity
import com.iqoo.vectorz.core.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyDashboardScreen(
    recentAudits: List<AuditEventEntity>,
    appPolicies: List<AppPolicyEntity>,
    onToggleAppPolicy: (String, Boolean) -> Unit,
    onPurgeEphemeralCache: () -> Unit,
    onResetDemoData: () -> Unit,
    onBack: () -> Unit
) {
    var isVectorZPaused by remember { mutableStateOf(false) }
    var requireBiometricGlobal by remember { mutableStateOf(true) }
    val dateFormat = remember { SimpleDateFormat("HH:mm:ss • dd MMM", Locale.getDefault()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "PRIVACY DASHBOARD",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = VectorZCyan,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "On-Device Audit Ledger & Controls",
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
            // Master Controls Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = VectorZSurface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Pause Vector-Z Trust Layer", fontWeight = FontWeight.Bold, color = VectorZTextPrimary, fontSize = 13.sp)
                                Text("Temporarily mute autofill suggestions & audit", color = VectorZTextSecondary, fontSize = 11.sp)
                            }
                            Switch(
                                checked = isVectorZPaused,
                                onCheckedChange = { isVectorZPaused = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = VectorZRiskRed,
                                    checkedTrackColor = VectorZRiskRed.copy(alpha = 0.5f)
                                )
                            )
                        }

                        Divider(modifier = Modifier.padding(vertical = 12.dp), color = VectorZBorder)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Biometric Gate on High Sensitivity", fontWeight = FontWeight.Bold, color = VectorZTextPrimary, fontSize = 13.sp)
                                Text("Aadhaar, PAN & Passports require fingerprint/PIN", color = VectorZSafetyGreen, fontSize = 11.sp)
                            }
                            Switch(
                                checked = requireBiometricGlobal,
                                onCheckedChange = { requireBiometricGlobal = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = VectorZCyan)
                            )
                        }
                    }
                }
            }

            // Ephemeral Purge Action
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onPurgeEphemeralCache,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = VectorZSurfaceElevated)
                    ) {
                        Icon(Icons.Default.CleaningServices, contentDescription = null, tint = VectorZCyan, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("PURGE TEMP ARTIFACTS", color = VectorZTextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onResetDemoData,
                        modifier = Modifier.height(44.dp),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, VectorZCyan.copy(alpha = 0.5f))
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, tint = VectorZCyan, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("RESET DEMO DATA", color = VectorZCyan, fontSize = 11.sp)
                    }
                }
            }

            // Per-App Autofill Allowlist Section
            item {
                Text(
                    text = "PER-APP AUTOFILL ACCESS POLICIES",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = VectorZTextSecondary,
                    letterSpacing = 1.sp
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = VectorZSurface)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        listOf(
                            Triple("com.chrome.browser", "Chrome Browser / Form Fields", true),
                            Triple("com.demo.kycapp", "FinTech KYC Onboarding Form", true),
                            Triple("com.shopping.portal", "Online Commerce Store", true),
                            Triple("com.suspicious.app", "Unverified 3rd Party APK", false)
                        ).forEach { (pkg, name, allowed) ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = VectorZTextPrimary)
                                    Text(pkg, fontSize = 10.sp, color = VectorZTextSecondary)
                                }
                                Switch(
                                    checked = allowed,
                                    onCheckedChange = { onToggleAppPolicy(pkg, it) },
                                    colors = SwitchDefaults.colors(checkedThumbColor = VectorZCyan)
                                )
                            }
                        }
                    }
                }
            }

            // Recent 10 Sensitive Actions Ledger
            item {
                Text(
                    text = "RECENT SENSITIVE ACTION LEDGER (ZERO RAW SECRETS)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = VectorZTextSecondary,
                    letterSpacing = 1.sp
                )
            }

            if (recentAudits.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = VectorZSurface)
                    ) {
                        Box(modifier = Modifier.fillMaxWidth().padding(20.dp), contentAlignment = Alignment.Center) {
                            Text("No audit events recorded in local ledger yet.", color = VectorZTextSecondary, fontSize = 12.sp)
                        }
                    }
                }
            } else {
                items(recentAudits, key = { it.id }) { audit ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = VectorZSurface)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = VectorZCyan.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = audit.category,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = VectorZCyan,
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(audit.actionSummary, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = VectorZTextPrimary)
                                }
                                Spacer(modifier = Modifier.height(3.dp))
                                Text("App: ${audit.appPackage} • ${dateFormat.format(Date(audit.timestamp))}", fontSize = 10.sp, color = VectorZTextSecondary)
                            }

                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = if (audit.riskLevel == "HIGH_RISK") VectorZRiskRed.copy(alpha = 0.15f) else VectorZSafetyGreen.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = audit.resultStatus,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (audit.riskLevel == "HIGH_RISK") VectorZRiskRed else VectorZSafetyGreen,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
