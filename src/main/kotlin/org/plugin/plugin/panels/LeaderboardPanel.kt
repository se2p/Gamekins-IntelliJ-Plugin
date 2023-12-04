package org.plugin.plugin.panels

import com.google.gson.Gson
import com.intellij.openapi.wm.impl.welcomeScreen.learnIde.coursesInProgress.mainBackgroundColor
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.table.JBTable
import com.intellij.util.ui.JBUI
import org.plugin.plugin.Constants
import org.plugin.plugin.Utility
import org.plugin.plugin.components.IconCellRenderer
import org.plugin.plugin.data.RestClient
import org.plugin.plugin.data.TeamList
import org.plugin.plugin.data.UserList
import java.awt.*
import java.util.*
import java.util.prefs.Preferences
import javax.swing.BorderFactory
import javax.swing.ImageIcon
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTable
import javax.swing.SwingConstants
import javax.swing.table.DefaultTableCellRenderer
import javax.swing.table.DefaultTableModel


class LeaderboardPanel : JPanel() {

    private val lCenterRenderer = object : DefaultTableCellRenderer() {
        init {
            horizontalAlignment = SwingConstants.CENTER
        }

        override fun getTableCellRendererComponent(
            table: JTable?,
            value: Any?,
            isSelected: Boolean,
            hasFocus: Boolean,
            row: Int,
            column: Int
        ): Component {
            val cellComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column)
            val isHeader = row == -1
            if (isHeader) {
                cellComponent.background = Color.BLACK
                cellComponent.foreground = Color.WHITE
            } else
            {
                cellComponent.background = Color.WHITE
                cellComponent.foreground = Color.BLACK
            }

            if (cellComponent is JComponent) {
                cellComponent.border = BorderFactory.createLineBorder(Color.GRAY, 1)
            }

            return cellComponent
        }
    }

    init {
        this.setBackground(mainBackgroundColor)
        this.layout = GridBagLayout()
        val lHeader = JLabel("Leaderboard")
        lHeader.setFont(Font("Arial", Font.BOLD, 18))

        lHeader.alignmentX = JLabel.CENTER_ALIGNMENT
        lHeader.alignmentY = JLabel.CENTER_ALIGNMENT

        val gbc = GridBagConstraints()
        gbc.insets = JBUI.insets(15, 15, 0, 15)
        gbc.gridx = 0
        gbc.gridy = 0
        gbc.weightx = 1.0
        gbc.weighty = 0.1
        gbc.fill = GridBagConstraints.BOTH
        gbc.gridwidth = GridBagConstraints.REMAINDER;

        lHeader.setHorizontalAlignment(SwingConstants.LEFT);

        this.add(lHeader, gbc)

        userTablePanel(this)
        teamTablePanel(this)
    }

    private fun userTablePanel(mainPanel: JPanel) {

        val lProjectName = Utility.lPreferences.get("projectName", "")
        if (lProjectName != "") {
            val queryParams = mapOf(
                "job" to lProjectName
            )

            val response = RestClient.getInstance().get(Utility.getBaseUrl()+ Constants.GET_USERS, queryParams)
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

            val tableModel = object : DefaultTableModel(columns, 0) {
                override fun isCellEditable(row: Int, column: Int): Boolean {
                    return false
                }
            }

            val table = JBTable(tableModel)
            table.background = mainBackgroundColor
            /*table.tableHeader.border = BorderFactory.createLineBorder(JBColor.GRAY, 1)
            table.tableHeader.background = JBColor.black*/

            table.rowHeight = 70

            table.columnModel.getColumn(1).cellRenderer = IconCellRenderer()

            for ((index, userDetails) in userList.withIndex()) {
                val imageUrl = "/avatars/${userDetails.image}"
                val icon = ImageIcon(this::class.java.getResource(imageUrl))
                val resizedIcon = Utility.resizeImageIcon(icon, 50, 50)

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


            table.setDefaultRenderer(Any::class.java, lCenterRenderer)
            val tableHeader = table.tableHeader
            tableHeader.defaultRenderer = lCenterRenderer
            table.tableHeader.setFont(Font("SansSerif", Font.BOLD, 12))
            val scrollPane = JBScrollPane(table)

            val gbc = GridBagConstraints()
            gbc.insets = JBUI.insets(10)
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

        val tableModel = object : DefaultTableModel(columns, 0) {
            override fun isCellEditable(row: Int, column: Int): Boolean {
                return false
            }
        }

        val table = JBTable(tableModel)
        table.background = mainBackgroundColor
        table.tableHeader.border = BorderFactory.createLineBorder(Color.GRAY, 1)
        table.rowHeight = 70

        val projectName = Utility.lPreferences.get("projectName", "")

        if (projectName.isNotEmpty()) {
            val queryParams = mapOf("job" to projectName)

            try {
                val response = RestClient.getInstance().get("${Utility.getBaseUrl()}${Constants.GET_TEAMS}", queryParams)
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

                table.tableHeader.font = Font("SansSerif", Font.BOLD, 12)
               // table.font = Font("SansSerif", Font.BOLD, 12)
                table.tableHeader.defaultRenderer = lCenterRenderer
                table.setDefaultRenderer(Any::class.java, lCenterRenderer)

                val scrollPane = JBScrollPane(table)
                val gbc = GridBagConstraints().apply {
                    insets = JBUI.insets(10)
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

