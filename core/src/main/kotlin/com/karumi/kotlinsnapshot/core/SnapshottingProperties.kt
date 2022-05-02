package com.karumi.kotlinsnapshot.core

interface SnapshottingProperties {
    fun isSnapshotPurgingEnabled(): Boolean
    fun shouldUpdateSnapshots(): Boolean
    fun shouldFailOnMissingSnapshots(): Boolean
}
