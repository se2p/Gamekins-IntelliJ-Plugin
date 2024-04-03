package org.gamekins.ide

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity

internal class ProjectActivity: ProjectActivity {

    override suspend fun execute(project: Project) {
        project.service<ProjectService>()
    }
}