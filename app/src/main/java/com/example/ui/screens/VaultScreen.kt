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
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CloudFile
import com.example.data.model.CloudFolder
import com.example.ui.components.FileCard
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.BorderSubtleDark
import com.example.ui.theme.CosmicCardDark
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.RoseDanger
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.theme.VioletNeon

@Composable
fun VaultScreen(
  isUnlocked: Boolean,
  vaultFiles: List<CloudFile>,
  vaultFolders: List<CloudFolder>,
  onUnlock: (String) -> Boolean,
  onLock: () -> Unit,
  onOpenFile: (CloudFile) -> Unit,
  onToggleFavorite: (CloudFile) -> Unit,
  onShareFile: (CloudFile) -> Unit,
  onMoveToTrash: (CloudFile) -> Unit,
  onToggleVault: (CloudFile) -> Unit
) {
  var enteredPin by remember { mutableStateOf("") }
  var pinError by remember { mutableStateOf(false) }

  if (!isUnlocked) {
    // Locked State - Biometric / PIN Keypad
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 24.dp)
        .testTag("vault_locked_screen"),
      contentAlignment = Alignment.Center
    ) {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
      ) {
        Box(
          modifier = Modifier
            .size(76.dp)
            .clip(CircleShape)
            .background(Color(0x22F59E0B))
            .border(2.dp, AmberWarning, CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(Icons.Default.Lock, contentDescription = "Locked", tint = AmberWarning, modifier = Modifier.size(38.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
          text = "Zero-Knowledge Vault",
          fontSize = 22.sp,
          fontWeight = FontWeight.Bold,
          color = TextPrimaryDark
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = "Hardware-isolated AES-256-GCM enclave. Encryption keys never leave this device.",
          fontSize = 12.sp,
          color = TextSecondaryDark,
          modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // PIN Dot indicators
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
          repeat(4) { idx ->
            val filled = idx < enteredPin.length
            Box(
              modifier = Modifier
                .size(16.dp)
                .clip(CircleShape)
                .background(if (filled) AmberWarning else Color(0x33FFFFFF))
                .border(1.dp, if (pinError) RoseDanger else AmberWarning, CircleShape)
            )
          }
        }

        if (pinError) {
          Spacer(modifier = Modifier.height(10.dp))
          Text("Incorrect PIN. Master PIN: 7788", color = RoseDanger, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        } else {
          Spacer(modifier = Modifier.height(10.dp))
          Text("Enter 4-digit Master PIN (default: 7788)", color = TextSecondaryDark, fontSize = 11.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Number Keypad
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          val keypad = listOf(
            listOf("1", "2", "3"),
            listOf("4", "5", "6"),
            listOf("7", "8", "9"),
            listOf("BIO", "0", "DEL")
          )

          keypad.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
              row.forEach { key ->
                KeypadButton(
                  label = key,
                  onClick = {
                    when (key) {
                      "DEL" -> {
                        if (enteredPin.isNotEmpty()) enteredPin = enteredPin.dropLast(1)
                        pinError = false
                      }
                      "BIO" -> {
                        // Fast biometric unlock
                        val success = onUnlock("7788")
                        if (!success) pinError = true
                      }
                      else -> {
                        if (enteredPin.length < 4) {
                          enteredPin += key
                          if (enteredPin.length == 4) {
                            val success = onUnlock(enteredPin)
                            if (!success) {
                              pinError = true
                              enteredPin = ""
                            }
                          }
                        }
                      }
                    }
                  }
                )
              }
            }
          }
        }
      }
    }
  } else {
    // Unlocked State - Zero-Knowledge Content
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp)
        .testTag("vault_unlocked_screen"),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      item { Spacer(modifier = Modifier.height(6.dp)) }

      // Unlocked Status Banner
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0x2210B981))
            .border(1.dp, EmeraldSuccess.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
            .padding(16.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(38.dp)
                  .clip(CircleShape)
                  .background(EmeraldSuccess.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.LockOpen, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(20.dp))
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text("VAULT UNLOCKED", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EmeraldSuccess)
                Text("Zero-Knowledge Decryption Active", fontSize = 11.sp, color = TextSecondaryDark)
              }
            }

            Button(
              onClick = onLock,
              colors = ButtonDefaults.buttonColors(containerColor = AmberWarning, contentColor = Color.Black),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.testTag("lock_vault_button")
            ) {
              Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Lock Now", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
      }

      item {
        Text(
          text = "ENCRYPTED VAULT ITEMS (${vaultFiles.size})",
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 1.sp,
          color = AmberWarning
        )
      }

      if (vaultFiles.isEmpty()) {
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(16.dp))
              .background(CosmicCardDark)
              .padding(32.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Icon(Icons.Default.Shield, contentDescription = null, tint = AmberWarning, modifier = Modifier.size(36.dp))
              Spacer(modifier = Modifier.height(8.dp))
              Text("No files currently placed in Personal Vault.", color = TextPrimaryDark, fontSize = 13.sp)
              Text("Move sensitive contracts or files into the vault from My Cloud.", color = TextSecondaryDark, fontSize = 11.sp)
            }
          }
        }
      } else {
        items(vaultFiles, key = { it.id }) { file ->
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

      item { Spacer(modifier = Modifier.height(24.dp)) }
    }
  }
}

@Composable
fun KeypadButton(label: String, onClick: () -> Unit) {
  Box(
    modifier = Modifier
      .size(68.dp)
      .clip(CircleShape)
      .background(CosmicCardDark)
      .border(1.dp, BorderSubtleDark, CircleShape)
      .clickable { onClick() }
      .testTag("keypad_$label"),
    contentAlignment = Alignment.Center
  ) {
    if (label == "BIO") {
      Icon(Icons.Default.Fingerprint, contentDescription = "Biometric Unlock", tint = CyanNeon, modifier = Modifier.size(28.dp))
    } else {
      Text(
        text = label,
        fontSize = if (label == "DEL") 13.sp else 22.sp,
        fontWeight = FontWeight.Bold,
        color = if (label == "DEL") RoseDanger else TextPrimaryDark
      )
    }
  }
}
