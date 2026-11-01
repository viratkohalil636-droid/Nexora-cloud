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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.ui.theme.CosmicSurfaceDark
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.theme.VioletNeon
import com.example.viewmodel.NexoraScreen

@Composable
fun NexoraTopBar(
  currentScreen: NexoraScreen,
  userProfile: UserProfile,
  onNavigate: (NexoraScreen) -> Unit,
  onOpenAi: () -> Unit,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .background(CosmicSurfaceDark.copy(alpha = 0.95f))
      .statusBarsPadding()
      .border(width = 1.dp, color = BorderSubtleDark)
      .padding(horizontal = 16.dp, vertical = 10.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Left: Logo & Brand
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
          .testTag("brand_logo_button")
          .clickable { onNavigate(NexoraScreen.HOME) }
      ) {
        Box(
          modifier = Modifier
            .size(38.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF0F172A))
            .border(1.dp, CyanNeon.copy(alpha = 0.6f), RoundedCornerShape(10.dp)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.CloudDone,
            contentDescription = "Nexora Logo",
            tint = CyanNeon,
            modifier = Modifier.size(22.dp)
          )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "NEXORA",
              fontSize = 17.sp,
              fontWeight = FontWeight.Black,
              letterSpacing = 1.5.sp,
              color = TextPrimaryDark
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "CLOUD",
              fontSize = 17.sp,
              fontWeight = FontWeight.Light,
              letterSpacing = 1.2.sp,
              color = CyanNeon
            )
          }
          Text(
            text = currentScreen.label.uppercase(),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            color = VioletNeon
          )
        }
      }

      // Right: Actions (Trial Pill, AI Trigger, Profile Avatar)
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        // 90-Day Free Trial Indicator Pill
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0x22F59E0B))
            .border(1.dp, AmberWarning.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
            .clickable { onNavigate(NexoraScreen.SUBSCRIPTION) }
            .padding(horizontal = 8.dp, vertical = 5.dp)
            .testTag("trial_pill_button"),
          contentAlignment = Alignment.Center
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.WorkspacePremium,
              contentDescription = "Trial",
              tint = AmberWarning,
              modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "${userProfile.trialDaysRemaining}d Free Trial",
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = AmberWarning
            )
          }
        }

        // Nexora AI Assistant Quick Button
        IconButton(
          onClick = onOpenAi,
          modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(Color(0x2200F0FF))
            .border(1.dp, CyanNeon.copy(alpha = 0.5f), CircleShape)
            .testTag("topbar_ai_assistant_button")
        ) {
          Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = "Open Nexora AI Assistant",
            tint = CyanNeon,
            modifier = Modifier.size(18.dp)
          )
        }

        // User Avatar / Settings Trigger
        Box(
          modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(Color(0xFF3B1B75))
            .border(1.dp, VioletNeon, CircleShape)
            .clickable { onNavigate(NexoraScreen.SETTINGS) }
            .testTag("topbar_avatar_button"),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "AV",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
        }
      }
    }
  }
}
