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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.window.Dialog
import com.example.data.model.CloudFile
import com.example.data.model.CloudFolder
import com.example.ui.components.FileCard
import com.example.ui.theme.BorderSubtleDark
import com.example.ui.theme.CosmicCardDark
import com.example.ui.theme.CosmicSurfaceDark
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.RoseDanger
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.theme.VioletNeon

@Composable
fun FilesScreen(
  files: List<CloudFile>,
  folders: List<CloudFolder>,
  trashFiles: List<CloudFile>,
  selectedCategory: String,
  searchQuery: String,
  onCategorySelected: (String) -> Unit,
  onSearchQueryChanged: (String) -> Unit,
  onOpenFile: (CloudFile) -> Unit,
  onToggleFavorite: (CloudFile) -> Unit,
  onShareFile: (CloudFile) -> Unit,
  onMoveToTrash: (CloudFile) -> Unit,
  onToggleVault: (CloudFile) -> Unit,
  onCreateFolder: (String) -> Unit,
  onUploadFile: (String, String, String, Long, String) -> Unit,
  onEmptyTrash: () -> Unit,
  onRestoreFromTrash: (CloudFile) -> Unit,
  onOpenOneClickBackup: (() -> Unit)? = null
) {
  var showNewFolderDialog by remember { mutableStateOf(false) }
  var showUploadDialog by remember { mutableStateOf(false) }
  var showTrashDialog by remember { mutableStateOf(false) }

  Box(modifier = Modifier.fillMaxSize()) {
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      item { Spacer(modifier = Modifier.height(6.dp)) }

      // Search Bar
      item {
        OutlinedTextField(
          value = searchQuery,
          onValueChange = onSearchQueryChanged,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("files_search_input"),
          placeholder = { Text("Search files, tags, or content...", color = TextSecondaryDark, fontSize = 13.sp) },
          leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = CyanNeon) },
          trailingIcon = {
            if (trashFiles.isNotEmpty()) {
              IconButton(onClick = { showTrashDialog = true }, modifier = Modifier.testTag("trash_icon_button")) {
                Icon(Icons.Default.Delete, contentDescription = "Trash (${trashFiles.size})", tint = RoseDanger)
              }
            }
          },
          singleLine = true,
          shape = RoundedCornerShape(16.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = TextPrimaryDark,
            unfocusedTextColor = TextPrimaryDark,
            focusedContainerColor = CosmicCardDark,
            unfocusedContainerColor = CosmicCardDark,
            focusedBorderColor = CyanNeon,
            unfocusedBorderColor = BorderSubtleDark
          )
        )
      }

      // Categories Horizontal Filter Chips
      item {
        val categories = listOf("ALL", "DOCUMENTS", "PHOTOS", "VIDEOS", "AUDIO", "DEVELOPMENT", "ARCHIVES")
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          items(categories) { cat ->
            FilterChip(
              selected = selectedCategory == cat,
              onClick = { onCategorySelected(cat) },
              label = { Text(cat, fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = CyanNeon,
                selectedLabelColor = Color.Black,
                containerColor = CosmicCardDark,
                labelColor = TextSecondaryDark
              ),
              modifier = Modifier.testTag("filter_chip_$cat")
            )
          }
        }
      }

      // Folders Section
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "FOLDERS",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            color = TextSecondaryDark
          )

          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            if (onOpenOneClickBackup != null) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .background(Color(0x3300F0FF))
                  .clickable { onOpenOneClickBackup() }
                  .padding(horizontal = 8.dp, vertical = 5.dp)
                  .testTag("files_one_click_backup_btn")
              ) {
                Icon(Icons.Default.CloudSync, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("1-Click Backup", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CyanNeon)
              }
            }

            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier
                .clickable { showNewFolderDialog = true }
                .testTag("new_folder_button")
            ) {
              Icon(Icons.Default.CreateNewFolder, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("New Folder", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = CyanNeon)
            }
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(10.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          items(folders) { folder ->
            FolderCard(folder = folder)
          }
        }
      }

      // Files Section Header
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "FILES (${files.size})",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            color = TextSecondaryDark
          )
        }
      }

      // File List
      if (files.isEmpty()) {
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
              Icon(Icons.Default.Folder, contentDescription = null, tint = TextSecondaryDark, modifier = Modifier.size(36.dp))
              Spacer(modifier = Modifier.height(10.dp))
              Text("No files match the selected filter.", color = TextPrimaryDark, fontSize = 14.sp, fontWeight = FontWeight.Medium)
              Text("Upload a file or choose another category.", color = TextSecondaryDark, fontSize = 12.sp)
            }
          }
        }
      } else {
        items(files, key = { it.id }) { file ->
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

      item { Spacer(modifier = Modifier.height(80.dp)) }
    }

    // Floating Action Button for Upload
    FloatingActionButton(
      onClick = { showUploadDialog = true },
      containerColor = CyanNeon,
      contentColor = Color.Black,
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(bottom = 24.dp, end = 20.dp)
        .testTag("fab_upload_file")
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(Icons.Default.Add, contentDescription = "Upload")
        Spacer(modifier = Modifier.width(6.dp))
        Text("Upload", fontWeight = FontWeight.Bold)
      }
    }
  }

  // New Folder Dialog
  if (showNewFolderDialog) {
    var folderName by remember { mutableStateOf("") }
    Dialog(onDismissRequest = { showNewFolderDialog = false }) {
      Surface(
        shape = RoundedCornerShape(20.dp),
        color = CosmicSurfaceDark,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtleDark),
        modifier = Modifier.fillMaxWidth().testTag("new_folder_dialog")
      ) {
        Column(modifier = Modifier.padding(20.dp)) {
          Text("Create New Cloud Directory", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
          Spacer(modifier = Modifier.height(12.dp))
          OutlinedTextField(
            value = folderName,
            onValueChange = { folderName = it },
            placeholder = { Text("Folder Name (e.g., Project Alpha)", color = TextSecondaryDark) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
              focusedTextColor = TextPrimaryDark,
              unfocusedTextColor = TextPrimaryDark,
              focusedBorderColor = CyanNeon
            )
          )
          Spacer(modifier = Modifier.height(16.dp))
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Button(
              onClick = { showNewFolderDialog = false },
              colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
            ) {
              Text("Cancel", color = TextSecondaryDark)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
              onClick = {
                if (folderName.isNotBlank()) {
                  onCreateFolder(folderName)
                  showNewFolderDialog = false
                }
              },
              colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = Color.Black)
            ) {
              Text("Create", fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }
  }

  // Upload File Dialog
  if (showUploadDialog) {
    UploadFileDialog(
      onDismiss = { showUploadDialog = false },
      onUpload = { name, ext, cat, size, content ->
        onUploadFile(name, ext, cat, size, content)
        showUploadDialog = false
      }
    )
  }

  // Recycle Bin Dialog
  if (showTrashDialog) {
    Dialog(onDismissRequest = { showTrashDialog = false }) {
      Surface(
        shape = RoundedCornerShape(20.dp),
        color = CosmicSurfaceDark,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtleDark),
        modifier = Modifier.fillMaxWidth().testTag("trash_dialog")
      ) {
        Column(modifier = Modifier.padding(20.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("Recycle Bin (${trashFiles.size})", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
            if (trashFiles.isNotEmpty()) {
              IconButton(onClick = onEmptyTrash) {
                Icon(Icons.Default.DeleteSweep, contentDescription = "Empty", tint = RoseDanger)
              }
            }
          }
          Spacer(modifier = Modifier.height(12.dp))
          if (trashFiles.isEmpty()) {
            Text("Recycle bin is empty.", color = TextSecondaryDark, fontSize = 13.sp)
          } else {
            trashFiles.forEach { tf ->
              Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(tf.name, fontSize = 13.sp, color = TextPrimaryDark, maxLines = 1, modifier = Modifier.weight(1f))
                Button(
                  onClick = { onRestoreFromTrash(tf) },
                  colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess.copy(alpha = 0.2f), contentColor = EmeraldSuccess)
                ) {
                  Text("Restore", fontSize = 11.sp)
                }
              }
            }
          }
          Spacer(modifier = Modifier.height(16.dp))
          Button(
            onClick = { showTrashDialog = false },
            colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = Color.Black),
            modifier = Modifier.fillMaxWidth()
          ) {
            Text("Close", fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}

@Composable
fun FolderCard(folder: CloudFolder) {
  Box(
    modifier = Modifier
      .width(130.dp)
      .clip(RoundedCornerShape(14.dp))
      .background(CosmicCardDark)
      .border(1.dp, BorderSubtleDark, RoundedCornerShape(14.dp))
      .padding(12.dp)
  ) {
    Column {
      Box(
        modifier = Modifier
          .size(34.dp)
          .clip(RoundedCornerShape(8.dp))
          .background(Color(0x3300F0FF)),
        contentAlignment = Alignment.Center
      ) {
        Icon(Icons.Default.Folder, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(20.dp))
      }
      Spacer(modifier = Modifier.height(10.dp))
      Text(folder.name, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimaryDark, maxLines = 1)
      Text("Cloud Folder", fontSize = 10.sp, color = TextSecondaryDark)
    }
  }
}

@Composable
fun UploadFileDialog(
  onDismiss: () -> Unit,
  onUpload: (name: String, ext: String, cat: String, size: Long, content: String) -> Unit
) {
  var fileName by remember { mutableStateOf("New_Cloud_Document.pdf") }
  var category by remember { mutableStateOf("DOCUMENTS") }
  var fileContent by remember { mutableStateOf("Uploaded document content created in Nexora Cloud universe.") }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(20.dp),
      color = CosmicSurfaceDark,
      border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtleDark),
      modifier = Modifier.fillMaxWidth().testTag("upload_file_dialog")
    ) {
      Column(modifier = Modifier.padding(20.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.UploadFile, contentDescription = null, tint = CyanNeon)
          Spacer(modifier = Modifier.width(8.dp))
          Text("Universal File Upload", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
        }
        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
          value = fileName,
          onValueChange = { fileName = it },
          label = { Text("File Name with Extension") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth(),
          colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = TextPrimaryDark,
            unfocusedTextColor = TextPrimaryDark,
            focusedBorderColor = CyanNeon
          )
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text("Select Category", fontSize = 12.sp, color = TextSecondaryDark)
        Spacer(modifier = Modifier.height(6.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          listOf("DOCUMENTS", "PHOTOS", "VIDEOS", "DEVELOPMENT").forEach { cat ->
            FilterChip(
              selected = category == cat,
              onClick = { category = cat },
              label = { Text(cat.take(4), fontSize = 10.sp) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = CyanNeon,
                selectedLabelColor = Color.Black
              )
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
          value = fileContent,
          onValueChange = { fileContent = it },
          label = { Text("Document Content / Notes") },
          maxLines = 3,
          modifier = Modifier.fillMaxWidth(),
          colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = TextPrimaryDark,
            unfocusedTextColor = TextPrimaryDark,
            focusedBorderColor = CyanNeon
          )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
          Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)) {
            Text("Cancel", color = TextSecondaryDark)
          }
          Spacer(modifier = Modifier.width(8.dp))
          Button(
            onClick = {
              val ext = fileName.substringAfterLast(".", "TXT").uppercase()
              val size = (1_200_000L..15_000_000L).random()
              onUpload(fileName, ext, category, size, fileContent)
            },
            colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = Color.Black)
          ) {
            Text("Upload to Cloud", fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}
