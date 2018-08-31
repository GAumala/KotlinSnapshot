package com.karumi.androidconsumer

import com.karumi.kotlinsnapshot.matchWithSnapshot
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun addition_isWrong() {
        val asd = 5

        asd.matchWithSnapshot()
    }
}
