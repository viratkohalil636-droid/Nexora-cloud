package com.example.data.model

data class SubscriptionPlan(
  val id: String,
  val name: String,
  val tagline: String,
  val storageGb: Long,
  val isUnlimited: Boolean = false,
  val priceMonthlyUsd: Double,
  val priceMonthlyInr: Double,
  val features: List<String>,
  val isPopular: Boolean = false,
  val colorHex: String = "#00F0FF"
)

data class UserProfile(
  val id: String = "user-nexus-001",
  val fullName: String = "Alex Vance",
  val email: String = "alex.vance@nexora.cloud",
  val phone: String = "+1 (555) 234-8971",
  val cloudHandle: String = "alex.nexora.cloud",
  val avatarSeed: String = "cosmic_nebula",
  val currentPlanId: String = "PREMIUM",
  val trialDaysRemaining: Int = 84, // 90-day trial with 84 days left
  val trialEndDate: String = "January 24, 2027",
  val e2eEncryptionEnabled: Boolean = true,
  val twoFactorEnabled: Boolean = true,
  val autoBackupPhotos: Boolean = true,
  val autoBackupVideos: Boolean = true,
  val syncOnWifiOnly: Boolean = true
)

data class ActivityLog(
  val id: String,
  val title: String,
  val timestamp: String,
  val iconType: String,
  val details: String
)

data class AiChatMessage(
  val id: String,
  val isUser: Boolean,
  val text: String,
  val timestamp: String,
  val actionSuggestion: String? = null,
  val matchedFiles: List<String> = emptyList()
)

data class ApkBuildConfig(
  val appName: String = "Nexora Cloud",
  val packageName: String = "com.aistudio.nexoracloud.kqxznv",
  val versionName: String = "1.0.0-PROD",
  val versionCode: Int = 100,
  val gitCommitHash: String = "e7b99d4f2c",
  val buildType: String = "Release / Signed",
  val targetSdk: Int = 36,
  val minSdk: Int = 24,
  val certFingerprint: String = "SHA256: 4C:8A:2F:91:0E:D3:5B:78:19:AC:6E:FA:90:31:8B:22",
  val status: String = "Ready for Distribution"
)
