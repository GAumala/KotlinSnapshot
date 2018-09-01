import com.karumi.kotlinsnapshot.core.Developer
import com.karumi.kotlinsnapshot.matchWithSnapshot
import org.junit.Test

class InferenceNameTestWithoutPackage {

    @Test
    fun `the snap test name will be inferred even the test class doesn't have package`() {
        val davide = Developer("Davide", 4)
        davide.matchWithSnapshot()
    }
}
