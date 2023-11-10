package org.plugin.plugin.panels

import org.plugin.plugin.Utility
import java.awt.BorderLayout
import javax.swing.*


class LeaderboardPanel : JPanel() {
    init {
        this.layout = BorderLayout()
        this.add(JLabel("Leaderboard"), BorderLayout.NORTH)

        Utility.userTablePanel(this)
        Utility.teamTablePanel(this)
    }
}

