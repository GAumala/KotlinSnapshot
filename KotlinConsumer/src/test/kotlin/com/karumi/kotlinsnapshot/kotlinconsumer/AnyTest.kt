package com.karumi.kotlinsnapshot.kotlinconsumer

import com.karumi.kotlinsnapshot.matchWithSnapshot
import org.junit.Test

class AnyTest {

    @Test
    fun anyTest() {
        14.matchWithSnapshot()
    }
}
