package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.CloudFile
import com.example.data.model.CloudFolder
import com.example.data.model.NoteItem
import com.example.data.model.StorageServer
import com.example.data.model.TaskItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
  entities = [CloudFile::class, CloudFolder::class, NoteItem::class, TaskItem::class, StorageServer::class],
  version = 1,
  exportSchema = false
)
abstract class NexoraDatabase : RoomDatabase() {
  abstract fun cloudFileDao(): CloudFileDao
  abstract fun cloudFolderDao(): CloudFolderDao
  abstract fun noteDao(): NoteDao
  abstract fun taskDao(): TaskDao
  abstract fun serverDao(): ServerDao

  companion object {
    @Volatile
    private var INSTANCE: NexoraDatabase? = null

    fun getDatabase(context: Context, scope: CoroutineScope): NexoraDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          NexoraDatabase::class.java,
          "nexora_cloud_db"
        )
        .addCallback(NexoraDatabaseCallback(scope))
        .fallbackToDestructiveMigration()
        .build()
        INSTANCE = instance
        instance
      }
    }
  }

  private class NexoraDatabaseCallback(
    private val scope: CoroutineScope
  ) : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
      super.onCreate(db)
      INSTANCE?.let { database ->
        scope.launch(Dispatchers.IO) {
          populateInitialDatabase(database)
        }
      }
    }
  }
}

suspend fun populateInitialDatabase(db: NexoraDatabase) {
  val now = System.currentTimeMillis()

  // Default Folders
  val defaultFolders = listOf(
    CloudFolder(id = "folder_docs", name = "Documents", parentFolder = "root", colorHex = "#38BDF8"),
    CloudFolder(id = "folder_photos", name = "Photos & Media", parentFolder = "root", colorHex = "#EC4899"),
    CloudFolder(id = "folder_finance", name = "Financial Records", parentFolder = "root", colorHex = "#10B981"),
    CloudFolder(id = "folder_dev", name = "Development & Code", parentFolder = "root", colorHex = "#8B5CF6"),
    CloudFolder(id = "folder_vault_conf", name = "Confidential Contracts", parentFolder = "vault_root", colorHex = "#F59E0B", isVault = true)
  )
  db.cloudFolderDao().insertFolders(defaultFolders)

  // Default Cloud Files
  val initialFiles = listOf(
    CloudFile(
      id = "file_1",
      name = "Nexora_Architecture_Whitepaper_2026.pdf",
      extension = "PDF",
      sizeBytes = 4_820_000L,
      mimeType = "application/pdf",
      category = "DOCUMENTS",
      parentFolder = "Documents",
      createdAt = now - 86400000L * 2,
      updatedAt = now - 3600000L * 4,
      isFavorite = true,
      tags = "Architecture, Cloud, Security",
      versionCount = 3,
      contentSummary = "Comprehensive architectural overview of Nexora distributed zero-knowledge cloud storage cluster with automated multi-region replication.",
      checksum = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855",
      textContent = "NEXORA CLOUD WHITE PAPER v3.2\n\nExecutive Summary:\nNexora Cloud implements zero-knowledge distributed object storage across hybrid S3 and bare-metal nodes. Every chunk is encrypted with client-side AES-256-GCM before transmission."
    ),
    CloudFile(
      id = "file_2",
      name = "Q3_Financial_Audit_Report.xlsx",
      extension = "XLSX",
      sizeBytes = 2_150_000L,
      mimeType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
      category = "DOCUMENTS",
      parentFolder = "Financial Records",
      createdAt = now - 86400000L * 5,
      updatedAt = now - 86400000L * 1,
      isFavorite = false,
      tags = "Finance, Audit, 2026",
      versionCount = 2,
      contentSummary = "Quarterly financial summary showing recurring ARR, storage infrastructure server overhead, and 90-day trial conversion analytics.",
      checksum = "7f83b1657ff1fc53b92dc18148a1d65dfc2d4b1fa3d677284addd200126d9069",
      textContent = "Q3 Financial Summary\nTotal ARR: $4.2M\nGross Margins: 88.4%\nPaid Conversions: 14.8%\nInfrastructure Cost per TB: $1.18/mo"
    ),
    CloudFile(
      id = "file_3",
      name = "Nebula_Galaxy_Nightsky_Wallpaper.jpg",
      extension = "JPG",
      sizeBytes = 12_400_000L,
      mimeType = "image/jpeg",
      category = "PHOTOS",
      parentFolder = "Photos & Media",
      createdAt = now - 86400000L * 3,
      updatedAt = now - 86400000L * 3,
      isFavorite = true,
      tags = "Cosmic, 4K, Wallpaper",
      versionCount = 1,
      contentSummary = "High-resolution astrophotography of the Carina Nebula taken in long exposure mode with 4K clarity.",
      checksum = "cb8379ac2098aa165029e3938a51da0745ea47477ee60a4f696345d58b321457",
      thumbnailUri = "file:///android_asset/sample_nebula.jpg"
    ),
    CloudFile(
      id = "file_4",
      name = "Nexora_Product_Keynote_Demo.mp4",
      extension = "MP4",
      sizeBytes = 145_000_000L,
      mimeType = "video/mp4",
      category = "VIDEOS",
      parentFolder = "root",
      createdAt = now - 86400000L * 7,
      updatedAt = now - 86400000L * 6,
      isFavorite = true,
      tags = "Video, Keynote, 1080p",
      versionCount = 1,
      contentSummary = "Product release keynote demonstration highlighting Nexora AI file assistant, digital time machine, and multi-cloud sync engine.",
      checksum = "9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08"
    ),
    CloudFile(
      id = "file_5",
      name = "Cosmic_Ambient_Soundtrack_Lossless.flac",
      extension = "FLAC",
      sizeBytes = 48_200_000L,
      mimeType = "audio/flac",
      category = "AUDIO",
      parentFolder = "root",
      createdAt = now - 86400000L * 10,
      updatedAt = now - 86400000L * 10,
      isFavorite = false,
      tags = "Audio, Flac, Ambient, Relax",
      versionCount = 1,
      contentSummary = "24-bit 96kHz studio master audio recording of meditative cosmic ambient space synthesizers.",
      checksum = "5d41402abc4b2a76b9719d911017c592"
    ),
    CloudFile(
      id = "file_6",
      name = "kubernetes_storage_cluster_config.json",
      extension = "JSON",
      sizeBytes = 28_000L,
      mimeType = "application/json",
      category = "DEVELOPMENT",
      parentFolder = "Development & Code",
      createdAt = now - 86400000L * 1,
      updatedAt = now - 3600000L * 2,
      isFavorite = false,
      tags = "K8s, DevOps, Cluster",
      versionCount = 4,
      contentSummary = "Kubernetes daemonset manifest and persistent volume claims for Nexora distributed Ceph object storage nodes.",
      checksum = "a384b6463fc216a5f8ecb6670f86456a",
      textContent = "{\n  \"apiVersion\": \"v1\",\n  \"kind\": \"StorageCluster\",\n  \"metadata\": {\n    \"name\": \"nexora-primary-us-east\"\n  },\n  \"spec\": {\n    \"replicas\": 5,\n    \"redundancyScheme\": \"Reed-Solomon-8-4\",\n    \"encryption\": \"AES-256-GCM\"\n  }\n}"
    ),
    CloudFile(
      id = "file_7",
      name = "Client_Master_Service_Agreement_Signed.pdf",
      extension = "PDF",
      sizeBytes = 3_400_000L,
      mimeType = "application/pdf",
      category = "DOCUMENTS",
      parentFolder = "Confidential Contracts",
      createdAt = now - 86400000L * 14,
      updatedAt = now - 86400000L * 14,
      isFavorite = true,
      isVault = true, // Zero-knowledge personal vault
      tags = "Legal, Secret, NDA",
      versionCount = 1,
      contentSummary = "Encrypted zero-knowledge master service agreement and enterprise non-disclosure terms.",
      checksum = "4b227777d4dd1fc61c6f884f48641d02b4d121d3fd328cb08b5531fcacdabf8a",
      textContent = "RESTRICTED & ENCRYPTED DOCUMENT\nZero-knowledge payload verified.\nClient: Enterprise Tier 1\nJurisdiction: International High Security Enclave"
    ),
    CloudFile(
      id = "file_8",
      name = "Duplicate_Presentation_Draft_v1.pptx",
      extension = "PPTX",
      sizeBytes = 18_900_000L,
      mimeType = "application/vnd.openxmlformats-officedocument.presentationml.presentation",
      category = "DOCUMENTS",
      parentFolder = "Documents",
      createdAt = now - 86400000L * 20,
      updatedAt = now - 86400000L * 18,
      isFavorite = false,
      tags = "Duplicate, Slide, Presentation",
      versionCount = 1,
      contentSummary = "Identified by Nexora Smart AI as redundant duplicate of Presentation_Final_v2.pptx. Recommended for cleanup to save 18.9 MB.",
      checksum = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"
    )
  )
  db.cloudFileDao().insertFiles(initialFiles)

  // Default Notes
  val initialNotes = listOf(
    NoteItem(
      id = "note_1",
      title = "Nexora Multi-Region Roadmap 2026",
      content = "1. Deploy Edge storage nodes in Tokyo and Frankfurt\n2. Roll out Passkey zero-knowledge authentication\n3. Launch 20 TB unlimited fair-use storage tier\n4. Expand Nexora AI document OCR to handwritten scripts",
      colorHex = "#1E293B",
      isPinned = true,
      tags = "Strategy, Architecture"
    ),
    NoteItem(
      id = "note_2",
      title = "Cryptographic Recovery Seeds",
      content = "Hardware YubiKey backup located in secure safe compartment #3. Emergency recovery contact: security-ops@nexora.cloud",
      colorHex = "#3B1B75",
      isPinned = false,
      tags = "Security, Personal"
    )
  )
  db.noteDao().insertNotes(initialNotes)

  // Default Tasks
  val initialTasks = listOf(
    TaskItem(
      id = "task_1",
      title = "Review Q3 Disaster Recovery Backup Run",
      description = "Verify integrity of replicated shards across EU-Central and AP-South MinIO clusters.",
      isCompleted = true,
      dueDate = "Yesterday",
      priority = "HIGH",
      category = "DevOps"
    ),
    TaskItem(
      id = "task_2",
      title = "Run Smart Storage Optimization Cleanup",
      description = "Delete detected duplicate presentation drafts and empty 30-day recycle bin.",
      isCompleted = false,
      dueDate = "Today",
      priority = "MEDIUM",
      category = "Storage"
    ),
    TaskItem(
      id = "task_3",
      title = "Authorize Tablet Device Sync Passkey",
      description = "Approve new Galaxy Tab S10 Ultra pairing in Security & Authorized Devices.",
      isCompleted = false,
      dueDate = "Tomorrow",
      priority = "LOW",
      category = "Security"
    )
  )
  db.taskDao().insertTasks(initialTasks)

  // Default Storage Servers Infrastructure
  val initialServers = listOf(
    StorageServer(
      id = "srv_1",
      name = "US-East High Speed Object Cluster (S3)",
      endpoint = "https://s3.us-east-1.nexora.cloud",
      provider = "S3_COMPATIBLE",
      region = "us-east-1 (Virginia)",
      capacityBytes = 500_000_000_000_000L, // 500 TB
      usedBytes = 342_000_000_000_000L,     // 342 TB
      status = "ONLINE",
      latencyMs = 18,
      isPrimary = true
    ),
    StorageServer(
      id = "srv_2",
      name = "EU-Central Ceph Distributed Node",
      endpoint = "https://ceph-eu1.nexora.cloud",
      provider = "CEPH",
      region = "eu-central-1 (Frankfurt)",
      capacityBytes = 350_000_000_000_000L, // 350 TB
      usedBytes = 198_000_000_000_000L,     // 198 TB
      status = "ONLINE",
      latencyMs = 42,
      isPrimary = false
    ),
    StorageServer(
      id = "srv_3",
      name = "AP-South MinIO High Density Pool",
      endpoint = "https://minio-in1.nexora.cloud",
      provider = "MINIO",
      region = "ap-south-1 (Mumbai)",
      capacityBytes = 400_000_000_000_000L, // 400 TB
      usedBytes = 224_000_000_000_000L,     // 224 TB
      status = "ONLINE",
      latencyMs = 26,
      isPrimary = false
    ),
    StorageServer(
      id = "srv_4",
      name = "Private On-Prem Bare Metal Node 04",
      endpoint = "https://vault-node04.internal.nexora.cloud",
      provider = "SELF_HOSTED",
      region = "Dedicated Enterprise Rack",
      capacityBytes = 120_000_000_000_000L, // 120 TB
      usedBytes = 54_000_000_000_000L,      // 54 TB
      status = "ONLINE",
      latencyMs = 4,
      isPrimary = false
    )
  )
  db.serverDao().insertServers(initialServers)
}
