package com.karumi.kotlinsnapshot.core

internal class SystemSnapshottingProperties(private val system: SystemAdapter) :
    SnapshottingProperties {
    override fun isSnapshotPurgingEnabled(): Boolean = isConfigured("purgeSnapshots")

    override fun shouldUpdateSnapshots(): Boolean = isConfigured("updateSnapshots")

    override fun shouldFailOnMissingSnapshots(): Boolean = isConfigured("failOnMissingSnapshots")

    private fun isConfigured(configName: String) =
        system.getEnv(configName).isSetToTrueValue() ||
            system.getProperty(configName).isSetToTrueValue()

    private fun String?.isSetToTrueValue(): Boolean = this == "1" || this == "true"
}

internal interface SystemAdapter {
    fun getProperty(key: String): String?
    fun getEnv(key: String): String?
}

internal object JavaSystem : SystemAdapter {
    override fun getProperty(key: String): String? = System.getProperty(key)

    override fun getEnv(key: String): String? = System.getenv(key)
}
