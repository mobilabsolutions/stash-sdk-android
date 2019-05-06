package com.mobilabsolutions.payment.android.psdk.internal

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type

class ThrowableSerializer : JsonSerializer<Throwable> {

    private val jsonParser = JsonParser()

    var gson = Gson()

    override fun serialize(
        throwable: Throwable?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        val jsonElement = gson.toJsonTree(throwable, Throwable::class.java)
        fillStackTrace(jsonElement, throwable)
        return jsonElement
    }

    private fun fillStackTrace(jsonElement: JsonElement?, throwable: Throwable?) {
        jsonElement?.let {
            throwable?.let {
                val jsonObject = jsonElement.asJsonObject
                jsonObject.add("stackTrace",
                    jsonParser.parse(gson.toJson(it.stackTrace)).asJsonArray)

                // Fill the stackTrace of cause as well
                fillStackTrace(jsonObject.get("cause"), it.cause)
            }
        }
    }
}
