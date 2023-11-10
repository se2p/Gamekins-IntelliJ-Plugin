package org.plugin.plugin.panels


import org.plugin.plugin.Utility
import java.awt.BorderLayout
import javax.swing.*



class CompletedQuestsChallengesPanel : JPanel() {
    init {

        this.layout = BorderLayout(11,11)
        this.add(JLabel("Completed Quests & Challenges"), BorderLayout.CENTER)

        Utility.createAndShowCompletedQuestsTable(this)
        Utility.createChallengePanel(this)
        
    }
}

