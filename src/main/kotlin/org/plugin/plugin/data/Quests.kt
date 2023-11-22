package org.plugin.plugin.data

import Challenge

data class  Quest(val name: String, val steps: ArrayList<QuestStep>);


data class QuestStep(val description: String, val challenge: Challenge);

data class QuestsList(val currentQuests: List<Quest>)

data class QuestsListTasks(val currentQuestTasks: List<QuestTask>)

