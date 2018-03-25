package com.gaumala.kotlinsnapshot

import org.junit.Test

class CameraTest {

    data class User(val id: Int, val name: String)

    @Test
    fun should_take_snapshots_of_data_classes() {
        val camera = Camera()

        val u1 = User(8, "gabriel")
        camera.matchWithSnapshot("should take snapshot of a data class", u1)
    }

    @Test
    fun should_take_snapshots_of_json_strings() {
        val camera = Camera()

        val json = """{"name":"gabriel","id":5}"""
        camera.matchWithSnapshot("should take snapshot of a json string", json)
    }
}