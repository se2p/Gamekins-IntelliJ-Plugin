package org.plugin.plugin

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.ui.content.Content
import com.intellij.ui.content.ContentManager
import org.jsoup.Jsoup

import com.intellij.psi.*

import java.util.regex.Matcher
import java.util.regex.Pattern

class CustomInspection : LocalInspectionTool() {
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : PsiElementVisitor() {
            override fun visitFile(file: PsiFile) {
                super.visitFile(file)
                val lChallengeList = Utility.getCurrentChallenges() ?: return

                for (lChallenge in lChallengeList) {
                    val document = PsiDocumentManager.getInstance(file.project).getDocument(file)
                    val fileNameWithExtension = lChallenge.details.fileName + "." + lChallenge.details.fileExtension

                    if (document != null && fileNameWithExtension == file.name) {
                        when {
                            lChallenge.name?.trim() == "Mutation" || lChallenge.name?.trim()?.contains("Smell") == true -> {
                                val fileDocument = Jsoup.parse(lChallenge.generalReason!!)
                                val lineNumberElement = fileDocument.select("b").first()
                                val lineNumberText = lineNumberElement?.text()
                                val documentLines = file.text.split("\n")
                                val lineNumberToHighlight = lineNumberText?.toIntOrNull()?.minus(1)

                                lineNumberToHighlight?.takeIf { it in documentLines.indices }?.let { line ->
                                    val lineToHighlight = documentLines[line].trim()
                                    val lineStartOffset = file.text.indexOf(lineToHighlight)
                                    val lineEndOffset = lineStartOffset + lineToHighlight.length
                                    val quickFix = QuickFix()
                                    file.findElementAt(lineStartOffset)?.let {
                                        holder.registerProblem(
                                            it.parent,
                                            "Check the challenge here",
                                            quickFix
                                        )
                                    }
                                }
                            }
                            lChallenge.name?.trim() == "Line Coverage" || lChallenge.name == "Branch Coverage" -> {
                                val fileDocument = Jsoup.parse(lChallenge.toolTipText!!.substringAfter("Line content:"))
                                val codeTagContent = fileDocument.select("body").text()
                                val startOffset = document.text.indexOf(codeTagContent)
                                val endOffset = startOffset + codeTagContent.length
                                val quickFix = QuickFix()
                                file.findElementAt(endOffset)?.let {
                                    holder.registerProblem(
                                        it.parent,
                                        "Check the challenge here",
                                        quickFix
                                    )
                                }
                            }
                            lChallenge.name?.trim() == "Method Coverage" || lChallenge.name == "Class Coverage" -> {
                                val fileDocument = Jsoup.parse(lChallenge.snippet!!)
                                val codeTagContent = fileDocument.select("code").text()
                                val pattern: Pattern = Pattern.compile("(?<=\\bpublic\\s|private\\s|protected\\s)\\w+\\s+(\\w+)")
                                val matcher: Matcher = pattern.matcher(codeTagContent)

                                if (matcher.find()) {
                                    val methodName = matcher.group(1)
                                    val startOffset = file.text.indexOf(methodName)
                                    val quickFix = QuickFix()
                                    file.findElementAt(startOffset)?.let {
                                        holder.registerProblem(
                                            it,
                                            "Check the challenge here",
                                            quickFix
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

class QuickFix : LocalQuickFix {
    override fun getName(): String {
        return "Goto challenge"
    }

    override fun getFamilyName(): String {
        return name
    }

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val toolWindowManager = ToolWindowManager.getInstance(project)
        val myToolWindow: ToolWindow? = toolWindowManager.getToolWindow("Gamekins")
        myToolWindow?.show()
    }
}
