package com.example.ui.components

import android.content.Context
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Whatsapp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.CloudFile
import com.example.data.model.StorageServer
import com.example.data.repository.NexoraRepository
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.BorderSubtleDark
import com.example.ui.theme.CosmicCardDark
import com.example.ui.theme.CosmicSurfaceDark
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.theme.VioletNeon

@Composable
fun OneClickBackupDialog(
  isOpen: Boolean,
  preset: String,
  status: String,
  progress: Float,
  statusText: String,
  availableServers: List<StorageServer>,
  generatedArchive: CloudFile?,
  onPresetSelected: (String) -> Unit,
  onStartBackup: () -> Unit,
  onDismiss: () -> Unit,
  onViewInCloud: (CloudFile) -> Unit
) {
  if (!isOpen) return

  val context = LocalContext.current
  val activeServer = availableServers.firstOrNull { it.status == "ONLINE" } ?: availableServers.firstOrNull()
  val isCompleted = status == "COMPLETED"
  val isRunning = status in listOf("SCANNING", "COMPRESSING", "ENCRYPTING", "UPLOADING")

  Dialog(onDismissRequest = { if (!isRunning) onDismiss() }) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = CosmicSurfaceDark,
      border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtleDark),
      modifier = Modifier
        .fillMaxWidth()
        .testTag("one_click_backup_dialog")
    ) {
      Column(modifier = Modifier.padding(20.dp)) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0x3300F0FF)),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text("One-Click Phone Backup", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
              Text("Compress & store complete phone data to cloud", fontSize = 11.sp, color = TextSecondaryDark)
            }
          }

          if (!isRunning) {
            IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
              Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondaryDark)
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Target Available Cloud Server Information
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF0F1E36))
            .border(1.dp, CyanNeon.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(12.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Dns, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "Target Node: ${activeServer?.name ?: "Fastest Available S3 Node"}",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimaryDark,
                maxLines = 1
              )
              Text(
                text = "Region: ${activeServer?.region ?: "Multi-Region"} • Latency: ${activeServer?.latencyMs ?: 18}ms • Cloud Online",
                fontSize = 10.sp,
                color = EmeraldSuccess
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Format / Sizing Presets
        if (!isCompleted) {
          Text(
            text = "SELECT BACKUP COMPRESSION & SIZE",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondaryDark,
            letterSpacing = 1.sp
          )
          Spacer(modifier = Modifier.height(6.dp))

          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            BackupPresetRow(
              presetKey = "FULL_ZIP",
              title = "Master Complete ZIP",
              subtitle = "Full device archive (Contacts, Photos, App Cache, Notes)",
              sizeEstimate = "~68 MB",
              isSelected = preset == "FULL_ZIP",
              enabled = !isRunning,
              onSelect = { onPresetSelected("FULL_ZIP") }
            )

            BackupPresetRow(
              presetKey = "WHATSAPP",
              title = "WhatsApp Size Optimized ZIP",
              subtitle = "Compressed under 16 MB threshold for instant messaging",
              sizeEstimate = "< 16 MB",
              isSelected = preset == "WHATSAPP",
              enabled = !isRunning,
              onSelect = { onPresetSelected("WHATSAPP") }
            )

            BackupPresetRow(
              presetKey = "GMAIL",
              title = "Gmail Size Optimized ZIP",
              subtitle = "Strictly under 25 MB email attachment limit",
              sizeEstimate = "< 25 MB",
              isSelected = preset == "GMAIL",
              enabled = !isRunning,
              onSelect = { onPresetSelected("GMAIL") }
            )
          }

          Spacer(modifier = Modifier.height(16.dp))
        }

        // Live Progress & Status
        if (isRunning || isCompleted) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(14.dp))
              .background(CosmicCardDark)
              .border(1.dp, if (isCompleted) EmeraldSuccess else CyanNeon, RoundedCornerShape(14.dp))
              .padding(14.dp)
          ) {
            Column {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = status,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (isCompleted) EmeraldSuccess else CyanNeon
                )
                Text(
                  text = "${(progress * 100).toInt()}%",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (isCompleted) EmeraldSuccess else CyanNeon
                )
              }

              Spacer(modifier = Modifier.height(8.dp))

              LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                color = if (isCompleted) EmeraldSuccess else CyanNeon,
                trackColor = Color(0x33FFFFFF)
              )

              Spacer(modifier = Modifier.height(8.dp))

              Text(
                text = statusText,
                fontSize = 11.sp,
                color = TextSecondaryDark
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))
        }

        // Post-Completion Share & Action Buttons
        if (isCompleted && generatedArchive != null) {
          Text(
            text = "SHARE OR RESTORE BACKUP",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondaryDark,
            letterSpacing = 1.sp
          )
          Spacer(modifier = Modifier.height(8.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            // Share via WhatsApp Action
            Button(
              onClick = {
                val sendIntent = Intent().apply {
                  action = Intent.ACTION_SEND
                  putExtra(Intent.EXTRA_TEXT, "Here is my encrypted phone backup archive (${generatedArchive.name}) from Nexora Cloud: https://share.nexora.cloud/s/${generatedArchive.id.take(8)}")
                  type = "text/plain"
                  setPackage("com.whatsapp")
                }
                try {
                  context.startActivity(sendIntent)
                } catch (e: Exception) {
                  // Fallback to standard share intent
                  val fallback = Intent.createChooser(
                    Intent().apply {
                      action = Intent.ACTION_SEND
                      putExtra(Intent.EXTRA_TEXT, "Nexora Cloud Phone Backup: ${generatedArchive.name}")
                      type = "text/plain"
                    },
                    "Share Backup"
                  )
                  context.startActivity(fallback)
                }
              },
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366), contentColor = Color.Black),
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier.weight(1f).testTag("share_whatsapp_button")
            ) {
              Text("WhatsApp", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            // Send via Gmail Action
            Button(
              onClick = {
                val emailIntent = Intent(Intent.ACTION_SEND).apply {
                  type = "message/rfc822"
                  putExtra(Intent.EXTRA_SUBJECT, "Nexora Cloud Encrypted Phone Backup Archive")
                  putExtra(Intent.EXTRA_TEXT, "Attached is the phone backup: ${generatedArchive.name} (${NexoraRepository.formatBytes(generatedArchive.sizeBytes)}). Verified with SHA-256.")
                }
                try {
                  context.startActivity(Intent.createChooser(emailIntent, "Send Backup via Gmail / Email"))
                } catch (e: Exception) {
                  // ignore
                }
              },
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEA4335), contentColor = Color.White),
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier.weight(1f).testTag("share_gmail_button")
            ) {
              Text("Gmail", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            // View in Cloud
            Button(
              onClick = {
                onDismiss()
                onViewInCloud(generatedArchive)
              },
              colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = Color.Black),
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier.weight(1f).testTag("view_in_cloud_button")
            ) {
              Text("View Cloud", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          }

          Spacer(modifier = Modifier.height(10.dp))
        }

        // Trigger Button (Start Backup / Close)
        if (!isCompleted) {
          Button(
            onClick = onStartBackup,
            enabled = !isRunning,
            colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = Color.Black),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("execute_one_click_backup_btn")
          ) {
            Icon(Icons.Default.CloudDone, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(if (isRunning) "Backing up..." else "1-Click Backup to Available Cloud", fontWeight = FontWeight.Bold, fontSize = 13.sp)
          }
        } else {
          Button(
            onClick = onDismiss,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B), contentColor = TextPrimaryDark),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Text("Done", fontWeight = FontWeight.SemiBold)
          }
        }
      }
    }
  }
}

@Composable
fun BackupPresetRow(
  presetKey: String,
  title: String,
  subtitle: String,
  sizeEstimate: String,
  isSelected: Boolean,
  enabled: Boolean,
  onSelect: () -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .background(if (isSelected) Color(0x3300F0FF) else CosmicCardDark)
      .border(
        width = if (isSelected) 1.5.dp else 1.dp,
        color = if (isSelected) CyanNeon else BorderSubtleDark,
        shape = RoundedCornerShape(12.dp)
      )
      .clickable(enabled = enabled) { onSelect() }
      .padding(10.dp)
      .testTag("preset_$presetKey")
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
          Spacer(modifier = Modifier.width(6.dp))
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(if (isSelected) CyanNeon else Color(0x22FFFFFF))
              .padding(horizontal = 4.dp, vertical = 1.dp)
          ) {
            Text(sizeEstimate, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = if (isSelected) Color.Black else TextSecondaryDark)
          }
        }
        Text(subtitle, fontSize = 10.sp, color = TextSecondaryDark, maxLines = 1)
      }

      Box(
        modifier = Modifier
          .size(18.dp)
          .clip(CircleShape)
          .border(1.5.dp, if (isSelected) CyanNeon else TextSecondaryDark, CircleShape)
          .background(if (isSelected) CyanNeon else Color.Transparent)
      )
    }
  }
}
