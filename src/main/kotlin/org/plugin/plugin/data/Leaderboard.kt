package org.plugin.plugin.data

import java.io.Serializable

/**
 * Container for the details of a user displayed on the Leaderboard.
 *
 * @author Philipp Straubinger
 * @since 0.1
 */
class UserDetails( val userName: String,  val teamName: String,
                   val score: Int,  val completedChallenges: Int,
                   val completedQuests: Int,  val completedQuestTasks: Int,
                   val unfinishedQuests: Int,  val completedAchievements: Int,
                   val url: String,  val image: String) : Serializable

data class UserList(val users: List<UserDetails>)


/**
 * Container for the details of a team displayed on the Leaderboard.
 *
 * @author Philipp Straubinger
 * @since 0.1
 */
class TeamDetails( val teamName: String,  var score: Int,
                   var completedChallenges: Int,  var completedQuests: Int,
                   val completedQuestTasks: Int,  var unfinishedQuests: Int,
                   var completedAchievements: Int): Serializable

data class TeamList(val teams: List<TeamDetails>)
