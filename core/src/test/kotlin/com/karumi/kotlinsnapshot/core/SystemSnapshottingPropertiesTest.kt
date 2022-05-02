package com.karumi.kotlinsnapshot.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvFileSource

class SystemSnapshottingPropertiesTest {
    @Nested
    inner class PurgeSnapshots :
        TestCases("purgeSnapshots", SnapshottingProperties::isSnapshotPurgingEnabled)

    @Nested
    inner class UpdateSnapshots :
        TestCases("updateSnapshots", SnapshottingProperties::shouldUpdateSnapshots)

    @Nested
    inner class FailOnMissingSnapshots :
        TestCases("failOnMissingSnapshots", SnapshottingProperties::shouldFailOnMissingSnapshots)

    abstract inner class TestCases(
        private val name: String,
        private val evaluate: SnapshottingProperties.() -> Boolean
    ) {

        @ParameterizedTest
        @CsvFileSource(resources = [testcaseFile], numLinesToSkip = 1)
        fun shouldEvaluatePropertyCorrectly(input: String, expectedOutcome: Boolean) {
            assertEquals(
                expectedOutcome,
                snapshottingProperties(properties = mapOf(name to input)).evaluate(),
                "Property '$name' with input '$input'"
            )
        }

        @ParameterizedTest
        @CsvFileSource(resources = [testcaseFile], numLinesToSkip = 1)
        fun shouldEvaluateEnvironmentCorrectly(input: String, expectedOutcome: Boolean) {
            assertEquals(
                expectedOutcome,
                snapshottingProperties(environment = mapOf(name to input)).evaluate(),
                "Environment variable '$name' with value '$input'"
            )
        }

        @Test
        fun shouldReturnFalse_ifNothingIsConfigured() {
            assertFalse(snapshottingProperties().evaluate())
        }
    }

    private fun snapshottingProperties(
        properties: Map<String, String> = emptyMap(),
        environment: Map<String, String> = emptyMap()
    ) = SystemSnapshottingProperties(TestSystem(properties, environment))

    private class TestSystem(
        private val properties: Map<String, String>,
        private val environment: Map<String, String>
    ) : SystemAdapter {
        override fun getProperty(key: String): String? = properties[key]

        override fun getEnv(key: String): String? = environment[key]
    }

    private companion object {
        const val testcaseFile = "/system_settings_property_testcases.csv"
    }
}
