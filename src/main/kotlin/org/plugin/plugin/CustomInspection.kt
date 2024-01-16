package org.plugin.plugin

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowManager
import org.jsoup.Jsoup

import com.intellij.psi.*

import java.util.regex.Matcher
import java.util.regex.Pattern

class CustomInspection : LocalInspectionTool() {
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : PsiElementVisitor() {
            override fun visitFile(file: PsiFile) {
                super.visitFile(file)
                val lChallengeList = Utility.getCurrentChallenges()?.filter { it.name != "Test" && it.name != "Build" }
                    ?: return

                for (lChallenge in lChallengeList) {
                    val document = PsiDocumentManager.getInstance(file.project).getDocument(file)
                    val fileNameWithExtension = lChallenge.details.fileName + "." + lChallenge.details.fileExtension

                    if (document != null && fileNameWithExtension == file.name) {
                        val description = when(lChallenge.name) {
                            "Mutation" -> "Write a test to kill the mutant here"
                            "Code Smell" -> "Improve your code by removing the smell here"
                            "Line Coverage" -> "Write a test to cover the line here"
                            "Method Coverage" -> "Write a test to cover more lines in this method"
                            "Class Coverage" -> "Write a test to cover more lines in this class"
                            "Branch Coverage" -> "Write a test to cover more branches in this line"
                            else -> "Check the challenge here"
                        }
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
                                    val quickFix = QuickFix()
                                    var element = file.findElementAt(lineStartOffset)
                                    var elementToHighlight = file.findElementAt(lineStartOffset)
                                    while (element != null) {
                                        if (element!!.text.length == lineToHighlight.length) {
                                            elementToHighlight = element
                                            element = null
                                        }
                                        if (element != null && element!!.parent != null && element!!.parent.text.length > lineToHighlight.length) {
                                            elementToHighlight = element
                                            element = null
                                        }
                                        if (element != null) {
                                            element = element!!.parent
                                        }
                                    }
                                    elementToHighlight?.let {
                                        holder.registerProblem(
                                            it,
                                            description,
                                            quickFix
                                        )
                                    }
                                }
                            }
                            lChallenge.name?.trim() == "Line Coverage" || lChallenge.name == "Branch Coverage" -> {
                                val fileDocument = Jsoup.parse(lChallenge.toolTipText!!.substringAfter("Line content:"))
                                val codeTagContent = fileDocument.select("body").text()
                                val startOffset = document.text.indexOf(codeTagContent)
                                val quickFix = QuickFix()
                                var element = file.findElementAt(startOffset)
                                var elementToHighlight = file.findElementAt(startOffset)
                                while (element != null) {
                                    if (element!!.text.length == codeTagContent.length) {
                                        elementToHighlight = element
                                        element = null
                                    }
                                    if (element != null && element!!.parent != null && element!!.parent.text.length > codeTagContent.length) {
                                        elementToHighlight = element
                                        element = null
                                    }
                                    if (element != null) {
                                        element = element!!.parent
                                    }
                                }
                                elementToHighlight?.let {
                                    holder.registerProblem(
                                        it,
                                        description,
                                        quickFix
                                    )
                                }
                            }
                            lChallenge.name?.trim() == "Method Coverage" || lChallenge.name == "Class Coverage" -> {
                                val fileDocument = Jsoup.parse(lChallenge.snippet!!)
                                val codeTagContent = fileDocument.select("code").text()
                                val pattern =
                                    if (lChallenge.name == "Class Coverage") Pattern.compile("(public\\s)?(class|interface|enum)\\s([^\\n\\s]*)")
                                    else Pattern.compile("(public|protected|private|static|\\s)( static)? +[\\w<>\\[\\]]+\\s+(\\w+) *\\([^)]*\\) *(\\{?|[^;])")
                                val matcher: Matcher = pattern.matcher(codeTagContent)

                                if (matcher.find()) {
                                    val methodName = matcher.group(3)
                                    val startOffset = file.text.indexOf(methodName)
                                    val quickFix = QuickFix()
                                    file.findElementAt(startOffset)?.let {
                                        holder.registerProblem(
                                            it,
                                            description,
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

    class QuickFix : LocalQuickFix {
        override fun getName(): String {
            return "Go to challenge"
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
}
