package com.rechabits.app.presentation.ui.screen.settings

import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rechabits.app.data.reminder.PermissionHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    
    var hasOverlayPermission by remember { mutableStateOf(false) }
    var hasExactAlarmPermission by remember { mutableStateOf(false) }
    var hasNotificationPermission by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        hasOverlayPermission = PermissionHelper.hasOverlayPermission(context)
        hasExactAlarmPermission = PermissionHelper.hasExactAlarmPermission(context)
        hasNotificationPermission = PermissionHelper.hasNotificationPermission(context)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ajustes", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                "Permisos de recordatorios",
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Overlay permission
            PermissionRow(
                title = "Ventana flotante",
                description = "Permite mostrar alertas sobre otras apps",
                hasPermission = hasOverlayPermission,
                onConfigure = {
                    context.startActivity(PermissionHelper.getOverlayPermissionIntent(context))
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Exact alarm permission
            PermissionRow(
                title = "Alarmas exactas",
                description = "Necesario para recordatorios a la hora exacta",
                hasPermission = hasExactAlarmPermission,
                onConfigure = {
                    context.startActivity(PermissionHelper.getExactAlarmPermissionIntent(context))
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Notification permission
            PermissionRow(
                title = "Notificaciones",
                description = "Permite enviar notificaciones de recordatorio",
                hasPermission = hasNotificationPermission,
                onConfigure = {
                    context.startActivity(PermissionHelper.getNotificationPermissionIntent(context))
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                "Acerca de",
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                "RechaBits v1.0",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                "Recordá tus hábitos, un bit a la vez",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun PermissionRow(
    title: String,
    description: String,
    hasPermission: Boolean,
    onConfigure: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (hasPermission) Icons.Default.CheckCircle else Icons.Default.Warning,
            contentDescription = null,
            tint = if (hasPermission) Color(0xFF4CAF50) else Color(0xFFFF9800),
            modifier = Modifier.padding(end = 12.dp)
        )
        
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Medium)
            Text(
                description,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Button(onClick = onConfigure) {
            Text(if (hasPermission) "Verificar" else "Activar")
        }
    }
}
