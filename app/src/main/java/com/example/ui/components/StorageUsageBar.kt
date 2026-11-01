package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.NexoraRepository
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.BorderSubtleDark
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.IndigoNeon
import com.example.ui.theme.NebulaPink
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.theme.VioletNeon
import com.example.viewmodel.StorageBreakdown

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StorageUsageBar(
  breakdown: StorageBreakdown,
  modifier: Modifier = Modifier
) {
  val total = breakdown.totalCapacityBytes.coerceAtLeast(1L)
  val used = breakdown.usedBytes
  val usedPercent = ((used.toDouble() / total.toDouble()) * 100).coerceIn(0.1, 100.0)

  Column(modifier = modifier.fillMaxWidth()) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = "CLOUD STORAGE POOL",
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 1.2.sp,
          color = CyanNeon
        )
        Text(
          text = "${NexoraRepository.formatBytes(used)} of ${NexoraRepository.formatBytes(total)} used",
          fontSize = 15.sp,
          fontWeight = FontWeight.SemiBold,
          color = TextPrimaryDark
        )
      }
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(8.dp))
          .background(Color(0x3300F0FF))
          .padding(horizontal = 8.dp, vertical = 4.dp)
      ) {
        Text(
          text = String.format("%.2f%%", usedPercent),
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          color = CyanNeon
        )
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Segmented Storage Progress Bar
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(10.dp)
        .clip(RoundedCornerShape(5.dp))
        .background(BorderSubtleDark)
    ) {
      Row(modifier = Modifier.fillMaxWidth()) {
        val photosWeight = (breakdown.photosBytes.toFloat() / total).coerceAtLeast(0.015f)
        val videosWeight = (breakdown.videosBytes.toFloat() / total).coerceAtLeast(0.02f)
        val docsWeight = (breakdown.documentsBytes.toFloat() / total).coerceAtLeast(0.015f)
        val vaultWeight = (breakdown.vaultBytes.toFloat() / total).coerceAtLeast(0.01f)
        val audioWeight = (breakdown.audioBytes.toFloat() / total).coerceAtLeast(0.01f)

        Box(modifier = Modifier.weight(photosWeight).height(10.dp).background(NebulaPink))
        Box(modifier = Modifier.weight(videosWeight).height(10.dp).background(VioletNeon))
        Box(modifier = Modifier.weight(docsWeight).height(10.dp).background(CyanNeon))
        Box(modifier = Modifier.weight(vaultWeight).height(10.dp).background(AmberWarning))
        Box(modifier = Modifier.weight(audioWeight).height(10.dp).background(IndigoNeon))
        val remaining = (1f - (photosWeight + videosWeight + docsWeight + vaultWeight + audioWeight)).coerceAtLeast(0.05f)
        Box(modifier = Modifier.weight(remaining).height(10.dp).background(Color(0x22FFFFFF)))
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Legend items
    FlowRow(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(12.dp),
      verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      LegendItem(color = NebulaPink, label = "Photos", size = breakdown.photosBytes)
      LegendItem(color = VioletNeon, label = "Videos", size = breakdown.videosBytes)
      LegendItem(color = CyanNeon, label = "Documents", size = breakdown.documentsBytes)
      LegendItem(color = AmberWarning, label = "Vault", size = breakdown.vaultBytes)
      LegendItem(color = IndigoNeon, label = "Audio", size = breakdown.audioBytes)
    }
  }
}

@Composable
private fun LegendItem(color: Color, label: String, size: Long) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    Box(
      modifier = Modifier
        .size(8.dp)
        .clip(CircleShape)
        .background(color)
    )
    Spacer(modifier = Modifier.width(5.dp))
    Text(
      text = "$label: ${NexoraRepository.formatBytes(size)}",
      fontSize = 11.sp,
      color = TextSecondaryDark
    )
  }
}
