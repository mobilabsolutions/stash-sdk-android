package com.mobilabsolutions.payment.android.psdk.exceptions.base

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.mobilabsolutions.payment.android.psdk.internal.ThrowableSerializer
import org.junit.Assert.assertTrue
import org.junit.Test

class PaymentExceptionTest {
    companion object {
        val gson: Gson = GsonBuilder()
            .registerTypeHierarchyAdapter(Throwable::class.java, ThrowableSerializer())
            .create()
    }

    @Test
    fun testSerializingAnException() {
        val anException = RuntimeException("Just an Exception")

        // Prepare
        val str = gson.toJson(anException)
        val throwable = gson.fromJson(str, Throwable::class.java)

        // Test
        assertTrue(throwable.message!!.isNotBlank())
    }

    @Test
    fun testSerializingARealException() {
        try {
            val a = 10 / 0
        } catch (e: Exception) {
            // Prepare
            val str = gson.toJson(e)
            val throwable = gson.fromJson(str, Throwable::class.java)

            // Test
            assertTrue(throwable.stackTrace.isNotEmpty())
        }
    }

    @Test
    fun testSerializingARealExceptionWithCause() {
        try {
            val a = 10 / 0
        } catch (e: Exception) {
            // Prepare
            e.initCause(Exception("Noting"))
            val str = gson.toJson(e)
            val throwable = gson.fromJson(str, Throwable::class.java)

            // Test
            assertTrue(throwable.stackTrace.isNotEmpty())
            assertTrue(throwable.cause!!.stackTrace.isNotEmpty())
        }
    }

    @Test
    fun testSerializingARealExceptionWithNestedCauses() {
        try {
            val a = 10 / 0
        } catch (e: Exception) {
            // Prepare
            e.initCause(Exception("One"))
            var t = e.cause
            t?.initCause(Exception("Two"))
            t = t?.cause
            t?.initCause(Exception("Three"))
            t = t?.cause
            t?.initCause(Exception("Four"))
            t = t?.cause
            t?.initCause(Exception("Five"))
            t = t?.cause
            t?.initCause(Exception("Six"))
            t = t?.cause
            t?.initCause(Exception("Seven"))
            val str = gson.toJson(e)
            val throwable = gson.fromJson(str, Throwable::class.java)

            // Test
            assertTrue(throwable.stackTrace.isNotEmpty())
            assertTrue(throwable.cause!!.stackTrace.isNotEmpty())
            assertTrue(throwable.cause!!.cause!!.stackTrace.isNotEmpty())
            assertTrue(throwable.cause!!.cause!!.cause!!.stackTrace.isNotEmpty())
            assertTrue(throwable.cause!!.cause!!.cause!!.cause!!.stackTrace.isNotEmpty())
            assertTrue(throwable.cause!!.cause!!.cause!!.cause!!.cause!!.stackTrace.isNotEmpty())
            assertTrue(throwable.cause!!.cause!!.cause!!.cause!!.cause!!.cause!!.stackTrace.isNotEmpty())
            assertTrue(throwable.cause!!.cause!!.cause!!.cause!!.cause!!.cause!!.cause!!.stackTrace.isNotEmpty())
        }
    }

    @Test
    fun testSerializingOtherExceptionWithCause() {

        val e = OtherException()

        // Prepare
        e.initCause(Exception("Noting"))
        // Prepare
        val str = gson.toJson(e)
        val throwable = gson.fromJson(str, Throwable::class.java)

        // Test
        assertTrue(throwable.stackTrace.isNotEmpty())
    }
}