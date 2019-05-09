package com.tddevelopment.loitr

import android.support.test.runner.AndroidJUnit4
import com.tddevelopment.loitr.model.Converters
import com.tddevelopment.loitr.model.endOfDay
import com.tddevelopment.loitr.model.startOfDay
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*
import java.util.*
import java.util.Calendar.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class TypeConvertersTest {
    @Test
    fun testConvertFromDateString() {
        // Context of the app under test.
        val dateString = "2018-04-13T20:00:00-05:00"
        val converter = Converters()
        val dateValue = converter.fromTimestamp(dateString)
        Calendar.getInstance()
    }

    @Test
    fun testConvertFromDateToString() {
        val cal = Calendar.getInstance()
        cal.set(2019, 2, 3, 5, 30, 23)
        val date = cal.time
        val converter = Converters()
        val convertedDateString = converter.dateToTimestamp(date)
        assertNotNull(convertedDateString)
        assertTrue(convertedDateString!!.contains("2019-03-03T05:30:23"))
    }

    @Test
    fun testGetStartOfDay() {
        val cal = Calendar.getInstance(TimeZone.getDefault())
        // Monday February 4th 2019
        cal.set(2019, 1, 5, 5, 30, 23)
        val monDate = cal.time
        val testCal = Calendar.getInstance(TimeZone.getDefault())
        testCal.time = monDate.startOfDay()
        val hour = testCal.get(HOUR_OF_DAY)
        val minute = testCal.get(MINUTE)
        val second = testCal.get(SECOND)
        assertEquals(hour, 0)
        assertEquals(minute, 0)
        assertEquals(second, 0)
    }
    @Test
    fun testGetEndOfDay() {
        val cal = Calendar.getInstance(TimeZone.getDefault())
        // Monday February 4th 2019
        cal.set(2019, 1, 5, 5, 30, 23)
        val monDate = cal.time
        val testCal = Calendar.getInstance(TimeZone.getDefault())
        testCal.time = monDate.endOfDay()
        val hour = testCal.get(HOUR_OF_DAY)
        val minute = testCal.get(MINUTE)
        val second = testCal.get(SECOND)
        assertEquals(23, hour)
        assertEquals(minute, 59)
        assertEquals(second, 59)
    }
}
