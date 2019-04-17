package com.mobilabsolutions.payment.sample.data.entities

import androidx.room.*

/**
 * @author <a href="yisuk@mobilabsolutions.com">yisuk</a>
 */
@Entity(
        tableName = "payment_method",
        indices = [
            Index(value = ["payment_method_id"], unique = true)
        ]
)
data class PaymentMethod(
        @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") override val id: Long = 0,
        @ColumnInfo(name = "payment_method_id") val paymentMethodId: String = "",
        @ColumnInfo(name = "alias") val alias: String = "",
        @ColumnInfo(name = "type") val _type: String = "credit_card"
) : SampleEntity {

    @delegate:Ignore
    val type by lazy(LazyThreadSafetyMode.NONE) {
        PaymentType.fromVapianoValue(_type)
    }
}