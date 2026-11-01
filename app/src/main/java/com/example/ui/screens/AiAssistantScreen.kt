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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.data.model.AiChatMessage
import com.example.ui.theme.BorderSubtleDark
import com.example.ui.theme.CosmicCardDark
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.theme.VioletNeon

@Composable
fun AiAssistantScreen(
  messages: List<AiChatMessage>,
  onSendMessage: (String) -> Unit
) {
  var inputText by remember { mutableStateOf("") }

  val samplePrompts = listOf(
    "Find duplicate files",
    "Summarize architecture whitepaper",
    "Show financial audit reports",
    "Recommend storage optimization",
    "Find 4K wallpaper photos"
  )

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp)
      .testTag("ai_assistant_screen")
  ) {
    Spacer(modifier = Modifier.height(6.dp))

    // Header info banner
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(16.dp))
        .background(Color(0x228B5CF6))
        .border(1.dp, VioletNeon.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
        .padding(14.dp)
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(VioletNeon),
          contentAlignment = Alignment.Center
        ) {
          Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
          Text("NEXORA AI ENGINE", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
          Text("Natural language search, OCR, document summaries & storage health.", fontSize = 11.sp, color = TextSecondaryDark)
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Quick Prompt Pills
    LazyRow(
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      items(samplePrompts) { prompt ->
        FilterChip(
          selected = false,
          onClick = { onSendMessage(prompt) },
          label = { Text(prompt, fontSize = 11.sp) },
          leadingIcon = { Icon(Icons.Default.ElectricBolt, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(13.dp)) },
          colors = FilterChipDefaults.filterChipColors(
            containerColor = CosmicCardDark,
            labelColor = TextPrimaryDark
          )
        )
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Chat Messages Timeline
    LazyColumn(
      modifier = Modifier
        .weight(1f)
        .fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      items(messages, key = { it.id }) { msg ->
        AiChatBubble(message = msg, onActionClick = { onSendMessage(it) })
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Input Bar
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      OutlinedTextField(
        value = inputText,
        onValueChange = { inputText = it },
        modifier = Modifier
          .weight(1f)
          .testTag("ai_input_text_field"),
        placeholder = { Text("Ask Nexora AI anything about your cloud...", color = TextSecondaryDark, fontSize = 12.sp) },
        shape = RoundedCornerShape(20.dp),
        colors = OutlinedTextFieldDefaults.colors(
          focusedTextColor = TextPrimaryDark,
          unfocusedTextColor = TextPrimaryDark,
          focusedContainerColor = CosmicCardDark,
          unfocusedContainerColor = CosmicCardDark,
          focusedBorderColor = CyanNeon,
          unfocusedBorderColor = BorderSubtleDark
        )
      )

      Spacer(modifier = Modifier.width(8.dp))

      IconButton(
        onClick = {
          if (inputText.isNotBlank()) {
            onSendMessage(inputText)
            inputText = ""
          }
        },
        modifier = Modifier
          .size(48.dp)
          .clip(CircleShape)
          .background(CyanNeon)
          .testTag("send_ai_message_button")
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.Send,
          contentDescription = "Send",
          tint = Color.Black,
          modifier = Modifier.size(20.dp)
        )
      }
    }
  }
}

@Composable
fun AiChatBubble(
  message: AiChatMessage,
  onActionClick: (String) -> Unit
) {
  val isUser = message.isUser

  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
  ) {
    if (!isUser) {
      Box(
        modifier = Modifier
          .size(32.dp)
          .clip(CircleShape)
          .background(VioletNeon),
        contentAlignment = Alignment.Center
      ) {
        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
      }
      Spacer(modifier = Modifier.width(8.dp))
    }

    Column(
      modifier = Modifier
        .fillMaxWidth(if (isUser) 0.8f else 0.88f)
    ) {
      Box(
        modifier = Modifier
          .clip(
            RoundedCornerShape(
              topStart = 16.dp,
              topEnd = 16.dp,
              bottomStart = if (isUser) 16.dp else 4.dp,
              bottomEnd = if (isUser) 4.dp else 16.dp
            )
          )
          .background(if (isUser) CyanNeon else CosmicCardDark)
          .border(
            1.dp,
            if (isUser) Color.Transparent else BorderSubtleDark,
            RoundedCornerShape(16.dp)
          )
          .padding(14.dp)
      ) {
        Column {
          Text(
            text = message.text,
            fontSize = 13.sp,
            lineHeight = 18.sp,
            color = if (isUser) Color.Black else TextPrimaryDark
          )

          if (message.matchedFiles.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
              message.matchedFiles.forEach { mf ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.Description, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(12.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(mf, fontSize = 11.sp, color = CyanNeon, fontWeight = FontWeight.SemiBold)
                }
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(4.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
      ) {
        Text(message.timestamp, fontSize = 10.sp, color = TextSecondaryDark)
      }

      if (message.actionSuggestion != null) {
        Spacer(modifier = Modifier.height(4.dp))
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0x3300F0FF))
            .clickable { onActionClick(message.actionSuggestion) }
            .padding(horizontal = 10.dp, vertical = 5.dp)
        ) {
          Text("Suggested Action: ${message.actionSuggestion}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CyanNeon)
        }
      }
    }
  }
}
