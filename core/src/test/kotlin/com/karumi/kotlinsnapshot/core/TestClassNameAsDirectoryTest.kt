package com.karumi.kotlinsnapshot.core

import com.karumi.kotlinsnapshot.core.SnapshotFileReader.getSnapshotFile
import junit.framework.TestCase.assertTrue
import org.junit.Test

class TestClassNameAsDirectoryTest {

    private val snapWithExtractorSupport = getCamera(true)
    private val snapWithoutExtractorSupport =
        getCamera(true, TestCaseExtractorNotSupported)

    @Test
    fun `should create test class name folder with the method name snap file`() {
        val json = """{"name":"gabriel","id":5}"""
        snapWithExtractorSupport.matchWithSnapshot(json)

        val stackTrace = Thread.currentThread().stackTrace[1]
        val snapshotFile = getSnapshotFile(stackTrace.className, stackTrace.methodName)
        assertTrue(snapshotFile.exists())
    }

    @Test
    fun `should create test class name folder with a custom name snap file`() {
        val json = """{"name":"gabriel","id":5}"""
        val snapshotName = "this file should be inside test class name folder"

        snapWithExtractorSupport.matchWithSnapshot(json, snapshotName)

        val snapshotFile = getSnapshotFile(this::class.qualifiedName!!, snapshotName)
        assertTrue(snapshotFile.exists())
    }

    @Test
    fun `should not create the test class name folder if doesn't find test class name`() {
        val json = """{"name":"gabriel","id":5}"""
        val snapshotName = "this file should be without test class name folder"

        snapWithoutExtractorSupport.matchWithSnapshot(json, snapshotName)

        assertTrue(getSnapshotFile(snapshotName).exists())
    }

    private fun getCamera(
        testClassAsDirectory: Boolean,
        testCaseNameExtractor: TestCaseExtractor = TestCaseExtractor()
    ) = Camera(KotlinSerialization(), testCaseNameExtractor, testClassAsDirectory)
}
