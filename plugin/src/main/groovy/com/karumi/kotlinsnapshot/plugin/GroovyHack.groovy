package com.karumi.kotlinsnapshot.plugin

import org.gradle.api.Project

class GroovyHack {

    static void configureProjectForSnapshotUpdate(Project project) {
        project.extensions.getByName("android").testOptions.unitTests.all {
            systemProperty "updateSnapshots", "1"
        }
        project.test {
            systemProperty "updateSnapshots", "1"
        }
    }

    static void configureProjectForSnapshotPurge(Project project) {
        project.extensions.getByName("android").testOptions.unitTests.all {
            systemProperty "purgeSnapshots", "1"
        }
        project.test {
            systemProperty "purgeSnapshots", "1"
        }
    }
}
