# KotlinSnapshot

Snapshot testing in Kotlin. The implementation of the diff algorithm used is taken from [diff-match-patch](https://github.com/google/diff-match-patch).


## Install

Download with gradle from JitPack. First add the JitPack repository to your root `build.gradle` file: 

``` gradle
  allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
Then on your module's `build.gradle`:

``` gradle 
dependencies {
  testImplementation 'com.github.GAumala:KotlinSnapshot:0.1'
}

```

## Usage

Create an instance of `Camera` in your test file and use the method `matchWithSnapshot`, which takes 2 arguments: A string with the name of the snapshot and a `Any` object to be shot using its `toString()` implementation. Example:

``` kotlin
class NetworkTest {

    private val camera = Camera()
    private val networkClient = MyNetworkClient()

    @Test
    fun shouldFetchDataFromNetwork() {
        val myData = networkClient.fetchData()
        camera.matchWithSnapshot("should fetch data from network", myData)
    }
```

After you run the test for the first time, a new  snapshot will be written in the `__snapshot__` directory of the root of your project. On subsequent tests, the value will be compared with the snapshot stored in the filesystem if the are not equal, your test will fail. The written snapshot for this example would look like this:

```bash
$ cat __snapshot__/should\ fetch\ data\ from\ network.snap 
{"name":"gabriel","id":5}
```

You can also specify the path relative to the project's root where you want the snapshot dir to be placed using `Camera`'s constructor:


``` kotlin
    val camera = Camera(relativePath = "src/test/kotlin/com/my/package")
```

 

## Updating Snapshots


In order to update and purge snapshots from the command line, you should add a new system property called "updateSnapshots" to your `build.gradle` file:

``` gradle

test {
   // update snapshots via command line
   systemProperty "updateSnapshots", System.getProperty("u")
}
```

Then, when you want to update the snapshot for a specific test: 
``` bash
./gradlew test -Du=1 --tests *MyTest.someTestINeedToUpdate
```

In this example I use "u" but, it can be any flag, depending on how you configure your gradle file.


## Purging Snapshots

As you rename snapshots, old unused snapshots may remain in your project. You can delete all existing snapshots and rebuild the ones that are actually used using the "purgeSnapshots" system property. 

``` gradle

test {
   // purge snapshots via command line
   systemProperty "purgeSnapshots", System.getProperty("p")
}
```

Then, when you want to delete the snapshots: 
``` bash
./gradlew test -Dp=1 
```
