package com.simple.meditrack

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.simple.meditrack.entities.Alarm
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {


    val DATE = 24 * 60 * 60 * 100

    @Test
    fun useAppContext() {

        val inDate = 3

        val currentTime = System.currentTimeMillis()

        val dateEnd = currentTime + inDate * DATE


        val alarms = listOf(
            Alarm(
                id = "1",
                step = 2,
                createTime = currentTime
            ),
            Alarm(
                id = "2",
                step = 1,
                createTime = currentTime - DATE
            ),
            Alarm(
                id = "3",
                step = 3,
                createTime = currentTime - 2 * DATE
            ),
            Alarm(
                id = "4",
                step = 9,
                createTime = currentTime - 3 * DATE
            ),
            Alarm(
                id = "5",
                step = 5,
                createTime = currentTime - 1 * DATE
            )
        )

        alarms.filter {

            val countDate = (currentTime - it.createTime) / DATE

            val countStep = countDate / it.step + 1

            var count = 0
            while (dateEnd > it.createTime + (countStep + count) * it.step * DATE) {

                count++
            }

            count > 0
        }.let {

            Log.d("tuanha", "useAppContext: ${it.map { it.id }}")
        }

//
//         Context of the app under test.
//        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
//        assertEquals("com.simple.meditrack", appContext.packageName)
    }
}