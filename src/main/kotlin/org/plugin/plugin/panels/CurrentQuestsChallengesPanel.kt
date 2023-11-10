package org.plugin.plugin.panels


import org.plugin.plugin.Utility
import java.awt.BorderLayout
import javax.swing.*



class CurrentQuestsChallengesPanel : JPanel() {
    init {

        this.layout = BorderLayout(11,11)
        this.add(JLabel("Current Quests & Challenges"), BorderLayout.CENTER)

        Utility.createQuests(this)
        Utility.createChallenges(this)
        Utility.createStoredButton(this)
    }
}

