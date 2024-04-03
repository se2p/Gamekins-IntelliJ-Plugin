package org.gamekins.ide.panels

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
import org.gamekins.ide.Constants
import org.gamekins.ide.Utility
import org.gamekins.ide.data.RestClient
import org.gamekins.ide.data.StoredChallengeList
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

            val challengeList = Utility.getCurrentChallenges()
            val separator = JSeparator(JSeparator.HORIZONTAL)

            if (challengeList != null) {
                for (index in challengeList.indices) {

                    val challenge = challengeList.getOrNull(index) ?: return
                    val challengePanel = JPanel()
                    challengePanel.setLayout(BorderLayout())
                    challengePanel.background = mainBackgroundColor
                    challengePanel.maximumHeight = 70
                    val rowNum = index + 1

                    val leftPanel = JPanel(FlowLayout(FlowLayout.LEFT))
                    val challengeHeader = JPanel(GridBagLayout())
                    val challengeTitleLabel = JLabel(
                        "<HTML><div WIDTH=550" + ">" +
                                "$rowNum. " +
                                challenge.generalReason + "</div></HTML>"
                    )
                    challengeTitleLabel.alignmentX = JLabel.CENTER_ALIGNMENT

                    val gbc = GridBagConstraints()
                    gbc.gridx = 0
                    gbc.gridy = 0
                    gbc.weightx = 0.8
                    gbc.fill = GridBagConstraints.BOTH

                    leftPanel.add(challengeTitleLabel)
                    challengeHeader.add(leftPanel, gbc)
                    challengeHeader.background = mainBackgroundColor
                    leftPanel.background = mainBackgroundColor

                    val scoreString = if (challenge.score!! > 1) "points" else "point"
                    val challengeTitleScore =
                        JLabel("<html><div style='padding: 3px;'>${challenge.score.toString() + "&nbsp;" + scoreString}</div></html>").apply {
                            isOpaque = true
                            background = Color.decode("#28a745")
                            foreground = JBColor.WHITE
                            font = font.deriveFont(Font.BOLD, 12f)
                            horizontalAlignment = SwingConstants.CENTER
                            verticalAlignment = SwingConstants.CENTER
                        }

                    val challengeTitleName =
                        JLabel("<html><div style='padding: 3px;'>${challenge.name}</div></html>").apply {
                            isOpaque = true
                            background = Color.decode("#ffc107")
                            foreground = Color.decode("#212529")
                            font = font.deriveFont(Font.BOLD, 12f)
                            horizontalAlignment = SwingConstants.CENTER
                            verticalAlignment = SwingConstants.CENTER
                        }

                    val rightPanel = JPanel().apply {
                        border = BorderFactory.createEmptyBorder(10, 0, 0, 20)
                        layout = BoxLayout(this, BoxLayout.X_AXIS)
                        background = mainBackgroundColor
                        add(challengeTitleScore)
                        add(Box.createHorizontalStrut(10))
                        add(challengeTitleName)
                    }

                    val buttonsPanel = JPanel()
                    buttonsPanel.background = mainBackgroundColor
                    buttonsPanel.layout = FlowLayout(FlowLayout.RIGHT)
                    val storeButton = JButton("Store")
                    val rejectButton = JButton("Reject")
                    rejectButton.background = mainBackgroundColor
                    rejectButton.foreground = JBColor.RED
                    rejectButton.isContentAreaFilled = false
                    rejectButton.isOpaque = true
                    rejectButton.font = Font("Arial", Font.BOLD, 13)

                    storeButton.background = mainBackgroundColor
                    storeButton.foreground = JBColor.DARK_GRAY
                    storeButton.isContentAreaFilled = false
                    storeButton.isOpaque = true
                    storeButton.font = Font("Arial", Font.BOLD, 13)

                    val expandButton = JButton("Expand")
                    expandButton.toolTipText = Constants.CHALLENGE_PANEL_DESCRIPTION
                    expandButton.background = mainBackgroundColor
                    expandButton.foreground = JBColor.DARK_GRAY
                    expandButton.isContentAreaFilled = false
                    expandButton.isOpaque = true
                    expandButton.font = Font("Arial", Font.BOLD, 13)

                    gbc.gridx = 1
                    gbc.gridy = 0
                    gbc.weightx = 0.4

                    challengeHeader.add(rightPanel, gbc)

                    challengePanel.add(challengeHeader, BorderLayout.PAGE_START)

                    val extraContentPanel = JPanel()
                    extraContentPanel.background = JBColor.LIGHT_GRAY
                    extraContentPanel.border = BorderFactory.createEmptyBorder(10,10,10,10)
                    extraContentPanel.layout = BorderLayout()
                    extraContentPanel.isVisible = false


                    if (challenge.snippet != "") {

                        val htmlTag = challenge.snippet!!.let { it1 -> Jsoup.parse(it1) }

                        val label = JLabel()
                        label.cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                        label.verticalAlignment = SwingConstants.CENTER
                        label.horizontalAlignment = SwingConstants.LEADING
                        setLinkLabelContent(label, htmlTag.select("a").toString())

                        if ((challenge.name?.trim()?.contains("Smell") == true))
                        {
                            val emTag = htmlTag.select("em")
                            emTag.select("a").unwrap().getOrNull(1)
                            val textLabel = JLabel("<HTML>$emTag</HTML>")
                            textLabel.cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                            textLabel.verticalAlignment = SwingConstants.CENTER
                            textLabel.horizontalAlignment = SwingConstants.LEFT
                            extraContentPanel.add(textLabel, BorderLayout.PAGE_START)
                        } else
                        {
                            val codeBlock = htmlTag.select("pre").getOrNull(1)
                            if (codeBlock != null) {
                                val codeBlockLabel = JLabel("<HTML>${codeBlock.toString().trim()}</HTML>")
                                codeBlockLabel.cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                                codeBlockLabel.verticalAlignment = SwingConstants.CENTER
                                codeBlockLabel.horizontalAlignment = SwingConstants.LEFT
                                extraContentPanel.add(codeBlockLabel, BorderLayout.PAGE_START)
                            }
                        }

                        extraContentPanel.add(Box.createVerticalStrut(10))
                        extraContentPanel.add(separator, BorderLayout.CENTER)
                        extraContentPanel.add(label, BorderLayout.PAGE_END)

                    }

                    val viewSourceButton = JButton("Go to source")

                    viewSourceButton.background = mainBackgroundColor
                    viewSourceButton.foreground = JBColor.DARK_GRAY
                    viewSourceButton.isContentAreaFilled = false
                    viewSourceButton.isOpaque = true
                    viewSourceButton.font = Font("Arial", Font.BOLD, 13)

                    when {
                        challenge.name?.trim().equals("Mutation")
                                || challenge.name?.trim()?.contains("Smell") == true -> {

                            buttonsPanel.add(expandButton)
                            buttonsPanel.add(viewSourceButton)
                            challengePanel.add(extraContentPanel, BorderLayout.CENTER)

                            viewSourceButton.addActionListener {

                                val details = challenge.details
                                val projectManager = ProjectManager.getInstance()
                                val projects = projectManager.openProjects
                                val fileSystem = LocalFileSystem.getInstance()

                                val filePath = projects.getOrNull(0)?.basePath + details.filePath
                                val file = fileSystem.findFileByPath(filePath)
                                file?.let { foundFile ->
                                    val fileEditorManager = projects.getOrNull(0)
                                        ?.let { it1 -> FileEditorManager.getInstance(it1) }
                                    val openFileDescriptor =
                                        projects.getOrNull(0)?.let { it1 -> OpenFileDescriptor(it1, foundFile) }
                                    val editor =
                                        openFileDescriptor?.let { it1 -> fileEditorManager?.openTextEditor(it1, true) }

                                    editor?.let { e ->
                                        val document = challenge.generalReason!!.let { it1 -> Jsoup.parse(it1) }

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

                        challenge.name?.trim()?.contains("Coverage") == true -> {
                            buttonsPanel.add(viewSourceButton)
                            viewSourceButton.addActionListener {

                                val details = challenge.details

                                val projectManager = ProjectManager.getInstance()
                                val projects = projectManager.openProjects
                                val fileSystem = LocalFileSystem.getInstance()

                                val filePath = projects.getOrNull(0)?.basePath + details.filePath
                                val file = fileSystem.findFileByPath(filePath)

                                if ((challenge.name.trim() == "Line Coverage" || challenge.name == "Branch Coverage")
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
                                            val document = challenge.toolTipText!!.substringAfter("Line content:")
                                                .let { it1 -> Jsoup.parse(it1) }
                                            val codeTagContent = document.select("body").text()

                                            val startOffset = codeTagContent.let { it1 -> e.document.text.indexOf(it1) }
                                            val endOffset = startOffset.plus(codeTagContent.length)

                                            highlightCode(e, startOffset, endOffset)
                                        }
                                    }
                                } else if ((challenge.name.trim() == "Method Coverage" || challenge.name == "Class Coverage")
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
                                            val document = challenge.snippet.let { it1 -> Jsoup.parse(it1) }
                                            val codeTagContent = document.select("code").text()


                                            val methodName: String?
                                            val pattern =
                                                if (challenge.name == "Class Coverage") Pattern.compile("(public\\s)?(class|interface|enum)\\s([^\\n\\s]*)")
                                                else Pattern.compile("(public|protected|private|static|\\s)( static)? +[\\w<>\\[\\]]+\\s+(\\w+) *\\([^)]*\\) *(\\{?|[^;])")
                                            val matcher: Matcher = pattern.matcher(codeTagContent)

                                            if (matcher.find()) {
                                                methodName = matcher.group(3)
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


                    buttonsPanel.add(storeButton)
                    buttonsPanel.add(rejectButton)

                    expandButton.addActionListener {
                        extraContentPanel.isVisible = !extraContentPanel.isVisible
                        if (expandButton.text == "Expand") {
                            expandButton.text = "Reduce"
                        } else {
                            expandButton.text = "Expand"
                        }
                        challengePanel.revalidate()
                        challengePanel.repaint()
                    }
                    storeButton.addActionListener {
                        Utility.storeChallenge(
                            challenge.generalReason?.replace(Regex("<[^>]++>"), "")
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
                    rejectButton.addActionListener {
                        Utility.createRejectModal(
                            challenge.generalReason?.replace(Regex("<[^>]++>"), ""), this
                        )
                    }

                    challengePanel.add(buttonsPanel, BorderLayout.PAGE_END)
                    this.add(challengePanel)

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
        val projectName = Utility.preferences["projectName", ""]
        if (projectName != "") {
            val queryParams = mapOf(
                "job" to projectName
            )
            val response =
                RestClient.getInstance().get(Utility.getBaseUrl() + Constants.GET_STORED_CHALLENGES, queryParams)
            val challengeList = Gson().fromJson(response, StoredChallengeList::class.java).storedChallenges

            val storedChallengesLimit = Utility.getStoredChallengesLimit()
            val storedChallengesCount = challengeList.size

            val storedChallengesButton = JButton("Stored Challenges ($storedChallengesCount/$storedChallengesLimit)")
            storedChallengesButton.font = Font("Arial", Font.PLAIN, 14)
            storedChallengesButton.addActionListener { _: ActionEvent? ->
                Utility.openStoredChallengesDialog(
                    challengeList, this
                )
            }
            storedChallengesButton.setSize(80, 40)
            storedChallengesButton.background = mainBackgroundColor

            val jPanel = JPanel()
            jPanel.add(storedChallengesButton)
            jPanel.background = mainBackgroundColor

            mainPanel.add(jPanel, BorderLayout.CENTER)

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