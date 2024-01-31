package org.gamekins.intellij.panels

import com.google.gson.Gson
import com.intellij.openapi.wm.impl.welcomeScreen.learnIde.coursesInProgress.mainBackgroundColor
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.util.maximumHeight
import com.intellij.ui.util.minimumHeight
import com.intellij.util.ui.JBUI
import org.gamekins.intellij.Constants
import org.gamekins.intellij.Utility
import org.gamekins.intellij.data.CompletedChallengeList
import org.gamekins.intellij.data.RejectedChallengeList
import org.gamekins.intellij.data.RestClient
import java.awt.*
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import javax.swing.*
import javax.swing.border.LineBorder


class AcceptedRejectedChallengesPanel : JPanel() {
    init {
        this.background = mainBackgroundColor
        performInitialization()
    }

    private fun performInitialization() {

        Utility.setAcceptedRejectedChallengesPanel(this)
        background = mainBackgroundColor
        layout = GridLayout(1, 2, 10, 0)

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

    private fun createCompletedChallengesTable(jPanel: JPanel) {

        jPanel.layout = BorderLayout(5,0)

        val descriptionLabel = JLabel("<html><div style='padding: 5px;'>Completed Challenges</div></html>")
        descriptionLabel.isOpaque = true
        descriptionLabel.font = Font("Arial", Font.BOLD, 16)
        descriptionLabel.horizontalAlignment = SwingConstants.LEFT
        descriptionLabel.verticalAlignment = SwingConstants.CENTER
        descriptionLabel.background = JBColor.BLACK
        descriptionLabel.foreground = JBColor.WHITE
        descriptionLabel.maximumHeight = 40

        jPanel.add(descriptionLabel, BorderLayout.PAGE_START)

        val challengesPanel = JPanel()
        challengesPanel.background = mainBackgroundColor
        challengesPanel.layout = BoxLayout(challengesPanel, BoxLayout.Y_AXIS)

        val projectName = Utility.preferences["projectName", ""]
        if (projectName != "") {
            val queryParams = mapOf(
                "job" to projectName
            )

            try {

                val response = RestClient.getInstance().get(Utility.getBaseUrl() + Constants.GET_COMPLETED_CHALLENGES, queryParams)
                val completedChallengeList = Gson().fromJson(response, CompletedChallengeList::class.java).completedChallenges

                for (index in completedChallengeList.indices) {

                    val challenge = completedChallengeList[index]
                    val challengePanel = JPanel(GridBagLayout())
                    challengePanel.background = Color.decode("#dbffe0")
                    challengePanel.border = LineBorder(JBColor.GRAY, 1)
                    val upperPanel = JPanel(FlowLayout(FlowLayout.LEFT))
                    upperPanel.background = Color.decode("#dbffe0")
                    upperPanel.preferredSize = Dimension(Int.MAX_VALUE, 70)

                    val challengeTitleLabelText = JLabel()
                    challengeTitleLabelText.alignmentX = JLabel.CENTER_ALIGNMENT
                    challengeTitleLabelText.foreground = JBColor.BLACK

                    upperPanel.addComponentListener(object : ComponentAdapter() {
                        override fun componentResized(evt: ComponentEvent) {
                            challengeTitleLabelText.text = "<HTML><p style='PADDING:0;MARGIN:0;WIDTH: " + upperPanel.width  +
                                    "'>" + challenge.generalReason + "</p></HTML>"
                        }
                    })

                    val challengeTitleName = JLabel("<html><div style='padding: 3px;'>${challenge.name}</div></html>").apply {
                        isOpaque = true
                        background = Color.decode("#ffc107")
                        foreground = Color.decode("#212529")
                        font = font.deriveFont(Font.BOLD, 12f)
                        horizontalAlignment = SwingConstants.CENTER
                        verticalAlignment = SwingConstants.CENTER
                    }

                    val scoreString = if (challenge.score!! > 1) "points" else "point"

                    val challengeTitleScore = JLabel("<html><div style='padding: 3px;'>${challenge.score.toString() + "&nbsp;" + scoreString}</div></html>").apply {
                        isOpaque = true
                        background = Color.decode("#28a745")
                        foreground = JBColor.WHITE
                        font = font.deriveFont(Font.BOLD, 12f)
                        horizontalAlignment = SwingConstants.CENTER
                        verticalAlignment = SwingConstants.CENTER
                    }

                    val gbc = GridBagConstraints()
                    gbc.gridx = 0
                    gbc.gridy = 0
                    gbc.weightx = 1.0
                    gbc.fill = GridBagConstraints.BOTH

                    upperPanel.add(challengeTitleLabelText)
                    challengePanel.add(upperPanel, gbc)
                    gbc.gridy = 1

                    val lowerPanel = JPanel(FlowLayout(FlowLayout.LEFT, 0, 0))
                    lowerPanel.background = Color.decode("#dbffe0")
                    lowerPanel.minimumHeight  = 35

                    challengeTitleLabelText.addComponentListener(object : ComponentAdapter() {
                        override fun componentResized(evt: ComponentEvent) {
                            challengePanel.maximumSize = Dimension(Int.MAX_VALUE, challengeTitleLabelText.height + 60)
                            challengePanel.revalidate()
                        }
                    })

                    lowerPanel.add(Box.createHorizontalStrut(5))
                    lowerPanel.add(challengeTitleScore)
                    lowerPanel.add(Box.createHorizontalStrut(5))
                    lowerPanel.add(challengeTitleName)
                    challengePanel.add(lowerPanel, gbc)
                    challengesPanel.add(challengePanel)
                }

                val scrollPane = JBScrollPane(challengesPanel)
                scrollPane.background = mainBackgroundColor
                scrollPane.verticalScrollBarPolicy = ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS
                scrollPane.horizontalScrollBarPolicy = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
                scrollPane.preferredSize = Dimension(200, 400)

                jPanel.add(scrollPane, BorderLayout.CENTER)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun createRejectedChallengesTable(jPanel: JPanel) {

        jPanel.layout = BorderLayout(5,0)

        val descriptionLabel = JLabel("<html><div style='padding: 5px;'>Rejected Challenges</div></html>")
        descriptionLabel.isOpaque = true
        descriptionLabel.font = Font("Arial", Font.BOLD, 16)
        descriptionLabel.horizontalAlignment = SwingConstants.LEFT
        descriptionLabel.verticalAlignment = SwingConstants.CENTER
        descriptionLabel.background = JBColor.BLACK
        descriptionLabel.foreground = JBColor.WHITE
        descriptionLabel.maximumHeight = 40

        jPanel.add(descriptionLabel, BorderLayout.PAGE_START)

        val challengesPanel = JPanel()
        challengesPanel.layout = BoxLayout(challengesPanel, BoxLayout.Y_AXIS)
        challengesPanel.background = mainBackgroundColor

        val projectName = Utility.preferences.get("projectName", "")
        if (projectName != "") {
            val queryParams = mapOf(
                "job" to projectName
            )

            try {

                val response = RestClient.getInstance().get(Utility.getBaseUrl() + Constants.GET_REJECTED_CHALLENGES, queryParams)
                val rejectedChallengeList = Gson().fromJson(response, RejectedChallengeList::class.java).rejectedChallenges

                rejectedChallengeList.indices.forEach { index ->
                    val challenge = rejectedChallengeList[index]
                    val challengePanel = JPanel(GridBagLayout())
                    challengePanel.maximumSize = Dimension(Int.MAX_VALUE, 120)
                    challengePanel.background = Color.decode("#fff4e8")
                    challengePanel.border = LineBorder(JBColor.GRAY, 1)


                    val upperPanel = JPanel(FlowLayout(FlowLayout.LEFT))
                    upperPanel.preferredSize = Dimension(Int.MAX_VALUE, 70)
                    upperPanel.background = Color.decode("#fff4e8")
                    val challengeTitleLabelText = JLabel()
                    challengeTitleLabelText.foreground = JBColor.BLACK
                    challengeTitleLabelText.alignmentX = JLabel.CENTER_ALIGNMENT


                    upperPanel.addComponentListener(object : ComponentAdapter() {
                        override fun componentResized(evt: ComponentEvent) {
                            challengeTitleLabelText.setText(
                                "<HTML><p style='PADDING:0;MARGIN:0;WIDTH: " + upperPanel.width  +
                                        "'>" + challenge.first.generalReason + "</p></HTML>"
                            )
                        }
                    })

                    val challengeTitleName = JLabel("<html><div style='padding: 3px;'>${challenge.first.name}</div></html>").apply {
                        isOpaque = true
                        background = Color.decode("#ffc107")
                        foreground = Color.decode("#212529")
                        font = font.deriveFont(Font.BOLD, 12f)
                        horizontalAlignment = SwingConstants.CENTER
                        verticalAlignment = SwingConstants.CENTER
                    }


                    val undoButton = JButton("Undo")
                    undoButton.isOpaque = true
                    undoButton.preferredSize = Dimension(80, 30)
                    undoButton.foreground = JBColor.RED
                    undoButton.font = Font("Arial", Font.BOLD, 13)

                    undoButton.addActionListener {
                        Utility.restoreChallenge(
                            rejectedChallengeList[index].first.generalReason?.replace(Regex("<[^>]++>"), "")
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

                    val gbc = GridBagConstraints()
                    gbc.insets = JBUI.emptyInsets()
                    gbc.gridx = 0
                    gbc.gridy = 0
                    gbc.weighty = 1.0
                    gbc.weightx = 1.0
                    gbc.fill = GridBagConstraints.BOTH

                    upperPanel.add(challengeTitleLabelText)
                    challengePanel.add(upperPanel, gbc)
                    gbc.gridy = 1

                    val lowerPanel = JPanel(FlowLayout(FlowLayout.LEFT, 10, 0))
                    lowerPanel.background = Color.decode("#fff4e8")

                    lowerPanel.minimumHeight  = 35
                    challengeTitleLabelText.addComponentListener(object : ComponentAdapter() {
                        override fun componentResized(evt: ComponentEvent) {
                            challengePanel.maximumSize = Dimension(Int.MAX_VALUE, challengeTitleLabelText.height + 60)
                            challengePanel.revalidate()
                        }
                    })

                    if (challenge.first.name == "Class Coverage") {
                        lowerPanel.add(undoButton)
                    }
                    lowerPanel.add(challengeTitleName)
                    challengePanel.add(lowerPanel, gbc)
                    challengesPanel.add(challengePanel)
                }

                challengesPanel.background = mainBackgroundColor

                val scrollPane = JBScrollPane(challengesPanel)
                scrollPane.background = mainBackgroundColor
                scrollPane.verticalScrollBarPolicy = ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS
                scrollPane.horizontalScrollBarPolicy = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
                scrollPane.preferredSize = Dimension(200, 400)

                jPanel.add(scrollPane, BorderLayout.CENTER)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

