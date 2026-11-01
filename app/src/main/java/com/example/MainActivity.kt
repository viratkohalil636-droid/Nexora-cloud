package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.example.ui.components.CosmicBackground
import com.example.ui.components.FilePreviewDialog
import com.example.ui.components.NexoraBottomBar
import com.example.ui.components.NexoraTopBar
import com.example.ui.components.OneClickBackupDialog
import com.example.ui.components.ShareDialog
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.AiAssistantScreen
import com.example.ui.screens.FilesScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PhotosScreen
import com.example.ui.screens.ProductivityScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SubscriptionScreen
import com.example.ui.screens.VaultScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.NexoraScreen
import com.example.viewmodel.NexoraViewModel

class MainActivity : ComponentActivity() {

  private val viewModel: NexoraViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    setContent {
      MyApplicationTheme(darkTheme = true) {
        NexoraApp(viewModel = viewModel)
      }
    }
  }
}

@Composable
fun NexoraApp(viewModel: NexoraViewModel) {
  val currentScreen by viewModel.currentScreen.collectAsState()
  val userProfile by viewModel.userProfile.collectAsState()
  val storageBreakdown by viewModel.storageBreakdown.collectAsState()
  val activeFiles by viewModel.activeFiles.collectAsState()
  val filteredFiles by viewModel.filteredFiles.collectAsState()
  val folders by viewModel.folders.collectAsState()
  val vaultFolders by viewModel.vaultFolders.collectAsState()
  val vaultFiles by viewModel.vaultFiles.collectAsState()
  val trashFiles by viewModel.trashFiles.collectAsState()
  val notes by viewModel.notes.collectAsState()
  val tasks by viewModel.tasks.collectAsState()
  val servers by viewModel.servers.collectAsState()
  val activities by viewModel.activities.collectAsState()
  val aiMessages by viewModel.aiMessages.collectAsState()
  val selectedCategory by viewModel.selectedCategory.collectAsState()
  val searchQuery by viewModel.searchQuery.collectAsState()
  val isVaultUnlocked by viewModel.isVaultUnlocked.collectAsState()
  val previewFile by viewModel.previewFile.collectAsState()
  val shareFile by viewModel.shareFile.collectAsState()
  val checkoutPlan by viewModel.checkoutPlan.collectAsState()

  // One-Click Backup States
  val isBackupDialogOpen by viewModel.isBackupDialogOpen.collectAsState()
  val backupPreset by viewModel.backupPreset.collectAsState()
  val backupStatus by viewModel.backupStatus.collectAsState()
  val backupProgress by viewModel.backupProgress.collectAsState()
  val backupStatusText by viewModel.backupStatusText.collectAsState()
  val lastBackupArchive by viewModel.lastBackupArchive.collectAsState()

  // Android BackHandler: returns to Home or previous screen when back is pressed
  BackHandler(enabled = currentScreen != NexoraScreen.HOME) {
    if (!viewModel.navigateBack()) {
      viewModel.navigateTo(NexoraScreen.HOME)
    }
  }

  CosmicBackground {
    Scaffold(
      modifier = Modifier.fillMaxSize().testTag("main_scaffold"),
      containerColor = androidx.compose.ui.graphics.Color.Transparent,
      topBar = {
        NexoraTopBar(
          currentScreen = currentScreen,
          userProfile = userProfile,
          onNavigate = { viewModel.navigateTo(it) },
          onOpenAi = { viewModel.navigateTo(NexoraScreen.AI_ASSISTANT) }
        )
      },
      bottomBar = {
        NexoraBottomBar(
          currentScreen = currentScreen,
          onNavigate = { viewModel.navigateTo(it) }
        )
      }
    ) { innerPadding ->
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding)
      ) {
        when (currentScreen) {
          NexoraScreen.HOME -> {
            HomeScreen(
              userProfile = userProfile,
              storageBreakdown = storageBreakdown,
              servers = servers,
              recentFiles = activeFiles,
              activities = activities,
              onNavigate = { viewModel.navigateTo(it) },
              onOpenFile = { viewModel.openFilePreview(it) },
              onToggleFavorite = { viewModel.toggleFavorite(it) },
              onShareFile = { viewModel.openShareDialog(it) },
              onMoveToTrash = { viewModel.moveToTrash(it) },
              onToggleVault = { viewModel.toggleVault(it) },
              onQuickUpload = {
                viewModel.uploadSimulatedFile(
                  name = "Scanned_Legal_Affidavit_${System.currentTimeMillis() % 1000}.pdf",
                  extension = "PDF",
                  category = "DOCUMENTS",
                  sizeBytes = 2_840_000L,
                  content = "Official legal affidavit verified by Nexora zero-knowledge cryptographic signature."
                )
              },
              onOpenOneClickBackup = {
                viewModel.openOneClickBackupDialog("FULL_ZIP")
              }
            )
          }

          NexoraScreen.FILES -> {
            FilesScreen(
              files = filteredFiles,
              folders = folders,
              trashFiles = trashFiles,
              selectedCategory = selectedCategory,
              searchQuery = searchQuery,
              onCategorySelected = { viewModel.setCategory(it) },
              onSearchQueryChanged = { viewModel.setSearchQuery(it) },
              onOpenFile = { viewModel.openFilePreview(it) },
              onToggleFavorite = { viewModel.toggleFavorite(it) },
              onShareFile = { viewModel.openShareDialog(it) },
              onMoveToTrash = { viewModel.moveToTrash(it) },
              onToggleVault = { viewModel.toggleVault(it) },
              onCreateFolder = { viewModel.createFolder(it) },
              onUploadFile = { name, ext, cat, size, content ->
                viewModel.uploadSimulatedFile(name, ext, cat, size, content)
              },
              onEmptyTrash = { viewModel.emptyTrash() },
              onRestoreFromTrash = { viewModel.restoreFromTrash(it) },
              onOpenOneClickBackup = { viewModel.openOneClickBackupDialog("FULL_ZIP") }
            )
          }

          NexoraScreen.PHOTOS -> {
            PhotosScreen(
              files = activeFiles,
              autoBackupEnabled = userProfile.autoBackupPhotos,
              onToggleAutoBackup = {
                viewModel.updateBackupSettings(
                  photos = it,
                  videos = userProfile.autoBackupVideos,
                  wifiOnly = userProfile.syncOnWifiOnly
                )
              },
              onOpenFile = { viewModel.openFilePreview(it) },
              onToggleFavorite = { viewModel.toggleFavorite(it) }
            )
          }

          NexoraScreen.VAULT -> {
            VaultScreen(
              isUnlocked = isVaultUnlocked,
              vaultFiles = vaultFiles,
              vaultFolders = vaultFolders,
              onUnlock = { viewModel.unlockVault(it) },
              onLock = { viewModel.lockVault() },
              onOpenFile = { viewModel.openFilePreview(it) },
              onToggleFavorite = { viewModel.toggleFavorite(it) },
              onShareFile = { viewModel.openShareDialog(it) },
              onMoveToTrash = { viewModel.moveToTrash(it) },
              onToggleVault = { viewModel.toggleVault(it) }
            )
          }

          NexoraScreen.AI_ASSISTANT -> {
            AiAssistantScreen(
              messages = aiMessages,
              onSendMessage = { viewModel.sendAiPrompt(it) }
            )
          }

          NexoraScreen.PRODUCTIVITY, NexoraScreen.BACKUP, NexoraScreen.ANALYTICS -> {
            ProductivityScreen(
              notes = notes,
              tasks = tasks,
              onSaveNote = { title, content, color, isPinned ->
                viewModel.saveNote(title, content, color, isPinned)
              },
              onDeleteNote = { viewModel.deleteNote(it) },
              onSaveTask = { title, desc, pri, due, cat ->
                viewModel.saveTask(title, desc, pri, due, cat)
              },
              onToggleTask = { viewModel.toggleTask(it) },
              onDeleteTask = { viewModel.deleteTask(it) },
              onScanDocument = { docName ->
                viewModel.uploadSimulatedFile(
                  name = docName,
                  extension = "PDF",
                  category = "DOCUMENTS",
                  sizeBytes = 1_850_000L,
                  content = "Scanned document text OCR processed:\n[Contract Title: Mutual Confidentiality]\n[Date: 2026-10-31]\n[Status: Verified Valid]"
                )
              }
            )
          }

          NexoraScreen.SUBSCRIPTION -> {
            SubscriptionScreen(
              userProfile = userProfile,
              plans = viewModel.subscriptionPlans,
              onSelectPlan = { viewModel.openCheckout(it) },
              checkoutPlan = checkoutPlan,
              onCompleteCheckout = { viewModel.completeCheckout(it) },
              onDismissCheckout = { viewModel.closeCheckout() }
            )
          }

          NexoraScreen.ADMIN -> {
            AdminDashboardScreen(
              servers = servers,
              apkConfig = viewModel.apkConfig,
              onAddServer = { name, endpoint, provider, region, cap ->
                viewModel.addStorageNode(name, endpoint, provider, region, cap)
              },
              onDeleteServer = { viewModel.deleteStorageNode(it) }
            )
          }

          NexoraScreen.SETTINGS -> {
            SettingsScreen(
              userProfile = userProfile,
              onNavigate = { viewModel.navigateTo(it) },
              onUpdateSecurity = { e2e, twoFactor ->
                viewModel.updateSecurity(e2e, twoFactor)
              },
              onUpdateBackup = { photos, videos, wifiOnly ->
                viewModel.updateBackupSettings(photos, videos, wifiOnly)
              }
            )
          }
        }
      }

      // In-App File Preview Modal
      previewFile?.let { file ->
        FilePreviewDialog(
          file = file,
          onDismiss = { viewModel.closeFilePreview() },
          onShare = {
            viewModel.closeFilePreview()
            viewModel.openShareDialog(file)
          },
          onSummarizeAi = { query ->
            viewModel.navigateTo(NexoraScreen.AI_ASSISTANT)
            viewModel.sendAiPrompt(query)
          }
        )
      }

      // In-App Secure Sharing Modal
      shareFile?.let { file ->
        ShareDialog(
          file = file,
          onDismiss = { viewModel.closeShareDialog() }
        )
      }

      // One-Click Phone Backup Dialog
      OneClickBackupDialog(
        isOpen = isBackupDialogOpen,
        preset = backupPreset,
        status = backupStatus,
        progress = backupProgress,
        statusText = backupStatusText,
        availableServers = servers,
        generatedArchive = lastBackupArchive,
        onPresetSelected = { viewModel.setBackupPreset(it) },
        onStartBackup = { viewModel.startOneClickBackup() },
        onDismiss = { viewModel.closeOneClickBackupDialog() },
        onViewInCloud = { file ->
          viewModel.navigateTo(NexoraScreen.FILES)
          viewModel.openFilePreview(file)
        }
      )
    }
  }
}
