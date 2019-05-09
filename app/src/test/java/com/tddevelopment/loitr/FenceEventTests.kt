package com.tddevelopment.loitr

import com.tddevelopment.loitr.model.FenceEvent
import org.junit.Assert
import org.junit.Test

import org.junit.Assert.*

class FenceEventTests {
    @Test
    fun testInstantiationFromRawString() {
        val enter = "enter"
        val exit = "exit"
        val invalid = "invalid"
        val anotherInvalid = "another-invalid"

        val eventEnter = FenceEvent.EventType.fromRawValue(enter)
        val eventExit = FenceEvent.EventType.fromRawValue(exit)
        val eventInvalid = FenceEvent.EventType.fromRawValue(invalid)
        val eventAnotherInvalid = FenceEvent.EventType.fromRawValue(anotherInvalid)

        assertEquals(eventEnter, FenceEvent.EventType.ENTERED)
        assertEquals(eventExit, FenceEvent.EventType.EXITED)
        assertEquals(eventInvalid, FenceEvent.EventType.INVALID)
        assertEquals(eventAnotherInvalid, FenceEvent.EventType.INVALID)

        assertEquals(eventEnter.toString(), "enter")
        assertEquals(eventExit.toString(), "exit")
        assertEquals(eventInvalid.toString(), "invalid")
        assertEquals(eventAnotherInvalid.toString(), "invalid")
    }
}