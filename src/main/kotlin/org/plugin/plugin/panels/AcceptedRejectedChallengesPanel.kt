package org.plugin.plugin.panels

import CompletedChallengeList
import RejectedChallengeList
import com.google.gson.Gson
import com.intellij.openapi.wm.impl.welcomeScreen.learnIde.coursesInProgress.mainBackgroundColor
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.util.maximumHeight
import com.intellij.ui.util.minimumHeight
import com.intellij.util.ui.JBUI
import org.plugin.plugin.Constants
import org.plugin.plugin.Utility
import org.plugin.plugin.WebSocketClient
import org.plugin.plugin.data.RestClient
import java.awt.*
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import javax.swing.*
import javax.swing.border.LineBorder


class AcceptedRejectedChallengesPanel : JPanel() {
    init {
        this.setBackground(mainBackgroundColor)
        performInitialization()
    }

    private fun performInitialization() {

        Utility.setAcceptedRejectedChallengesPanel(this)
        background = mainBackgroundColor
        setLayout(GridLayout(1, 2, 10, 0))

        // Completed Challenges Table
        val completedChallengesPanel = JPanel()
        completedChallengesPanel.background = mainBackgroundColor
        createCompletedChallengesTable(completedChallengesPanel)
        add(completedChallengesPanel)

        // Rejected Challenges Table
        val rejectedChallengesPanel = JPanel()
        rejectedChallengesPanel.background = mainBackgroundColor
        createRejectedChallengesTable(rejectedChallengesPanel)
        add(rejectedChallengesPanel)
    }

    fun update(){
        removeAll()
        revalidate()
        repaint()
        performInitialization()
    }

    private fun createCompletedChallengesTable(aInJPanel: JPanel) {

        aInJPanel.layout = BorderLayout(5,0)

        val lDescriptionLabel = JLabel("<html><div style='padding: 5px;'>Completed Challenges</div></html>")
        lDescriptionLabel.isOpaque = true
        lDescriptionLabel.font = Font("Arial", Font.BOLD, 16)
        lDescriptionLabel.horizontalAlignment = SwingConstants.LEFT
        lDescriptionLabel.verticalAlignment = SwingConstants.CENTER
        lDescriptionLabel.background = JBColor.BLACK
        lDescriptionLabel.foreground = JBColor.WHITE
        lDescriptionLabel.maximumHeight = 40

        aInJPanel.add(lDescriptionLabel, BorderLayout.PAGE_START)

        val lChallengesPanel = JPanel()
        lChallengesPanel.background = mainBackgroundColor
        lChallengesPanel.setLayout(BoxLayout(lChallengesPanel, BoxLayout.Y_AXIS))

        val lProjectName = Utility.lPreferences.get("projectName", "")
        if (lProjectName != "") {
            val queryParams = mapOf(
                "job" to lProjectName
            )

            try {

                val response = RestClient.getInstance().get(Utility.getBaseUrl() + Constants.GET_COMPLETED_CHALLENGES, queryParams)
                val lCompletedChallengeList = Gson().fromJson(response, CompletedChallengeList::class.java).completedChallenges

                for (index in lCompletedChallengeList.indices) {

                    val lChallenge = lCompletedChallengeList[index]
                    val lChallengePanel = JPanel(GridBagLayout())
                    lChallengePanel.background = Color.decode("#dbffe0")
                    lChallengePanel.border = LineBorder(Color.GRAY, 1)
                    val lUpperPanel = JPanel(FlowLayout(FlowLayout.LEFT))
                    lUpperPanel.background = Color.decode("#dbffe0")
                    lUpperPanel.preferredSize = Dimension(Int.MAX_VALUE, 70)

                    val lChallengeTitleLabelText = JLabel()
                    lChallengeTitleLabelText.alignmentX = JLabel.CENTER_ALIGNMENT
                    lChallengeTitleLabelText.foreground = Color.black

                    lUpperPanel.addComponentListener(object : ComponentAdapter() {
                        override fun componentResized(evt: ComponentEvent) {
                            lChallengeTitleLabelText.setText(
                                "<HTML><p style='PADDING:0;MARGIN:0;WIDTH: " + lUpperPanel.width  +
                                        "'>" + lChallenge.generalReason + "</p></HTML>"
                            )
                        }
                    })

                    val lChallengeTitleName = JLabel("<html><div style='padding: 3px;'>${lChallenge.name}</div></html>").apply {
                        setOpaque(true)
                        setBackground(Color.decode("#ffc107"))
                        setForeground(Color.decode("#212529"))
                        setFont(font.deriveFont(Font.BOLD, 12f))
                        setHorizontalAlignment(SwingConstants.CENTER)
                        setVerticalAlignment(SwingConstants.CENTER)
                    }

                    val lScoreString = if (lChallenge.score!! > 1) "points" else "point"

                    val lChallengeTitleScore = JLabel("<html><div style='padding: 3px;'>${lChallenge.score.toString() + "&nbsp;" + lScoreString}</div></html>").apply {
                        setOpaque(true)
                        setBackground(Color.decode("#28a745"))
                        setForeground(Color.WHITE)
                        setFont(font.deriveFont(Font.BOLD, 12f))
                        setHorizontalAlignment(SwingConstants.CENTER)
                        setVerticalAlignment(SwingConstants.CENTER)
                    }

                    val lGbc = GridBagConstraints()
                    lGbc.gridx = 0
                    lGbc.gridy = 0
                    lGbc.weightx = 1.0
                    lGbc.fill = GridBagConstraints.BOTH

                    lUpperPanel.add(lChallengeTitleLabelText)
                    lChallengePanel.add(lUpperPanel, lGbc)
                    lGbc.gridy = 1

                    val lLowerPanel = JPanel(FlowLayout(FlowLayout.LEFT, 0, 0))
                    lLowerPanel.background = Color.decode("#dbffe0")
                    lLowerPanel.minimumHeight  = 35

                    lChallengeTitleLabelText.addComponentListener(object : ComponentAdapter() {
                        override fun componentResized(evt: ComponentEvent) {
                            lChallengePanel.maximumSize = Dimension(Int.MAX_VALUE, lChallengeTitleLabelText.height + 60)
                            lChallengePanel.revalidate()
                        }
                    })

                    lLowerPanel.add(Box.createHorizontalStrut(5))
                    lLowerPanel.add(lChallengeTitleScore)
                    lLowerPanel.add(Box.createHorizontalStrut(5))
                    lLowerPanel.add(lChallengeTitleName)
                    lChallengePanel.add(lLowerPanel, lGbc)
                    lChallengesPanel.add(lChallengePanel)
                }

                val lScrollPane = JBScrollPane(lChallengesPanel)
                lScrollPane.background = mainBackgroundColor
                lScrollPane.verticalScrollBarPolicy = ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS
                lScrollPane.horizontalScrollBarPolicy = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
                lScrollPane.preferredSize = Dimension(200, 400)

                aInJPanel.add(lScrollPane, BorderLayout.CENTER)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun createRejectedChallengesTable(aInJPanel: JPanel) {

        aInJPanel.layout = BorderLayout(5,0)

        val lDescriptionLabel = JLabel("<html><div style='padding: 5px;'>Rejected Challenges</div></html>")
        lDescriptionLabel.isOpaque = true
        lDescriptionLabel.font = Font("Arial", Font.BOLD, 16)
        lDescriptionLabel.horizontalAlignment = SwingConstants.LEFT
        lDescriptionLabel.verticalAlignment = SwingConstants.CENTER
        lDescriptionLabel.background = JBColor.BLACK
        lDescriptionLabel.foreground = JBColor.WHITE
        lDescriptionLabel.maximumHeight = 40

        aInJPanel.add(lDescriptionLabel, BorderLayout.PAGE_START)

        val lChallengesPanel = JPanel()
        lChallengesPanel.setLayout(BoxLayout(lChallengesPanel, BoxLayout.Y_AXIS))
        lChallengesPanel.background = Color.decode("#fff4e8")

        val lProjectName = Utility.lPreferences.get("projectName", "")
        if (lProjectName != "") {
            val queryParams = mapOf(
                "job" to lProjectName
            )

            try {

                val response = RestClient.getInstance().get(Utility.getBaseUrl() + Constants.GET_REJECTED_CHALLENGES, queryParams)
                val lRejectedChallengeList = Gson().fromJson(response, RejectedChallengeList::class.java).rejectedChallenges

                for (index in lRejectedChallengeList.indices) {
                    val lChallenge = lRejectedChallengeList[index]
                    val lChallengePanel = JPanel(GridBagLayout())
                    lChallengePanel.maximumSize = Dimension(Int.MAX_VALUE, 120)
                    lChallengePanel.background = Color.decode("#fff4e8")
                    lChallengePanel.border = LineBorder(Color.GRAY, 1)


                    val lUpperPanel = JPanel(FlowLayout(FlowLayout.LEFT))
                    lUpperPanel.preferredSize = Dimension(Int.MAX_VALUE, 70)
                    lUpperPanel.background = Color.decode("#fff4e8")
                    val lChallengeTitleLabelText = JLabel()
                    lChallengeTitleLabelText.foreground = Color.black
                    lChallengeTitleLabelText.alignmentX = JLabel.CENTER_ALIGNMENT


                    lUpperPanel.addComponentListener(object : ComponentAdapter() {
                        override fun componentResized(evt: ComponentEvent) {
                            lChallengeTitleLabelText.setText(
                                "<HTML><p style='PADDING:0;MARGIN:0;WIDTH: " + lUpperPanel.width  +
                                        "'>" + lChallenge.first.generalReason + "</p></HTML>"
                            )
                        }
                    })

                    val lChallengeTitleName = JLabel("<html><div style='padding: 3px;'>${lChallenge.first.name}</div></html>").apply {
                        isOpaque = true
                        background = Color.decode("#ffc107")
                        foreground = Color.decode("#212529")
                        font = font.deriveFont(Font.BOLD, 12f)
                        horizontalAlignment = SwingConstants.CENTER
                        verticalAlignment = SwingConstants.CENTER
                    }


                    val lUndoButton = JButton("Undo")
                    lUndoButton.isOpaque = true
                    lUndoButton.preferredSize = Dimension(80, 30)
                    lUndoButton.foreground = JBColor.RED
                    lUndoButton.font = Font("Arial", Font.BOLD, 13)

                    lUndoButton.addActionListener {
                        Utility.restoreChallenge(
                            lRejectedChallengeList[index].first.generalReason?.replace(Regex("<[^>]++>"), "")
                        ) { success, errorMessage ->
                            if (success) {
                                Utility.showMessageDialog("Restore successful!")
                                Utility.challengesPanel?.removeAll()
                                Utility.challengesPanel?.initializePanel()
                            } else {
                                Utility.showErrorDialog("Restore failed: $errorMessage")
                            }
                        }
                    }

                    val lGbc = GridBagConstraints()
                    lGbc.insets = JBUI.emptyInsets()
                    lGbc.gridx = 0
                    lGbc.gridy = 0
                    lGbc.weighty = 1.0
                    lGbc.weightx = 1.0
                    lGbc.fill = GridBagConstraints.BOTH

                    lUpperPanel.add(lChallengeTitleLabelText)
                    lChallengePanel.add(lUpperPanel, lGbc)
                    lGbc.gridy = 1

                    val lLowerPanel = JPanel(FlowLayout(FlowLayout.LEFT, 10, 0))
                    lLowerPanel.background = Color.decode("#fff4e8")

                    lLowerPanel.minimumHeight  = 35
                    lChallengeTitleLabelText.addComponentListener(object : ComponentAdapter() {
                        override fun componentResized(evt: ComponentEvent) {
                            lChallengePanel.maximumSize = Dimension(Int.MAX_VALUE, lChallengeTitleLabelText.height + 60)
                            lChallengePanel.revalidate()
                        }
                    })

                    if (lChallenge.first.name == "Class Coverage") {
                        lLowerPanel.add(lUndoButton)
                    }
                    lLowerPanel.add(lChallengeTitleName)
                    lChallengePanel.add(lLowerPanel, lGbc)
                    lChallengesPanel.add(lChallengePanel)
                }

                lChallengesPanel.background = mainBackgroundColor

                val scrollPane = JBScrollPane(lChallengesPanel)
                scrollPane.background = mainBackgroundColor
                scrollPane.verticalScrollBarPolicy = ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS
                scrollPane.horizontalScrollBarPolicy = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
                scrollPane.preferredSize = Dimension(200, 400)

                aInJPanel.add(scrollPane, BorderLayout.CENTER)

                //WebSocketClient().showNotification("Congratulations! You solved the challenge:\n${lRejectedChallengeList.last().first.generalReason?.replace("<b>", "")?.replace("</b>", "")}")

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

