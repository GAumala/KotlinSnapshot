package com.karumi.kotlinsnapshot.core

import com.karumi.kotlinsnapshot.exceptions.TestNameNotFoundException
import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch
import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch.Operation.INSERT
import java.io.File
import java.nio.file.Paths

internal class Camera(
    private val serializationModule: SerializationModule,
    private val extractor: TestCaseExtractor,
    private val testClassAsDirectory: Boolean = false,
    private val relativePath: String = "",
    private val snapshottingProperties: SnapshottingProperties = SystemSnapshottingProperties(
        JavaSystem
    )
) {
    private val snapshotDir: File
    private val dmp = DiffMatchPatch()

    init {
        snapshotDir = createSnapshotDir(relativePath)
        purgeSnapshotsIfNeeded(snapshotDir, snapshottingProperties)
    }

    fun matchWithSnapshot(value: Any?, snapshotName: String? = null) {
        val snapshotTestCaseName = if (snapshotName != null)
            TestCaseName(null, snapshotName)
        else
            extractTestCaseName()
        val snapshotFile = getFile(snapshotTestCaseName)
        if (snapshotFile.exists())
            matchValueWithExistingSnapshot(snapshotFile, value)
        else
            handleMissingSnapshot(snapshotFile, value)
    }

    private fun handleMissingSnapshot(snapshotFile: File, value: Any?) {
        if (snapshottingProperties.shouldFailOnMissingSnapshots()) {
            val diffs = listOf(DiffMatchPatch.Diff(INSERT, serializationModule.serialize(value)))
            throw SnapshotException(diffs, "Snapshot file does not exist.", snapshotFile.name, "")
        } else {
            writeSnapshot(false, snapshotFile, value)
        }
    }

    private fun getFile(testCaseName: TestCaseName): File =
        if (testClassAsDirectory) {
            val testClassName = testCaseName.className
                ?: extractor.getTestStackElement()?.className ?: ""
            Paths.get(
                snapshotDir.absolutePath,
                testClassName,
                "${testCaseName.methodName}.snap"
            ).toFile().also {
                it.parentFile.mkdirs()
            }
        } else {
            File(snapshotDir, "$testCaseName.snap")
        }

    private fun differsFromSnapshot(diffs: List<DiffMatchPatch.Diff>): Boolean =
        diffs.find { diff -> diff.operation != DiffMatchPatch.Operation.EQUAL } != null

    private fun matchValueWithExistingSnapshot(snapshotFile: File, value: Any?) {
        val rawSnapshotContents = snapshotFile.readText()
        val snapshotContents = replaceWindowsLineEndings(rawSnapshotContents)
        val valueString = serializationModule.serialize(value)
        val diffs = dmp.diffMain(snapshotContents, valueString)
        val hasChanged = differsFromSnapshot(diffs)
        if (hasChanged && snapshottingProperties.shouldUpdateSnapshots())
            writeSnapshot(true, snapshotFile, value)
        else if (hasChanged) {
            val msg = DiffPrinter.toReadableConsoleMessage(snapshotFile.name, diffs)
            throw SnapshotException(diffs, msg, snapshotContents, valueString)
        }
    }

    private fun replaceWindowsLineEndings(string: String) = string.replace(Regex("\\r\\n"), "\n")

    private fun writeSnapshot(update: Boolean, snapshotFile: File, value: Any?) {
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

        fun purgeSnapshotsIfNeeded(
            snapshotDir: File,
            snapshottingProperties: SnapshottingProperties
        ) {
            val pathToPurge = snapshotDir.absolutePath
            val shouldPurge = snapshottingProperties.isSnapshotPurgingEnabled() &&
                !purgedDirectories.contains(pathToPurge)

            if (shouldPurge) {
                snapshotDir.deleteAllContainedFiles()
                purgedDirectories.add(pathToPurge)
            }
        }
    }

    private fun extractTestCaseName(): TestCaseName {
        val testCaseTrace = extractor.getTestStackElement()
        if (testCaseTrace != null) {
            return TestCaseName(testCaseTrace.className, testCaseTrace.methodName)
        } else {
            throw TestNameNotFoundException(
                "Kotlin Snapshot library couldn't find the name of the test. " +
                    "Review if the test case file or the spec file contains the word " +
                    "test or spec or specify a snapshot name manually, " +
                    "this is a requirement needed to use Kotlin Snapshot"
            )
        }
    }

    private data class TestCaseName(val className: String?, val methodName: String) {
        override fun toString(): String = if (className != null) {
            "${className}_$methodName"
        } else {
            methodName
        }
    }
}
