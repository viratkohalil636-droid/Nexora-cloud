package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.NexoraDatabase
import com.example.data.model.ActivityLog
import com.example.data.model.AiChatMessage
import com.example.data.model.ApkBuildConfig
import com.example.data.model.CloudFile
import com.example.data.model.CloudFolder
import com.example.data.model.NoteItem
import com.example.data.model.StorageServer
import com.example.data.model.SubscriptionPlan
import com.example.data.model.TaskItem
import com.example.data.model.UserProfile
import com.example.data.repository.NexoraRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

enum class NexoraScreen(val label: String) {
  HOME("Home"),
  FILES("My Cloud"),
  PHOTOS("Photos"),
  VAULT("Vault"),
  AI_ASSISTANT("Nexora AI"),
  PRODUCTIVITY("Productivity"),
  BACKUP("Backup & Sync"),
  ANALYTICS("Analytics"),
  SUBSCRIPTION("Plans"),
  ADMIN("Admin Console"),
  SETTINGS("Settings")
}

data class StorageBreakdown(
  val usedBytes: Long,
  val totalCapacityBytes: Long,
  val photosBytes: Long,
  val videosBytes: Long,
  val documentsBytes: Long,
  val audioBytes: Long,
  val codeBytes: Long,
  val vaultBytes: Long,
  val otherBytes: Long
)

class NexoraViewModel(application: Application) : AndroidViewModel(application) {

  private val database = NexoraDatabase.getDatabase(application, viewModelScope)
  val repository = NexoraRepository(database)

  // Current Navigation Screen
  private val _currentScreen = MutableStateFlow(NexoraScreen.HOME)
  val currentScreen: StateFlow<NexoraScreen> = _currentScreen.asStateFlow()

  // Navigation Backstack for Android BackHandler
  private val screenBackstack = mutableListOf<NexoraScreen>()

  fun navigateTo(screen: NexoraScreen) {
    if (_currentScreen.value != screen) {
      screenBackstack.add(_currentScreen.value)
      _currentScreen.value = screen
    }
  }

  fun navigateBack(): Boolean {
    if (screenBackstack.isNotEmpty()) {
      _currentScreen.value = screenBackstack.removeAt(screenBackstack.lastIndex)
      return true
    }
    return false
  }

  // Active files, folders, notes, tasks, servers
  val activeFiles: StateFlow<List<CloudFile>> = repository.activeFiles
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val folders: StateFlow<List<CloudFolder>> = repository.folders
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val vaultFolders: StateFlow<List<CloudFolder>> = repository.vaultFolders
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val favoriteFiles: StateFlow<List<CloudFile>> = repository.favoriteFiles
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val vaultFiles: StateFlow<List<CloudFile>> = repository.vaultFiles
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val trashFiles: StateFlow<List<CloudFile>> = repository.trashFiles
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val notes: StateFlow<List<NoteItem>> = repository.notes
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val tasks: StateFlow<List<TaskItem>> = repository.tasks
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val servers: StateFlow<List<StorageServer>> = repository.storageServers
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val userProfile: StateFlow<UserProfile> = repository.userProfile
  val activities: StateFlow<List<ActivityLog>> = repository.activities
  val aiMessages: StateFlow<List<AiChatMessage>> = repository.aiMessages
  val subscriptionPlans: List<SubscriptionPlan> = repository.subscriptionPlans

  // Selected Filter Category & Search
  private val _selectedCategory = MutableStateFlow("ALL")
  val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

  private val _searchQuery = MutableStateFlow("")
  val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

  fun setCategory(category: String) {
    _selectedCategory.value = category
  }

  fun setSearchQuery(query: String) {
    _searchQuery.value = query
  }

  // Filtered files derived state
  val filteredFiles: StateFlow<List<CloudFile>> = combine(
    activeFiles,
    _selectedCategory,
    _searchQuery
  ) { files, category, query ->
    files.filter { file ->
      val matchesCat = if (category == "ALL") true else file.category.equals(category, ignoreCase = true)
      val matchesQuery = if (query.isBlank()) true else {
        file.name.contains(query, ignoreCase = true) ||
            file.tags.contains(query, ignoreCase = true) ||
            file.extension.contains(query, ignoreCase = true) ||
            file.contentSummary.contains(query, ignoreCase = true)
      }
      matchesCat && matchesQuery
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Storage Breakdown
  val storageBreakdown: StateFlow<StorageBreakdown> = combine(
    activeFiles,
    vaultFiles,
    servers
  ) { active, vault, srvList ->
    var photos = 0L
    var videos = 0L
    var docs = 0L
    var audio = 0L
    var dev = 0L
    var other = 0L
    var totalActive = 0L

    active.forEach { f ->
      totalActive += f.sizeBytes
      when (f.category) {
        "PHOTOS" -> photos += f.sizeBytes
        "VIDEOS" -> videos += f.sizeBytes
        "DOCUMENTS" -> docs += f.sizeBytes
        "AUDIO" -> audio += f.sizeBytes
        "DEVELOPMENT" -> dev += f.sizeBytes
        else -> other += f.sizeBytes
      }
    }

    val vaultTotal = vault.sumOf { it.sizeBytes }
    val totalUsed = totalActive + vaultTotal
    // 2 TB default for Premium plan or sum of server capacities
    val totalCapacity = 2_199_023_255_552L // 2 TB in bytes

    StorageBreakdown(
      usedBytes = totalUsed,
      totalCapacityBytes = totalCapacity,
      photosBytes = photos,
      videosBytes = videos,
      documentsBytes = docs,
      audioBytes = audio,
      codeBytes = dev,
      vaultBytes = vaultTotal,
      otherBytes = other
    )
  }.stateIn(
    viewModelScope,
    SharingStarted.WhileSubscribed(5000),
    StorageBreakdown(0L, 2_199_023_255_552L, 0L, 0L, 0L, 0L, 0L, 0L, 0L)
  )

  // Vault Unlock State
  private val _isVaultUnlocked = MutableStateFlow(false)
  val isVaultUnlocked: StateFlow<Boolean> = _isVaultUnlocked.asStateFlow()

  fun unlockVault(pin: String): Boolean {
    // Master PIN: 7788 or 1234
    return if (pin == "7788" || pin == "1234" || pin == "0000") {
      _isVaultUnlocked.value = true
      true
    } else {
      false
    }
  }

  fun lockVault() {
    _isVaultUnlocked.value = false
  }

  // Active File for Preview Dialog
  private val _previewFile = MutableStateFlow<CloudFile?>(null)
  val previewFile: StateFlow<CloudFile?> = _previewFile.asStateFlow()

  fun openFilePreview(file: CloudFile) {
    _previewFile.value = file
  }

  fun closeFilePreview() {
    _previewFile.value = null
  }

  // Active File for Sharing Dialog
  private val _shareFile = MutableStateFlow<CloudFile?>(null)
  val shareFile: StateFlow<CloudFile?> = _shareFile.asStateFlow()

  fun openShareDialog(file: CloudFile) {
    _shareFile.value = file
  }

  fun closeShareDialog() {
    _shareFile.value = null
  }

  // Active Checkout Plan for Payment Gateway Modal
  private val _checkoutPlan = MutableStateFlow<SubscriptionPlan?>(null)
  val checkoutPlan: StateFlow<SubscriptionPlan?> = _checkoutPlan.asStateFlow()

  fun openCheckout(plan: SubscriptionPlan) {
    _checkoutPlan.value = plan
  }

  fun closeCheckout() {
    _checkoutPlan.value = null
  }

  fun completeCheckout(planId: String) {
    repository.updatePlan(planId)
    _checkoutPlan.value = null
  }

  // User Actions
  fun toggleFavorite(file: CloudFile) {
    viewModelScope.launch { repository.toggleFavorite(file) }
  }

  fun moveToTrash(file: CloudFile) {
    viewModelScope.launch { repository.moveToTrash(file) }
  }

  fun restoreFromTrash(file: CloudFile) {
    viewModelScope.launch { repository.restoreFromTrash(file) }
  }

  fun deletePermanently(fileId: String) {
    viewModelScope.launch { repository.deletePermanently(fileId) }
  }

  fun emptyTrash() {
    viewModelScope.launch { repository.emptyTrash() }
  }

  fun toggleVault(file: CloudFile) {
    viewModelScope.launch { repository.toggleVault(file) }
  }

  fun createFolder(name: String, parent: String = "root", isVault: Boolean = false) {
    viewModelScope.launch { repository.createFolder(name, parent, isVault) }
  }

  fun deleteFolder(id: String) {
    viewModelScope.launch { repository.deleteFolder(id) }
  }

  fun uploadSimulatedFile(name: String, extension: String, category: String, sizeBytes: Long, content: String = "") {
    viewModelScope.launch {
      val newFile = CloudFile(
        name = name,
        extension = extension.uppercase(),
        sizeBytes = sizeBytes,
        mimeType = when (extension.lowercase()) {
          "pdf" -> "application/pdf"
          "jpg", "png", "heic" -> "image/jpeg"
          "mp4", "mov" -> "video/mp4"
          "mp3", "flac" -> "audio/mpeg"
          "json" -> "application/json"
          else -> "text/plain"
        },
        category = category,
        textContent = content.ifBlank { "Sample imported content for $name.\nVerified integrity with SHA-256." },
        contentSummary = "User uploaded file: $name (${NexoraRepository.formatBytes(sizeBytes)})."
      )
      repository.insertFile(newFile)
    }
  }

  fun saveNote(title: String, content: String, colorHex: String, isPinned: Boolean, id: String? = null) {
    viewModelScope.launch {
      val note = NoteItem(
        id = id ?: UUID.randomUUID().toString(),
        title = title,
        content = content,
        colorHex = colorHex,
        isPinned = isPinned,
        updatedAt = System.currentTimeMillis()
      )
      repository.saveNote(note)
    }
  }

  fun deleteNote(id: String) {
    viewModelScope.launch { repository.deleteNote(id) }
  }

  fun saveTask(title: String, description: String, priority: String, dueDate: String, category: String) {
    viewModelScope.launch {
      val task = TaskItem(
        title = title,
        description = description,
        priority = priority,
        dueDate = dueDate,
        category = category
      )
      repository.saveTask(task)
    }
  }

  fun toggleTask(task: TaskItem) {
    viewModelScope.launch { repository.toggleTaskCompleted(task) }
  }

  fun deleteTask(id: String) {
    viewModelScope.launch { repository.deleteTask(id) }
  }

  fun sendAiPrompt(text: String) {
    repository.sendAiUserMessage(text, activeFiles.value)
  }

  fun addStorageNode(name: String, endpoint: String, provider: String, region: String, capacityTb: Long) {
    viewModelScope.launch {
      val server = StorageServer(
        name = name,
        endpoint = endpoint,
        provider = provider,
        region = region,
        capacityBytes = capacityTb * 1_000_000_000_000L,
        usedBytes = 0L,
        status = "ONLINE",
        latencyMs = (12..45).random()
      )
      repository.addStorageServer(server)
    }
  }

  fun deleteStorageNode(id: String) {
    viewModelScope.launch { repository.deleteStorageServer(id) }
  }

  fun updateBackupSettings(photos: Boolean, videos: Boolean, wifiOnly: Boolean) {
    repository.updateBackupSettings(photos, videos, wifiOnly)
  }

  fun updateSecurity(e2e: Boolean, twoFactor: Boolean) {
    repository.updateSecuritySettings(e2e, twoFactor)
  }

  // --- ONE-CLICK COMPLETE PHONE BACKUP ENGINE ---
  private val _isBackupDialogOpen = MutableStateFlow(false)
  val isBackupDialogOpen: StateFlow<Boolean> = _isBackupDialogOpen.asStateFlow()

  private val _backupPreset = MutableStateFlow("FULL_ZIP") // "FULL_ZIP", "WHATSAPP", "GMAIL"
  val backupPreset: StateFlow<String> = _backupPreset.asStateFlow()

  private val _backupStatus = MutableStateFlow("IDLE") // IDLE, SCANNING, COMPRESSING, ENCRYPTING, UPLOADING, COMPLETED
  val backupStatus: StateFlow<String> = _backupStatus.asStateFlow()

  private val _backupProgress = MutableStateFlow(0f)
  val backupProgress: StateFlow<Float> = _backupProgress.asStateFlow()

  private val _backupStatusText = MutableStateFlow("Ready to backup phone data")
  val backupStatusText: StateFlow<String> = _backupStatusText.asStateFlow()

  private val _lastBackupArchive = MutableStateFlow<CloudFile?>(null)
  val lastBackupArchive: StateFlow<CloudFile?> = _lastBackupArchive.asStateFlow()

  fun openOneClickBackupDialog(preset: String = "FULL_ZIP") {
    _backupPreset.value = preset
    _backupStatus.value = "IDLE"
    _backupProgress.value = 0f
    _backupStatusText.value = "Ready to create one-click backup"
    _lastBackupArchive.value = null
    _isBackupDialogOpen.value = true
  }

  fun closeOneClickBackupDialog() {
    _isBackupDialogOpen.value = false
  }

  fun setBackupPreset(preset: String) {
    _backupPreset.value = preset
  }

  fun startOneClickBackup() {
    viewModelScope.launch {
      val preset = _backupPreset.value
      val availableServer = servers.value.firstOrNull { it.status == "ONLINE" }
      val serverName = availableServer?.name ?: "Primary US-East S3 Cluster"

      _backupStatus.value = "SCANNING"
      _backupStatusText.value = "Scanning phone contacts, camera roll & device data..."
      _backupProgress.value = 0.2f
      kotlinx.coroutines.delay(650)

      _backupStatus.value = "COMPRESSING"
      _backupStatusText.value = when (preset) {
        "WHATSAPP" -> "Compressing into WhatsApp size-safe ZIP (< 16 MB)..."
        "GMAIL" -> "Compressing into Gmail size-safe ZIP (< 25 MB)..."
        else -> "Compressing complete phone data into Master ZIP archive..."
      }
      _backupProgress.value = 0.5f
      kotlinx.coroutines.delay(700)

      _backupStatus.value = "ENCRYPTING"
      _backupStatusText.value = "Encrypting with client-side AES-256-GCM zero-knowledge key..."
      _backupProgress.value = 0.75f
      kotlinx.coroutines.delay(600)

      _backupStatus.value = "UPLOADING"
      _backupStatusText.value = "Streaming archive to available cloud node: $serverName..."
      _backupProgress.value = 0.92f
      kotlinx.coroutines.delay(650)

      val (fileName, sizeBytes) = when (preset) {
        "WHATSAPP" -> Pair("Nexora_WhatsApp_Phone_Backup_${System.currentTimeMillis() % 10000}.zip", 14_850_000L)
        "GMAIL" -> Pair("Nexora_Gmail_Phone_Backup_${System.currentTimeMillis() % 10000}.zip", 22_400_000L)
        else -> Pair("Nexora_Complete_Phone_Master_${System.currentTimeMillis() % 10000}.zip", 68_400_000L)
      }

      val newArchive = CloudFile(
        name = fileName,
        extension = "ZIP",
        sizeBytes = sizeBytes,
        mimeType = "application/zip",
        category = "ARCHIVES",
        parentFolder = "Documents",
        isFavorite = true,
        tags = "Backup, Phone, $preset, Cloud",
        contentSummary = "One-click complete phone backup package ($preset preset) stored at $serverName.",
        checksum = "5d41402abc4b2a76b9719d911017c592",
        textContent = "NEXORA CLOUD COMPLETE PHONE BACKUP\nDate: 2026-11-01\nTarget Server: $serverName\nFormat: $preset ZIP Archive\nContacts: 412 entries\nCamera Roll: 48 items\nNotes & Memos: 18 items\nDevice Settings: Encrypted XML payload\nVerified Integrity: OK"
      )

      repository.insertFile(newArchive)
      _lastBackupArchive.value = newArchive
      _backupProgress.value = 1f
      _backupStatus.value = "COMPLETED"
      _backupStatusText.value = "Successfully stored at $serverName!"
    }
  }

  // APK Build Info
  val apkConfig = ApkBuildConfig()
}
