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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ActivityLog
import com.example.data.model.CloudFile
import com.example.data.model.StorageServer
import com.example.data.model.UserProfile
import com.example.ui.components.FileCard
import com.example.ui.components.StorageUsageBar
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.BorderGlowDark
import com.example.ui.theme.BorderSubtleDark
import com.example.ui.theme.CosmicCardDark
import com.example.ui.theme.CosmicCardElevated
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.IndigoNeon
import com.example.ui.theme.NebulaPink
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.theme.VioletNeon
import com.example.viewmodel.NexoraScreen
import com.example.viewmodel.StorageBreakdown

@Composable
fun HomeScreen(
  userProfile: UserProfile,
  storageBreakdown: StorageBreakdown,
  servers: List<StorageServer>,
  recentFiles: List<CloudFile>,
  activities: List<ActivityLog>,
  onNavigate: (NexoraScreen) -> Unit,
  onOpenFile: (CloudFile) -> Unit,
  onToggleFavorite: (CloudFile) -> Unit,
  onShareFile: (CloudFile) -> Unit,
  onMoveToTrash: (CloudFile) -> Unit,
  onToggleVault: (CloudFile) -> Unit,
  onQuickUpload: () -> Unit,
  onOpenOneClickBackup: () -> Unit
) {
  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item { Spacer(modifier = Modifier.height(6.dp)) }

    // Hero Cosmic Greeting Card
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(24.dp))
          .background(
            Brush.linearGradient(
              colors = listOf(Color(0xFF0F1E36), Color(0xFF1E103A))
            )
          )
          .border(1.dp, BorderGlowDark, RoundedCornerShape(24.dp))
          .padding(20.dp)
          .testTag("hero_greeting_card")
      ) {
        Column {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "YOUR PRIVATE UNIVERSE",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.3.sp,
                color = CyanNeon
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = "Welcome, ${userProfile.fullName}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark
              )
            }
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0x3310B981))
                .border(1.dp, EmeraldSuccess.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(EmeraldSuccess))
                Spacer(modifier = Modifier.width(6.dp))
                Text("E2E SECURED", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EmeraldSuccess)
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))
          Text(
            text = "Your files, notes, tasks, and media are replicated across ${servers.size} distributed private cloud nodes with zero-knowledge cryptographic encryption.",
            fontSize = 12.sp,
            lineHeight = 17.sp,
            color = TextSecondaryDark
          )

          Spacer(modifier = Modifier.height(16.dp))

          // 90-Day Free Trial Notice Banner
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(14.dp))
              .background(Color(0x22F59E0B))
              .border(1.dp, AmberWarning.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
              .clickable { onNavigate(NexoraScreen.SUBSCRIPTION) }
              .padding(12.dp)
              .testTag("trial_banner_card")
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = AmberWarning, modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                  Text(
                    text = "3-Month Free Trial Active (${userProfile.trialDaysRemaining} days remaining)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = AmberWarning
                  )
                  Text(
                    text = "Expires ${userProfile.trialEndDate} • Transparent renewal",
                    fontSize = 11.sp,
                    color = TextSecondaryDark
                  )
                }
              }
              Icon(Icons.Default.ArrowForward, contentDescription = null, tint = AmberWarning, modifier = Modifier.size(16.dp))
            }
          }
        }
      }
    }

    // Storage Usage Overview Card
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(20.dp))
          .background(CosmicCardDark)
          .border(1.dp, BorderSubtleDark, RoundedCornerShape(20.dp))
          .padding(18.dp)
      ) {
        StorageUsageBar(breakdown = storageBreakdown)
      }
    }

    // --- 1-CLICK COMPLETE PHONE BACKUP SUPER BUTTON ---
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(20.dp))
          .background(
            Brush.horizontalGradient(
              colors = listOf(Color(0xFF0F3A4A), Color(0xFF1E103A))
            )
          )
          .border(1.5.dp, CyanNeon, RoundedCornerShape(20.dp))
          .clickable { onOpenOneClickBackup() }
          .padding(16.dp)
          .testTag("one_click_backup_card")
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Box(
              modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(CyanNeon),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.CloudSync, contentDescription = null, tint = Color.Black, modifier = Modifier.size(26.dp))
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text("1-CLICK PHONE BACKUP", fontSize = 13.sp, fontWeight = FontWeight.Black, color = CyanNeon, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(EmeraldSuccess)
                    .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                  Text("AUTO-STORE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }
              }
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                "Full ZIP • WhatsApp (<16MB) • Gmail (<25MB) • Auto-upload to available cloud",
                fontSize = 11.sp,
                color = TextPrimaryDark,
                maxLines = 2
              )
            }
          }

          Icon(Icons.Default.ArrowForward, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(20.dp))
        }
      }
    }

    // Quick Actions Grid (6 Core Actions)
    item {
      Text(
        text = "QUICK ACTIONS",
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        color = TextSecondaryDark
      )
      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        QuickActionButton(
          label = "Upload File",
          icon = Icons.Default.Upload,
          color = CyanNeon,
          modifier = Modifier.weight(1f),
          onClick = onQuickUpload
        )
        QuickActionButton(
          label = "Scan Doc",
          icon = Icons.Default.PhotoCamera,
          color = NebulaPink,
          modifier = Modifier.weight(1f),
          onClick = { onNavigate(NexoraScreen.PRODUCTIVITY) }
        )
        QuickActionButton(
          label = "Nexora AI",
          icon = Icons.Default.AutoAwesome,
          color = VioletNeon,
          modifier = Modifier.weight(1f),
          onClick = { onNavigate(NexoraScreen.AI_ASSISTANT) }
        )
        QuickActionButton(
          label = "Vault",
          icon = Icons.Default.Lock,
          color = AmberWarning,
          modifier = Modifier.weight(1f),
          onClick = { onNavigate(NexoraScreen.VAULT) }
        )
      }
    }

    // Storage Infrastructure Status Strip
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .background(CosmicCardElevated)
          .border(1.dp, BorderSubtleDark, RoundedCornerShape(16.dp))
          .clickable { onNavigate(NexoraScreen.ADMIN) }
          .padding(14.dp)
          .testTag("server_cluster_strip")
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0x3300F0FF)),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.Dns, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "${servers.size} Active Storage Clusters",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimaryDark
              )
              Text(
                text = "S3 Object Store • Ceph • MinIO • Bare Metal",
                fontSize = 11.sp,
                color = TextSecondaryDark
              )
            }
          }

          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "Cluster Health",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = CyanNeon
            )
            Icon(Icons.Default.ArrowForward, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(14.dp))
          }
        }
      }
    }

    // Recent Files Header & List
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "RECENT FILES",
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 1.sp,
          color = TextSecondaryDark
        )
        Text(
          text = "View All",
          fontSize = 12.sp,
          fontWeight = FontWeight.SemiBold,
          color = CyanNeon,
          modifier = Modifier
            .clickable { onNavigate(NexoraScreen.FILES) }
            .testTag("view_all_files_button")
        )
      }
    }

    if (recentFiles.isEmpty()) {
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CosmicCardDark)
            .padding(24.dp),
          contentAlignment = Alignment.Center
        ) {
          Text("No files uploaded yet. Tap Upload File to begin.", color = TextSecondaryDark, fontSize = 13.sp)
        }
      }
    } else {
      items(recentFiles.take(4), key = { it.id }) { file ->
        FileCard(
          file = file,
          onClick = { onOpenFile(file) },
          onToggleFavorite = { onToggleFavorite(file) },
          onShare = { onShareFile(file) },
          onMoveToTrash = { onMoveToTrash(file) },
          onToggleVault = { onToggleVault(file) }
        )
      }
    }

    // Recent Activity Section
    item {
      Text(
        text = "SYSTEM & REPLICATION ACTIVITY",
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        color = TextSecondaryDark
      )
    }

    items(activities.take(3), key = { it.id }) { act ->
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(14.dp))
          .background(CosmicCardDark)
          .border(1.dp, BorderSubtleDark, RoundedCornerShape(14.dp))
          .padding(12.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(34.dp)
              .clip(CircleShape)
              .background(Color(0x228B5CF6)),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.CloudSync, contentDescription = null, tint = VioletNeon, modifier = Modifier.size(18.dp))
          }
          Spacer(modifier = Modifier.width(12.dp))
          Column(modifier = Modifier.weight(1f)) {
            Text(text = act.title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimaryDark)
            Text(text = act.details, fontSize = 11.sp, color = TextSecondaryDark, maxLines = 1)
          }
          Text(text = act.timestamp, fontSize = 11.sp, color = TextSecondaryDark)
        }
      }
    }

    item { Spacer(modifier = Modifier.height(20.dp)) }
  }
}

@Composable
fun QuickActionButton(
  label: String,
  icon: ImageVector,
  color: Color,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(16.dp))
      .background(CosmicCardDark)
      .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
      .clickable { onClick() }
      .padding(vertical = 14.dp, horizontal = 6.dp)
      .testTag("quick_action_${label.lowercase().replace(" ", "_")}"),
    contentAlignment = Alignment.Center
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Box(
        modifier = Modifier
          .size(40.dp)
          .clip(CircleShape)
          .background(color.copy(alpha = 0.18f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(imageVector = icon, contentDescription = label, tint = color, modifier = Modifier.size(20.dp))
      }
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = label,
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        color = TextPrimaryDark,
        maxLines = 1
      )
    }
  }
}
