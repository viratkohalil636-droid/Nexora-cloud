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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Widgets
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BorderSubtleDark
import com.example.ui.theme.CosmicSurfaceDark
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextTertiaryDark
import com.example.viewmodel.NexoraScreen

@Composable
fun NexoraBottomBar(
  currentScreen: NexoraScreen,
  onNavigate: (NexoraScreen) -> Unit,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .background(CosmicSurfaceDark.copy(alpha = 0.96f))
      .border(width = 1.dp, color = BorderSubtleDark)
      .navigationBarsPadding()
      .padding(horizontal = 8.dp, vertical = 6.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceAround,
      verticalAlignment = Alignment.CenterVertically
    ) {
      BottomNavItem(
        screen = NexoraScreen.HOME,
        label = "Home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
        isSelected = currentScreen == NexoraScreen.HOME,
        onClick = { onNavigate(NexoraScreen.HOME) }
      )

      BottomNavItem(
        screen = NexoraScreen.FILES,
        label = "Files",
        selectedIcon = Icons.Filled.Folder,
        unselectedIcon = Icons.Outlined.Folder,
        isSelected = currentScreen == NexoraScreen.FILES,
        onClick = { onNavigate(NexoraScreen.FILES) }
      )

      BottomNavItem(
        screen = NexoraScreen.PHOTOS,
        label = "Photos",
        selectedIcon = Icons.Filled.Image,
        unselectedIcon = Icons.Outlined.Image,
        isSelected = currentScreen == NexoraScreen.PHOTOS,
        onClick = { onNavigate(NexoraScreen.PHOTOS) }
      )

      BottomNavItem(
        screen = NexoraScreen.VAULT,
        label = "Vault",
        selectedIcon = Icons.Filled.Lock,
        unselectedIcon = Icons.Outlined.Lock,
        isSelected = currentScreen == NexoraScreen.VAULT,
        onClick = { onNavigate(NexoraScreen.VAULT) }
      )

      BottomNavItem(
        screen = NexoraScreen.PRODUCTIVITY,
        label = "Tools",
        selectedIcon = Icons.Filled.Widgets,
        unselectedIcon = Icons.Outlined.Widgets,
        isSelected = currentScreen == NexoraScreen.PRODUCTIVITY ||
            currentScreen == NexoraScreen.BACKUP ||
            currentScreen == NexoraScreen.ANALYTICS ||
            currentScreen == NexoraScreen.ADMIN,
        onClick = { onNavigate(NexoraScreen.PRODUCTIVITY) }
      )
    }
  }
}

@Composable
private fun BottomNavItem(
  screen: NexoraScreen,
  label: String,
  selectedIcon: ImageVector,
  unselectedIcon: ImageVector,
  isSelected: Boolean,
  onClick: () -> Unit
) {
  Column(
    modifier = Modifier
      .minimumInteractiveComponentSize()
      .clip(RoundedCornerShape(12.dp))
      .clickable { onClick() }
      .padding(horizontal = 10.dp, vertical = 4.dp)
      .testTag("nav_${screen.name.lowercase()}"),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Box(
      modifier = Modifier
        .clip(RoundedCornerShape(16.dp))
        .background(if (isSelected) Color(0x3300F0FF) else Color.Transparent)
        .padding(horizontal = 16.dp, vertical = 4.dp),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = if (isSelected) selectedIcon else unselectedIcon,
        contentDescription = label,
        tint = if (isSelected) CyanNeon else TextTertiaryDark,
        modifier = Modifier.size(22.dp)
      )
    }
    Spacer(modifier = Modifier.height(2.dp))
    Text(
      text = label,
      fontSize = 11.sp,
      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
      color = if (isSelected) CyanNeon else TextTertiaryDark
    )
  }
}
