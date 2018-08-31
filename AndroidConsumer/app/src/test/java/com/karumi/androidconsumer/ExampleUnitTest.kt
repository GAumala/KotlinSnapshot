package com.karumi.androidconsumer

import com.karumi.kotlinsnapshot.matchWithSnapshot
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun addition_isWrong() {
        val asd = 13

        print(System.getProperties())
        asd.matchWithSnapshot()
    }
}
