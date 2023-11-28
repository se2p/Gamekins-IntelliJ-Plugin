package org.plugin.plugin.panels


import StoredChallengeList
import com.google.gson.Gson
import com.intellij.openapi.editor.ScrollType
import com.intellij.openapi.editor.markup.HighlighterLayer
import com.intellij.openapi.editor.markup.HighlighterTargetArea
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBScrollPane
import com.intellij.util.ui.JBUI
import org.jsoup.Jsoup
import org.plugin.plugin.Constants
import org.plugin.plugin.Utility
import org.plugin.plugin.data.QuestTask
import org.plugin.plugin.data.QuestsListTasks
import org.plugin.plugin.data.RestClient
import java.awt.BorderLayout
import java.awt.Button
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.Font
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.GridLayout
import java.awt.event.ActionEvent
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.util.prefs.Preferences
import javax.swing.*
import javax.swing.border.LineBorder


class CurrentQuestsChallengesPanel : JPanel() {

    val lPreferences = Preferences.userRoot().node("org.plugin.plugin.panels")

    init {
        performInitialization()
    }

    private fun performInitialization() {

        this.setLayout(GridBagLayout())
        val gbc = GridBagConstraints()
        gbc.gridx = 0
        gbc.weightx = 1.0
        gbc.fill = GridBagConstraints.HORIZONTAL
        gbc.insets = JBUI.insets(5)

        val row1 = JPanel()
        val row2 = JPanel()
        val row3 = JPanel()
        val row4 = JPanel()

        row1.preferredSize = Dimension(Int.MAX_VALUE, 40)
        row2.preferredSize = Dimension(400, 280)
        row3.preferredSize = Dimension(400, 280)
        row4.preferredSize = Dimension(400, 40)

        row3.layout = BoxLayout(row3, BoxLayout.Y_AXIS)

        val lTitle = JLabel("<html><h1>Current Quests & Challenges</h1></html>");
        row1.add(lTitle)

        gbc.gridy = 0;
        this.add(row1, gbc);

        gbc.gridy = 1;
        this.add(row2, gbc);

        gbc.gridy = 2;
        this.add(row3, gbc);

        gbc.gridy = 3;
        this.add(row4, gbc);

        this.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0))
        Utility.setCurrentQuestsChallengesPanel(this)
        createQuests(row2)
        createChallenges(row3)
        createStoredButton(row4)
    }

    fun update(){
        removeAll()
        revalidate()
        repaint()
        performInitialization()
    }

    private fun createQuests(mainPanel: JPanel) {
        val lProjectName = lPreferences.get("projectName", "") ?: return

        try {
            val questsTasksList = fetchQuestsTasks(lProjectName)

            val lQuestsPanel = createQuestsPanel(questsTasksList)
            configureMainPanel(mainPanel, lQuestsPanel)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun fetchQuestsTasks(projectName: String): List<QuestTask> {
        val queryParams = mapOf("job" to projectName)
        val response = RestClient.getInstance().get(Constants.API_BASE_URL + Constants.GET_CURRENT_QUESTS_TASKS, queryParams)
        return Gson().fromJson(response, QuestsListTasks::class.java).currentQuestTasks
    }

    private fun createQuestsPanel(questsTasksList: List<QuestTask>): JPanel {
        val lQuestsPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            border = LineBorder(JBColor.GRAY, 1)
        }

        questsTasksList.forEachIndexed { index, task ->
            val lQuestPanel = createSingleQuestPanel(task, index)
            lQuestsPanel.add(lQuestPanel)
        }
        return lQuestsPanel
    }

    private fun createSingleQuestPanel(task: QuestTask, index: Int): JPanel {
        val lQuestPanel = JPanel(GridLayout(2, 1, 0, 5)).apply {
            border = BorderFactory.createCompoundBorder(
                LineBorder(JBColor.GRAY, 0),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
            )
            maximumSize = Dimension(Int.MAX_VALUE, 70)
        }

        val questLabel = JLabel(task.title)
        val headerPanel = JPanel(FlowLayout(FlowLayout.LEFT, 5, 0))

        val spacerLabel = JLabel("")
        spacerLabel.preferredSize = Dimension(0, 0)

        val lScoreString = if (task.score > 1) "points" else "point"
        val lChallengeTitleScore = JLabel("${task.score} $lScoreString").apply {
            font = Font("Arial", Font.BOLD, 13)
            foreground = JBColor.GREEN
            horizontalAlignment = SwingConstants.CENTER
            verticalAlignment = SwingConstants.CENTER
        }

        val lIndex = index + 1
        val progressBarLabel = JLabel(task.completedPercentage.toString()).apply {
            foreground = JBColor.BLUE
            text = "$lIndex. "
        }
        headerPanel.add(progressBarLabel)
        val progressBar = JProgressBar(0, 100).apply {
            value = task.completedPercentage
            isStringPainted = true
        }

        headerPanel.add(questLabel)
        headerPanel.add(spacerLabel)
        headerPanel.add(lChallengeTitleScore)
        lQuestPanel.add(headerPanel)
        lQuestPanel.add(progressBar)

        return lQuestPanel
    }

    private fun configureMainPanel(mainPanel: JPanel, questsPanel: JPanel) {
        mainPanel.apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            preferredSize = Dimension(300, 250)
            add(JBScrollPane(questsPanel).apply {
                verticalScrollBarPolicy = ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS
            })
        }
    }

    private fun createChallenges(aInMainPanel: JPanel) {

        val challengesPanel = JPanel()
        val scrollPane = JBScrollPane(challengesPanel)
        challengesPanel.setLayout(BoxLayout(challengesPanel, BoxLayout.Y_AXIS))
        challengesPanel.border = LineBorder(JBColor.GRAY, 1)

        try {

            val lChallengeList = Utility.getCurrentChallenges()

            if (lChallengeList != null) {
                for (index in lChallengeList.indices) {

                    val challenge = lChallengeList.getOrNull(index) ?: return
                    val challengePanel = JPanel()
                    challengePanel.setLayout(BorderLayout())
                    challengePanel.border = LineBorder(JBColor.GRAY, 1)

                    val lLeftPanel = JPanel(FlowLayout(FlowLayout.LEFT))
                    val lChallengeHeader = JPanel(GridBagLayout())
                    val lChallengeTitleLabel = JLabel(
                        "<HTML><div WIDTH=" + lLeftPanel.width + ">" +
                                lChallengeList[index].generalReason + "</div></HTML>"
                    )
                    lChallengeTitleLabel.alignmentX = JLabel.CENTER_ALIGNMENT

                    lLeftPanel.addComponentListener(object : ComponentAdapter() {
                        override fun componentResized(evt: ComponentEvent) {
                            lChallengeTitleLabel.setText(
                                "<HTML><div WIDTH=" + lLeftPanel.width + ">" +
                                        lChallengeList[index].generalReason + "</div></HTML>"
                            )
                        }
                    })

                    val gbc = GridBagConstraints()
                    gbc.gridx = 0
                    gbc.gridy = 0
                    gbc.weightx = 0.8

                    gbc.fill = GridBagConstraints.BOTH

                    lLeftPanel.add(lChallengeTitleLabel)
                    lChallengeHeader.add(lLeftPanel, gbc)

                    val scoreString = if (challenge.score!! > 1) "points" else "point"
                    val lChallengeTitleScore = JLabel(challenge.score.toString() + " " + scoreString)
                    lChallengeTitleScore.setFont(Font("Arial", Font.BOLD, 13))
                    lChallengeTitleScore.setForeground(JBColor.GREEN)
                    lChallengeTitleScore.horizontalAlignment = SwingConstants.CENTER
                    lChallengeTitleScore.verticalAlignment = SwingConstants.CENTER

                    val lChallengeTitleName = JLabel(challenge.name)
                    val lExpandButton = JButton("Expand")
                    lExpandButton.toolTipText = Constants.CHALLENGE_PANEL_DESCRIPTION

                    val rightPanel = JPanel(FlowLayout(FlowLayout.RIGHT))

                    if (challenge.score > 0)
                        rightPanel.add(lChallengeTitleScore)

                    lChallengeTitleName.setFont(Font("Arial", Font.BOLD, 13))
                    lChallengeTitleName.setForeground(JBColor.YELLOW)
                    lChallengeTitleName.horizontalAlignment = SwingConstants.CENTER
                    lChallengeTitleName.verticalAlignment = SwingConstants.CENTER

                    rightPanel.add(lChallengeTitleName)

                    val spacerLabel = JLabel("")
                    spacerLabel.preferredSize = Dimension(10, 0)

                    rightPanel.add(spacerLabel)

                    val lButtonsPanel = JPanel()
                    lButtonsPanel.setLayout(FlowLayout(FlowLayout.RIGHT))
                    val lStoreButton = JButton("Store")
                    val lRejectButton = JButton("Reject")
                    lButtonsPanel.add(lExpandButton)
                    lButtonsPanel.add(lStoreButton)
                    lButtonsPanel.add(lRejectButton)

                    val lExtraContentPanel = JPanel()
                    lExtraContentPanel.setLayout(BorderLayout())
                    lExtraContentPanel.isVisible = false

                    val lChallengeSnippetLabel: JLabel

                    if (challenge.snippet != "") {
                        lChallengeSnippetLabel =
                            JLabel("<HTML><div WIDTH=80>" + challenge.snippet.toString() + "</div></HTML>")
                        lExtraContentPanel.add(lChallengeSnippetLabel, BorderLayout.PAGE_START)
                    }

                    if (challenge.highlightedFileContent != "") {
                        val separator = JSeparator(JSeparator.HORIZONTAL)
                        lExtraContentPanel.add(separator, BorderLayout.CENTER)

                        val lViewSourceButton = Button("View source")

                        lViewSourceButton.preferredSize = Dimension(40,40)

                        lViewSourceButton.addActionListener{
                            val details = challenge.details

                            val projectManager = ProjectManager.getInstance()
                            val projects = projectManager.openProjects
                            val fileSystem = LocalFileSystem.getInstance()

                            val lFilePath = projects.getOrNull(0)?.basePath + details.filePath
                            val file = fileSystem.findFileByPath(lFilePath)

                            file?.let { foundFile ->
                                val fileEditorManager = projects.getOrNull(0)
                                    ?.let { it1 -> FileEditorManager.getInstance(it1) }
                                val openFileDescriptor =
                                    projects.getOrNull(0)?.let { it1 -> OpenFileDescriptor(it1, foundFile) }
                                val editor =
                                    openFileDescriptor?.let { it1 -> fileEditorManager?.openTextEditor(it1, true) }

                                editor?.let { e ->
                                    val document = challenge.snippet?.let { it1 -> Jsoup.parse(it1) }
                                    val codeTagContent = document?.select("code")?.text()

                                    val startOffset = codeTagContent?.let { it1 -> e.document.text.indexOf(it1) }
                                    val endOffset = startOffset?.plus(codeTagContent.length)

                                    e.markupModel.let { markup ->
                                        val highlighter = startOffset?.let { it1 ->
                                            if (endOffset != null) {
                                                markup.addRangeHighlighter(
                                                    it1,
                                                    endOffset,
                                                    HighlighterLayer.SELECTION,
                                                    TextAttributes(),
                                                    HighlighterTargetArea.EXACT_RANGE
                                                )
                                            }
                                        }

                                        if (startOffset != null) {
                                            e.caretModel.moveToOffset(startOffset)
                                        }
                                        if (endOffset != null) {
                                            e.caretModel.moveToOffset(endOffset)
                                        }
                                        if (startOffset != null) {
                                            if (endOffset != null) {
                                                e.caretModel.currentCaret.setSelection(startOffset, endOffset)
                                            }
                                        }

                                        e.scrollingModel.scrollToCaret(ScrollType.CENTER)
                                    }
                                }
                            }

                        }
                        lExtraContentPanel.add(lViewSourceButton)
                    }

                    lExpandButton.addActionListener {
                        lExtraContentPanel.isVisible = !lExtraContentPanel.isVisible
                        challengePanel.revalidate()
                        challengePanel.repaint()
                    }

                    lStoreButton.addActionListener {
                        Utility.storeChallenge(
                            challenge.generalReason?.replace(Regex("<[^>]++>"), "")
                        ) { success, errorMessage ->
                            if (success) {
                                Utility.showMessageDialog("Store successful!")
                                update()
                            } else {
                                Utility.showErrorDialog("Store failed: $errorMessage")
                            }
                        }
                    }

                    lRejectButton.addActionListener {
                        Utility.createRejectModal(
                            challenge.generalReason?.replace(Regex("<[^>]++>"), "")
                        )
                    }

                    val gbc2 = GridBagConstraints()
                    gbc2.gridx = 1
                    gbc2.gridy = 0
                    gbc2.weightx = 0.2


                    gbc2.fill = GridBagConstraints.BOTH
                    lChallengeHeader.add(rightPanel, gbc2)

                    challengePanel.add(lChallengeHeader, BorderLayout.PAGE_START)
                    challengePanel.add(lExtraContentPanel, BorderLayout.CENTER)
                    challengePanel.add(lButtonsPanel, BorderLayout.PAGE_END)
                    challengesPanel.add(challengePanel)

                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        scrollPane.verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_ALWAYS
        scrollPane.horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER

        aInMainPanel.add(scrollPane)
        /*aInMainPanel.add(Box.createVerticalGlue())*/
    }

    private fun createStoredButton(mainPanel: JPanel) {
        val lProjectName = Utility.lPreferences.get("projectName", "")
        if (lProjectName != "") {
            val queryParams = mapOf(
                "job" to lProjectName
            )
            val response = RestClient.getInstance().get(Constants.API_BASE_URL + Constants.GET_STORED_CHALLENGES, queryParams)
            val challengeList = Gson().fromJson(response, StoredChallengeList::class.java).storedChallenges

            val storedChallengesLimit = 2
            val storedChallengesCount = challengeList.size

                val storedChallengesButton = JButton("Stored Challenges ($storedChallengesCount/$storedChallengesLimit)")
                storedChallengesButton.setFont(Font("Arial", Font.PLAIN, 14))
                storedChallengesButton.addActionListener { e: ActionEvent? ->
                    Utility.openStoredChallengesDialog(
                        challengeList
                    )
                }
                mainPanel.add(storedChallengesButton, BorderLayout.CENTER)

        }
    }

}

