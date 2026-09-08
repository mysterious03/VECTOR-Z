package com.iqoo.vectorz.feature.payment

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.iqoo.vectorz.domain.model.PaymentActionIntent
import com.iqoo.vectorz.domain.model.UpiSecurityDecision
import com.iqoo.vectorz.domain.model.UpiTransaction

/**
 * Real PhonePe UPI Payment App Simulator with 6-Dot PIN Bottom Sheet & Intent Conflict Intercept.
 */
@Composable
fun PhonePePaymentScreen(
    onBack: () -> Unit,
    onAuthorizeTransaction: (UpiTransaction) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedPayeeType by remember { mutableStateOf("refund") }
    var enteredAmount by remember { mutableStateOf("25000") }
    var showPinSheet by remember { mutableStateOf(false) }
    var pinDigits by remember { mutableStateOf("") }
    var receiptMessage by remember { mutableStateOf<String?>(null) }
    var isBlocked by remember { mutableStateOf(false) }

    val payeeName = if (selectedPayeeType == "friend") "Rahul Sharma (Friend)" else "ABC Mart Refund Desk"
    val payeeVpa = if (selectedPayeeType == "friend") "rahul@oksbi" else "refund-desk@fakebank"

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VectorZBlack)
            .padding(16.dp)
    ) {
        // App Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "←",
                fontSize = 24.sp,
                color = Color.White,
                modifier = Modifier.clickable { onBack() }
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "PhonePe UPI",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF673AB7)
                )
                Text(
                    text = "Linked Bank: ICICI Bank (•••• 8912)",
                    fontSize = 11.sp,
                    color = VectorZSubtext
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Payee Selector Card
        Card(
            colors = CardDefaults.cardColors(containerColor = VectorZDarkGray),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(text = "SELECT TRANSFER PAYEE", fontSize = 10.sp, fontWeight = FontWeight.Black, color = VectorZCyan)
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { selectedPayeeType = "refund" },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedPayeeType == "refund") VectorZRed else Color(0xFF2A2A38)
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "Scam QR Payee", fontSize = 11.sp)
                    }
                    Button(
                        onClick = { selectedPayeeType = "friend" },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedPayeeType == "friend") VectorZGreen else Color(0xFF2A2A38)
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "Rahul Sharma", fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(text = "Payee: $payeeName", fontWeight = FontWeight.Bold, color = Color.White)
                Text(text = "VPA: $payeeVpa", fontSize = 12.sp, color = VectorZSubtext)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Amount Input Card
        Card(
            colors = CardDefaults.cardColors(containerColor = VectorZDarkGray),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(text = "TRANSFER AMOUNT (INR)", fontSize = 10.sp, fontWeight = FontWeight.Black, color = VectorZCyan)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = enteredAmount,
                    onValueChange = { enteredAmount = it },
                    label = { Text("Amount ₹") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Trigger PIN Sheet Button
        Button(
            onClick = {
                pinDigits = ""
                showPinSheet = true
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF673AB7)),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Text(text = "PROCEED TO ENTER UPI PIN", fontWeight = FontWeight.Bold)
        }

        // Receipt Area
        receiptMessage?.let { msg ->
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isBlocked) Color(0xFF33000A) else Color(0xFF003311)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = if (isBlocked) "🚨 TRANSACTION BLOCKED BY VECTOR-Z" else "✅ PAYMENT SUCCESSFUL",
                        fontWeight = FontWeight.Black,
                        color = if (isBlocked) VectorZRed else VectorZGreen
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = msg, fontSize = 12.sp, color = Color.White)
                }
            }
        }
    }

    // 6-Dot PIN Bottom Sheet Dialog
    if (showPinSheet) {
        AlertDialog(
            onDismissRequest = { showPinSheet = false },
            confirmButton = {},
            containerColor = Color(0xFF151520),
            title = {
                Text(text = "NPCI UPI PIN Verification", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Paying ₹$enteredAmount to $payeeName", color = VectorZSubtext, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(16.dp))

                    // 6 Dots
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(6) { index ->
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (index < pinDigits.length) VectorZCyan else Color.Gray.copy(alpha = 0.4f)
                                    )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Simple 3x4 Numeric Pad
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(
                            listOf("1", "2", "3"),
                            listOf("4", "5", "6"),
                            listOf("7", "8", "9"),
                            listOf("C", "0", "OK")
                        ).forEach { row ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                row.forEach { digit ->
                                    Button(
                                        onClick = {
                                            when (digit) {
                                                "C" -> pinDigits = ""
                                                "OK" -> {
                                                    showPinSheet = false
                                                    if (selectedPayeeType == "refund") {
                                                        isBlocked = true
                                                        receiptMessage = "Intent Inversion Conflict Detected! Deceptive refund instruction attempted to DEBIT ₹$enteredAmount. UPI PIN was held securely in hardware Keystore."
                                                    } else {
                                                        isBlocked = false
                                                        receiptMessage = "Transferred ₹$enteredAmount to $payeeName. UTR: 891240182910."
                                                    }
                                                }
                                                else -> {
                                                    if (pinDigits.length < 6) pinDigits += digit
                                                    if (pinDigits.length == 6) {
                                                        showPinSheet = false
                                                        if (selectedPayeeType == "refund") {
                                                            isBlocked = true
                                                            receiptMessage = "Intent Inversion Conflict Detected! Deceptive refund instruction attempted to DEBIT ₹$enteredAmount."
                                                        } else {
                                                            isBlocked = false
                                                            receiptMessage = "Transferred ₹$enteredAmount to $payeeName. UTR: 891240182910."
                                                        }
                                                    }
                                                }
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2A2A38)),
                                        modifier = Modifier.size(54.dp),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text(text = digit, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        )
    }
}
