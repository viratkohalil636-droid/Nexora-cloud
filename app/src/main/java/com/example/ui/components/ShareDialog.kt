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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.CloudFile
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
fun ShareDialog(
  file: CloudFile,
  onDismiss: () -> Unit
) {
  var selectedExpiry by remember { mutableStateOf("24 Hours") }
  var passwordProtected by remember { mutableStateOf(false) }
  var passwordText by remember { mutableStateOf("") }
  var viewOnlyMode by remember { mutableStateOf(false) }
  var copiedSuccess by remember { mutableStateOf(false) }

  val shareLink = "https://share.nexora.cloud/s/${file.id.take(8)}"

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = CosmicSurfaceDark,
      border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtleDark),
      modifier = Modifier
        .fillMaxWidth()
        .testTag("share_dialog")
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
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0x3300F0FF)),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.QrCode2, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text("Secure Cloud Share", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
          }

          IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondaryDark)
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
          text = "Sharing: ${file.name}",
          fontSize = 13.sp,
          fontWeight = FontWeight.SemiBold,
          color = CyanNeon
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Expiry Selector
        Text("Link Expiration", fontSize = 12.sp, color = TextSecondaryDark)
        Spacer(modifier = Modifier.height(6.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          listOf("1 Hour", "24 Hours", "7 Days", "Permanent").forEach { exp ->
            FilterChip(
              selected = selectedExpiry == exp,
              onClick = { selectedExpiry = exp },
              label = { Text(exp, fontSize = 11.sp) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = CyanNeon,
                selectedLabelColor = Color.Black,
                containerColor = CosmicCardDark,
                labelColor = TextSecondaryDark
              )
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Password Protection Toggle
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Lock, contentDescription = null, tint = AmberWarning, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Password Protect Link", fontSize = 13.sp, color = TextPrimaryDark)
          }
          Switch(
            checked = passwordProtected,
            onCheckedChange = { passwordProtected = it },
            colors = SwitchDefaults.colors(checkedThumbColor = AmberWarning, checkedTrackColor = AmberWarning.copy(alpha = 0.5f))
          )
        }

        if (passwordProtected) {
          Spacer(modifier = Modifier.height(8.dp))
          OutlinedTextField(
            value = passwordText,
            onValueChange = { passwordText = it },
            placeholder = { Text("Set access passkey/PIN", color = TextSecondaryDark, fontSize = 12.sp) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
              focusedTextColor = TextPrimaryDark,
              unfocusedTextColor = TextPrimaryDark,
              focusedBorderColor = AmberWarning,
              unfocusedBorderColor = BorderSubtleDark
            )
          )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Link Box with Copy Button
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CosmicCardDark)
            .border(1.dp, BorderSubtleDark, RoundedCornerShape(12.dp))
            .padding(12.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = shareLink,
              fontSize = 12.sp,
              color = CyanNeon,
              fontFamily = FontFamily.Monospace,
              maxLines = 1,
              modifier = Modifier.weight(1f)
            )

            IconButton(
              onClick = { copiedSuccess = true },
              modifier = Modifier.size(32.dp).testTag("copy_share_link_button")
            ) {
              Icon(
                imageVector = Icons.Default.ContentCopy,
                contentDescription = "Copy Link",
                tint = if (copiedSuccess) EmeraldSuccess else CyanNeon,
                modifier = Modifier.size(18.dp)
              )
            }
          }
        }

        if (copiedSuccess) {
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "Link copied to clipboard! Ready to share.",
            fontSize = 11.sp,
            color = EmeraldSuccess,
            fontWeight = FontWeight.Medium
          )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
          onClick = onDismiss,
          colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = Color.Black),
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
          Text("Done", fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}
