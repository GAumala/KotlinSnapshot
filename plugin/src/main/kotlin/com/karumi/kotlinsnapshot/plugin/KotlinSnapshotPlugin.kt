package com.karumi.kotlinsnapshot.plugin

import com.karumi.kotlinsnapshot.plugin.task.KotlinSnapshotTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.DependencyResolutionListener
import org.gradle.api.artifacts.ResolvableDependencies

class KotlinSnapshotPlugin : Plugin<Project> {
    override fun apply(target: Project?) {
        target ?: return

        addTasks(target)
        addKotlinSnapshotDependency(target)
    }

    private fun addTasks(project: Project) {
        val updateSnapshotsTask = project.tasks.create(
            KotlinSnapshotTask.UpdateSnapshots.name,
            KotlinSnapshotTask.UpdateSnapshots::class.java)
        val purgeSnapshotsTask = project.tasks.create(
            KotlinSnapshotTask.PurgeSnapshots.name,
            KotlinSnapshotTask.PurgeSnapshots::class.java)

        updateSnapshotsTask.dependsOn("test")
        purgeSnapshotsTask.dependsOn("test")
    }

    private fun addKotlinSnapshotDependency(project: Project) {
        project.gradle.addListener(object : DependencyResolutionListener {
            override fun beforeResolve(dependencies: ResolvableDependencies?) {
                val dependency = project.dependencies
                    .create("com.karumi:kotlinsnapshot:${project.version}")
                project.dependencies.add("testImplementation", dependency)
                project.gradle.removeListener(this)
            }

            override fun afterResolve(dependencies: ResolvableDependencies?) {}
        })
    }
}
