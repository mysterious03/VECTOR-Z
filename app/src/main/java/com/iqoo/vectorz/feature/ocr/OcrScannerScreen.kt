package com.iqoo.vectorz.feature.ocr

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
import com.iqoo.vectorz.core.model.DocumentType
import com.iqoo.vectorz.core.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OcrScannerScreen(
    onSaveExtracted: (DocumentType, String, Map<CanonicalFieldType, String>) -> Unit,
    onBack: () -> Unit
) {
    val documentParser = remember { DocumentParser() }
    var selectedPreset by remember { mutableStateOf("PAN") }
    var ocrRawText by remember {
        mutableStateOf(
            """
            INCOME TAX DEPARTMENT
            GOVT. OF INDIA
            PERMANENT ACCOUNT NUMBER CARD
            AARAV VIKRAM SHARMA
            15/08/1996
            ABCDE1234F
            """.trimIndent()
        )
    }

    var parsedResult by remember { mutableStateOf(documentParser.parseOcrText(ocrRawText)) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "LOCAL OCR INGESTION",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = VectorZCyan,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Zero Cloud Leakage • Canonical Parser",
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
                    text = "SELECT SYNTHETIC TEST DOCUMENT",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = VectorZTextSecondary,
                    letterSpacing = 1.sp
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedPreset == "PAN",
                        onClick = {
                            selectedPreset = "PAN"
                            ocrRawText = "INCOME TAX DEPARTMENT\nGOVT. OF INDIA\nAARAV VIKRAM SHARMA\n15/08/1996\nABCDE1234F"
                            parsedResult = documentParser.parseOcrText(ocrRawText)
                        },
                        label = { Text("PAN Card") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = VectorZCyan,
                            selectedLabelColor = VectorZBlack
                        )
                    )
                    FilterChip(
                        selected = selectedPreset == "AADHAAR",
                        onClick = {
                            selectedPreset = "AADHAAR"
                            ocrRawText = "GOVERNMENT OF INDIA\nAARAV VIKRAM SHARMA\nDOB: 15/08/1996\nMALE\n9081 4452 7819\nIndiranagar, Bengaluru - 560038"
                            parsedResult = documentParser.parseOcrText(ocrRawText)
                        },
                        label = { Text("Aadhaar") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = VectorZCyan,
                            selectedLabelColor = VectorZBlack
                        )
                    )
                    FilterChip(
                        selected = selectedPreset == "PASSPORT",
                        onClick = {
                            selectedPreset = "PASSPORT"
                            ocrRawText = "REPUBLIC OF INDIA\nPASSPORT\nAARAV VIKRAM SHARMA\nZ8912401\nDOB: 15/08/1996\nPLACE OF BIRTH: BENGALURU"
                            parsedResult = documentParser.parseOcrText(ocrRawText)
                        },
                        label = { Text("Passport") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = VectorZCyan,
                            selectedLabelColor = VectorZBlack
                        )
                    )
                }
            }

            item {
                OutlinedTextField(
                    value = ocrRawText,
                    onValueChange = {
                        ocrRawText = it
                        parsedResult = documentParser.parseOcrText(it)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VectorZCyan,
                        unfocusedBorderColor = VectorZBorder,
                        focusedContainerColor = VectorZSurface,
                        unfocusedContainerColor = VectorZSurface,
                        focusedTextColor = VectorZTextPrimary,
                        unfocusedTextColor = VectorZTextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    label = { Text("Simulated OCR Camera Text Stream", color = VectorZTextSecondary) }
                )
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, VectorZCyan.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = VectorZSurface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "CANONICAL FIELDS EXTRACTED",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = VectorZCyan,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Confidence: ${(parsedResult.confidence * 100).toInt()}%",
                                fontSize = 11.sp,
                                color = VectorZSafetyGreen,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            parsedResult.extractedFields.forEach { (type, value) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(VectorZSurfaceElevated)
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = type.label, fontSize = 12.sp, color = VectorZTextSecondary)
                                    Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = VectorZTextPrimary)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                onSaveExtracted(
                                    parsedResult.detectedType,
                                    "Extracted ${parsedResult.detectedType.displayName}",
                                    parsedResult.extractedFields
                                )
                                onBack()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = VectorZCyan)
                        ) {
                            Text(
                                text = "SAVE TO ENCRYPTED VAULT",
                                fontWeight = FontWeight.Bold,
                                color = VectorZBlack,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
