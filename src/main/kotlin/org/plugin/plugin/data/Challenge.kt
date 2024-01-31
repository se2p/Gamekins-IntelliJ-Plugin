package org.plugin.plugin.data

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class ChallengeParameter(
  val workspace: Workspace,
  @SerializedName("jacocoCSVPath") val jacocoCsvPath: String,
  @SerializedName("searchCommitCount") val searchCommitCount: Int,
  @SerializedName("showPitOutput") val showPitOutput: Boolean,
  @SerializedName("projectCoverage") val projectCoverage: Double,
  @SerializedName("currentQuestsCount") val currentQuestsCount: Int,
  val solved: Long,
  val remote: String,
  val branch: String,
  @SerializedName("jacocoResultsPath") val jacocoResultsPath: String,
  @SerializedName("storedChallengesCount") val storedChallengesCount: Int,
  val generated: Int,
  @SerializedName("projectTests") val projectTests: Int,
  @SerializedName("currentChallengesCount") val currentChallengesCount: Int,
  @SerializedName("pitConfiguration") val pitConfiguration: String,
  @SerializedName("projectName") val projectName: String
)

@Serializable
data class Details(
  val coverage: Double,
  val fileName: String,
  val test: Boolean,
  val filePath: String,
  val path: String,
  val fileExtension: String,
  val packageName: String,
  val parameters: ChallengeParameter,
)

@Serializable
data class Workspace(
  val parent: Workspace?,
  @SerializedName("totalDiskSpace") val totalDiskSpace: Long,
  @SerializedName("usableDiskSpace") val usableDiskSpace: Long,
  val name: String,
  @SerializedName("freeDiskSpace") val freeDiskSpace: Long,
  val remote: Boolean,
  @SerializedName("baseName") val baseName: String,
  @SerializedName("directory") val isDirectory: Boolean
)


@Serializable
data class Challenge(
  val snippet: String?,
  val score: Int?,
  val created: Long,
  @SerializedName("builtCorrectly") val isBuiltCorrectly: Boolean? = false,
  val name: String?,
  @SerializedName("toolTip") val hasToolTip: Boolean? = false,
  @SerializedName("highlightedFileContent") val highlightedFileContent: String? = null,
  val solved: Long? = 0,
  val parameters: ChallengeParameter? = null,
  @SerializedName("toolTipText") val toolTipText: String? = null,
  val generalReason: String? = null,
  @SerializedName("details")
  val details: Details,

  )

data class RejectedChallenge(val first: Challenge, val second: String)

@Serializable
data class ChallengeList(val currentChallenges: List<Challenge>)

data class StoredChallengeList(val storedChallenges: List<Challenge>)

data class CompletedChallengeList(val completedChallenges: List<Challenge>)

data class RejectedChallengeList(val rejectedChallenges: List<RejectedChallenge>)
