package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.CloudFile
import com.example.data.model.CloudFolder
import com.example.data.model.NoteItem
import com.example.data.model.StorageServer
import com.example.data.model.TaskItem
import kotlinx.coroutines.flow.Flow

@Dao
interface CloudFileDao {
  @Query("SELECT * FROM cloud_files WHERE isTrash = 0 AND isVault = 0 ORDER BY updatedAt DESC")
  fun getAllActiveFiles(): Flow<List<CloudFile>>

  @Query("SELECT * FROM cloud_files WHERE isTrash = 0 AND isVault = 0 AND parentFolder = :folder ORDER BY updatedAt DESC")
  fun getFilesByFolder(folder: String): Flow<List<CloudFile>>

  @Query("SELECT * FROM cloud_files WHERE isTrash = 0 AND isVault = 0 AND category = :category ORDER BY updatedAt DESC")
  fun getFilesByCategory(category: String): Flow<List<CloudFile>>

  @Query("SELECT * FROM cloud_files WHERE isTrash = 0 AND isVault = 0 AND isFavorite = 1 ORDER BY updatedAt DESC")
  fun getFavoriteFiles(): Flow<List<CloudFile>>

  @Query("SELECT * FROM cloud_files WHERE isTrash = 0 AND isVault = 1 ORDER BY updatedAt DESC")
  fun getVaultFiles(): Flow<List<CloudFile>>

  @Query("SELECT * FROM cloud_files WHERE isTrash = 1 ORDER BY updatedAt DESC")
  fun getTrashFiles(): Flow<List<CloudFile>>

  @Query("SELECT * FROM cloud_files WHERE isTrash = 0 AND (name LIKE '%' || :query || '%' OR tags LIKE '%' || :query || '%' OR contentSummary LIKE '%' || :query || '%')")
  fun searchFiles(query: String): Flow<List<CloudFile>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertFile(file: CloudFile)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertFiles(files: List<CloudFile>)

  @Update
  suspend fun updateFile(file: CloudFile)

  @Delete
  suspend fun deleteFile(file: CloudFile)

  @Query("DELETE FROM cloud_files WHERE id = :id")
  suspend fun deleteFileById(id: String)

  @Query("DELETE FROM cloud_files WHERE isTrash = 1")
  suspend fun emptyTrash()

  @Query("SELECT COUNT(*) FROM cloud_files")
  suspend fun getFileCount(): Int
}

@Dao
interface CloudFolderDao {
  @Query("SELECT * FROM cloud_folders WHERE isVault = 0 ORDER BY name ASC")
  fun getAllFolders(): Flow<List<CloudFolder>>

  @Query("SELECT * FROM cloud_folders WHERE isVault = 1 ORDER BY name ASC")
  fun getVaultFolders(): Flow<List<CloudFolder>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertFolder(folder: CloudFolder)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertFolders(folders: List<CloudFolder>)

  @Query("DELETE FROM cloud_folders WHERE id = :id")
  suspend fun deleteFolder(id: String)
}

@Dao
interface NoteDao {
  @Query("SELECT * FROM notes ORDER BY isPinned DESC, updatedAt DESC")
  fun getAllNotes(): Flow<List<NoteItem>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertNote(note: NoteItem)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertNotes(notes: List<NoteItem>)

  @Update
  suspend fun updateNote(note: NoteItem)

  @Query("DELETE FROM notes WHERE id = :id")
  suspend fun deleteNote(id: String)
}

@Dao
interface TaskDao {
  @Query("SELECT * FROM tasks ORDER BY isCompleted ASC, priority DESC")
  fun getAllTasks(): Flow<List<TaskItem>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertTask(task: TaskItem)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertTasks(tasks: List<TaskItem>)

  @Update
  suspend fun updateTask(task: TaskItem)

  @Query("DELETE FROM tasks WHERE id = :id")
  suspend fun deleteTask(id: String)
}

@Dao
interface ServerDao {
  @Query("SELECT * FROM storage_servers ORDER BY isPrimary DESC, name ASC")
  fun getAllServers(): Flow<List<StorageServer>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertServer(server: StorageServer)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertServers(servers: List<StorageServer>)

  @Update
  suspend fun updateServer(server: StorageServer)

  @Query("DELETE FROM storage_servers WHERE id = :id")
  suspend fun deleteServer(id: String)
}
