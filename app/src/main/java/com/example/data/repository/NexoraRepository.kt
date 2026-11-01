package com.example.data.repository

import com.example.data.local.NexoraDatabase
import com.example.data.model.ActivityLog
import com.example.data.model.AiChatMessage
import com.example.data.model.CloudFile
import com.example.data.model.CloudFolder
import com.example.data.model.NoteItem
import com.example.data.model.StorageServer
import com.example.data.model.SubscriptionPlan
import com.example.data.model.TaskItem
import com.example.data.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class NexoraRepository(private val db: NexoraDatabase) {

  // DAOs
  val activeFiles: Flow<List<CloudFile>> = db.cloudFileDao().getAllActiveFiles()
  val folders: Flow<List<CloudFolder>> = db.cloudFolderDao().getAllFolders()
  val vaultFolders: Flow<List<CloudFolder>> = db.cloudFolderDao().getVaultFolders()
  val favoriteFiles: Flow<List<CloudFile>> = db.cloudFileDao().getFavoriteFiles()
  val vaultFiles: Flow<List<CloudFile>> = db.cloudFileDao().getVaultFiles()
  val trashFiles: Flow<List<CloudFile>> = db.cloudFileDao().getTrashFiles()
  val notes: Flow<List<NoteItem>> = db.noteDao().getAllNotes()
  val tasks: Flow<List<TaskItem>> = db.taskDao().getAllTasks()
  val storageServers: Flow<List<StorageServer>> = db.serverDao().getAllServers()

  // User Profile State
  private val _userProfile = MutableStateFlow(UserProfile())
  val userProfile = _userProfile.asStateFlow()

  // Activity Logs
  private val _activities = MutableStateFlow<List<ActivityLog>>(
    listOf(
      ActivityLog(UUID.randomUUID().toString(), "Replicated Nexora_Architecture_Whitepaper_2026.pdf", "12 mins ago", "sync", "Replicated to EU-Central & AP-South clusters with 256-bit encryption."),
      ActivityLog(UUID.randomUUID().toString(), "Automatic Phone Backup Completed", "1 hour ago", "backup", "38 new camera photos and 2 videos uploaded over secure Wi-Fi."),
      ActivityLog(UUID.randomUUID().toString(), "Zero-Knowledge Personal Vault Locked", "3 hours ago", "lock", "Automated biometric timeout triggered after 15 minutes of inactivity."),
      ActivityLog(UUID.randomUUID().toString(), "AI Smart Storage Audit Ran", "Yesterday", "ai", "Identified 1 duplicate file candidate (18.9 MB recoverable).")
    )
  )
  val activities = _activities.asStateFlow()

  // AI Chat History
  private val _aiMessages = MutableStateFlow<List<AiChatMessage>>(
    listOf(
      AiChatMessage(
        id = "msg_init",
        isUser = false,
        text = "Hello Alex! I am NEXORA AI, your private cloud intelligence. I can search files using natural language, summarize complex documents, identify duplicates, and optimize your storage cluster. How can I assist you today?",
        timestamp = "Just now",
        actionSuggestion = "Scan for duplicates"
      )
    )
  )
  val aiMessages = _aiMessages.asStateFlow()

  // Available Subscription Plans
  val subscriptionPlans = listOf(
    SubscriptionPlan(
      id = "FREE",
      name = "Free Tier",
      tagline = "Personal starter cloud",
      storageGb = 15L,
      priceMonthlyUsd = 0.0,
      priceMonthlyInr = 0.0,
      features = listOf("15 GB encrypted cloud storage", "Standard upload speed", "Basic file sharing & links", "Universal mobile app access", "Community support")
    ),
    SubscriptionPlan(
      id = "PREMIUM",
      name = "Premium Cloud",
      tagline = "High-speed personal universe",
      storageGb = 2048L, // 2 TB
      priceMonthlyUsd = 9.99,
      priceMonthlyInr = 499.0,
      features = listOf("2,048 GB (2 TB) scalable storage", "90-Day Free Trial included", "Nexora AI full document intelligence", "Zero-Knowledge Personal Vault", "Automatic phone photo/video backup", "Digital Time Machine (30-day versions)", "Priority 24/7 support"),
      isPopular = true,
      colorHex = "#00F0FF"
    ),
    SubscriptionPlan(
      id = "PRO",
      name = "Pro Powerhouse",
      tagline = "For creators & power users",
      storageGb = 5120L, // 5 TB
      priceMonthlyUsd = 19.99,
      priceMonthlyInr = 999.0,
      features = listOf("5,120 GB (5 TB) NVMe object storage", "Multi-cloud replication (S3 + Ceph + MinIO)", "Unlimited version history", "Passkey & Biometric hardware keys", "Custom cloud identity handle", "Advanced PDF tools & batch conversion", "Dedicated DevOps SLA"),
      colorHex = "#8B5CF6"
    ),
    SubscriptionPlan(
      id = "ULTIMATE",
      name = "Ultimate Universe",
      tagline = "Unlimited capacity for teams & family",
      storageGb = 20480L, // 20 TB fair-use
      isUnlimited = true,
      priceMonthlyUsd = 39.99,
      priceMonthlyInr = 1999.0,
      features = listOf("Unlimited Storage (20 TB fair-use pool)", "Private Family Cloud groups (up to 10 users)", "Bring Your Own Server (S3 & Bare-metal)", "Enterprise Zero-Trust security enclaves", "High-throughput 10 Gbps pipes", "Personal Account Executive"),
      colorHex = "#EC4899"
    )
  )

  // File Operations
  suspend fun insertFile(file: CloudFile) {
    db.cloudFileDao().insertFile(file)
    addActivity("Uploaded ${file.name}", "file", "Uploaded ${formatBytes(file.sizeBytes)} to ${file.parentFolder}")
  }

  suspend fun updateFile(file: CloudFile) {
    db.cloudFileDao().updateFile(file)
  }

  suspend fun toggleFavorite(file: CloudFile) {
    val updated = file.copy(isFavorite = !file.isFavorite)
    db.cloudFileDao().updateFile(updated)
  }

  suspend fun moveToTrash(file: CloudFile) {
    val updated = file.copy(isTrash = true)
    db.cloudFileDao().updateFile(updated)
    addActivity("Moved to Trash: ${file.name}", "trash", "File moved to recycle bin.")
  }

  suspend fun restoreFromTrash(file: CloudFile) {
    val updated = file.copy(isTrash = false)
    db.cloudFileDao().updateFile(updated)
    addActivity("Restored ${file.name}", "restore", "File restored from recycle bin.")
  }

  suspend fun deletePermanently(fileId: String) {
    db.cloudFileDao().deleteFileById(fileId)
  }

  suspend fun emptyTrash() {
    db.cloudFileDao().emptyTrash()
    addActivity("Emptied Recycle Bin", "trash", "All deleted items permanently removed.")
  }

  suspend fun toggleVault(file: CloudFile) {
    val updated = file.copy(isVault = !file.isVault)
    db.cloudFileDao().updateFile(updated)
    val status = if (updated.isVault) "moved into Zero-Knowledge Vault" else "moved out of Vault"
    addActivity("${file.name} $status", "lock", "Encryption state updated.")
  }

  suspend fun createFolder(name: String, parent: String = "root", isVault: Boolean = false) {
    val folder = CloudFolder(
      name = name,
      parentFolder = parent,
      colorHex = if (isVault) "#F59E0B" else "#00F0FF",
      isVault = isVault
    )
    db.cloudFolderDao().insertFolder(folder)
    addActivity("Created folder '$name'", "folder", "New directory created in $parent.")
  }

  suspend fun deleteFolder(id: String) {
    db.cloudFolderDao().deleteFolder(id)
  }

  // Notes & Tasks
  suspend fun saveNote(note: NoteItem) {
    db.noteDao().insertNote(note)
  }

  suspend fun deleteNote(id: String) {
    db.noteDao().deleteNote(id)
  }

  suspend fun saveTask(task: TaskItem) {
    db.taskDao().insertTask(task)
  }

  suspend fun toggleTaskCompleted(task: TaskItem) {
    db.taskDao().updateTask(task.copy(isCompleted = !task.isCompleted))
  }

  suspend fun deleteTask(id: String) {
    db.taskDao().deleteTask(id)
  }

  // Storage Server Management (Admin)
  suspend fun addStorageServer(server: StorageServer) {
    db.serverDao().insertServer(server)
    addActivity("Added Storage Node: ${server.name}", "server", "Storage capacity expanded by ${formatBytes(server.capacityBytes)}.")
  }

  suspend fun deleteStorageServer(id: String) {
    db.serverDao().deleteServer(id)
  }

  // Profile & Settings
  fun updateBackupSettings(photos: Boolean, videos: Boolean, wifiOnly: Boolean) {
    _userProfile.value = _userProfile.value.copy(
      autoBackupPhotos = photos,
      autoBackupVideos = videos,
      syncOnWifiOnly = wifiOnly
    )
  }

  fun updatePlan(planId: String) {
    _userProfile.value = _userProfile.value.copy(currentPlanId = planId)
    addActivity("Subscription updated to $planId", "plan", "Plan features and quotas immediately provisioned.")
  }

  fun updateSecuritySettings(e2e: Boolean, twoFactor: Boolean) {
    _userProfile.value = _userProfile.value.copy(
      e2eEncryptionEnabled = e2e,
      twoFactorEnabled = twoFactor
    )
  }

  // AI Assistant Chat & Actions
  fun sendAiUserMessage(text: String, allFiles: List<CloudFile>) {
    val nowStr = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
    val userMsg = AiChatMessage(UUID.randomUUID().toString(), isUser = true, text = text, timestamp = nowStr)

    // Compute AI intelligent response
    val queryLower = text.lowercase()
    val matched = mutableListOf<String>()
    val aiResponseText: String
    var suggestion: String? = null

    when {
      queryLower.contains("duplicate") || queryLower.contains("redundant") -> {
        val duplicates = allFiles.filter { it.tags.contains("Duplicate", ignoreCase = true) || it.name.contains("Duplicate", ignoreCase = true) }
        matched.addAll(duplicates.map { it.name })
        aiResponseText = if (duplicates.isNotEmpty()) {
          "I analyzed your distributed storage cluster using cryptographic SHA-256 checksums and identified ${duplicates.size} duplicate file: ${duplicates.first().name} (${formatBytes(duplicates.first().sizeBytes)}). Removing this will immediately free up high-speed storage."
        } else {
          "Storage health scan completed! No duplicate files found across your S3 and Ceph storage partitions."
        }
        suggestion = if (duplicates.isNotEmpty()) "Clean duplicate file" else null
      }
      queryLower.contains("summar") || queryLower.contains("whitepaper") || queryLower.contains("pdf") -> {
        val target = allFiles.firstOrNull { it.extension == "PDF" }
        if (target != null) {
          matched.add(target.name)
          aiResponseText = "Here is the AI executive summary of '${target.name}':\n\n${target.contentSummary}\n\n• Key Architecture: Zero-knowledge AES-256-GCM encryption.\n• Redundancy: Reed-Solomon erasure coding (8 data + 4 parity).\n• Cross-Region: Automatic background sync to 4 global nodes."
        } else {
          aiResponseText = "I reviewed your documents. All PDFs have been indexed for full-text semantic search and OCR text extraction."
        }
      }
      queryLower.contains("invoice") || queryLower.contains("financial") || queryLower.contains("audit") || queryLower.contains("2025") || queryLower.contains("2026") -> {
        val finance = allFiles.filter { it.category == "DOCUMENTS" && (it.name.contains("Financial") || it.name.contains("Audit")) }
        matched.addAll(finance.map { it.name })
        aiResponseText = "Found ${finance.size} financial audit and ledger files from 2026. File '${finance.firstOrNull()?.name ?: "Audit Report"}' reports an ARR of $4.2M with 88.4% gross storage margin."
      }
      queryLower.contains("large") || queryLower.contains("optimize") || queryLower.contains("space") -> {
        val largeFiles = allFiles.sortedByDescending { it.sizeBytes }.take(3)
        matched.addAll(largeFiles.map { it.name })
        val totalLarge = largeFiles.sumOf { it.sizeBytes }
        aiResponseText = "Top 3 largest files consuming ${formatBytes(totalLarge)}:\n" +
            largeFiles.joinToString("\n") { "• ${it.name} (${formatBytes(it.sizeBytes)})" } +
            "\n\nRecommendation: Enable video HEVC re-encoding or move archives to cold-storage MinIO pool."
        suggestion = "Compress video media"
      }
      queryLower.contains("photo") || queryLower.contains("image") || queryLower.contains("wallpaper") -> {
        val photos = allFiles.filter { it.category == "PHOTOS" }
        matched.addAll(photos.map { it.name })
        aiResponseText = "Retrieved ${photos.size} image files. '${photos.firstOrNull()?.name ?: "Nebula"}' is indexed with visual tags: Cosmic, Nebula, 4K Astrophotography."
      }
      else -> {
        aiResponseText = "Understood! I've scanned your Nexora Universe for '$text'. Your cloud storage is healthy across 4 distributed server nodes, fully encrypted at rest with zero-knowledge keys."
      }
    }

    val aiMsg = AiChatMessage(
      id = UUID.randomUUID().toString(),
      isUser = false,
      text = aiResponseText,
      timestamp = nowStr,
      actionSuggestion = suggestion,
      matchedFiles = matched
    )

    _aiMessages.value = _aiMessages.value + listOf(userMsg, aiMsg)
  }

  private fun addActivity(title: String, iconType: String, details: String) {
    val newLog = ActivityLog(
      id = UUID.randomUUID().toString(),
      title = title,
      timestamp = "Just now",
      iconType = iconType,
      details = details
    )
    _activities.value = listOf(newLog) + _activities.value.take(15)
  }

  companion object {
    fun formatBytes(bytes: Long): String {
      if (bytes < 1024) return "$bytes B"
      val exp = (Math.log(bytes.toDouble()) / Math.log(1024.0)).toInt()
      val pre = "KMGTPE"[exp - 1]
      return String.format(Locale.US, "%.1f %sB", bytes / Math.pow(1024.0, exp.toDouble()), pre)
    }
  }
}
