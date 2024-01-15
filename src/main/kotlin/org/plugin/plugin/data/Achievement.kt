package org.plugin.plugin.data

import java.util.HashMap

data class Achievement(val fullyQualifiedFunctionName: String, val badgePath: String, val solvedTimeString: String,
                  val unsolvedBadgePath: String, val description: String,
                  val additionalParameters: HashMap<String, String>, val secret: Boolean, val title: String)

data class BadgeAchievement(var badgePaths: List<String>, val lowerBounds: List<Double>,
                       val fullyQualifiedFunctionName: String, val description: String, val title: String,
                       var badgeCounts : MutableList<Int>, val unit: String, var titles: List<String>,
                       var ascending: Boolean)

data class ProgressAchievement(var badgePath: String, val milestones: List<Int>,
                          val fullyQualifiedFunctionName: String, val description: String, val title: String,
                          var progress : Int, val unit: String)

data class CompletedAchievementsList(val completedAchievements: List<Achievement>)

data class UnsolvedAchievementsList(val unsolvedAchievements: List<Achievement>)

data class BadgeAchievementsList(val badgeAchievements: List<BadgeAchievement>)

data class ProgressAchievementsList(val progressAchievements: List<ProgressAchievement>)