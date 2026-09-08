package com.iqoo.vectorz.feature.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iqoo.vectorz.core.theme.*

@Composable
fun HomeScreen(
    onNavigateToVault: () -> Unit,
    onNavigateToPaymentGuard: () -> Unit,
    onNavigateToCommerceGuard: () -> Unit,
    onNavigateToTruthAudit: () -> Unit,
    onNavigateToAgent: () -> Unit,
    onNavigateToSimulator: () -> Unit,
    onNavigateToPrivacyDashboard: () -> Unit,
    onToggleBubbleService: (Boolean) -> Unit,
    isBubbleActive: Boolean
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(VectorZBlack)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 24.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "VECTOR-Z",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = VectorZCyan,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "Trust before action.",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = VectorZTextSecondary
                    )
                }

                IconButton(
                    onClick = onNavigateToPrivacyDashboard,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(VectorZSurfaceElevated)
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Privacy Dashboard",
                        tint = VectorZCyan
                    )
                }
            }
        }

        // Trust Hero Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        Brush.horizontalGradient(listOf(VectorZCyan.copy(alpha = 0.6f), VectorZBlue.copy(alpha = 0.3f))),
                        RoundedCornerShape(20.dp)
                    ),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = VectorZSurface)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(VectorZSafetyGreen)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "NPU HARDWARE ENCLAVE ACTIVE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = VectorZSafetyGreen,
                                letterSpacing = 1.sp
                            )
                        }

                        Text(
                            text = "0% Cloud Dependent",
                            fontSize = 10.sp,
                            color = VectorZTextSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Phone-Native Trust Layer",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = VectorZTextPrimary
                    )
                    Text(
                        text = "Understands what is on your screen, guards payments & commerce, and protects identity.",
                        fontSize = 13.sp,
                        color = VectorZTextSecondary,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Floating Bubble Toggle Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(VectorZSurfaceElevated)
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🛡️", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Vector-Z Trust Bubble",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = VectorZTextPrimary
                                )
                                Text(
                                    text = if (isBubbleActive) "Docked at screen edge" else "Tap to enable overlay",
                                    fontSize = 11.sp,
                                    color = VectorZTextSecondary
                                )
                            }
                        }

                        Switch(
                            checked = isBubbleActive,
                            onCheckedChange = onToggleBubbleService,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = VectorZCyan,
                                checkedTrackColor = VectorZBlue.copy(alpha = 0.5f)
                            )
                        )
                    }
                }
            }
        }

        // Section Label
        item {
            Text(
                text = "CORE TRUST CAPABILITIES",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = VectorZTextSecondary,
                letterSpacing = 1.2.sp
            )
        }

        // Grid 1: Vault & Payment Guard
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionCard(
                    modifier = Modifier.weight(1f),
                    title = "Identity Vault",
                    subtitle = "Aadhaar, PAN, Passport",
                    icon = Icons.Default.Lock,
                    accentColor = VectorZCyan,
                    onClick = onNavigateToVault
                )
                QuickActionCard(
                    modifier = Modifier.weight(1f),
                    title = "Payment Guard",
                    subtitle = "UPI & QR Intent Check",
                    icon = Icons.Default.QrCodeScanner,
                    accentColor = VectorZWarningAmber,
                    onClick = onNavigateToPaymentGuard
                )
            }
        }

        // Grid 2: Commerce Guard & Truth Audit
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionCard(
                    modifier = Modifier.weight(1f),
                    title = "Commerce Guard",
                    subtitle = "Price, Return & Seller Risk",
                    icon = Icons.Default.ShoppingCart,
                    accentColor = VectorZSafetyGreen,
                    onClick = onNavigateToCommerceGuard
                )
                QuickActionCard(
                    modifier = Modifier.weight(1f),
                    title = "Truth & Media Audit",
                    subtitle = "Claim & AI-Media Check",
                    icon = Icons.Default.FactCheck,
                    accentColor = VectorZRiskRed,
                    onClick = onNavigateToTruthAudit
                )
            }
        }

        // Grid 3: Bounded Agent & KYC Simulator
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionCard(
                    modifier = Modifier.weight(1f),
                    title = "Bounded Agent",
                    subtitle = "Policy-Gated Phone Action",
                    icon = Icons.Default.SmartToy,
                    accentColor = VectorZBlue,
                    onClick = onNavigateToAgent
                )
                QuickActionCard(
                    modifier = Modifier.weight(1f),
                    title = "KYC Simulator",
                    subtitle = "Live Autofill & Consent",
                    icon = Icons.Default.PlayArrow,
                    accentColor = VectorZCyan,
                    onClick = onNavigateToSimulator
                )
            }
        }
    }
}

@Composable
private fun QuickActionCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(130.dp)
            .clickable { onClick() }
            .border(1.dp, VectorZBorder, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = VectorZSurface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = VectorZTextPrimary
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = VectorZTextSecondary,
                    maxLines = 1
                )
            }
        }
    }
}
