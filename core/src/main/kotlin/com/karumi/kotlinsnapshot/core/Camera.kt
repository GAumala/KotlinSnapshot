package com.karumi.kotlinsnapshot.core

import com.karumi.kotlinsnapshot.exceptions.TestNameNotFoundException
import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch
import java.io.File
import java.nio.file.Paths

internal class Camera<in A>(
    private val serializationModule: SerializationModule<A>,
    private val extractor: TestCaseExtractor,
    relativePath: String = ""
) {
    private val snapshotDir: File
    private val dmp = DiffMatchPatch()

    init {
        snapshotDir = createSnapshotDir(relativePath)
        purgeSnapshotsIfNeeded(snapshotDir)
    }

    fun matchWithSnapshot(value: A, snapshotName: String? = null) {
        val snapshotFileName = if (snapshotName != null)
            "$snapshotName.snap"
        else
            "${extractTestCaseName()}.snap"
        val snapshotFile = File(snapshotDir, snapshotFileName)
        if (snapshotFile.exists())
            matchValueWithExistingSnapshot(snapshotFile, value)
        else
            writeSnapshot(false, snapshotFile, value)
    }

    private val shouldUpdateSnapshots: Boolean by lazy {
        System.getProperty("updateSnapshots") == "1"
    }

    private fun differsFromSnapshot(diffs: List<DiffMatchPatch.Diff>): Boolean =
        diffs.find { diff -> diff.operation != DiffMatchPatch.Operation.EQUAL } != null

    private fun matchValueWithExistingSnapshot(snapshotFile: File, value: A) {
        val snapshotContents = snapshotFile.readText()
        val valueString = serializationModule.serialize(value)
        val diffs = dmp.diffMain(snapshotContents, valueString)
        val hasChanged = differsFromSnapshot(diffs)
        if (hasChanged && shouldUpdateSnapshots)
            writeSnapshot(true, snapshotFile, value)
        else if (hasChanged) {
            val msg = DiffPrinter.toReadableConsoleMessage(snapshotFile.name, diffs)
            throw SnapshotException(diffs, msg)
        }
    }

    private fun writeSnapshot(update: Boolean, snapshotFile: File, value: A) {
        val serializedValue = serializationModule.serialize(value)
        snapshotFile.writeText(serializedValue)
        val fileName = "\"${snapshotFile.name}\""
        val msg = if (update) "${Term.green("1 snapshot, $fileName,")} updated."
        else "${Term.green("1 snapshot, $fileName,")} written."
        println(msg)
    }

    private companion object {
        private val purgedDirectories = HashSet<String>()
        fun createSnapshotDir(relativePath: String): File {
            val dir = System.getProperty("user.dir")
            val snapshotDirPath = Paths.get(dir, relativePath, "__snapshot__").toString()
            val snapshotDir = File(snapshotDirPath)
            snapshotDir.mkdirs()
            return snapshotDir
        }

        fun purgeSnapshotsIfNeeded(snapshotDir: File) {
            val pathToPurge = snapshotDir.absolutePath
            val shouldPurge = System.getProperty("purgeSnapshots") == "1" &&
                !purgedDirectories.contains(pathToPurge)

            if (shouldPurge) {
                snapshotDir.deleteAllContainedFiles()
                purgedDirectories.add(pathToPurge)
            }
        }
    }

    private fun extractTestCaseName(): String {
        val testCaseTrace = extractor.getTestStackElement()
        if (testCaseTrace != null) {
            return "${testCaseTrace.className}_${testCaseTrace.methodName}"
        } else {
            throw TestNameNotFoundException("Kotlin Snapshot library couldn't find the name " +
                "of the test. Review if the test case file or the spec file contains the word " +
                "test or spec or specify a snapshot name manually, this is a requirement needed " +
                "to use Kotlin Snapshot")
        }
    }
}
