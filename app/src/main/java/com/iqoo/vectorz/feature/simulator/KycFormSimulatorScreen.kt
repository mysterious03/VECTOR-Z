package com.iqoo.vectorz.feature.simulator

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
import com.iqoo.vectorz.core.model.CanonicalFieldType
import com.iqoo.vectorz.core.model.SensitivityClass
import com.iqoo.vectorz.core.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KycFormSimulatorScreen(
    onTriggerAutofill: (CanonicalFieldType, (String) -> Unit) -> Unit,
    onBack: () -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var panNumber by remember { mutableStateOf("") }
    var aadhaarNumber by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "SIMULATED KYC FORM",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = VectorZCyan,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Live Autofill & Biometric Gate",
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
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = VectorZSurfaceElevated)
                ) {
                    Text(
                        text = "💡 Tap any input field or icon. Vector-Z matches the canonical schema and displays a masked suggestion card requiring explicit user confirmation.",
                        fontSize = 12.sp,
                        color = VectorZTextSecondary,
                        modifier = Modifier.padding(14.dp),
                        lineHeight = 17.sp
                    )
                }
            }

            // Field 1: Full Name
            item {
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Full Legal Name", color = VectorZTextSecondary) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VectorZCyan,
                        unfocusedBorderColor = VectorZBorder,
                        focusedContainerColor = VectorZSurface,
                        unfocusedContainerColor = VectorZSurface,
                        focusedTextColor = VectorZTextPrimary,
                        unfocusedTextColor = VectorZTextPrimary
                    ),
                    trailingIcon = {
                        IconButton(onClick = {
                            onTriggerAutofill(CanonicalFieldType.FULL_NAME) { filled ->
                                fullName = filled
                            }
                        }) {
                            Icon(Icons.Default.Lock, contentDescription = "Autofill", tint = VectorZCyan)
                        }
                    }
                )
            }

            // Field 2: PAN Number (High Sensitivity)
            item {
                OutlinedTextField(
                    value = panNumber,
                    onValueChange = { panNumber = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Permanent Account Number (PAN) [High Sensitivity]", color = VectorZTextSecondary) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VectorZCyan,
                        unfocusedBorderColor = VectorZBorder,
                        focusedContainerColor = VectorZSurface,
                        unfocusedContainerColor = VectorZSurface,
                        focusedTextColor = VectorZTextPrimary,
                        unfocusedTextColor = VectorZTextPrimary
                    ),
                    trailingIcon = {
                        IconButton(onClick = {
                            onTriggerAutofill(CanonicalFieldType.PAN_NUMBER) { filled ->
                                panNumber = filled
                            }
                        }) {
                            Icon(Icons.Default.Fingerprint, contentDescription = "Biometric Autofill", tint = VectorZWarningAmber)
                        }
                    }
                )
            }

            // Field 3: Aadhaar Number (High Sensitivity)
            item {
                OutlinedTextField(
                    value = aadhaarNumber,
                    onValueChange = { aadhaarNumber = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("12-Digit Aadhaar Number [High Sensitivity]", color = VectorZTextSecondary) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VectorZCyan,
                        unfocusedBorderColor = VectorZBorder,
                        focusedContainerColor = VectorZSurface,
                        unfocusedContainerColor = VectorZSurface,
                        focusedTextColor = VectorZTextPrimary,
                        unfocusedTextColor = VectorZTextPrimary
                    ),
                    trailingIcon = {
                        IconButton(onClick = {
                            onTriggerAutofill(CanonicalFieldType.AADHAAR_NUMBER) { filled ->
                                aadhaarNumber = filled
                            }
                        }) {
                            Icon(Icons.Default.Fingerprint, contentDescription = "Biometric Autofill", tint = VectorZWarningAmber)
                        }
                    }
                )
            }

            // Field 4: Date of Birth
            item {
                OutlinedTextField(
                    value = dob,
                    onValueChange = { dob = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Date of Birth (DD/MM/YYYY)", color = VectorZTextSecondary) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VectorZCyan,
                        unfocusedBorderColor = VectorZBorder,
                        focusedContainerColor = VectorZSurface,
                        unfocusedContainerColor = VectorZSurface,
                        focusedTextColor = VectorZTextPrimary,
                        unfocusedTextColor = VectorZTextPrimary
                    ),
                    trailingIcon = {
                        IconButton(onClick = {
                            onTriggerAutofill(CanonicalFieldType.DATE_OF_BIRTH) { filled ->
                                dob = filled
                            }
                        }) {
                            Icon(Icons.Default.Lock, contentDescription = "Autofill", tint = VectorZCyan)
                        }
                    }
                )
            }

            item {
                Button(
                    onClick = {
                        fullName = ""
                        panNumber = ""
                        aadhaarNumber = ""
                        dob = ""
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VectorZSurfaceElevated)
                ) {
                    Text("CLEAR FORM", color = VectorZTextSecondary, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
