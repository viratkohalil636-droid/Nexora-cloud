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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.NoteItem
import com.example.data.model.TaskItem
import com.example.ui.theme.AmberWarning
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
fun ProductivityScreen(
  notes: List<NoteItem>,
  tasks: List<TaskItem>,
  onSaveNote: (title: String, content: String, colorHex: String, isPinned: Boolean) -> Unit,
  onDeleteNote: (String) -> Unit,
  onSaveTask: (title: String, description: String, priority: String, dueDate: String, category: String) -> Unit,
  onToggleTask: (TaskItem) -> Unit,
  onDeleteTask: (String) -> Unit,
  onScanDocument: (String) -> Unit
) {
  var selectedTab by remember { mutableIntStateOf(0) } // 0 = Notes, 1 = Tasks, 2 = PDF & OCR Tools
  var showNewNoteDialog by remember { mutableStateOf(false) }
  var showNewTaskDialog by remember { mutableStateOf(false) }
  var showScannerSuccessDialog by remember { mutableStateOf(false) }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp)
      .testTag("productivity_screen")
  ) {
    Spacer(modifier = Modifier.height(6.dp))

    // Tab Selector
    TabRow(
      selectedTabIndex = selectedTab,
      containerColor = CosmicCardDark,
      contentColor = CyanNeon,
      modifier = Modifier
        .clip(RoundedCornerShape(14.dp))
        .border(1.dp, BorderSubtleDark, RoundedCornerShape(14.dp))
    ) {
      Tab(
        selected = selectedTab == 0,
        onClick = { selectedTab = 0 },
        text = { Text("Notes (${notes.size})", fontWeight = FontWeight.SemiBold, fontSize = 12.sp) }
      )
      Tab(
        selected = selectedTab == 1,
        onClick = { selectedTab = 1 },
        text = { Text("To-Do (${tasks.size})", fontWeight = FontWeight.SemiBold, fontSize = 12.sp) }
      )
      Tab(
        selected = selectedTab == 2,
        onClick = { selectedTab = 2 },
        text = { Text("Tools & OCR", fontWeight = FontWeight.SemiBold, fontSize = 12.sp) }
      )
    }

    Spacer(modifier = Modifier.height(14.dp))

    when (selectedTab) {
      0 -> NotesSection(
        notes = notes,
        onAddClick = { showNewNoteDialog = true },
        onDelete = onDeleteNote
      )
      1 -> TasksSection(
        tasks = tasks,
        onAddClick = { showNewTaskDialog = true },
        onToggle = onToggleTask,
        onDelete = onDeleteTask
      )
      2 -> ToolsSection(
        onScan = { docName ->
          onScanDocument(docName)
          showScannerSuccessDialog = true
        }
      )
    }
  }

  // New Note Dialog
  if (showNewNoteDialog) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var isPinned by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = { showNewNoteDialog = false }) {
      Surface(
        shape = RoundedCornerShape(20.dp),
        color = CosmicSurfaceDark,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtleDark),
        modifier = Modifier.fillMaxWidth().testTag("new_note_dialog")
      ) {
        Column(modifier = Modifier.padding(20.dp)) {
          Text("Create Cloud Note", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
          Spacer(modifier = Modifier.height(12.dp))
          OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            placeholder = { Text("Note Title", color = TextSecondaryDark) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimaryDark, unfocusedTextColor = TextPrimaryDark, focusedBorderColor = CyanNeon)
          )
          Spacer(modifier = Modifier.height(8.dp))
          OutlinedTextField(
            value = content,
            onValueChange = { content = it },
            placeholder = { Text("Write your thoughts, memos, or ideas...", color = TextSecondaryDark) },
            maxLines = 5,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimaryDark, unfocusedTextColor = TextPrimaryDark, focusedBorderColor = CyanNeon)
          )
          Spacer(modifier = Modifier.height(10.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Checkbox(
                checked = isPinned,
                onCheckedChange = { isPinned = it },
                colors = CheckboxDefaults.colors(checkedColor = AmberWarning)
              )
              Text("Pin to top", fontSize = 12.sp, color = TextPrimaryDark)
            }
            Row {
              Button(onClick = { showNewNoteDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)) {
                Text("Cancel", color = TextSecondaryDark)
              }
              Spacer(modifier = Modifier.width(6.dp))
              Button(
                onClick = {
                  if (title.isNotBlank()) {
                    onSaveNote(title, content, "#1E293B", isPinned)
                    showNewNoteDialog = false
                  }
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = Color.Black)
              ) {
                Text("Save", fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }
    }
  }

  // New Task Dialog
  if (showNewTaskDialog) {
    var title by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("MEDIUM") }
    var dueDate by remember { mutableStateOf("Today") }

    Dialog(onDismissRequest = { showNewTaskDialog = false }) {
      Surface(
        shape = RoundedCornerShape(20.dp),
        color = CosmicSurfaceDark,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtleDark),
        modifier = Modifier.fillMaxWidth().testTag("new_task_dialog")
      ) {
        Column(modifier = Modifier.padding(20.dp)) {
          Text("New Productivity Task", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
          Spacer(modifier = Modifier.height(12.dp))
          OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            placeholder = { Text("Task description", color = TextSecondaryDark) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimaryDark, unfocusedTextColor = TextPrimaryDark, focusedBorderColor = CyanNeon)
          )
          Spacer(modifier = Modifier.height(12.dp))
          Text("Priority", fontSize = 12.sp, color = TextSecondaryDark)
          Spacer(modifier = Modifier.height(6.dp))
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("HIGH", "MEDIUM", "LOW").forEach { p ->
              FilterChip(
                selected = priority == p,
                onClick = { priority = p },
                label = { Text(p, fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = CyanNeon, selectedLabelColor = Color.Black)
              )
            }
          }
          Spacer(modifier = Modifier.height(16.dp))
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Button(onClick = { showNewTaskDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)) {
              Text("Cancel", color = TextSecondaryDark)
            }
            Spacer(modifier = Modifier.width(6.dp))
            Button(
              onClick = {
                if (title.isNotBlank()) {
                  onSaveTask(title, "", priority, dueDate, "Personal")
                  showNewTaskDialog = false
                }
              },
              colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = Color.Black)
            ) {
              Text("Add Task", fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }
  }

  // Scanner Success Dialog
  if (showScannerSuccessDialog) {
    Dialog(onDismissRequest = { showScannerSuccessDialog = false }) {
      Surface(
        shape = RoundedCornerShape(20.dp),
        color = CosmicSurfaceDark,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtleDark),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
          Box(modifier = Modifier.size(50.dp).clip(CircleShape).background(Color(0x3310B981)), contentAlignment = Alignment.Center) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(28.dp))
          }
          Spacer(modifier = Modifier.height(12.dp))
          Text("Document Scanned & OCR Processed!", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
          Spacer(modifier = Modifier.height(6.dp))
          Text("Saved as PDF to your Documents folder with searchable OCR text layer.", fontSize = 12.sp, color = TextSecondaryDark)
          Spacer(modifier = Modifier.height(16.dp))
          Button(
            onClick = { showScannerSuccessDialog = false },
            colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = Color.Black),
            modifier = Modifier.fillMaxWidth()
          ) {
            Text("Done", fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}

@Composable
fun NotesSection(
  notes: List<NoteItem>,
  onAddClick: () -> Unit,
  onDelete: (String) -> Unit
) {
  LazyColumn(
    verticalArrangement = Arrangement.spacedBy(10.dp),
    modifier = Modifier.fillMaxSize()
  ) {
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text("PERSONAL NOTES", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondaryDark, letterSpacing = 1.sp)
        Button(
          onClick = onAddClick,
          colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = Color.Black),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.testTag("add_note_button")
        ) {
          Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("New Note", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
      }
    }

    if (notes.isEmpty()) {
      item {
        Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(CosmicCardDark).padding(24.dp), contentAlignment = Alignment.Center) {
          Text("No notes yet. Tap 'New Note' to create one.", color = TextSecondaryDark, fontSize = 13.sp)
        }
      }
    } else {
      items(notes, key = { it.id }) { note ->
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CosmicCardDark)
            .border(1.dp, BorderSubtleDark, RoundedCornerShape(16.dp))
            .padding(14.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                if (note.isPinned) {
                  Icon(Icons.Default.PushPin, contentDescription = "Pinned", tint = AmberWarning, modifier = Modifier.size(14.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                }
                Text(note.title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimaryDark)
              }
              Spacer(modifier = Modifier.height(6.dp))
              Text(note.content, fontSize = 12.sp, lineHeight = 17.sp, color = TextSecondaryDark)
            }
            IconButton(onClick = { onDelete(note.id) }, modifier = Modifier.size(32.dp)) {
              Icon(Icons.Default.Delete, contentDescription = "Delete", tint = TextSecondaryDark, modifier = Modifier.size(18.dp))
            }
          }
        }
      }
    }
  }
}

@Composable
fun TasksSection(
  tasks: List<TaskItem>,
  onAddClick: () -> Unit,
  onToggle: (TaskItem) -> Unit,
  onDelete: (String) -> Unit
) {
  LazyColumn(
    verticalArrangement = Arrangement.spacedBy(10.dp),
    modifier = Modifier.fillMaxSize()
  ) {
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text("TASK MANAGER", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondaryDark, letterSpacing = 1.sp)
        Button(
          onClick = onAddClick,
          colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = Color.Black),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.testTag("add_task_button")
        ) {
          Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Add Task", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
      }
    }

    items(tasks, key = { it.id }) { task ->
      val priColor = when (task.priority) {
        "HIGH" -> RoseDanger
        "MEDIUM" -> AmberWarning
        else -> EmeraldSuccess
      }

      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(14.dp))
          .background(CosmicCardDark)
          .border(1.dp, BorderSubtleDark, RoundedCornerShape(14.dp))
          .padding(12.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
          ) {
            IconButton(onClick = { onToggle(task) }, modifier = Modifier.size(32.dp)) {
              Icon(
                imageVector = if (task.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                contentDescription = null,
                tint = if (task.isCompleted) EmeraldSuccess else TextSecondaryDark
              )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = task.title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = if (task.isCompleted) TextSecondaryDark else TextPrimaryDark,
                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
              )
              Text("Due: ${task.dueDate} • Priority: ${task.priority}", fontSize = 10.sp, color = priColor)
            }
          }

          IconButton(onClick = { onDelete(task.id) }, modifier = Modifier.size(30.dp)) {
            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = TextSecondaryDark, modifier = Modifier.size(16.dp))
          }
        }
      }
    }
  }
}

@Composable
fun ToolsSection(onScan: (String) -> Unit) {
  LazyColumn(
    verticalArrangement = Arrangement.spacedBy(14.dp),
    modifier = Modifier.fillMaxSize()
  ) {
    item {
      // Document Scanner Tool
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(18.dp))
          .background(Color(0xFF0F1E36))
          .border(1.dp, CyanNeon.copy(alpha = 0.4f), RoundedCornerShape(18.dp))
          .padding(16.dp)
      ) {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0x3300F0FF)),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.CameraAlt, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text("AI Document & OCR Scanner", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
              Text("Capture paper contracts or receipts with edge detection & OCR.", fontSize = 11.sp, color = TextSecondaryDark)
            }
          }
          Spacer(modifier = Modifier.height(14.dp))
          Button(
            onClick = { onScan("Scanned_Contract_${System.currentTimeMillis() % 1000}.pdf") },
            colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = Color.Black),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().testTag("scan_document_button")
          ) {
            Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Launch Camera Scanner", fontWeight = FontWeight.Bold)
          }
        }
      }
    }

    item {
      // PDF & Archive Tools
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        UtilityToolCard(
          title = "PDF Split & Merge",
          desc = "Combine or extract pages without server re-upload",
          icon = Icons.Default.PictureAsPdf,
          color = RoseDanger,
          modifier = Modifier.weight(1f)
        )
        UtilityToolCard(
          title = "File Compressor",
          desc = "Lossless ZIP/7z compression & extraction",
          icon = Icons.Default.FolderZip,
          color = AmberWarning,
          modifier = Modifier.weight(1f)
        )
      }
    }
  }
}

@Composable
fun UtilityToolCard(
  title: String,
  desc: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  color: Color,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(16.dp))
      .background(CosmicCardDark)
      .border(1.dp, BorderSubtleDark, RoundedCornerShape(16.dp))
      .padding(14.dp)
  ) {
    Column {
      Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(color.copy(alpha = 0.2f)), contentAlignment = Alignment.Center) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
      }
      Spacer(modifier = Modifier.height(10.dp))
      Text(title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimaryDark)
      Spacer(modifier = Modifier.height(4.dp))
      Text(desc, fontSize = 11.sp, color = TextSecondaryDark, lineHeight = 15.sp)
    }
  }
}
