package org.plugin.plugin.panels

import Challenge
import ChallengeList
import CompletedChallengeList
import RejectedChallengeList
import com.google.gson.Gson
import com.intellij.ui.Gray
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.table.JBTable
import org.plugin.plugin.Constants
import org.plugin.plugin.Utility
import org.plugin.plugin.data.RestClient
import java.awt.*
import java.util.List
import javax.swing.*
import javax.swing.border.LineBorder
import javax.swing.table.DefaultTableCellRenderer
import javax.swing.table.DefaultTableModel


class AcceptedRejectedChallengesPanel : JPanel() {
    init {
        setLayout(GridLayout(1, 2, 2, 0))

        // Completed Challenges Table
        val completedChallengesPanel = JScrollPane()

        completedChallengesPanel.layout = ScrollPaneLayout()
        completedChallengesPanel.setBackground(Color(219, 255, 224)) // #dbffe0
        val completedChallengesLabel = JLabel("Completed Challenges")
        completedChallengesLabel.setFont(Font("SansSerif", Font.PLAIN, 14))
        completedChallengesLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0))
        createCompletedChallengesTable(completedChallengesPanel)
        add(completedChallengesPanel)

        // Rejected Challenges Table
        val rejectedChallengesPanel = JPanel()
        rejectedChallengesPanel.setBackground(Color(255, 244, 232))
        val rejectedChallengesLabel = JLabel("Rejected Challenges")
        rejectedChallengesLabel.setFont(Font("SansSerif", Font.PLAIN, 14))
        rejectedChallengesLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0))
        createRejectedChallengesTable(rejectedChallengesPanel)

        add(rejectedChallengesPanel)

    }

    private fun createCompletedChallengesTable(aInJPanel: JScrollPane) {

        val completedChallenges = List.of<Any>(
            Challenge("Challenge 1", 2, 1, name = "test1"),
            Challenge("Challenge 2", 1, 1, name = "test2"),
            Challenge("Challenge 3", 3, 1, name = "test3"),
            Challenge("Challenge 4", 1, 1, name = "test4")
        )

        val tableModel = DefaultTableModel(arrayOf("Completed Challenges"), 0)
        val table = JBTable(tableModel)

        table.tableHeader.setFont(Font("SansSerif", Font.BOLD, 18))
        table.tableHeader.setBackground(Gray._77)
        table.tableHeader.setForeground(JBColor.WHITE)

        val cellRenderer = DefaultTableCellRenderer()
        cellRenderer.setHorizontalAlignment(SwingConstants.LEFT)
        table.columnModel.getColumn(0).setCellRenderer(cellRenderer)
        table.rowHeight = 20

        for (challenge in completedChallenges) {
            if (!challenge.toString().contains("nothing developed recently")) {
                val challengeName: String = "challenge.getName()"
                val score: Int = 11
                var rowData = challengeName
                if (score > 1) {
                    rowData += " ($score points)"
                } else if (score == 1) {
                    rowData += " ($score point)"
                }
                if ("Mutation" == "challenge.getName()") {
                    rowData = "<html><font color='blue'>$rowData</font></html>"
                }
                tableModel.addRow(arrayOf<Any>(rowData))
            }
        }

        aInJPanel.add(table)
        val scrollPane = JBScrollPane(table)
        aInJPanel.setViewportView(scrollPane)
    }

    private fun createRejectedChallengesTable(aInJPanel: JPanel) {

        aInJPanel.layout = BorderLayout(130,30)

        val descriptionLabel = JLabel("Rejected Challenges")
        aInJPanel.add(descriptionLabel, BorderLayout.PAGE_START)

        val challengesPanel = JPanel()
        challengesPanel.setLayout(BoxLayout(challengesPanel, BoxLayout.Y_AXIS))
        challengesPanel.border = LineBorder(JBColor.RED, 1)


        val queryParams = mapOf(
            "job" to Constants.TEST_JOB
        )

        try {

            val response = RestClient().get(Constants.API_BASE_URL + Constants.GET_REJECTED_CHALLENGES, queryParams)
            val lRejectedChallengeList = Gson().fromJson(response, RejectedChallengeList::class.java).rejectedChallenges

            for (index in 0 until lRejectedChallengeList.size) {

                val challengePanel = JPanel()
                challengePanel.border = LineBorder(JBColor.GREEN, 1)
                challengePanel.setLayout(BorderLayout())

                val challengeHeader = JPanel()
                challengeHeader.setLayout(FlowLayout(FlowLayout.LEFT))

                val challengeTitleLabel = JLabel(lRejectedChallengeList[index].first.generalReason)
                val challengeTitleName = JLabel(lRejectedChallengeList[index].first.name)

                challengeTitleName.background = JBColor.yellow

                challengeHeader.add(challengeTitleLabel)
                challengeHeader.add(challengeTitleName)

                challengePanel.add(challengeHeader, BorderLayout.CENTER)
                challengesPanel.add(challengePanel)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }



       /* val completedChallenges = List.of<Any>(
            Challenge("Challenge 1", 2, 1, name = "test1"),
            Challenge("Challenge 2", 1, 1, name = "test2"),
            Challenge("Challenge 3", 3, 1, name = "test3"),
            Challenge("Challenge 4", 1, 1, name = "test4")
        )

        val tableModel = DefaultTableModel(arrayOf("Rejected Challenges"), 0)

        val table = JBTable(tableModel)

        table.tableHeader.setFont(Font("SansSerif", Font.BOLD, 18))
        table.tableHeader.setBackground(Gray._77)
        table.tableHeader.setForeground(JBColor.WHITE)

        val cellRenderer = DefaultTableCellRenderer()
        cellRenderer.setHorizontalAlignment(SwingConstants.LEFT)
        table.columnModel.getColumn(0).setCellRenderer(cellRenderer)
        table.rowHeight = 20

        for (challenge in completedChallenges) {
            if (!challenge.toString().contains("nothing developed recently")) {
                val challengeName: String = "challenge.getName()"
                val score: Int = 11
                var rowData = challengeName
                if (score > 1) {
                    rowData += " ($score points)"
                } else if (score == 1) {
                    rowData += " ($score point)"
                }
                if ("Mutation" == "challenge.getName()") {
                    rowData = "<html><font color='blue'>$rowData</font></html>"
                }
                tableModel.addRow(arrayOf<Any>(rowData))
            }
        }

        val scrollPane = JBScrollPane(table)
        aInJPanel.setViewportView(scrollPane)*/
        aInJPanel.add(challengesPanel)


    }
}

