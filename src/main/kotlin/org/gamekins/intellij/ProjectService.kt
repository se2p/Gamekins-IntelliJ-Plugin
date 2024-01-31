package org.gamekins.intellij

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project

@Service(Service.Level.PROJECT)
class ProjectService(project: Project) : Disposable {

    init {
        Utility.startWebSocket()
        Utility.setCurrentProject(project)
    }

    override fun dispose() = Unit
}