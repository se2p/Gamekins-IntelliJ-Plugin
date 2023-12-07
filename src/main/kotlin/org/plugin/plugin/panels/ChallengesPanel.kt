package org.plugin.plugin.panels

import StoredChallengeList
import com.google.gson.Gson
import com.intellij.openapi.editor.Editor
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
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.net.URI
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
            val separator = JSeparator(JSeparator.HORIZONTAL)

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
                                lChallenge.generalReason + "</div></HTML>"
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
                    val lChallengeTitleScore =
                        JLabel("<html><div style='padding: 3px;'>${lChallenge.score.toString() + "&nbsp;" + scoreString}</div></html>").apply {
                            isOpaque = true
                            background = Color.decode("#28a745")
                            foreground = JBColor.WHITE
                            font = font.deriveFont(Font.BOLD, 12f)
                            horizontalAlignment = SwingConstants.CENTER
                            verticalAlignment = SwingConstants.CENTER
                        }

                    val lChallengeTitleName =
                        JLabel("<html><div style='padding: 3px;'>${lChallenge.name}</div></html>").apply {
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
                    lRejectButton.background = mainBackgroundColor
                    lRejectButton.foreground = Color.decode("#ffc107")
                    lRejectButton.isContentAreaFilled = false
                    lRejectButton.isOpaque = true
                    lRejectButton.font = Font("Arial", Font.BOLD, 13)

                    lStoreButton.background = mainBackgroundColor
                    lStoreButton.foreground = JBColor.DARK_GRAY
                    lStoreButton.isContentAreaFilled = false
                    lStoreButton.isOpaque = true
                    lStoreButton.font = Font("Arial", Font.BOLD, 13)

                    val lExpandButton = JButton("Expand")
                    lExpandButton.toolTipText = Constants.CHALLENGE_PANEL_DESCRIPTION
                    lExpandButton.background = mainBackgroundColor
                    lExpandButton.foreground = JBColor.DARK_GRAY
                    lExpandButton.isContentAreaFilled = false
                    lExpandButton.isOpaque = true
                    lExpandButton.font = Font("Arial", Font.BOLD, 13)

                    gbc.gridx = 1
                    gbc.gridy = 0
                    gbc.weightx = 0.4

                    lChallengeHeader.add(lRightPanel, gbc)

                    lChallengePanel.add(lChallengeHeader, BorderLayout.PAGE_START)

                    val lExtraContentPanel = JPanel()
                    lExtraContentPanel.background = Color.decode("#227755")
                    lExtraContentPanel.border = BorderFactory.createEmptyBorder(10,10,10,10)
                    lExtraContentPanel.layout = BorderLayout()
                    lExtraContentPanel.isVisible = false


                    if (lChallenge.snippet != "") {

                        val lHtmlTag = lChallenge.snippet!!.let { it1 -> Jsoup.parse(it1) }

                        val label = JLabel()
                        label.cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                        label.verticalAlignment = SwingConstants.CENTER
                        label.horizontalAlignment = SwingConstants.LEADING
                        setLinkLabelContent(label, lHtmlTag.select("a").toString())

                        if ((lChallenge.name?.trim()?.contains("Smell") == true))
                        {
                            val emTag = lHtmlTag.select("em")
                            emTag.select("a").unwrap().getOrNull(1)
                            val lTextLabel = JLabel("<HTML>$emTag</HTML>")
                            lTextLabel.cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                            lTextLabel.verticalAlignment = SwingConstants.CENTER
                            lTextLabel.horizontalAlignment = SwingConstants.LEFT
                            lExtraContentPanel.add(lTextLabel, BorderLayout.PAGE_START)
                        } else
                        {
                            val lCodeBlock = lHtmlTag.select("pre").getOrNull(1)
                            if (lCodeBlock != null) {
                                val lCodeBlockLabel = JLabel("<HTML>${lCodeBlock.toString().trim()}</HTML>")
                                lCodeBlockLabel.cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                                lCodeBlockLabel.verticalAlignment = SwingConstants.CENTER
                                lCodeBlockLabel.horizontalAlignment = SwingConstants.LEFT
                                lExtraContentPanel.add(lCodeBlockLabel, BorderLayout.PAGE_START)
                            }
                        }

                        lExtraContentPanel.add(Box.createVerticalStrut(10))
                        lExtraContentPanel.add(separator, BorderLayout.CENTER)
                        lExtraContentPanel.add(label, BorderLayout.PAGE_END)

                    }

                    val lViewSourceButton = JButton("Go to source")

                    lViewSourceButton.background = mainBackgroundColor
                    lViewSourceButton.foreground = JBColor.DARK_GRAY
                    lViewSourceButton.isContentAreaFilled = false
                    lViewSourceButton.isOpaque = true
                    lViewSourceButton.font = Font("Arial", Font.BOLD, 13)

                    when {
                        lChallenge.name?.trim().equals("Mutation")
                                || lChallenge.name?.trim()?.contains("Smell") == true -> {

                            lButtonsPanel.add(lExpandButton)
                            lButtonsPanel.add(lViewSourceButton)
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
                                        val document = lChallenge.generalReason!!.let { it1 -> Jsoup.parse(it1) }

                                        val lineNumberElement =
                                            document.select("b").first() // Select the first <b> element

                                        val lineNumberText = lineNumberElement?.text()

                                        val documentLines = e.document.text.split("\n")
                                        val lineNumberToHighlight = lineNumberText!!.toInt() - 1


                                        val lineToHighlight = documentLines[lineNumberToHighlight].trim()
                                        val lineStartOffset = e.document.text.indexOf(lineToHighlight)
                                        val lineEndOffset = lineStartOffset + lineToHighlight.length

                                        highlightCode(e, lineStartOffset, lineEndOffset)
                                    }
                                }
                            }

                        }

                        lChallenge.name?.trim()?.contains("Coverage") == true -> {
                            lButtonsPanel.add(lViewSourceButton)
                            lViewSourceButton.addActionListener {

                                val details = lChallenge.details

                                val projectManager = ProjectManager.getInstance()
                                val projects = projectManager.openProjects
                                val fileSystem = LocalFileSystem.getInstance()

                                val lFilePath = projects.getOrNull(0)?.basePath + details.filePath
                                val file = fileSystem.findFileByPath(lFilePath)

                                if ((lChallenge.name.trim() == "Line Coverage" || lChallenge.name == "Branch Coverage")
                                ) {
                                    file?.let { foundFile ->
                                        val fileEditorManager = projects.getOrNull(0)
                                            ?.let { it1 -> FileEditorManager.getInstance(it1) }
                                        val openFileDescriptor =
                                            projects.getOrNull(0)?.let { it1 -> OpenFileDescriptor(it1, foundFile) }
                                        val editor =
                                            openFileDescriptor?.let { it1 ->
                                                fileEditorManager?.openTextEditor(
                                                    it1,
                                                    true
                                                )
                                            }

                                        editor?.let { e ->
                                            val document = lChallenge.toolTipText!!.substringAfter("Line content:")
                                                .let { it1 -> Jsoup.parse(it1) }
                                            val codeTagContent = document.select("body").text()

                                            val startOffset = codeTagContent.let { it1 -> e.document.text.indexOf(it1) }
                                            val endOffset = startOffset.plus(codeTagContent.length)

                                            highlightCode(e, startOffset, endOffset)
                                        }
                                    }
                                } else if ((lChallenge.name.trim() == "Method Coverage" || lChallenge.name == "Class Coverage")
                                ) {
                                    file?.let { foundFile ->
                                        val fileEditorManager = projects.getOrNull(0)
                                            ?.let { it1 -> FileEditorManager.getInstance(it1) }
                                        val openFileDescriptor =
                                            projects.getOrNull(0)?.let { it1 -> OpenFileDescriptor(it1, foundFile) }
                                        val editor =
                                            openFileDescriptor?.let { it1 ->
                                                fileEditorManager?.openTextEditor(
                                                    it1,
                                                    true
                                                )
                                            }

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

                                                highlightCode(e, startOffset, endOffset)

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
                        if (lExpandButton.text == "Expand") {
                            lExpandButton.text = "Reduce"
                        } else {
                            lExpandButton.text = "Expand"
                        }
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

            val storedChallengesLimit = Utility.getStoredChallengesLimit()
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

    private fun highlightCode(editor: Editor, startOffset: Int, endOffset: Int) {
        editor.markupModel.addRangeHighlighter(
            startOffset,
            endOffset,
            HighlighterLayer.SELECTION,
            TextAttributes(),
            HighlighterTargetArea.EXACT_RANGE
        )
        editor.caretModel.moveToOffset(startOffset)
        editor.caretModel.moveToOffset(endOffset)
        editor.caretModel.currentCaret.setSelection(startOffset, endOffset)
        editor.scrollingModel.scrollToCaret(ScrollType.CENTER)
    }

    private fun setLinkLabelContent(label: JLabel, htmlTag: String) {
        val pattern = Pattern.compile("href=['\"](.*?)['\"]")
        val matcher = pattern.matcher(htmlTag)

        if (matcher.find()) {
            val url = matcher.group(1)
            val displayText = htmlTag.replace("<.*?>".toRegex(), "")


            label.text = "<html><a href='$url' style='color: black; text-decoration: underline;'>$displayText</a></html>"
            label.addMouseListener(object : MouseAdapter() {
                override fun mouseClicked(e: MouseEvent) {
                    if (e.button == MouseEvent.BUTTON1) {
                        try {
                            Desktop.getDesktop().browse(URI(url))
                        } catch (ex: Exception) {
                            ex.printStackTrace()
                        }
                    }
                }
            })
        }
    }
}