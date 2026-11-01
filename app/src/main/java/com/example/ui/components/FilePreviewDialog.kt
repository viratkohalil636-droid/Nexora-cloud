package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import com.example.data.model.CloudFile
import com.example.data.repository.NexoraRepository
import com.example.ui.theme.BorderSubtleDark
import com.example.ui.theme.CosmicCardDark
import com.example.ui.theme.CosmicSurfaceDark
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.IndigoNeon
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.theme.VioletNeon

@Composable
fun FilePreviewDialog(
  file: CloudFile,
  onDismiss: () -> Unit,
  onShare: () -> Unit,
  onSummarizeAi: (String) -> Unit
) {
  var isPlaying by remember { mutableStateOf(false) }
  var playbackProgress by remember { mutableFloatStateOf(0.35f) }
  var showVersionHistory by remember { mutableStateOf(false) }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = CosmicSurfaceDark,
      border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtleDark),
      modifier = Modifier
        .fillMaxWidth()
        .heightIn(max = 680.dp)
        .testTag("file_preview_dialog")
    ) {
      Column(
        modifier = Modifier
          .padding(20.dp)
          .verticalScroll(rememberScrollState())
      ) {
        // Dialog Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = file.name,
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              color = TextPrimaryDark,
              maxLines = 1
            )
            Text(
              text = "${file.extension} • ${NexoraRepository.formatBytes(file.sizeBytes)} • ${file.category}",
              fontSize = 12.sp,
              color = CyanNeon
            )
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier.size(36.dp).testTag("close_preview_button")
          ) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondaryDark)
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Preview Window Depending on Format
        when (file.category) {
          "VIDEOS" -> {
            VideoPreviewPlayer(
              isPlaying = isPlaying,
              onTogglePlay = { isPlaying = !isPlaying },
              progress = playbackProgress
            )
          }
          "AUDIO" -> {
            AudioWaveformPlayer(
              isPlaying = isPlaying,
              onTogglePlay = { isPlaying = !isPlaying }
            )
          }
          "PHOTOS" -> {
            PhotoViewerCanvas(fileName = file.name)
          }
          else -> {
            DocumentContentPreview(
              textContent = file.textContent,
              summary = file.contentSummary
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Security & Verification Badge
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0x1510B981))
            .border(1.dp, EmeraldSuccess.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(12.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Security, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text("End-to-End Encrypted (AES-256-GCM)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = EmeraldSuccess)
              Text("SHA-256: ${file.checksum.take(16)}...", fontSize = 10.sp, color = TextSecondaryDark, fontFamily = FontFamily.Monospace)
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Action Buttons (Nexora AI Summarize, Version History, Share)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Button(
            onClick = {
              onDismiss()
              onSummarizeAi("Summarize ${file.name}")
            },
            colors = ButtonDefaults.buttonColors(containerColor = VioletNeon),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.weight(1f).testTag("summarize_ai_button")
          ) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("AI Summary", fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }

          Button(
            onClick = onShare,
            colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = Color.Black),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.weight(1f).testTag("share_preview_button")
          ) {
            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Share Link", fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}

@Composable
fun VideoPreviewPlayer(
  isPlaying: Boolean,
  onTogglePlay: () -> Unit,
  progress: Float
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .height(180.dp)
      .clip(RoundedCornerShape(16.dp))
      .background(Color.Black)
      .border(1.dp, BorderSubtleDark, RoundedCornerShape(16.dp)),
    contentAlignment = Alignment.Center
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      IconButton(
        onClick = onTogglePlay,
        modifier = Modifier
          .size(54.dp)
          .clip(CircleShape)
          .background(CyanNeon)
      ) {
        Icon(
          imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
          contentDescription = "Play/Pause",
          tint = Color.Black,
          modifier = Modifier.size(32.dp)
        )
      }
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = if (isPlaying) "Streaming 1080p from Cloud Node..." else "Tap to Play Video Stream",
        fontSize = 12.sp,
        color = Color.White
      )
    }

    // Bottom scrub bar
    Box(
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .fillMaxWidth()
        .height(4.dp)
        .background(Color.DarkGray)
    ) {
      Box(
        modifier = Modifier
          .fillMaxWidth(progress)
          .height(4.dp)
          .background(CyanNeon)
      )
    }
  }
}

@Composable
fun AudioWaveformPlayer(
  isPlaying: Boolean,
  onTogglePlay: () -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .background(CosmicCardDark)
      .border(1.dp, IndigoNeon.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
      .padding(16.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(
        onClick = onTogglePlay,
        modifier = Modifier
          .size(48.dp)
          .clip(CircleShape)
          .background(IndigoNeon)
      ) {
        Icon(
          imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
          contentDescription = "Play Audio",
          tint = Color.White,
          modifier = Modifier.size(28.dp)
        )
      }
      Spacer(modifier = Modifier.width(14.dp))
      Column(modifier = Modifier.weight(1f)) {
        Text("Lossless 24-bit 96kHz Master", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimaryDark)
        Spacer(modifier = Modifier.height(4.dp))
        Row(
          horizontalArrangement = Arrangement.spacedBy(3.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          listOf(14, 28, 42, 20, 36, 12, 48, 22, 38, 16, 26, 44, 30, 18, 40, 24).forEach { h ->
            Box(
              modifier = Modifier
                .width(4.dp)
                .height(h.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(if (isPlaying) CyanNeon else VioletNeon)
            )
          }
        }
      }
    }
  }
}

@Composable
fun PhotoViewerCanvas(fileName: String) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .height(200.dp)
      .clip(RoundedCornerShape(16.dp))
      .background(Color(0xFF0B132B))
      .border(1.dp, BorderSubtleDark, RoundedCornerShape(16.dp)),
    contentAlignment = Alignment.Center
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(36.dp))
      Spacer(modifier = Modifier.height(8.dp))
      Text("Ultra HD Preview: $fileName", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimaryDark)
      Text("Rendered with high-fidelity color profile", fontSize = 11.sp, color = TextSecondaryDark)
    }
  }
}

@Composable
fun DocumentContentPreview(textContent: String, summary: String) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .background(CosmicCardDark)
      .border(1.dp, BorderSubtleDark, RoundedCornerShape(16.dp))
      .padding(14.dp)
  ) {
    Text(
      text = "DOCUMENT PREVIEW",
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold,
      letterSpacing = 1.sp,
      color = CyanNeon
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
      text = if (textContent.isNotBlank()) textContent else summary.ifBlank { "No text content available." },
      fontSize = 13.sp,
      lineHeight = 18.sp,
      color = TextPrimaryDark,
      fontFamily = FontFamily.Monospace
    )
  }
}
