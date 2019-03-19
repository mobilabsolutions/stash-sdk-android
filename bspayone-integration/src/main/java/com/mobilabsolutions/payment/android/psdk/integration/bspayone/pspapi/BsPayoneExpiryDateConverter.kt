package com.mobilabsolutions.payment.android.psdk.integration.bspayone.pspapi

import com.google.gson.*
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import java.lang.reflect.Type

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class BsPayoneExpiryDateConverter : JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {
    private val FORMATTER = DateTimeFormatter.ofPattern("yyMMdd")

    override fun serialize(src: LocalDate, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return JsonPrimitive(FORMATTER.format(src))
    }

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): LocalDate {
        return FORMATTER.parse(json.getAsString() + "01", LocalDate.FROM)
    }
}