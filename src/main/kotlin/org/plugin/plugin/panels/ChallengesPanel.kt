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
import com.intellij.openapi.wm.impl.welcomeScreen.learnIde.coursesInProgress.mainBackgroundColor
import com.intellij.ui.JBColor
import com.intellij.ui.util.maximumHeight
import org.jsoup.Jsoup
import org.plugin.plugin.Constants
import org.plugin.plugin.Utility
import org.plugin.plugin.data.RestClient
import java.awt.*
import java.awt.event.ActionEvent
import java.util.regex.Matcher
import java.util.regex.Pattern
import javax.swing.*

class ChallengesPanel: JPanel() {

    init {
        this.background = mainBackgroundColor
        initializePanel()
    }

    fun initializePanel() {
        this.layout = BoxLayout(this, BoxLayout.Y_AXIS)

        this.add(JPanel().apply {
            layout = BorderLayout()
            val label = JLabel("Current Challenges")
            label.font = Font("Arial", Font.BOLD, 16)
            label.horizontalAlignment = SwingConstants.LEADING
            label.verticalAlignment = SwingConstants.CENTER
            label.foreground = JBColor.WHITE
            label.border = BorderFactory.createEmptyBorder(0, 10, 0, 0)
            background = JBColor.BLACK
            foreground = JBColor.WHITE
            preferredSize = Dimension(300, 40)
            maximumHeight = 40
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
                    lChallengePanel.maximumHeight = 70
                    val lRowNum = index + 1

                    val lLeftPanel = JPanel(FlowLayout(FlowLayout.LEFT))
                    val lChallengeHeader = JPanel(GridBagLayout())
                    val lChallengeTitleLabel = JLabel(
                        "<HTML><div WIDTH=550" + ">" +
                                "$lRowNum. " +
                                lChallengeList[index].generalReason + "</div></HTML>"
                    )
                    lChallengeTitleLabel.alignmentX = JLabel.CENTER_ALIGNMENT

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
                        isOpaque = true
                        background = Color.decode("#28a745")
                        foreground = JBColor.WHITE
                        font = font.deriveFont(Font.BOLD, 12f)
                        horizontalAlignment = SwingConstants.CENTER
                        verticalAlignment = SwingConstants.CENTER
                    }

                    val lChallengeTitleName = JLabel("<html><div style='padding: 3px;'>${lChallenge.name}</div></html>").apply {
                        isOpaque = true
                        background = Color.decode("#ffc107")
                        foreground = Color.decode("#212529")
                        font = font.deriveFont(Font.BOLD, 12f)
                        horizontalAlignment = SwingConstants.CENTER
                        verticalAlignment = SwingConstants.CENTER
                    }

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
                    lButtonsPanel.layout = FlowLayout(FlowLayout.RIGHT)
                    val lStoreButton = JButton("Store")
                    val lRejectButton = JButton("Reject")
                    lRejectButton.background = JBColor.RED
                    lRejectButton.foreground = JBColor.WHITE
                    lRejectButton.border = null
                    lRejectButton.isContentAreaFilled = false
                    lRejectButton.isOpaque = true

                    lStoreButton.background = JBColor.GRAY
                    lStoreButton.foreground = JBColor.WHITE
                    lStoreButton.border = null
                    lStoreButton.isContentAreaFilled = false
                    lStoreButton.isOpaque = true

                    val lExpandButton = JButton("Expand")
                    lExpandButton.toolTipText = Constants.CHALLENGE_PANEL_DESCRIPTION
                    lExpandButton.background = JBColor.GRAY
                    lExpandButton.foreground = JBColor.WHITE
                    lExpandButton.border = null
                    lExpandButton.isContentAreaFilled = false
                    lExpandButton.isOpaque = true

                    gbc.gridx = 1
                    gbc.gridy = 0
                    gbc.weightx = 0.4

                    lChallengeHeader.add(lRightPanel, gbc)

                    lChallengePanel.add(lChallengeHeader, BorderLayout.PAGE_START)

                    val lExtraContentPanel = JPanel()
                    lExtraContentPanel.background = mainBackgroundColor
                    lExtraContentPanel.layout = BorderLayout()
                    lExtraContentPanel.isVisible = false

                    val lChallengeSnippetLabel: JLabel

                    if (lChallenge.snippet != "") {
                        lChallengeSnippetLabel =
                            JLabel("<HTML><p WIDTH=80>" + lChallenge.snippet.toString() + "</p></HTML>")
                        lExtraContentPanel.add(lChallengeSnippetLabel, BorderLayout.PAGE_START)
                    }

                    val separator = JSeparator(JSeparator.HORIZONTAL)
                    lExtraContentPanel.add(separator, BorderLayout.CENTER)

                    val lViewSourceButton = JButton("Go to source")

                    lViewSourceButton.background = JBColor.GRAY
                    lViewSourceButton.foreground = JBColor.WHITE
                    lViewSourceButton.border = null
                    lViewSourceButton.isContentAreaFilled = false
                    lViewSourceButton.isOpaque = true

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
                                        startOffset.let { it1 ->
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
                                            startOffset.let { it1 ->
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


                                        val methodName: String?
                                        val pattern: Pattern =
                                            Pattern.compile("(?<=\\bpublic\\s|private\\s|protected\\s)\\w+\\s+(\\w+)")
                                        val matcher: Matcher = pattern.matcher(codeTagContent)

                                        if (matcher.find()) {
                                            methodName = matcher.group(1)
                                            val startOffset = methodName.let { it1 -> e.document.text.indexOf(it1) }
                                            val endOffset = startOffset.plus(methodName.length)

                                            e.markupModel.let { markup ->
                                                startOffset.let { it1 ->
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
                                removeAll()
                                initializePanel()
                            } else {
                                Utility.showErrorDialog("Store failed: $errorMessage")
                            }
                        }
                    }
                    lRejectButton.addActionListener {
                        Utility.createRejectModal(
                            lChallenge.generalReason?.replace(Regex("<[^>]++>"), ""), this
                        )
                    }

                    lChallengePanel.add(lButtonsPanel, BorderLayout.PAGE_END)
                    this.add(lChallengePanel)

                    this.add(separator, BorderLayout.CENTER)
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        this.add(Box.createRigidArea(Dimension(0, 10)))

        createStoredButton(this)

        this.add(Box.createRigidArea(Dimension(0, 10)))

        this.add(AcceptedRejectedChallengesPanel())

        Utility.challengesPanel = this
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
            lStoredChallengesButton.font = Font("Arial", Font.PLAIN, 14)
            lStoredChallengesButton.addActionListener { _: ActionEvent? ->
                Utility.openStoredChallengesDialog(
                    challengeList, this
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
}