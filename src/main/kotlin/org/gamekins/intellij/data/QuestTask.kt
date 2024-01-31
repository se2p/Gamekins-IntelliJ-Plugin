package org.gamekins.intellij.data

class QuestTask(val name: String, val completedPercentage: Int, val score: Int, val title: String)

data class QuestTasksList(val currentQuestTasks: List<QuestTask>)

data class CompletedQuestTasksList(val completedQuestTasks: List<QuestTask>)