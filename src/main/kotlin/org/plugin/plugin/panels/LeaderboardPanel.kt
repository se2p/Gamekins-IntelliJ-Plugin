package org.plugin.plugin.panels

import com.google.gson.Gson
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.table.JBTable
import com.intellij.util.ui.JBUI
import org.plugin.plugin.Constants
import org.plugin.plugin.Utility
import org.plugin.plugin.components.IconCellRenderer
import org.plugin.plugin.data.RestClient
import org.plugin.plugin.data.TeamList
import org.plugin.plugin.data.UserList
import java.awt.Font
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.util.*
import java.util.prefs.Preferences
import javax.swing.*
import javax.swing.table.DefaultTableCellRenderer
import javax.swing.table.DefaultTableModel


class LeaderboardPanel : JPanel() {

    val lPreferences = Preferences.userRoot().node("org.plugin.plugin.panels")

    init {
        this.layout = GridBagLayout()
        val lHeader = JLabel("<html><h1>Leaderboard</h1></html>")
        lHeader.alignmentX = JLabel.CENTER_ALIGNMENT
        lHeader.alignmentY = JLabel.CENTER_ALIGNMENT

        val gbc = GridBagConstraints()
        gbc.gridx = 0
        gbc.gridy = 0
        gbc.weightx = 1.0
        gbc.weighty = 0.1
        gbc.fill = GridBagConstraints.BOTH

        gbc.gridwidth = GridBagConstraints.REMAINDER;

        lHeader.setHorizontalAlignment(SwingConstants.CENTER);

        this.add(lHeader, gbc)

        userTablePanel(this)
        teamTablePanel(this)
    }

    private fun userTablePanel(mainPanel: JPanel) {

        val lProjectName = lPreferences.get("projectName", "")
        if (lProjectName != "") {
            val queryParams = mapOf(
                "job" to lProjectName
            )

            val response = RestClient.getInstance().get(Constants.API_BASE_URL + Constants.GET_USERS, queryParams)
            val userList = Gson().fromJson(response, UserList::class.java).users

            val columnNames = arrayOf(
                "#",
                "icon",
                "Participant",
                "Team",
                "Completed Challenges",
                "Completed Quests",
                "Completed Achievements",
                "Score"
            )
            val columns: Vector<String> = Vector()
            for (columnName in columnNames) {
                columns.add(columnName)
            }

            val tableModel = DefaultTableModel(columns, 0)

            val table = JBTable(tableModel)
            table.rowHeight = 100

            table.columnModel.getColumn(1).cellRenderer = IconCellRenderer()


            for ((index, userDetails) in userList.withIndex()) {
                val imageUrl = "/avatars/${userDetails.image}"
                val icon = ImageIcon(this::class.java.getResource(imageUrl))

                val resizedIcon = Utility.resizeImageIcon(icon, 60, 60)

                tableModel.addRow(
                    arrayOf(
                        index,
                        resizedIcon,
                        userDetails.userName,
                        userDetails.teamName,
                        userDetails.completedChallenges,
                        userDetails.completedQuests,
                        userDetails.completedAchievements,
                        userDetails.score
                    )
                )
            }

            val centerRenderer = DefaultTableCellRenderer()
            centerRenderer.setHorizontalAlignment(SwingConstants.CENTER)

            table.setDefaultRenderer(Any::class.java, centerRenderer)
            val tableHeader = table.tableHeader
            tableHeader.defaultRenderer = centerRenderer
            table.tableHeader.setFont(Font("SansSerif", Font.BOLD, 12))
            val scrollPane = JBScrollPane(table)

            val gbc = GridBagConstraints()
            gbc.gridx = 0
            gbc.gridy = 1
            gbc.weightx = 1.0
            gbc.weighty = 0.45
            gbc.fill = GridBagConstraints.BOTH

            mainPanel.add(scrollPane, gbc)

        }
    }

    private fun teamTablePanel(mainPanel: JPanel) {

        val columnNames = arrayOf("#", "Team", "Completed Challenges", "Completed Quests", "Completed Achievements", "Score")
        val columns: Vector<String> = Vector(columnNames.asList())

        val tableModel = DefaultTableModel(columns, 0)
        val table = JBTable(tableModel)
        table.rowHeight = 100

        val projectName = Utility.lPreferences.get("projectName", "")
        if (projectName.isNotEmpty()) {
            val queryParams = mapOf("job" to projectName)

            try {
                val response = RestClient.getInstance().get("${Constants.API_BASE_URL}${Constants.GET_TEAMS}", queryParams)
                val teamList = Gson().fromJson(response, TeamList::class.java).teams

                teamList.forEachIndexed { index, team ->
                    tableModel.addRow(
                        arrayOf(
                            index,
                            team.teamName,
                            team.completedChallenges,
                            team.completedQuests,
                            team.completedAchievements,
                            team.score
                        )
                    )
                }

                val centerRenderer = DefaultTableCellRenderer().apply {
                    horizontalAlignment = SwingConstants.CENTER
                }

                table.setDefaultRenderer(Any::class.java, centerRenderer)
                table.tableHeader.font = Font("SansSerif", Font.BOLD, 12)
                table.font = Font("SansSerif", Font.BOLD, 14)
                table.tableHeader.defaultRenderer = centerRenderer

                val scrollPane = JBScrollPane(table)
                val gbc = GridBagConstraints().apply {
                    insets = JBUI.insets(5, 0)
                    gridx = 0
                    gridy = 2
                    weightx = 1.0
                    weighty = 0.45
                    fill = GridBagConstraints.BOTH
                }

                mainPanel.add(scrollPane, gbc)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}

