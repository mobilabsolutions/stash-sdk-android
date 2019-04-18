package com.mobilabsolutions.payment.sample.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey

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