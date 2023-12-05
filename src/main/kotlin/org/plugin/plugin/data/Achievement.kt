package org.plugin.plugin.data

import java.util.HashMap

class Achievement(val fullyQualifiedFunctionName: String, val badgePath: String, val solvedTimeString: String,
                  val unsolvedBadgePath: String, val description: String,
                  val additionalParameters: HashMap<String, String>, val secret: Boolean, val title: String
)

data class CompletedAchievementsList(val completedAchievements: List<Achievement>)

data class UnsolvedAchievementsList(val unsolvedAchievements: List<Achievement>)