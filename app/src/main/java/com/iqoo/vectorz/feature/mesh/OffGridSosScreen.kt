package com.iqoo.vectorz.feature.mesh

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

/**
 * Off-Grid SOS BLE Mesh Relay Screen for Disaster & Zero-Cellular Scenarios.
 */
@Composable
fun OffGridSosScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isBroadcasting by remember { mutableStateOf(false) }
    var activeHops by remember { mutableStateOf(3) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VectorZBlack)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
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
                    text = "Off-Grid SOS Mesh",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = VectorZRed
                )
                Text(
                    text = "Air-Gapped BLE Multi-Hop Relay (0 SIM Required)",
                    fontSize = 11.sp,
                    color = VectorZSubtext
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // SOS Big Button
        Box(
            modifier = Modifier
                .size(140.dp)
                .clip(CircleShape)
                .background(if (isBroadcasting) Color(0xFFB71C1C) else VectorZRed)
                .clickable { isBroadcasting = !isBroadcasting },
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "🚨", fontSize = 36.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isBroadcasting) "RELAYING" else "BROADCAST SOS",
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Mesh Status Card
        Card(
            colors = CardDefaults.cardColors(containerColor = VectorZDarkGray),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "ACTIVE MESH RADAR", fontSize = 10.sp, fontWeight = FontWeight.Black, color = VectorZCyan)
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Local Nodes Discovered:", fontSize = 12.sp, color = VectorZSubtext)
                    Text(text = "4 iQOO Devices", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Estimated Mesh Hops:", fontSize = 12.sp, color = VectorZSubtext)
                    Text(text = "$activeHops Hops (Max 7)", fontWeight = FontWeight.Bold, color = VectorZGreen, fontSize = 12.sp)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "GPS Fix:", fontSize = 12.sp, color = VectorZSubtext)
                    Text(text = "12.9716° N, 77.5946° E", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                }
            }
        }
    }
}
