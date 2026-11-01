package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserProfile
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.BorderSubtleDark
import com.example.ui.theme.CosmicCardDark
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.RoseDanger
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.theme.VioletNeon
import com.example.viewmodel.NexoraScreen

@Composable
fun SettingsScreen(
  userProfile: UserProfile,
  onNavigate: (NexoraScreen) -> Unit,
  onUpdateSecurity: (e2e: Boolean, twoFactor: Boolean) -> Unit,
  onUpdateBackup: (photos: Boolean, videos: Boolean, wifiOnly: Boolean) -> Unit
) {
  var showExportSuccess by remember { mutableStateOf(false) }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp)
      .testTag("settings_screen"),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item { Spacer(modifier = Modifier.height(6.dp)) }

    // User Profile Card with Personal Cloud Address
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(20.dp))
          .background(CosmicCardDark)
          .border(1.dp, BorderSubtleDark, RoundedCornerShape(20.dp))
          .padding(18.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(54.dp)
              .clip(CircleShape)
              .background(Color(0xFF3B1B75))
              .border(2.dp, VioletNeon, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Text("AV", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
          }

          Spacer(modifier = Modifier.width(14.dp))

          Column {
            Text(userProfile.fullName, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
            Text(userProfile.email, fontSize = 12.sp, color = TextSecondaryDark)
            Spacer(modifier = Modifier.height(4.dp))
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0x3300F0FF))
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text(userProfile.cloudHandle, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = CyanNeon)
            }
          }
        }
      }
    }

    // Quick Portal Links (Admin Console & Storage Health Analytics)
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Box(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(16.dp))
            .background(CosmicCardDark)
            .border(1.dp, CyanNeon.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            .clickable { onNavigate(NexoraScreen.ADMIN) }
            .padding(14.dp)
            .testTag("go_to_admin_portal"),
          contentAlignment = Alignment.Center
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Admin Console", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
          }
        }

        Box(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(16.dp))
            .background(CosmicCardDark)
            .border(1.dp, VioletNeon.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            .clickable { onNavigate(NexoraScreen.SUBSCRIPTION) }
            .padding(14.dp)
            .testTag("go_to_subscription_portal"),
          contentAlignment = Alignment.Center
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Star, contentDescription = null, tint = AmberWarning, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Subscription Plans", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
          }
        }
      }
    }

    // Security & Cryptography Preferences
    item {
      Text("SECURITY & ZERO-TRUST PRIVACY", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondaryDark, letterSpacing = 1.sp)
    }

    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .background(CosmicCardDark)
          .border(1.dp, BorderSubtleDark, RoundedCornerShape(16.dp))
          .padding(16.dp)
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
          SettingToggleRow(
            title = "End-to-End Encryption (AES-256)",
            subtitle = "Zero-knowledge encryption before leaving device",
            isChecked = userProfile.e2eEncryptionEnabled,
            onCheckedChange = { onUpdateSecurity(it, userProfile.twoFactorEnabled) }
          )

          SettingToggleRow(
            title = "Two-Factor Authentication / Passkey",
            subtitle = "Hardware security keys & biometric prompt",
            isChecked = userProfile.twoFactorEnabled,
            onCheckedChange = { onUpdateSecurity(userProfile.e2eEncryptionEnabled, it) }
          )
        }
      }
    }

    // Backup & Sync Preferences
    item {
      Text("AUTOMATION & DEVICE BACKUP", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondaryDark, letterSpacing = 1.sp)
    }

    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .background(CosmicCardDark)
          .border(1.dp, BorderSubtleDark, RoundedCornerShape(16.dp))
          .padding(16.dp)
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
          SettingToggleRow(
            title = "Auto-Backup Camera Photos",
            subtitle = "Instantly back up new camera roll captures",
            isChecked = userProfile.autoBackupPhotos,
            onCheckedChange = { onUpdateBackup(it, userProfile.autoBackupVideos, userProfile.syncOnWifiOnly) }
          )

          SettingToggleRow(
            title = "Auto-Backup High-Res Videos",
            subtitle = "Replicate 1080p and 4K footage to cloud",
            isChecked = userProfile.autoBackupVideos,
            onCheckedChange = { onUpdateBackup(userProfile.autoBackupPhotos, it, userProfile.syncOnWifiOnly) }
          )

          SettingToggleRow(
            title = "Wi-Fi Only Sync",
            subtitle = "Pause background uploads on cellular data",
            isChecked = userProfile.syncOnWifiOnly,
            onCheckedChange = { onUpdateBackup(userProfile.autoBackupPhotos, userProfile.autoBackupVideos, it) }
          )
        }
      }
    }

    // Active Connected Devices
    item {
      Text("AUTHORIZED DEVICES (4 ACTIVE)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondaryDark, letterSpacing = 1.sp)
    }

    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .background(CosmicCardDark)
          .border(1.dp, BorderSubtleDark, RoundedCornerShape(16.dp))
          .padding(14.dp)
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          DeviceRow("Google Pixel 9 Pro (This Device)", "Android 15 • Current Session", true)
          DeviceRow("Galaxy Tab S10 Ultra", "Android 15 • Synced 2 hours ago", false)
          DeviceRow("MacBook Pro M3 Max", "macOS Sonoma • Desktop Companion Agent", false)
          DeviceRow("Chrome Desktop Web App", "San Francisco, US • Active yesterday", false)
        }
      }
    }

    // Data Export & Account Actions
    item {
      Button(
        onClick = { showExportSuccess = true },
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B), contentColor = CyanNeon),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth().testTag("export_data_button")
      ) {
        Icon(Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text("Export All Cloud Data (Encrypted ZIP)", fontWeight = FontWeight.SemiBold)
      }

      if (showExportSuccess) {
        Spacer(modifier = Modifier.height(6.dp))
        Text("Export package generated and queued for download.", fontSize = 11.sp, color = EmeraldSuccess)
      }
    }

    item { Spacer(modifier = Modifier.height(30.dp)) }
  }
}

@Composable
fun SettingToggleRow(
  title: String,
  subtitle: String,
  isChecked: Boolean,
  onCheckedChange: (Boolean) -> Unit
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Column(modifier = Modifier.weight(1f)) {
      Text(title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimaryDark)
      Text(subtitle, fontSize = 11.sp, color = TextSecondaryDark)
    }
    Switch(
      checked = isChecked,
      onCheckedChange = onCheckedChange,
      colors = SwitchDefaults.colors(checkedThumbColor = CyanNeon, checkedTrackColor = CyanNeon.copy(alpha = 0.5f))
    )
  }
}

@Composable
fun DeviceRow(deviceName: String, sessionInfo: String, isCurrent: Boolean) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(Color(0x228B5CF6)), contentAlignment = Alignment.Center) {
        Icon(Icons.Default.Devices, contentDescription = null, tint = VioletNeon, modifier = Modifier.size(16.dp))
      }
      Spacer(modifier = Modifier.width(10.dp))
      Column {
        Text(deviceName, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextPrimaryDark)
        Text(sessionInfo, fontSize = 10.sp, color = if (isCurrent) EmeraldSuccess else TextSecondaryDark)
      }
    }
    if (isCurrent) {
      Box(modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(Color(0x3310B981)).padding(horizontal = 6.dp, vertical = 2.dp)) {
        Text("THIS PHONE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = EmeraldSuccess)
      }
    }
  }
}
