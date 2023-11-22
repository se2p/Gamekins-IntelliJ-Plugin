package org.plugin.plugin.panels

import CompletedChallengeList
import RejectedChallengeList
import com.google.gson.Gson
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBScrollPane
import com.intellij.util.ui.JBUI
import org.plugin.plugin.Constants
import org.plugin.plugin.Utility
import org.plugin.plugin.data.RestClient
import java.awt.*
import javax.swing.*
import javax.swing.border.LineBorder


class AcceptedRejectedChallengesPanel : JPanel() {
    init {
        performInitialization()
    }

    private fun performInitialization() {

        Utility.setAcceptedRejectedChallengesPanel(this)
        setLayout(GridLayout(1, 2, 10, 10))

        // Completed Challenges Table
        val completedChallengesPanel = JPanel()

        createCompletedChallengesTable(completedChallengesPanel)
        add(completedChallengesPanel)

        // Rejected Challenges Table
        val rejectedChallengesPanel = JPanel()
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


        aInJPanel.layout = BorderLayout(10,10)

        val lDescriptionLabel = JLabel("Rejected Challenges")
        lDescriptionLabel.font = Font("SansSerif", Font.BOLD, 18)
        lDescriptionLabel.horizontalAlignment = SwingConstants.CENTER
        lDescriptionLabel.verticalAlignment = SwingConstants.CENTER

        aInJPanel.add(lDescriptionLabel, BorderLayout.PAGE_START)

        val challengesPanel = JPanel()
        challengesPanel.setLayout(BoxLayout(challengesPanel, BoxLayout.Y_AXIS))
        challengesPanel.border = LineBorder(JBColor.GRAY, 2)

        val lProjectName = Utility.lPreferences.get("projectName", "")
        if (lProjectName != "") {
            val queryParams = mapOf(
                "job" to lProjectName
            )

            try {

                val response = RestClient.getInstance().get(Constants.API_BASE_URL + Constants.GET_COMPLETED_CHALLENGES, queryParams)
                val lRejectedChallengeList = Gson().fromJson(response, CompletedChallengeList::class.java).completedChallenges

                for (index in lRejectedChallengeList.indices) {

                    val challengePanel = JPanel(GridBagLayout())
                    challengePanel.border = LineBorder(JBColor.GRAY, 1)
                    challengePanel.maximumSize = Dimension(Int.MAX_VALUE, 90)

                    val padding = 4
                    val lHtmlContent =
                        (("<HTML><div style='padding: " + padding + "px; WIDTH: " + (challengePanel.width - 2 * padding)) + "px;'>" +
                                lRejectedChallengeList[index].generalReason) +
                                "</div></HTML>"
                    val lChallengeTitleLabelText = JEditorPane("text/html", lHtmlContent)
                    lChallengeTitleLabelText.isEditable = false


                    val challengeTitleName = JLabel(lRejectedChallengeList[index].name)
                    challengeTitleName.setFont(Font("Arial", Font.BOLD, 14))
                    challengeTitleName.setForeground(JBColor.YELLOW)
                    challengeTitleName.horizontalAlignment = SwingConstants.CENTER
                    challengeTitleName.verticalAlignment = SwingConstants.CENTER
                    challengeTitleName.preferredSize = Dimension(150, 30)


                    val lUndoButton = JButton("Undo")
                    lUndoButton.setForeground(JBColor.white)

                    lUndoButton.preferredSize = Dimension(50, 26)

                    lUndoButton.addActionListener {
                        Utility.restoreChallenge(
                            lRejectedChallengeList[index].generalReason?.replace(Regex("<[^>]++>"), "")
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

                    val gbc = GridBagConstraints()
                    gbc.gridx = 0
                    gbc.gridy = 0
                    gbc.weightx = 1.0
                    gbc.weighty = 1.0
                    gbc.gridwidth = GridBagConstraints.REMAINDER
                    gbc.insets = JBUI.insets(1)
                    gbc.fill = GridBagConstraints.BOTH
                    challengePanel.add(lChallengeTitleLabelText, gbc)
                    gbc.gridy = 1

                    val lInnerPanel = JPanel(FlowLayout(FlowLayout.LEFT, 10, 0))
                    lInnerPanel.minimumSize = Dimension(150, 30)
                    lInnerPanel.add(lUndoButton)
                    lInnerPanel.add(challengeTitleName)

                    gbc.fill = GridBagConstraints.HORIZONTAL
                    challengePanel.add(lInnerPanel, gbc)
                    challengesPanel.add(challengePanel, BorderLayout.NORTH)
                }

                val scrollPane = JBScrollPane(challengesPanel)
                scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
                scrollPane.horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER

                aInJPanel.add(scrollPane)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun createRejectedChallengesTable(aInJPanel: JPanel) {

        aInJPanel.layout = BorderLayout(10,10)

        val lDescriptionLabel = JLabel("Rejected Challenges")
        lDescriptionLabel.font = Font("SansSerif", Font.BOLD, 18)
        lDescriptionLabel.horizontalAlignment = SwingConstants.CENTER
        lDescriptionLabel.verticalAlignment = SwingConstants.CENTER

        aInJPanel.add(lDescriptionLabel, BorderLayout.PAGE_START)

        val challengesPanel = JPanel()
        challengesPanel.setLayout(BoxLayout(challengesPanel, BoxLayout.Y_AXIS))
        challengesPanel.border = LineBorder(JBColor.GRAY, 2)

        val lProjectName = Utility.lPreferences.get("projectName", "")
        if (lProjectName != "") {
            val queryParams = mapOf(
                "job" to lProjectName
            )

            try {

                val response = RestClient.getInstance().get(Constants.API_BASE_URL + Constants.GET_REJECTED_CHALLENGES, queryParams)
                val lRejectedChallengeList = Gson().fromJson(response, RejectedChallengeList::class.java).rejectedChallenges

                for (index in lRejectedChallengeList.indices) {

                    val challengePanel = JPanel(GridBagLayout())
                    challengePanel.border = LineBorder(JBColor.GRAY, 1)
                    challengePanel.maximumSize = Dimension(Int.MAX_VALUE, 90)

                    val padding = 4
                    val lHtmlContent =
                        (("<HTML><div style='padding: " + padding + "px; WIDTH: " + (challengePanel.width - 2 * padding)) + "px;'>" +
                                lRejectedChallengeList[index].first.generalReason) +
                                "</div></HTML>"
                    val lChallengeTitleLabelText = JEditorPane("text/html", lHtmlContent)
                    lChallengeTitleLabelText.isEditable = false


                    val challengeTitleName = JLabel(lRejectedChallengeList[index].first.name)
                    challengeTitleName.setFont(Font("Arial", Font.BOLD, 14))
                    challengeTitleName.setForeground(JBColor.YELLOW)
                    challengeTitleName.horizontalAlignment = SwingConstants.CENTER
                    challengeTitleName.verticalAlignment = SwingConstants.CENTER
                    challengeTitleName.preferredSize = Dimension(150, 30)


                    val lUndoButton = JButton("Undo")
                    lUndoButton.setForeground(JBColor.white)

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

                    val gbc = GridBagConstraints()
                    gbc.gridx = 0
                    gbc.gridy = 0
                    gbc.weightx = 1.0
                    gbc.weighty = 1.0
                    gbc.gridwidth = GridBagConstraints.REMAINDER
                    gbc.insets = JBUI.insets(1)
                    gbc.fill = GridBagConstraints.BOTH
                    challengePanel.add(lChallengeTitleLabelText, gbc)
                    gbc.gridy = 1

                    val lInnerPanel = JPanel(FlowLayout(FlowLayout.LEFT, 10, 0))
                    lInnerPanel.minimumSize = Dimension(150, 30)
                    lInnerPanel.add(lUndoButton)
                    lInnerPanel.add(challengeTitleName)

                    gbc.fill = GridBagConstraints.HORIZONTAL
                    challengePanel.add(lInnerPanel, gbc)
                    challengesPanel.add(challengePanel, BorderLayout.NORTH)
                }

                val scrollPane = JBScrollPane(challengesPanel)
                scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
                scrollPane.horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER

                aInJPanel.add(scrollPane)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

