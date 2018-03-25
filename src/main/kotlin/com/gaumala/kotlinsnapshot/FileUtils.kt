package com.gaumala.kotlinsnapshot

import java.io.File

/**
 * Created by gabriel on 3/25/18.
 */
internal fun File.deleteAllContainedFiles() {
    this.listFiles().forEach { it.delete() }
}
