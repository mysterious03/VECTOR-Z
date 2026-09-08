package com.iqoo.vectorz.feature.launcher

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iqoo.vectorz.core.theme.*
import com.iqoo.vectorz.domain.model.AppCategory
import com.iqoo.vectorz.domain.model.PhoneApp

/**
 * Authentic Vivo / iQOO OriginOS 6 Smartphone Launcher Home Screen.
 */
@Composable
fun OriginOSLauncherScreen(
    onLaunchApp: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val phoneApps = listOf(
        PhoneApp("phonepe", "com.phonepe.app", "PhonePe", AppCategory.PAYMENTS_FINTECH, "💳", false, 1),
        PhoneApp("whatsapp", "com.whatsapp", "WhatsApp", AppCategory.COMMUNICATION, "💬", false, 3),
        PhoneApp("amazon", "com.amazon.shopping", "Amazon", AppCategory.ECOMMERCE, "🛒"),
        PhoneApp("agent", "com.iqoo.agent", "AI Copilot", AppCategory.SYSTEM_SECURITY, "⚡"),
        PhoneApp("digilocker", "gov.in.digilocker", "DigiLocker", AppCategory.IDENTITY_GOV, "🪪"),
        PhoneApp("vault", "com.iqoo.vectorz.vault", "Keystore", AppCategory.SYSTEM_SECURITY, "🔒"),
        PhoneApp("sos", "com.iqoo.mesh.sos", "Off-Grid SOS", AppCategory.EMERGENCY_MESH, "📡"),
        PhoneApp("video_shield", "com.iqoo.shield.call", "Call Shield", AppCategory.SYSTEM_SECURITY, "🛡️"),
        PhoneApp("honeypot", "com.iqoo.honeypot", "Honeypot", AppCategory.SYSTEM_SECURITY, "🤖"),
        PhoneApp("settings", "com.android.settings", "Settings", AppCategory.SYSTEM_SECURITY, "⚙️")
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VectorZBlack)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Status & Clock Widget
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "09:41",
            fontSize = 54.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = "Bengaluru • 28°C Sunny • 5G SA",
            fontSize = 13.sp,
            color = VectorZSubtext
        )

        // Vector-Z NPU Pill
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(Brush.horizontalGradient(listOf(Color(0xFF00E5FF).copy(alpha = 0.15f), Color(0xFFFFD600).copy(alpha = 0.15f))))
                .padding(horizontal = 14.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(VectorZGreen))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "VECTOR-Z NPU ACTIVE (1.84ms)",
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                color = VectorZCyan
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // App Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(phoneApps) { app ->
                PhoneAppIconItem(
                    app = app,
                    onClick = { onLaunchApp(app.id) }
                )
            }
        }

        // Phone Dock (4 Quick Launch Apps)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(VectorZDarkGray.copy(alpha = 0.8f))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            DockIconItem(emoji = "📞", label = "Phone", onClick = { onLaunchApp("dialer") })
            DockIconItem(emoji = "💬", label = "Messages", onClick = { onLaunchApp("whatsapp") })
            DockIconItem(emoji = "🌐", label = "Chrome", onClick = { onLaunchApp("simulator") })
            DockIconItem(emoji = "📷", label = "Camera", onClick = { onLaunchApp("ocr") })
        }

        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
fun PhoneAppIconItem(
    app: PhoneApp,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(VectorZDarkGray),
            contentAlignment = Alignment.Center
        ) {
            Text(text = app.iconEmoji, fontSize = 24.sp)
            if (app.unreadBadgeCount > 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(2.dp)
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(VectorZRed),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = app.unreadBadgeCount.toString(),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = app.displayName,
            fontSize = 11.sp,
            color = Color.White,
            maxLines = 1
        )
    }
}

@Composable
fun DockIconItem(
    emoji: String,
    label: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(50.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF1E1E2A))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(text = emoji, fontSize = 22.sp)
    }
}
