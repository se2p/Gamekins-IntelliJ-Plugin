package org.plugin.plugin

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.ui.switcher.QuickActionProvider
import javax.swing.SwingUtilities

class Action : AnAction() {
    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }

    override fun update(e: AnActionEvent) {
        val quickActionProvider = e.getData(QuickActionProvider.KEY)
        if (quickActionProvider == null) {
            e.presentation.isEnabled = false
            return
        }
        val actions = quickActionProvider.getActions(true)
        e.presentation.isEnabled = !actions.isEmpty()
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project
        if (project != null) {
            SwingUtilities.invokeLater { AuthenticationDialog(project) }
        }
    }
}
