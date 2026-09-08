package com.iqoo.vectorz.feature.vault

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
import com.iqoo.vectorz.core.model.*
import com.iqoo.vectorz.core.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VaultScreen(
    documents: List<IdentityDocument>,
    onAddDocumentClick: () -> Unit,
    onDeleteDocument: (String) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "IDENTITY VAULT",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = VectorZCyan,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Hardware Keystore Isolated • Zero Cloud",
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
                actions = {
                    IconButton(onClick = onAddDocumentClick) {
                        Icon(imageVector = Icons.Default.AddPhotoAlternate, contentDescription = "Scan Document", tint = VectorZCyan)
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
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = VectorZSafetyGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Data Minimization Active: Canonical identity fields are encrypted individually with AES-256-GCM. Biometric prompt required for high sensitivity attributes.",
                            fontSize = 11.sp,
                            color = VectorZTextSecondary,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            if (documents.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No identity documents in vault.\nTap '+' to scan or seed demo records.",
                            color = VectorZTextSecondary,
                            fontSize = 13.sp
                        )
                    }
                }
            } else {
                items(documents, key = { it.id }) { doc ->
                    DocumentCard(doc = doc, onDelete = { onDeleteDocument(doc.id) })
                }
            }
        }
    }
}

@Composable
private fun DocumentCard(
    doc: IdentityDocument,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, VectorZBorder, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = VectorZSurface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                when (doc.documentType) {
                                    DocumentType.AADHAAR -> VectorZCyan.copy(alpha = 0.15f)
                                    DocumentType.PAN -> VectorZBlue.copy(alpha = 0.15f)
                                    DocumentType.PASSPORT -> VectorZSafetyGreen.copy(alpha = 0.15f)
                                    else -> VectorZWarningAmber.copy(alpha = 0.15f)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (doc.documentType) {
                                DocumentType.AADHAAR -> Icons.Default.Badge
                                DocumentType.PAN -> Icons.Default.CreditCard
                                DocumentType.PASSPORT -> Icons.Default.Flight
                                else -> Icons.Default.Description
                            },
                            contentDescription = null,
                            tint = when (doc.documentType) {
                                DocumentType.AADHAAR -> VectorZCyan
                                DocumentType.PAN -> VectorZBlue
                                DocumentType.PASSPORT -> VectorZSafetyGreen
                                else -> VectorZWarningAmber
                            },
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = doc.displayName,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = VectorZTextPrimary
                        )
                        Text(
                            text = "DEMO DATA — NOT A REAL ID",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = VectorZWarningAmber,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete",
                        tint = VectorZRiskRed.copy(alpha = 0.7f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Field List
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                doc.fields.forEach { field ->
                    FieldRow(field = field)
                }
            }
        }
    }
}

@Composable
private fun FieldRow(field: IdentityField) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(VectorZSurfaceElevated)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = field.fieldType.label,
                fontSize = 10.sp,
                color = VectorZTextSecondary
            )
            Text(
                text = field.maskedValue,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = VectorZTextPrimary
            )
        }

        Surface(
            shape = RoundedCornerShape(4.dp),
            color = if (field.fieldType.sensitivity == SensitivityClass.HIGH_SENSITIVITY)
                VectorZWarningAmber.copy(alpha = 0.15f)
            else
                VectorZCyan.copy(alpha = 0.15f)
        ) {
            Text(
                text = if (field.fieldType.sensitivity == SensitivityClass.HIGH_SENSITIVITY) "BIOMETRIC GATED" else "MASKED",
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                color = if (field.fieldType.sensitivity == SensitivityClass.HIGH_SENSITIVITY) VectorZWarningAmber else VectorZCyan,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }
    }
}
