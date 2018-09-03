package com.karumi.kotlinsnapshot.plugin

import org.gradle.api.Project

class GroovyHack {

    static void configureProjectForSnapshotUpdate(Project project) {
        def androidExtension = project.extensions.findByName("android")
        if (androidExtension != null) {
            androidExtension.testOptions.unitTests.all {
                systemProperty "updateSnapshots", "1"
            }
        } else {
            project.test {
                System.setProperty "updateSnapshots", "1"
            }
        }
    }

    static void configureProjectForSnapshotPurge(Project project) {
        def androidExtension = project.extensions.findByName("android")
        if (androidExtension != null) {
            androidExtension.testOptions.unitTests.all {
                systemProperty "purgeSnapshots", "1"
            }
        } else {
            project.test {
                System.setProperty "purgeSnapshots", "1"
            }
        }
    }
}
