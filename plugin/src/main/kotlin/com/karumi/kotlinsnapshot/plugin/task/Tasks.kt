package com.karumi.kotlinsnapshot.plugin.task

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

sealed class KotlinSnapshotTask : DefaultTask() {

    init {
        group = "KotlinSnapshot"
    }

    open class UpdateSnapshots : KotlinSnapshotTask() {
        companion object {
            const val name = "updateSnapshots"
        }

        init {
            description = "Run your tests updating all snapshots."
        }

        @TaskAction
        fun updateSnapshots() {
            System.setProperty("updateSnapshots", "1")
        }
    }

    open class PurgeSnapshots : KotlinSnapshotTask() {
        companion object {
            const val name = "purgeSnapshots"
        }

        init {
            description = "Delete all your snapshots."
        }

        @TaskAction
        fun purgeSnapshots() {
            System.setProperty("purgeSnapshots", "1")
        }
    }
}
