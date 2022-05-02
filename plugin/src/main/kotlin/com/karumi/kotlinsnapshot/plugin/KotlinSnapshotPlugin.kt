package com.karumi.kotlinsnapshot.plugin

import com.karumi.kotlinsnapshot.plugin.task.KotlinSnapshotTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.DependencyResolutionListener
import org.gradle.api.artifacts.ResolvableDependencies

open class KotlinSnapshotPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        addKotlinSnapshotDependency(target)
        target.afterEvaluate { addTasks(target) }
    }

    private fun addTasks(project: Project) {
        val updateSnapshotsTask = project.tasks.create(
            KotlinSnapshotTask.UpdateSnapshots.name,
            KotlinSnapshotTask.UpdateSnapshots::class.java)
        val purgeSnapshotsTask = project.tasks.create(
            KotlinSnapshotTask.PurgeSnapshots.name,
            KotlinSnapshotTask.PurgeSnapshots::class.java)

        val testTask = project.tasks.findByName("test")
        updateSnapshotsTask.finalizedBy(testTask)
        purgeSnapshotsTask.finalizedBy(testTask)
    }

    private fun addKotlinSnapshotDependency(project: Project) {
        project.gradle.addListener(object : DependencyResolutionListener {
            override fun beforeResolve(dependencies: ResolvableDependencies) {
                val dependency = project.dependencies
                    .create("com.karumi.kotlinsnapshot:core:2.2.4-SNAPSHOT")
                project.dependencies.add("testImplementation", dependency)
                project.gradle.removeListener(this)
            }

            override fun afterResolve(dependencies: ResolvableDependencies) {}
        })
    }
}
