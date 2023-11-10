import com.google.gson.annotations.SerializedName

data class ChallengeParameter(
  val workspace: Workspace,
  @SerializedName("jacocoCSVPath") val jacocoCsvPath: String,
  @SerializedName("searchCommitCount") val searchCommitCount: Int,
  @SerializedName("showPitOutput") val showPitOutput: Boolean,
  @SerializedName("projectCoverage") val projectCoverage: Int,
  @SerializedName("currentQuestsCount") val currentQuestsCount: Int,
  val solved: Int,
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


data class Challenge(
  val snippet: String?,
  val score: Int?,
  val created: Long,
  @SerializedName("builtCorrectly") val isBuiltCorrectly: Boolean? = false,
  val name: String?,
  @SerializedName("toolTip") val hasToolTip: Boolean? = false,
  @SerializedName("highlightedFileContent") val highlightedFileContent: String? = null,
  val solved: Int? = 0,
  val parameters: ChallengeParameter? = null,
  @SerializedName("toolTipText") val toolTipText: String? = null,
  val generalReason: String? = null,
  )

data class RejectedChallenge(val first: Challenge, val second: String)

data class ChallengeList(val currentChallenges: List<Challenge>)

data class StoredChallengeList(val storedChallenges: List<Challenge>)

data class CompletedChallengeList(val completedChallenges: List<Challenge>)

data class RejectedChallengeList(val rejectedChallenges: List<RejectedChallenge>)
