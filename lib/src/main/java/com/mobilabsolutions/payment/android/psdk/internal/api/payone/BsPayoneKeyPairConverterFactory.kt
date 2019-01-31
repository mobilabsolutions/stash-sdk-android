package com.mobilabsolutions.payment.android.psdk.internal.api.payone

import com.google.gson.annotations.SerializedName
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.lang.reflect.Type
import java.util.*


/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class PayoneKeyPairConverterFactory private constructor(val requestConverter: PayoneKeyPairRequestConverter)
    : Converter.Factory() {

    companion object {
        val REQUEST_CONVERTER_INSTANCE : PayoneKeyPairRequestConverter = PayoneKeyPairRequestConverter()

        fun create() = PayoneKeyPairConverterFactory(REQUEST_CONVERTER_INSTANCE)
    }

    override fun requestBodyConverter(type: Type?, parameterAnnotations: Array<out Annotation>?, methodAnnotations: Array<out Annotation>?, retrofit: Retrofit?): Converter<*, RequestBody>? {
        return requestConverter
    }
}

/**
 * Here we use java reflection to get the field names and values from the object being converted.
 * We traverse the parents to collect all fields, and ignore transient and static fields.
 * We also apply SerialzedName annotation so we can keep some resemblance of standardization with
 * the rest of the library.
 */
class PayoneKeyPairRequestConverter : Converter<Any, RequestBody> {
    private val MEDIA_TYPE = MediaType.parse("application/x-www-form-urlencoded; charset=UTF-8")


    override fun convert(data : Any) : RequestBody {


        val clazz = data.javaClass
        val fields = LinkedList<Field>()
        var temporaryClass = clazz
        while (temporaryClass != Any::class.java) {
            fields.addAll(temporaryClass.declaredFields)
            temporaryClass = temporaryClass.superclass as Class<Any>
        }

        val request = fields.fold("", operation = {
            acc, field ->
            field.isAccessible = true
            if (!Modifier.isTransient(field.modifiers) && !Modifier.isStatic(field.modifiers)) {
                val memberName = if (field.getAnnotation(SerializedName::class.java) != null) {
                    field.getAnnotation(SerializedName::class.java).value
                } else {
                    field.name
                }
                acc + "${memberName}=${field.get(data)}&"
            } else acc
        }).removeSuffix("&")
        return RequestBody.create(MEDIA_TYPE, request)
    }

}