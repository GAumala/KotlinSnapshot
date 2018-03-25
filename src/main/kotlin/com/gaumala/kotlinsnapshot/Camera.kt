package com.gaumala.kotlinsnapshot

import name.fraser.neil.plaintext.diff_match_patch
import java.io.File
import java.nio.file.Paths
import java.util.*

/**
 * Created by gabriel on 3/24/18.
 */

class Camera {
    private val snapshotDir: File
    private val dmp = diff_match_patch()
    init {
        val dir = System.getProperty("user.dir");
        val snapshotDirPath = Paths.get(dir, "__snapshot__").toString()
        snapshotDir = File(snapshotDirPath)
        snapshotDir.mkdirs()
    }
    private fun prefixFromOperation(operation: diff_match_patch.Operation): String =
            when (operation) {
                diff_match_patch.Operation.EQUAL -> " "
                diff_match_patch.Operation.DELETE -> "- "
                diff_match_patch.Operation.INSERT -> "+ "
            }

    private fun createDiffMessage(snapshotName: String, diffs: LinkedList<diff_match_patch.Diff>): String {
        val sb = StringBuilder("Received value does not match stored snapshot: \"$snapshotName\"\n\n-Snapshot\n+Received\n\n")
        dmp.diff_cleanupSemantic(diffs)
        diffs.forEach { diff ->
            val prefix = prefixFromOperation(diff.operation)
            sb.append("$prefix${diff.text}\n")
        }
        return sb.toString()
    }

    private fun differsFromSnapshot(diffs: List<diff_match_patch.Diff>): Boolean =
            diffs.find { diff -> diff.operation != diff_match_patch.Operation.EQUAL } != null

    private fun matchValueWithExistingSnapshot(snapshotFile: File, value: Any) {
        val snapshotContents = snapshotFile.readText()
        val valueString = value.toString()
        val diffs = dmp.diff_main(snapshotContents, valueString)
        if (differsFromSnapshot(diffs)) {
            val msg = createDiffMessage(snapshotFile.name, diffs)
            throw SnapshotException(diffs, msg)
        }
    }

    private fun writeSnapshot(snapshotFile: File, value: Any) {
        snapshotFile.writeText(value.toString())
    }

    fun matchWithSnapshot(snapshotName: String, value: Any) {
        val snapshotFile = File(snapshotDir, "$snapshotName.snap")
        if (snapshotFile.exists())
            matchValueWithExistingSnapshot(snapshotFile, value)
        else
            writeSnapshot(snapshotFile, value)
    }
}