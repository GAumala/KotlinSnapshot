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

    private val shouldUpdateSnapshots: Boolean by lazy {
        System.getProperty("updateSnapshots") == "1"
    }

    private fun printDiff(diff: diff_match_patch.Diff): String =
            when (diff.operation) {
                diff_match_patch.Operation.EQUAL, null -> " ${diff.text}\n"
                diff_match_patch.Operation.DELETE -> Term.green("- ${diff.text}\n")
                diff_match_patch.Operation.INSERT -> Term.red("+ ${diff.text}\n")
            }

    private fun createDiffMessage(snapshotName: String, diffs: LinkedList<diff_match_patch.Diff>): String {
        val sb = StringBuilder("${Term.red("Received value")} does not match ${
            Term.green("stored snapshot: \"$snapshotName\"")}\n\n${
            Term.green("-Snapshot")}\n${Term.red("+Received")}\n\n")

        dmp.diff_cleanupSemantic(diffs)
        diffs.forEach { diff -> sb.append(printDiff(diff)) }
        return sb.toString()
    }

    private fun differsFromSnapshot(diffs: List<diff_match_patch.Diff>): Boolean =
            diffs.find { diff -> diff.operation != diff_match_patch.Operation.EQUAL } != null

    private fun matchValueWithExistingSnapshot(snapshotFile: File, value: Any) {
        val snapshotContents = snapshotFile.readText()
        val valueString = value.toString()
        val diffs = dmp.diff_main(snapshotContents, valueString)
        val hasChanged = differsFromSnapshot(diffs)
        if (hasChanged && shouldUpdateSnapshots)
            writeSnapshot(true, snapshotFile, value)
        else if (hasChanged) {
            val msg = createDiffMessage(snapshotFile.name, diffs)
            throw SnapshotException(diffs, msg)
        }


    }

    private fun writeSnapshot(update: Boolean, snapshotFile: File, value: Any) {
        snapshotFile.writeText(value.toString())
        val fileName = "\"${snapshotFile.name}\""
        val msg = if (update) "${Term.green("1 snapshot, $fileName,")} updated."
                  else "${Term.green("1 snapshot, $fileName,")} written."
        println(msg)
    }

    fun matchWithSnapshot(snapshotName: String, value: Any) {
        val snapshotFile = File(snapshotDir, "$snapshotName.snap")
        if (snapshotFile.exists())
            matchValueWithExistingSnapshot(snapshotFile, value)
        else
            writeSnapshot(false, snapshotFile, value)
    }
}