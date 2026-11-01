package com.example.ui.components

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
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CloudFile
import com.example.data.repository.NexoraRepository
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.BorderSubtleDark
import com.example.ui.theme.CosmicCardDark
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.IndigoNeon
import com.example.ui.theme.NebulaPink
import com.example.ui.theme.RoseDanger
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.theme.VioletNeon
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun FileCard(
  file: CloudFile,
  onClick: () -> Unit,
  onToggleFavorite: () -> Unit,
  onShare: () -> Unit,
  onMoveToTrash: () -> Unit,
  onToggleVault: () -> Unit,
  modifier: Modifier = Modifier
) {
  var menuExpanded by remember { mutableStateOf(false) }

  val (extColor, extIcon) = getExtensionStyling(file.extension)
  val dateStr = remember(file.updatedAt) {
    SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(file.updatedAt))
  }

  Box(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .background(CosmicCardDark)
      .border(1.dp, BorderSubtleDark, RoundedCornerShape(16.dp))
      .clickable { onClick() }
      .padding(14.dp)
      .testTag("file_card_${file.id}")
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Extension Badge / Thumbnail Icon
      Box(
        modifier = Modifier
          .size(46.dp)
          .clip(RoundedCornerShape(12.dp))
          .background(extColor.copy(alpha = 0.18f))
          .border(1.dp, extColor.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = extIcon,
          contentDescription = file.extension,
          tint = extColor,
          modifier = Modifier.size(24.dp)
        )
      }

      Spacer(modifier = Modifier.width(14.dp))

      // File Details
      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = file.name,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimaryDark,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f, fill = false)
          )
          if (file.isVault) {
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
              imageVector = Icons.Default.Lock,
              contentDescription = "Zero-Knowledge Encrypted",
              tint = AmberWarning,
              modifier = Modifier.size(14.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Text(
            text = NexoraRepository.formatBytes(file.sizeBytes),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = CyanNeon
          )
          Text(
            text = "•",
            fontSize = 10.sp,
            color = TextSecondaryDark
          )
          Text(
            text = dateStr,
            fontSize = 12.sp,
            color = TextSecondaryDark
          )
          if (file.versionCount > 1) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0x338B5CF6))
                .padding(horizontal = 4.dp, vertical = 1.dp)
            ) {
              Text(
                text = "v${file.versionCount}",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = VioletNeon
              )
            }
          }
        }
      }

      // Action Buttons
      Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(
          onClick = onToggleFavorite,
          modifier = Modifier.size(36.dp).testTag("star_file_${file.id}")
        ) {
          Icon(
            imageVector = if (file.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
            contentDescription = "Favorite",
            tint = if (file.isFavorite) AmberWarning else TextSecondaryDark,
            modifier = Modifier.size(20.dp)
          )
        }

        Box {
          IconButton(
            onClick = { menuExpanded = true },
            modifier = Modifier.size(36.dp).testTag("more_file_${file.id}")
          ) {
            Icon(
              imageVector = Icons.Default.MoreVert,
              contentDescription = "Actions",
              tint = TextSecondaryDark,
              modifier = Modifier.size(20.dp)
            )
          }

          DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = { menuExpanded = false },
            modifier = Modifier.background(CosmicCardDark)
          ) {
            DropdownMenuItem(
              text = { Text("Preview File", color = TextPrimaryDark) },
              leadingIcon = { Icon(Icons.Default.Visibility, contentDescription = null, tint = CyanNeon) },
              onClick = {
                menuExpanded = false
                onClick()
              }
            )
            DropdownMenuItem(
              text = { Text("Share Link & QR", color = TextPrimaryDark) },
              leadingIcon = { Icon(Icons.Default.Share, contentDescription = null, tint = VioletNeon) },
              onClick = {
                menuExpanded = false
                onShare()
              }
            )
            DropdownMenuItem(
              text = {
                Text(
                  if (file.isVault) "Move out of Vault" else "Move to Private Vault",
                  color = TextPrimaryDark
                )
              },
              leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = AmberWarning) },
              onClick = {
                menuExpanded = false
                onToggleVault()
              }
            )
            DropdownMenuItem(
              text = { Text("Move to Trash", color = RoseDanger) },
              onClick = {
                menuExpanded = false
                onMoveToTrash()
              }
            )
          }
        }
      }
    }
  }
}

fun getExtensionStyling(ext: String): Pair<Color, ImageVector> {
  return when (ext.uppercase()) {
    "PDF" -> Pair(RoseDanger, Icons.Default.Description)
    "DOC", "DOCX", "TXT", "ODT", "RTF" -> Pair(CyanNeon, Icons.Default.Description)
    "XLS", "XLSX", "CSV" -> Pair(EmeraldSuccess, Icons.Default.Description)
    "PPT", "PPTX" -> Pair(AmberWarning, Icons.Default.Description)
    "JPG", "JPEG", "PNG", "WEBP", "HEIC", "SVG" -> Pair(NebulaPink, Icons.Default.Image)
    "MP4", "MOV", "MKV", "AVI", "WEBM" -> Pair(VioletNeon, Icons.Default.Movie)
    "MP3", "WAV", "FLAC", "AAC", "M4A" -> Pair(IndigoNeon, Icons.Default.Audiotrack)
    "JSON", "JS", "TS", "PY", "JAVA", "HTML", "CSS", "SQL" -> Pair(CyanNeon, Icons.Default.Code)
    "ZIP", "RAR", "7Z", "TAR", "GZ" -> Pair(AmberWarning, Icons.Default.FolderZip)
    else -> Pair(TextSecondaryDark, Icons.Default.Description)
  }
}
