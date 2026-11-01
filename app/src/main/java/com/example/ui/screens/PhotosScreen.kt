package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CloudFile
import com.example.data.repository.NexoraRepository
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.BorderSubtleDark
import com.example.ui.theme.CosmicCardDark
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.NebulaPink
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.theme.VioletNeon

@Composable
fun PhotosScreen(
  files: List<CloudFile>,
  autoBackupEnabled: Boolean,
  onToggleAutoBackup: (Boolean) -> Unit,
  onOpenFile: (CloudFile) -> Unit,
  onToggleFavorite: (CloudFile) -> Unit
) {
  val mediaFiles = files.filter { it.category == "PHOTOS" || it.category == "VIDEOS" }

  LazyVerticalGrid(
    columns = GridCells.Fixed(2),
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    horizontalArrangement = Arrangement.spacedBy(10.dp),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    item(span = { GridItemSpan(2) }) { Spacer(modifier = Modifier.height(6.dp)) }

    // Auto Photo Backup Status Bar
    item(span = { GridItemSpan(2) }) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(18.dp))
          .background(CosmicCardDark)
          .border(1.dp, BorderSubtleDark, RoundedCornerShape(18.dp))
          .padding(16.dp)
          .testTag("auto_backup_banner")
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(if (autoBackupEnabled) Color(0x3310B981) else Color(0x22FFFFFF)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = if (autoBackupEnabled) Icons.Default.CloudDone else Icons.Default.Sync,
                contentDescription = null,
                tint = if (autoBackupEnabled) EmeraldSuccess else TextSecondaryDark,
                modifier = Modifier.size(24.dp)
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "Camera Auto-Backup",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimaryDark
              )
              Text(
                text = if (autoBackupEnabled) "Real-time sync over Wi-Fi active" else "Paused to save battery",
                fontSize = 11.sp,
                color = if (autoBackupEnabled) EmeraldSuccess else TextSecondaryDark
              )
            }
          }

          Switch(
            checked = autoBackupEnabled,
            onCheckedChange = onToggleAutoBackup,
            colors = SwitchDefaults.colors(
              checkedThumbColor = EmeraldSuccess,
              checkedTrackColor = EmeraldSuccess.copy(alpha = 0.5f)
            ),
            modifier = Modifier.testTag("auto_backup_toggle")
          )
        }
      }
    }

    item(span = { GridItemSpan(2) }) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "ALL PHOTOS & VIDEOS (${mediaFiles.size})",
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 1.sp,
          color = TextSecondaryDark
        )
      }
    }

    if (mediaFiles.isEmpty()) {
      item(span = { GridItemSpan(2) }) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CosmicCardDark)
            .padding(32.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Image, contentDescription = null, tint = TextSecondaryDark, modifier = Modifier.size(36.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text("No photos or videos yet.", color = TextPrimaryDark, fontSize = 14.sp)
          }
        }
      }
    } else {
      items(mediaFiles, key = { it.id }) { item ->
        MediaGridCard(
          file = item,
          onClick = { onOpenFile(item) },
          onToggleFavorite = { onToggleFavorite(item) }
        )
      }
    }

    item(span = { GridItemSpan(2) }) { Spacer(modifier = Modifier.height(24.dp)) }
  }
}

@Composable
fun MediaGridCard(
  file: CloudFile,
  onClick: () -> Unit,
  onToggleFavorite: () -> Unit
) {
  val isVideo = file.category == "VIDEOS"

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .aspectRatio(1f)
      .clip(RoundedCornerShape(16.dp))
      .background(
        Brush.verticalGradient(
          colors = if (isVideo) listOf(Color(0xFF2E1065), Color(0xFF0F172A))
          else listOf(Color(0xFF831843), Color(0xFF0F172A))
        )
      )
      .border(1.dp, BorderSubtleDark, RoundedCornerShape(16.dp))
      .clickable { onClick() }
      .padding(10.dp)
      .testTag("media_card_${file.id}")
  ) {
    // Favorite Star Top Right
    IconButton(
      onClick = onToggleFavorite,
      modifier = Modifier
        .align(Alignment.TopEnd)
        .size(30.dp)
    ) {
      Icon(
        imageVector = Icons.Default.Star,
        contentDescription = "Favorite",
        tint = if (file.isFavorite) AmberWarning else Color.White.copy(alpha = 0.5f),
        modifier = Modifier.size(18.dp)
      )
    }

    // Center icon
    Box(
      modifier = Modifier.align(Alignment.Center),
      contentAlignment = Alignment.Center
    ) {
      if (isVideo) {
        Icon(Icons.Default.PlayCircle, contentDescription = null, tint = VioletNeon, modifier = Modifier.size(44.dp))
      } else {
        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = NebulaPink, modifier = Modifier.size(36.dp))
      }
    }

    // Bottom file info overlay
    Column(
      modifier = Modifier
        .align(Alignment.BottomStart)
        .fillMaxWidth()
    ) {
      Text(
        text = file.name,
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        color = Color.White,
        maxLines = 1
      )
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = NexoraRepository.formatBytes(file.sizeBytes),
          fontSize = 10.sp,
          color = CyanNeon
        )
        Text(
          text = if (isVideo) "1080p HD" else "4K RAW",
          fontSize = 9.sp,
          fontWeight = FontWeight.Bold,
          color = Color.White.copy(alpha = 0.7f)
        )
      }
    }
  }
}
