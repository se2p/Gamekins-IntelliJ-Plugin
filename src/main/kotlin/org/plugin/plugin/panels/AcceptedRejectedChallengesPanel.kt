package org.plugin.plugin.panels

import CompletedChallengeList
import RejectedChallengeList
import com.google.gson.Gson
import com.intellij.openapi.wm.impl.welcomeScreen.learnIde.coursesInProgress.mainBackgroundColor
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.util.minimumHeight
import com.intellij.util.ui.JBUI
import org.plugin.plugin.Constants
import org.plugin.plugin.Utility
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

        aInJPanel.layout = BorderLayout(5,5)

        val lDescriptionLabel = JLabel("Completed Challenges")
        lDescriptionLabel.font = Font("SansSerif", Font.BOLD, 18)
        lDescriptionLabel.horizontalAlignment = SwingConstants.CENTER
        lDescriptionLabel.verticalAlignment = SwingConstants.CENTER

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
                    lChallengePanel.background = mainBackgroundColor
                    lChallengePanel.border = LineBorder(JBColor.GRAY, 1)
                    val lUpperPanel = JPanel(FlowLayout(FlowLayout.LEFT))
                    lUpperPanel.background = mainBackgroundColor
                    lUpperPanel.preferredSize = Dimension(Int.MAX_VALUE, 70)

                    val lChallengeTitleLabelText = JLabel()
                    lChallengeTitleLabelText.alignmentX = JLabel.CENTER_ALIGNMENT

                    lUpperPanel.addComponentListener(object : ComponentAdapter() {
                        override fun componentResized(evt: ComponentEvent) {
                            lChallengeTitleLabelText.setText(
                                "<HTML><p style='PADDING:0;MARGIN:0;WIDTH: " + lUpperPanel.width  +
                                        "'>" + lChallenge.generalReason + "</p></HTML>"
                            )
                        }
                    })

                    val lChallengeTitleName = JLabel(lChallenge.name)
                    lChallengeTitleName.setFont(Font("Arial", Font.BOLD, 14))
                    lChallengeTitleName.setForeground(JBColor.BLUE)
                    lChallengeTitleName.horizontalAlignment = SwingConstants.CENTER
                    lChallengeTitleName.verticalAlignment = SwingConstants.CENTER
                    lChallengeTitleName.preferredSize = Dimension(130, 30)

                    val lScoreString = if (lChallenge.score!! > 1) "points" else "point"

                    val lChallengeTitleScore = JLabel("${lChallenge.score} $lScoreString").apply {
                        font = Font("Arial", Font.BOLD, 14)
                        foreground = JBColor.GREEN
                        preferredSize = Dimension(60, 30)
                        horizontalAlignment = SwingConstants.CENTER
                        verticalAlignment = SwingConstants.CENTER
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
                    lLowerPanel.background = mainBackgroundColor
                    lLowerPanel.minimumHeight  = 35

                    lChallengeTitleLabelText.addComponentListener(object : ComponentAdapter() {
                        override fun componentResized(evt: ComponentEvent) {
                            lChallengePanel.maximumSize = Dimension(Int.MAX_VALUE, lChallengeTitleLabelText.height + 60)
                            lChallengePanel.revalidate()
                        }
                    })


                    lLowerPanel.add(lChallengeTitleName)
                    lLowerPanel.add(lChallengeTitleScore)
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

        aInJPanel.layout = BorderLayout(5,5)

        val lDescriptionLabel = JLabel("Rejected Challenges")
        lDescriptionLabel.font = Font("SansSerif", Font.BOLD, 18)
        lDescriptionLabel.horizontalAlignment = SwingConstants.CENTER
        lDescriptionLabel.verticalAlignment = SwingConstants.CENTER

        aInJPanel.add(lDescriptionLabel, BorderLayout.PAGE_START)

        val lChallengesPanel = JPanel()
        lChallengesPanel.setLayout(BoxLayout(lChallengesPanel, BoxLayout.Y_AXIS))
        lChallengesPanel.background = mainBackgroundColor
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
                    lChallengePanel.background = mainBackgroundColor
                    lChallengePanel.border = LineBorder(JBColor.GRAY, 1)


                    val lUpperPanel = JPanel(FlowLayout(FlowLayout.LEFT))
                    lUpperPanel.preferredSize = Dimension(Int.MAX_VALUE, 70)
                    lUpperPanel.background = mainBackgroundColor
                    val lChallengeTitleLabelText = JLabel()
                    lChallengeTitleLabelText.alignmentX = JLabel.CENTER_ALIGNMENT

                    lUpperPanel.addComponentListener(object : ComponentAdapter() {
                        override fun componentResized(evt: ComponentEvent) {
                            lChallengeTitleLabelText.setText(
                                "<HTML><p style='PADDING:0;MARGIN:0;WIDTH: " + lUpperPanel.width  +
                                        "'>" + lChallenge.first.generalReason + "</p></HTML>"
                            )
                        }
                    })

                    val challengeTitleName = JLabel(lChallenge.first.name)
                    challengeTitleName.setFont(Font("Arial", Font.BOLD, 14))
                    challengeTitleName.setForeground(JBColor.BLUE)
                    challengeTitleName.horizontalAlignment = SwingConstants.CENTER
                    challengeTitleName.verticalAlignment = SwingConstants.CENTER
                    challengeTitleName.preferredSize = Dimension(150, 30)


                    val lUndoButton = JButton("Undo")
                    lUndoButton.setForeground(mainBackgroundColor)
                    lUndoButton.background = mainBackgroundColor
                    lUndoButton.preferredSize = Dimension(50, 26)

                    lUndoButton.addActionListener {
                        Utility.restoreChallenge(
                            lRejectedChallengeList[index].first.generalReason?.replace(Regex("<[^>]++>"), "")
                        ) { success, errorMessage ->
                            if (success) {
                                Utility.showMessageDialog("Restore successful!")
                                Utility.lCurrentQuestsChallengesPanel?.update()
                                update()
                            } else {
                                Utility.showErrorDialog("Restore failed: $errorMessage")
                            }
                        }
                    }

                    lUndoButton.isEnabled = lChallenge.first.name == "Class Coverage"

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
                    lLowerPanel.background = mainBackgroundColor

                    lLowerPanel.minimumHeight  = 35
                    lChallengeTitleLabelText.addComponentListener(object : ComponentAdapter() {
                        override fun componentResized(evt: ComponentEvent) {
                            lChallengePanel.maximumSize = Dimension(Int.MAX_VALUE, lChallengeTitleLabelText.height + 60)
                            lChallengePanel.revalidate()
                        }
                    })

                    lLowerPanel.add(lUndoButton)
                    lLowerPanel.add(challengeTitleName)
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

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

