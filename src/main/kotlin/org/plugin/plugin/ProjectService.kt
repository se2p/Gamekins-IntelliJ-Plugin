package org.plugin.plugin


import com.intellij.openapi.Disposable
import com.intellij.openapi.components.Service
import com.intellij.openapi.editor.ScrollType
import com.intellij.openapi.editor.markup.HighlighterLayer
import com.intellij.openapi.editor.markup.HighlighterTargetArea
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.vfs.LocalFileSystem
import javax.swing.SwingUtilities
import com.intellij.openapi.editor.EditorFactory


@Service(Service.Level.PROJECT)
class ProjectService(project: Project) : Disposable {

    init {
        Utility.startWebSocket()
        SwingUtilities.invokeLater {
            //initSources()
        }

    }

    fun initSources() {
        val lChallengeList = Utility.getCurrentChallenges()
        if (lChallengeList != null) {
            for (index in lChallengeList.indices) {

                val challenge = lChallengeList.get(index)

                if (challenge.highlightedFileContent != "") {

                    val details = challenge.details

                    val projectManager = ProjectManager.getInstance()
                    val projects = projectManager.openProjects
                    val fileSystem = LocalFileSystem.getInstance()

                    val lFilePath = projects.getOrNull(0)?.basePath + details.filePath
                    val file = fileSystem.findFileByPath(lFilePath)


                    val project = projects.get(0)

                    file?.let { foundFile ->
                        val editorFactory = EditorFactory.getInstance()
                        val document = editorFactory.createDocument(foundFile.contentsToByteArray().toString(Charsets.UTF_8))

                        val startIndex = document.text.indexOf(challenge.toolTipText.toString())
                        if (startIndex != -1) {
                            val endIndex = startIndex + challenge.toolTipText.toString().length

                            val editor = editorFactory.createEditor(document)
                            val markupModel = editor.markupModel

                            markupModel.addRangeHighlighter(
                                startIndex,
                                endIndex,
                                0x77ff0000, // RGBA color code for red with 50% opacity
                                null,
                                com.intellij.openapi.editor.markup.HighlighterTargetArea.EXACT_RANGE
                            )

                            editorFactory.releaseEditor(editor)
                        }
                    }







                    /*val lTextAttributes = TextAttributes()
                    lTextAttributes.foregroundColor = JBColor.magenta

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
                                            lTextAttributes,
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
                    }*/

                }
            }
        }
    }


    override fun dispose() = Unit
}