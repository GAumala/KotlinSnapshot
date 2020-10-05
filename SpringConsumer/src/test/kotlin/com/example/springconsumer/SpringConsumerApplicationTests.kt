package com.example.springconsumer

import com.karumi.kotlinsnapshot.matchWithSnapshot
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class SpringConsumerApplicationTests {

    @Test
    fun contextLoads() {
        // This test intentionally forces the snapshots to be invalid
        // This is meant to test the update snapshot ability
        9001.matchWithSnapshot()
        9002.matchWithSnapshot()
    }

}
