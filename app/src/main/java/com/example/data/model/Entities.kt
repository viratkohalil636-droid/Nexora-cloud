package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "cloud_files")
data class CloudFile(
  @PrimaryKey val id: String = UUID.randomUUID().toString(),
  val name: String,
  val extension: String,
  val sizeBytes: Long,
  val mimeType: String,
  val category: String, // DOCUMENTS, PHOTOS, VIDEOS, AUDIO, DEVELOPMENT, ARCHIVES, OTHER
  val parentFolder: String = "root",
  val createdAt: Long = System.currentTimeMillis(),
  val updatedAt: Long = System.currentTimeMillis(),
  val isFavorite: Boolean = false,
  val isVault: Boolean = false,
  val isTrash: Boolean = false,
  val isOffline: Boolean = false,
  val tags: String = "",
  val versionCount: Int = 1,
  val contentSummary: String = "",
  val checksum: String = "",
  val thumbnailUri: String = "",
  val textContent: String = ""
)

@Entity(tableName = "cloud_folders")
data class CloudFolder(
  @PrimaryKey val id: String = UUID.randomUUID().toString(),
  val name: String,
  val parentFolder: String = "root",
  val colorHex: String = "#00F0FF",
  val isVault: Boolean = false,
  val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "notes")
data class NoteItem(
  @PrimaryKey val id: String = UUID.randomUUID().toString(),
  val title: String,
  val content: String,
  val colorHex: String = "#1E293B",
  val updatedAt: Long = System.currentTimeMillis(),
  val isPinned: Boolean = false,
  val tags: String = ""
)

@Entity(tableName = "tasks")
data class TaskItem(
  @PrimaryKey val id: String = UUID.randomUUID().toString(),
  val title: String,
  val description: String = "",
  val isCompleted: Boolean = false,
  val dueDate: String = "Today",
  val priority: String = "MEDIUM", // HIGH, MEDIUM, LOW
  val category: String = "Work"
)

@Entity(tableName = "storage_servers")
data class StorageServer(
  @PrimaryKey val id: String = UUID.randomUUID().toString(),
  val name: String,
  val endpoint: String,
  val provider: String, // S3_COMPATIBLE, SELF_HOSTED, MINIO, CEPH
  val region: String,
  val capacityBytes: Long,
  val usedBytes: Long,
  val status: String, // ONLINE, DEGRADED, SYNCING
  val latencyMs: Int,
  val isPrimary: Boolean = false
)
