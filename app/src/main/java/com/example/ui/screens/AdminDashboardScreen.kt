package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.ApkBuildConfig
import com.example.data.model.StorageServer
import com.example.data.repository.NexoraRepository
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.BorderSubtleDark
import com.example.ui.theme.CosmicCardDark
import com.example.ui.theme.CosmicSurfaceDark
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.RoseDanger
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.theme.VioletNeon

@Composable
fun AdminDashboardScreen(
  servers: List<StorageServer>,
  apkConfig: ApkBuildConfig,
  onAddServer: (name: String, endpoint: String, provider: String, region: String, capacityTb: Long) -> Unit,
  onDeleteServer: (String) -> Unit
) {
  var showAddServerDialog by remember { mutableStateOf(false) }
  var apkDownloadSuccess by remember { mutableStateOf(false) }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp)
      .testTag("admin_dashboard_screen"),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item { Spacer(modifier = Modifier.height(6.dp)) }

    // Admin Header
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(20.dp))
          .background(Color(0xFF141F38))
          .border(1.dp, CyanNeon.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
          .padding(18.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(modifier = Modifier.size(44.dp).clip(CircleShape).background(Color(0x3300F0FF)), contentAlignment = Alignment.Center) {
            Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(26.dp))
          }
          Spacer(modifier = Modifier.width(14.dp))
          Column {
            Text("NEXORA CLOUD CONTROL PLANE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CyanNeon, letterSpacing = 1.2.sp)
            Text("Super Administrator Console", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
            Text("Full control over distributed storage clusters, pricing, and APK builds.", fontSize = 11.sp, color = TextSecondaryDark)
          }
        }
      }
    }

    // High Level Metrics
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        AdminMetricCard(title = "Total Storage Pool", value = "1.37 PB", subtitle = "${servers.size} Active Nodes", icon = Icons.Default.Dns, color = CyanNeon, modifier = Modifier.weight(1f))
        AdminMetricCard(title = "Active Universes", value = "14,820", subtitle = "84% in 90d Trial", icon = Icons.Default.People, color = VioletNeon, modifier = Modifier.weight(1f))
        AdminMetricCard(title = "Monthly ARR", value = "$348.6K", subtitle = "+22% this quarter", icon = Icons.Default.TrendingUp, color = EmeraldSuccess, modifier = Modifier.weight(1f))
      }
    }

    // Android APK & AAB Builder & Release Engine Section
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(20.dp))
          .background(CosmicCardDark)
          .border(1.dp, BorderSubtleDark, RoundedCornerShape(20.dp))
          .padding(18.dp)
          .testTag("apk_builder_panel")
      ) {
        Column {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(Color(0x2210B981)), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Android, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(20.dp))
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text("ANDROID APK BUILD ENGINE", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EmeraldSuccess, letterSpacing = 1.sp)
                Text("${apkConfig.appName} (${apkConfig.versionName})", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
              }
            }

            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0x3310B981))
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text("SIGNED & READY", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EmeraldSuccess)
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          Column(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .background(Color(0xFF090D1A))
              .padding(12.dp)
          ) {
            Text("Package: ${apkConfig.packageName}", fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = TextSecondaryDark)
            Text("Version Code: ${apkConfig.versionCode} • Target SDK: ${apkConfig.targetSdk} • Min SDK: ${apkConfig.minSdk}", fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = TextSecondaryDark)
            Text("Keystore Fingerprint: ${apkConfig.certFingerprint}", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = CyanNeon)
            Text("Build Status: Production verified Gradle build successful", fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = EmeraldSuccess)
          }

          Spacer(modifier = Modifier.height(14.dp))

          Button(
            onClick = { apkDownloadSuccess = true },
            colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess, contentColor = Color.Black),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().testTag("download_apk_button")
          ) {
            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Download Distribution APK (Signed)", fontWeight = FontWeight.Bold)
          }

          if (apkDownloadSuccess) {
            Spacer(modifier = Modifier.height(8.dp))
            Text("APK binary packaged and ready for direct install on Android devices.", fontSize = 11.sp, color = EmeraldSuccess)
          }
        }
      }
    }

    // Storage Infrastructure Servers Management
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text("STORAGE CLUSTER SERVERS (${servers.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondaryDark, letterSpacing = 1.sp)

        Button(
          onClick = { showAddServerDialog = true },
          colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = Color.Black),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.testTag("add_server_node_button")
        ) {
          Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Add Node", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
      }
    }

    items(servers, key = { it.id }) { srv ->
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .background(CosmicCardDark)
          .border(1.dp, BorderSubtleDark, RoundedCornerShape(16.dp))
          .padding(14.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Box(modifier = Modifier.size(38.dp).clip(CircleShape).background(Color(0x3300F0FF)), contentAlignment = Alignment.Center) {
              Icon(Icons.Default.Dns, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(srv.name, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimaryDark, maxLines = 1)
                if (srv.isPrimary) {
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("(Primary)", fontSize = 10.sp, color = AmberWarning, fontWeight = FontWeight.Bold)
                }
              }
              Text(
                text = "${srv.region} • ${srv.provider} • ${NexoraRepository.formatBytes(srv.capacityBytes)} cap",
                fontSize = 11.sp,
                color = TextSecondaryDark
              )
              Text("Latency: ${srv.latencyMs}ms • Status: ${srv.status}", fontSize = 10.sp, color = EmeraldSuccess)
            }
          }

          if (!srv.isPrimary) {
            IconButton(onClick = { onDeleteServer(srv.id) }) {
              Icon(Icons.Default.Delete, contentDescription = "Delete", tint = TextSecondaryDark, modifier = Modifier.size(18.dp))
            }
          }
        }
      }
    }

    item { Spacer(modifier = Modifier.height(20.dp)) }
  }

  // Add Server Dialog
  if (showAddServerDialog) {
    var srvName by remember { mutableStateOf("EU-North Ceph Storage Pool") }
    var endpoint by remember { mutableStateOf("https://ceph-north.nexora.cloud") }
    var provider by remember { mutableStateOf("CEPH") }
    var region by remember { mutableStateOf("eu-north-1 (Stockholm)") }
    var capacityTb by remember { mutableStateOf("250") }

    Dialog(onDismissRequest = { showAddServerDialog = false }) {
      Surface(
        shape = RoundedCornerShape(20.dp),
        color = CosmicSurfaceDark,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtleDark),
        modifier = Modifier.fillMaxWidth().testTag("add_server_dialog")
      ) {
        Column(modifier = Modifier.padding(20.dp)) {
          Text("Connect Storage Server Node", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
          Spacer(modifier = Modifier.height(12.dp))

          OutlinedTextField(
            value = srvName,
            onValueChange = { srvName = it },
            label = { Text("Server Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimaryDark, unfocusedTextColor = TextPrimaryDark, focusedBorderColor = CyanNeon)
          )

          Spacer(modifier = Modifier.height(8.dp))

          OutlinedTextField(
            value = endpoint,
            onValueChange = { endpoint = it },
            label = { Text("S3 or REST Endpoint URL") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimaryDark, unfocusedTextColor = TextPrimaryDark, focusedBorderColor = CyanNeon)
          )

          Spacer(modifier = Modifier.height(8.dp))

          OutlinedTextField(
            value = capacityTb,
            onValueChange = { capacityTb = it },
            label = { Text("Capacity in Terabytes (TB)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimaryDark, unfocusedTextColor = TextPrimaryDark, focusedBorderColor = CyanNeon)
          )

          Spacer(modifier = Modifier.height(16.dp))

          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Button(onClick = { showAddServerDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)) {
              Text("Cancel", color = TextSecondaryDark)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
              onClick = {
                val cap = capacityTb.toLongOrNull() ?: 100L
                onAddServer(srvName, endpoint, provider, region, cap)
                showAddServerDialog = false
              },
              colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = Color.Black)
            ) {
              Text("Provision Node", fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }
  }
}

@Composable
fun AdminMetricCard(
  title: String,
  value: String,
  subtitle: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  color: Color,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(16.dp))
      .background(CosmicCardDark)
      .border(1.dp, BorderSubtleDark, RoundedCornerShape(16.dp))
      .padding(12.dp)
  ) {
    Column {
      Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
      Spacer(modifier = Modifier.height(8.dp))
      Text(value, fontSize = 16.sp, fontWeight = FontWeight.Black, color = TextPrimaryDark)
      Text(title, fontSize = 10.sp, color = TextSecondaryDark, maxLines = 1)
      Text(subtitle, fontSize = 9.sp, color = color, fontWeight = FontWeight.SemiBold)
    }
  }
}
