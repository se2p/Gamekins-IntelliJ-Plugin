package org.plugin.plugin.panels


import StoredChallengeList
import com.google.gson.Gson
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.ScrollType
import com.intellij.openapi.editor.markup.HighlighterLayer
import com.intellij.openapi.editor.markup.HighlighterTargetArea
import com.intellij.openapi.editor.markup.MarkupModel
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.wm.impl.welcomeScreen.learnIde.coursesInProgress.mainBackgroundColor
import com.intellij.ui.components.JBScrollPane
import com.intellij.util.ui.JBUI
import org.jsoup.Jsoup
import org.plugin.plugin.Constants
import org.plugin.plugin.Utility
import org.plugin.plugin.data.QuestTask
import org.plugin.plugin.data.QuestsListTasks
import org.plugin.plugin.data.RestClient
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.util.prefs.Preferences
import java.util.regex.Matcher
import java.util.regex.Pattern
import javax.swing.*
import javax.swing.border.LineBorder


class CurrentQuestsChallengesPanel : JPanel() {

    val lPreferences = Preferences.userRoot().node("org.plugin.plugin.panels")

    init {
        this.setBackground(mainBackgroundColor)
        performInitialization()
    }

    private fun performInitialization() {

        this.setLayout(GridBagLayout())
        val gbc = GridBagConstraints()
        gbc.gridx = 0
        gbc.weightx = 1.0
        gbc.weighty = 1.0
        gbc.fill = GridBagConstraints.BOTH
        gbc.insets = JBUI.insets(15, 15, 0, 15)
        val row1 = JPanel(BorderLayout())
        row1.background = mainBackgroundColor
        val row2 = JPanel()
        val row3 = JPanel()
        val row4 = JPanel(BorderLayout())
        row2.background = mainBackgroundColor
        row3.background = mainBackgroundColor
        row4.background = mainBackgroundColor


        row3.preferredSize = Dimension(400, 200)
        row4.preferredSize = Dimension(400, 40)

        row3.layout = BoxLayout(row3, BoxLayout.Y_AXIS)

        val label = JLabel("Quests & Challenges")
        label.setFont(Font("Arial", Font.BOLD, 18))
        label.foreground = Color.BLACK
        label.horizontalAlignment = SwingConstants.LEFT
        label.verticalAlignment = SwingConstants.CENTER

        row1.add(label, BorderLayout.CENTER)

        gbc.gridy = 0
        this.add(row1, gbc)

        gbc.gridy = 1
        this.add(row2, gbc)

        gbc.gridy = 2
        this.add(row3, gbc)

        gbc.gridy = 3
        gbc.weighty = 0.1
        this.add(row4, gbc)

        Utility.setCurrentQuestsChallengesPanel(this)
        createQuests(row2)
        createChallenges(row3)
        createStoredButton(row4)
    }

    fun update() {
        removeAll()
        revalidate()
        repaint()
        performInitialization()
    }

    private fun createQuests(mainPanel: JPanel) {

        mainPanel.background = mainBackgroundColor
        val lProjectName = lPreferences.get("projectName", "") ?: return

        try {
            val questsTasksList = fetchQuestsTasks(lProjectName)
            val lQuestsPanel = createQuestsPanel(questsTasksList)
            configureMainPanel(mainPanel, lQuestsPanel)

            mainPanel.border = LineBorder(Color.GRAY, 1)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun fetchQuestsTasks(projectName: String): List<QuestTask> {
        val queryParams = mapOf("job" to projectName)
        val response =
            RestClient.getInstance().get(Utility.getBaseUrl() + Constants.GET_CURRENT_QUESTS_TASKS, queryParams)
        return Gson().fromJson(response, QuestsListTasks::class.java).currentQuestTasks
    }

    private fun createQuestsPanel(questsTasksList: List<QuestTask>): JPanel {
        val lQuestsPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
        }

        questsTasksList.forEachIndexed { index, task ->
            val lQuestPanel = createSingleQuestPanel(task, index)
            lQuestPanel.background = mainBackgroundColor
            lQuestsPanel.add(lQuestPanel)
            if (index != (questsTasksList.size - 1)) {
                val separator = JSeparator(JSeparator.HORIZONTAL)
                lQuestsPanel.add(separator, BorderLayout.CENTER)
            }

        }
        lQuestsPanel.background = mainBackgroundColor
        return lQuestsPanel
    }

    private fun createSingleQuestPanel(task: QuestTask, index: Int): JPanel {
        val lQuestPanel = JPanel(GridLayout(2, 1, 0, 5)).apply {
            border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
            maximumSize = Dimension(Int.MAX_VALUE, 70)
        }
        lQuestPanel.background = mainBackgroundColor
        val questLabel = JLabel(task.title)
        val headerPanel = JPanel(FlowLayout(FlowLayout.LEFT, 5, 0))
        headerPanel.background = mainBackgroundColor

        val spacerLabel = JLabel("")
        spacerLabel.preferredSize = Dimension(0, 0)

        val lScoreString = if (task.score > 1) "points" else "point"
        val lChallengeTitleScore = JLabel("${task.score} $lScoreString").apply {
            setOpaque(true)
            setBackground(Color.decode("#28a745"))
            setForeground(Color.WHITE)
            setFont(font.deriveFont(Font.BOLD, 10f))
            setHorizontalAlignment(SwingConstants.CENTER)
            setVerticalAlignment(SwingConstants.CENTER)
            preferredSize = Dimension(60, 20)
        }

        val lIndex = index + 1
        val progressBarLabel = JLabel(task.completedPercentage.toString()).apply {
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
        mainPanel.background = mainBackgroundColor
        mainPanel.apply {
            background = mainBackgroundColor
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            preferredSize = Dimension(300, 200)

            add(JPanel().apply {

                layout = BorderLayout()
                val label = JLabel("Current Quests")
                label.setFont(Font("Arial", Font.BOLD, 16))
                label.horizontalAlignment = SwingConstants.LEADING
                label.verticalAlignment = SwingConstants.CENTER
                label.foreground = Color.WHITE
                label.border = BorderFactory.createEmptyBorder(0, 10, 0, 0)
                background = Color.BLACK
                foreground = Color.WHITE
                preferredSize = Dimension(300, 40)
                add(label, BorderLayout.CENTER)
            })

            add(JBScrollPane(questsPanel).apply {
                verticalScrollBarPolicy = ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS
            })
        }
    }

    private fun createChallenges(aInMainPanel: JPanel) {

        val lChallengesPanel = JPanel()
        val lScrollPane = JBScrollPane(lChallengesPanel)
        lChallengesPanel.border = LineBorder(Color.GRAY, 1)
        lChallengesPanel.setLayout(BoxLayout(lChallengesPanel, BoxLayout.Y_AXIS))
        lChallengesPanel.background = mainBackgroundColor

        aInMainPanel.add(JPanel().apply {
            layout = BorderLayout()
            val label = JLabel("Current Challenges")
            label.setFont(Font("Arial", Font.BOLD, 16))
            label.horizontalAlignment = SwingConstants.LEADING
            label.verticalAlignment = SwingConstants.CENTER
            label.foreground = Color.WHITE
            label.border = BorderFactory.createEmptyBorder(0, 10, 0, 0)
            background = Color.BLACK
            foreground = Color.WHITE
            preferredSize = Dimension(300, 40)
            add(label, BorderLayout.CENTER)
        })

        try {

            val lChallengeList = Utility.getCurrentChallenges()

            if (lChallengeList != null) {
                for (index in lChallengeList.indices) {

                    val lChallenge = lChallengeList.getOrNull(index) ?: return
                    val lChallengePanel = JPanel()
                    lChallengePanel.setLayout(BorderLayout())
                    lChallengePanel.background = mainBackgroundColor
                    val lRowNum = index + 1

                    val lLeftPanel = JPanel(FlowLayout(FlowLayout.LEFT))
                    val lChallengeHeader = JPanel(GridBagLayout())
                    val lChallengeTitleLabel = JLabel(
                        "<HTML><p WIDTH=" + lLeftPanel.width + ">" +
                                "$lRowNum. " +
                                lChallengeList[index].generalReason + "</p></HTML>"
                    )
                    lChallengeTitleLabel.alignmentX = JLabel.CENTER_ALIGNMENT

                    lLeftPanel.addComponentListener(object : ComponentAdapter() {
                        override fun componentResized(evt: ComponentEvent) {
                            lChallengeTitleLabel.setText(
                                "<HTML><p style='margin: 0' WIDTH=" + lLeftPanel.width + ">" +
                                        "$lRowNum. " + lChallengeList[index].generalReason + "</p></HTML>"
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
                    lChallengeHeader.background = mainBackgroundColor
                    lLeftPanel.background = mainBackgroundColor

                    val scoreString = if (lChallenge.score!! > 1) "points" else "point"
                    val lChallengeTitleScore = JLabel("<html><div style='padding: 3px;'>${lChallenge.score.toString() + "&nbsp;" + scoreString}</div></html>").apply {
                        setOpaque(true)
                        setBackground(Color.decode("#28a745"))
                        setForeground(Color.WHITE)
                        setFont(font.deriveFont(Font.BOLD, 12f))
                        setHorizontalAlignment(SwingConstants.CENTER)
                        setVerticalAlignment(SwingConstants.CENTER)
                    }

                    val lChallengeTitleName = JLabel("<html><div style='padding: 3px;'>${lChallenge.name}</div></html>").apply {
                        setOpaque(true)
                        setBackground(Color.decode("#ffc107"))
                        setForeground(Color.decode("#212529"))
                        setFont(font.deriveFont(Font.BOLD, 12f))
                        setHorizontalAlignment(SwingConstants.CENTER)
                        setVerticalAlignment(SwingConstants.CENTER)
                    }
                    val lExpandButton = JButton("Expand")
                    lExpandButton.background = mainBackgroundColor
                    lExpandButton.toolTipText = Constants.CHALLENGE_PANEL_DESCRIPTION

                    val lRightPanel = JPanel().apply {
                        border = BorderFactory.createEmptyBorder(10, 0, 0, 20)
                        layout = BoxLayout(this, BoxLayout.X_AXIS)
                        background = mainBackgroundColor
                        add(lChallengeTitleScore)
                        add(Box.createHorizontalStrut(10))
                        add(lChallengeTitleName)
                    }

                    val lButtonsPanel = JPanel()
                    lButtonsPanel.background = mainBackgroundColor
                    lButtonsPanel.setLayout(FlowLayout(FlowLayout.RIGHT))
                    val lStoreButton = JButton("Store")
                    val lRejectButton = JButton("Reject")
                    lRejectButton.background = Color.RED
                    lRejectButton.foreground = Color.WHITE
                    lRejectButton.border = null
                    lRejectButton.setContentAreaFilled(false)
                    lRejectButton.setOpaque(true)

                    lStoreButton.background = Color.GRAY
                    lStoreButton.foreground = Color.WHITE
                    lStoreButton.border = null
                    lStoreButton.setContentAreaFilled(false)
                    lStoreButton.setOpaque(true)

                    lExpandButton.background = Color.GRAY
                    lExpandButton.foreground = Color.WHITE
                    lExpandButton.border = null
                    lExpandButton.setContentAreaFilled(false)
                    lExpandButton.setOpaque(true)

                    gbc.gridx = 1
                    gbc.gridy = 0
                    gbc.weightx = 0.4

                    lChallengeHeader.add(lRightPanel, gbc)

                    lChallengePanel.add(lChallengeHeader, BorderLayout.PAGE_START)

                    val lExtraContentPanel = JPanel()
                    lExtraContentPanel.background = mainBackgroundColor
                    lExtraContentPanel.setLayout(BorderLayout())
                    lExtraContentPanel.isVisible = false

                    val lChallengeSnippetLabel: JLabel

                    if (lChallenge.snippet != "") {
                        lChallengeSnippetLabel =
                            JLabel("<HTML><p WIDTH=80>" + lChallenge.snippet.toString() + "</p></HTML>")
                        lExtraContentPanel.add(lChallengeSnippetLabel, BorderLayout.PAGE_START)
                    }

                    val separator = JSeparator(JSeparator.HORIZONTAL)
                    lExtraContentPanel.add(separator, BorderLayout.CENTER)

                    val lViewSourceButton = JButton("Goto source")

                    lViewSourceButton.background = Color.GRAY
                    lViewSourceButton.foreground = Color.WHITE
                    lViewSourceButton.border = null
                    lViewSourceButton.setContentAreaFilled(false)
                    lViewSourceButton.setOpaque(true)

                    if ((lChallenge.name?.trim()?.contains("Smell") == true)) {

                        lButtonsPanel.add(lExpandButton)
                        lExtraContentPanel.add(lViewSourceButton)
                        lChallengePanel.add(lExtraContentPanel, BorderLayout.CENTER)

                    } else if ((lChallenge.name?.trim().equals("Mutation"))) {

                        lButtonsPanel.add(lExpandButton)
                        lExtraContentPanel.add(lViewSourceButton)
                        lChallengePanel.add(lExtraContentPanel, BorderLayout.CENTER)

                        lViewSourceButton.addActionListener {

                            val details = lChallenge.details
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
                                    val document = lChallenge.snippet!!.let { it1 -> Jsoup.parse(it1) }
                                    val codeTagContent = document.select("code").first()!!.text()

                                    val document1 = lChallenge.generalReason!!.let { it1 -> Jsoup.parse(it1) }

                                    val lineNumberElement =
                                        document1.select("b").first() // Select the first <b> element

                                    val lineNumberText = lineNumberElement?.text()

                                    if (lineNumberText != null) {
                                        // highlightLineByNumber(lineNumberText.toInt(), e.document, e.markupModel)
                                    }

                                    val documentLines = e.document.text.split("\n")
                                    val lineNumberToHighlight = lineNumberText!!.toInt() - 1


                                    val lineToHighlight = documentLines[lineNumberToHighlight]
                                    val lineStartOffset = e.document.text.indexOf(lineToHighlight)
                                    val lineEndOffset = lineStartOffset + lineToHighlight.length

                                    val startOffset = codeTagContent.let { it1 -> e.document.text.indexOf(it1) }
                                    val endOffset = startOffset.plus(codeTagContent.length)

                                    e.markupModel.let { markup ->
                                        val highlighter = startOffset.let { it1 ->
                                            markup.addRangeHighlighter(
                                                it1,
                                                lineEndOffset,
                                                HighlighterLayer.SELECTION,
                                                TextAttributes(),
                                                HighlighterTargetArea.EXACT_RANGE
                                            )
                                        }

                                        e.caretModel.moveToOffset(lineStartOffset)
                                        e.caretModel.moveToOffset(lineEndOffset)
                                        e.caretModel.currentCaret.setSelection(lineStartOffset, lineStartOffset)
                                        e.scrollingModel.scrollToCaret(ScrollType.CENTER)
                                    }
                                }
                            }
                        }

                    } else {
                        lButtonsPanel.add(lViewSourceButton)
                        lViewSourceButton.addActionListener {

                            val details = lChallenge.details

                            val projectManager = ProjectManager.getInstance()
                            val projects = projectManager.openProjects
                            val fileSystem = LocalFileSystem.getInstance()

                            val lFilePath = projects.getOrNull(0)?.basePath + details.filePath
                            val file = fileSystem.findFileByPath(lFilePath)

                            if ((lChallenge.name?.trim()
                                    .equals("Line Coverage") || lChallenge.name.equals("Branch Coverage"))
                            ) {
                                file?.let { foundFile ->
                                    val fileEditorManager = projects.getOrNull(0)
                                        ?.let { it1 -> FileEditorManager.getInstance(it1) }
                                    val openFileDescriptor =
                                        projects.getOrNull(0)?.let { it1 -> OpenFileDescriptor(it1, foundFile) }
                                    val editor =
                                        openFileDescriptor?.let { it1 -> fileEditorManager?.openTextEditor(it1, true) }

                                    editor?.let { e ->
                                        val document = lChallenge.toolTipText!!.substringAfter("Line content:")
                                            .let { it1 -> Jsoup.parse(it1) }
                                        val codeTagContent = document.select("body").text()

                                        val startOffset = codeTagContent.let { it1 -> e.document.text.indexOf(it1) }
                                        val endOffset = startOffset.plus(codeTagContent.length)

                                        e.markupModel.let { markup ->
                                            val highlighter = startOffset.let { it1 ->
                                                markup.addRangeHighlighter(
                                                    it1,
                                                    endOffset,
                                                    HighlighterLayer.SELECTION,
                                                    TextAttributes(),
                                                    HighlighterTargetArea.EXACT_RANGE
                                                )
                                            }

                                            e.caretModel.moveToOffset(startOffset)
                                            e.caretModel.moveToOffset(endOffset)
                                            e.caretModel.currentCaret.setSelection(startOffset, endOffset)
                                            e.scrollingModel.scrollToCaret(ScrollType.CENTER)
                                        }
                                    }
                                }
                            } else if ((lChallenge.name?.trim()
                                    .equals("Method Coverage") || lChallenge.name.equals("Class Coverage"))
                            ) {
                                file?.let { foundFile ->
                                    val fileEditorManager = projects.getOrNull(0)
                                        ?.let { it1 -> FileEditorManager.getInstance(it1) }
                                    val openFileDescriptor =
                                        projects.getOrNull(0)?.let { it1 -> OpenFileDescriptor(it1, foundFile) }
                                    val editor =
                                        openFileDescriptor?.let { it1 -> fileEditorManager?.openTextEditor(it1, true) }

                                    editor?.let { e ->
                                        val document = lChallenge.snippet.let { it1 -> Jsoup.parse(it1) }
                                        val codeTagContent = document.select("code").text()


                                        var methodName: String? = null
                                        val pattern: Pattern =
                                            Pattern.compile("(?<=\\bpublic\\s|private\\s|protected\\s)\\w+\\s+(\\w+)")
                                        val matcher: Matcher = pattern.matcher(codeTagContent)

                                        if (matcher.find()) {
                                            methodName = matcher.group(1)
                                            val startOffset = methodName.let { it1 -> e.document.text.indexOf(it1) }
                                            val endOffset = startOffset.plus(methodName.length)

                                            e.markupModel.let { markup ->
                                                val highlighter = startOffset.let { it1 ->
                                                    markup.addRangeHighlighter(
                                                        it1,
                                                        endOffset,
                                                        HighlighterLayer.SELECTION,
                                                        TextAttributes(),
                                                        HighlighterTargetArea.EXACT_RANGE
                                                    )
                                                }

                                                e.caretModel.moveToOffset(startOffset)
                                                e.caretModel.moveToOffset(endOffset)
                                                e.caretModel.currentCaret.setSelection(startOffset, endOffset)
                                                e.scrollingModel.scrollToCaret(ScrollType.CENTER)
                                            }

                                        }


                                    }
                                }
                            }
                        }
                    }


                    lButtonsPanel.add(lStoreButton)
                    lButtonsPanel.add(lRejectButton)

                    lExpandButton.addActionListener {
                        lExtraContentPanel.isVisible = !lExtraContentPanel.isVisible
                        lChallengePanel.revalidate()
                        lChallengePanel.repaint()
                    }
                    lStoreButton.addActionListener {
                        Utility.storeChallenge(
                            lChallenge.generalReason?.replace(Regex("<[^>]++>"), "")
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
                            lChallenge.generalReason?.replace(Regex("<[^>]++>"), "")
                        )
                    }

                    lChallengePanel.add(lButtonsPanel, BorderLayout.PAGE_END)
                    lChallengesPanel.add(lChallengePanel)

                    if (index != (lChallengeList.size - 1)) {
                        lChallengesPanel.add(separator, BorderLayout.CENTER)
                    }

                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        lScrollPane.verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_ALWAYS
        lScrollPane.horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER

        aInMainPanel.add(lScrollPane)
    }

    private fun createStoredButton(mainPanel: JPanel) {
        val lProjectName = Utility.lPreferences.get("projectName", "")
        if (lProjectName != "") {
            val queryParams = mapOf(
                "job" to lProjectName
            )
            val response =
                RestClient.getInstance().get(Utility.getBaseUrl() + Constants.GET_STORED_CHALLENGES, queryParams)
            val challengeList = Gson().fromJson(response, StoredChallengeList::class.java).storedChallenges

            val storedChallengesLimit = 20
            val storedChallengesCount = challengeList.size

            val lStoredChallengesButton = JButton("Stored Challenges ($storedChallengesCount/$storedChallengesLimit)")
            lStoredChallengesButton.setFont(Font("Arial", Font.PLAIN, 14))
            lStoredChallengesButton.addActionListener { e: ActionEvent? ->
                Utility.openStoredChallengesDialog(
                    challengeList
                )
            }
            lStoredChallengesButton.setSize(80, 40)
            lStoredChallengesButton.background = mainBackgroundColor

            val lJPanel = JPanel()
            lJPanel.add(lStoredChallengesButton)
            lJPanel.background = mainBackgroundColor

            mainPanel.add(lJPanel, BorderLayout.CENTER)

        }
    }

    fun highlightLineByNumber(lineNumber: Int, document: Document, markupModel: MarkupModel) {
        val documentLines = document.text.split("\n")
        val lineNumberToHighlight = lineNumber - 1 // Adjust for zero-based indexing

        if (lineNumberToHighlight >= 0 && lineNumberToHighlight < documentLines.size) {
            val lineToHighlight = documentLines[lineNumberToHighlight]
            val lineStartOffset = document.text.indexOf(lineToHighlight)
            val lineEndOffset = lineStartOffset + lineToHighlight.length

            if (lineStartOffset >= 0) {
                val highlighter = markupModel.addRangeHighlighter(
                    lineStartOffset,
                    lineEndOffset,
                    HighlighterLayer.SELECTION,
                    TextAttributes(),
                    HighlighterTargetArea.EXACT_RANGE
                )
                // Additional settings for the highlighter if needed
            }
        }
    }

}

