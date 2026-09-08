package com.iqoo.vectorz.feature.commerce

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
import com.iqoo.vectorz.core.model.CommerceDecision
import com.iqoo.vectorz.core.model.CommerceEvaluation
import com.iqoo.vectorz.core.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommerceGuardScreen(
    currentEvaluation: CommerceEvaluation?,
    onEvaluateProduct: (String) -> Unit,
    onBack: () -> Unit
) {
    var productText by remember {
        mutableStateOf(
            """
            Mega Brand Earbuds Pro
            Special Price: ₹999 (90% OFF from ₹9,999)
            Seller: QuickRetail Traders (WhatsApp Pay for extra 10% off)
            Shipping: ₹150 delivery surcharge at checkout
            Policy: Final Sale - Strictly Non-Refundable / No Returns.
            """.trimIndent()
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "COMMERCE GUARD",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = VectorZCyan,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Price Sanity • Return Risk • Seller Verification",
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
                    text = "PRODUCT / CART / INVOICE TEXT CONTEXT",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = VectorZTextSecondary,
                    letterSpacing = 1.sp
                )
            }

            item {
                OutlinedTextField(
                    value = productText,
                    onValueChange = { productText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VectorZCyan,
                        unfocusedBorderColor = VectorZBorder,
                        focusedContainerColor = VectorZSurface,
                        unfocusedContainerColor = VectorZSurface,
                        focusedTextColor = VectorZTextPrimary,
                        unfocusedTextColor = VectorZTextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    placeholder = { Text("Paste product page, invoice, or screenshot OCR text...", color = VectorZTextSecondary) }
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { onEvaluateProduct(productText) },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = VectorZCyan)
                    ) {
                        Text(
                            text = "AUDIT PURCHASE RISK",
                            fontWeight = FontWeight.Bold,
                            color = VectorZBlack,
                            letterSpacing = 0.5.sp
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            productText = "Genuine Smartwatch Series 5\nPrice: ₹4,499 (25% OFF from ₹5,999)\nOfficial Flagship Brand Store\nFree Prime Shipping\nPolicy: 15-Day Free Replacement & 1-Year Manufacturer Warranty."
                            onEvaluateProduct(productText)
                        },
                        modifier = Modifier.height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, VectorZBorder)
                    ) {
                        Text("Safe Preset", color = VectorZTextSecondary, fontSize = 11.sp)
                    }
                }
            }

            if (currentEvaluation != null) {
                item {
                    CommerceDecisionCard(evaluation = currentEvaluation)
                }
            }
        }
    }
}

@Composable
private fun CommerceDecisionCard(evaluation: CommerceEvaluation) {
    val decisionColor = when (evaluation.decision) {
        CommerceDecision.GOOD_VALUE -> VectorZSafetyGreen
        CommerceDecision.REVIEW -> VectorZWarningAmber
        CommerceDecision.AVOID -> VectorZRiskRed
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, decisionColor.copy(alpha = 0.6f), RoundedCornerShape(16.dp)),
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
                    color = decisionColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = evaluation.decision.label,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = decisionColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        letterSpacing = 0.5.sp
                    )
                }

                Text(
                    text = "Confidence: ${(evaluation.confidence * 100).toInt()}%",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = VectorZTextSecondary
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Snapshot Breakdown Grid
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(VectorZSurfaceElevated)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Effective Price", fontSize = 10.sp, color = VectorZTextSecondary)
                    Text(evaluation.extractedPrice, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = VectorZTextPrimary)
                }
                Column {
                    Text("Shipping & Surcharges", fontSize = 10.sp, color = VectorZTextSecondary)
                    Text(evaluation.shippingAndFees, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = VectorZTextPrimary)
                }
                Column {
                    Text("Return Policy", fontSize = 10.sp, color = VectorZTextSecondary)
                    Text(if (evaluation.returnWindowDays > 0) "${evaluation.returnWindowDays} Days Window" else "NON-REFUNDABLE",
                        fontSize = 12.sp, fontWeight = FontWeight.Bold,
                        color = if (evaluation.returnWindowDays > 0) VectorZSafetyGreen else VectorZRiskRed)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Extracted Signals
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                evaluation.signals.forEach { sig ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(VectorZSurfaceElevated)
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(if (sig.isWarning) "⚠️" else "✔", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(sig.title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = VectorZTextPrimary)
                            Text(sig.detail, fontSize = 11.sp, color = VectorZTextSecondary, lineHeight = 15.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Recommendation Summary
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = VectorZSurfaceElevated)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "PURCHASE RECOMMENDATION",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = VectorZCyan,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = evaluation.summaryReason,
                        fontSize = 12.sp,
                        color = VectorZTextPrimary,
                        lineHeight = 17.sp
                    )
                }
            }
        }
    }
}
