package com.mobilabsolutions.payment.sample.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * @author <a href="yisuk@mobilabsolutions.com">yisuk</a>
 */
@Entity(
        tableName = "payment_method",
        indices = [
            Index(value = ["paymentMethod_id"], unique = true),
            Index(value = ["user_id"])
        ],
        foreignKeys = [
            ForeignKey(
                    entity = User::class,
                    parentColumns = arrayOf("server_user_id"),
                    childColumns = arrayOf("user_id"),
                    onDelete = ForeignKey.CASCADE,
                    onUpdate = ForeignKey.CASCADE
            )
        ]
)
data class PaymentMethod(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") override val id: Long = 0,
    @ColumnInfo(name = "user_id") val userId: String = "",
    @ColumnInfo(name = "paymentMethod_id") val paymentMethodId: String = "",
    @ColumnInfo(name = "alias_id") val aliasId: String = "",
    @ColumnInfo(name = "alias") val alias: String = "",
    @ColumnInfo(name = "type") val _type: String = "CC",
    @ColumnInfo(name = "description") val description: String = "Description"
) : SampleEntity {

    @delegate:Ignore
    val type by lazy(LazyThreadSafetyMode.NONE) {
        PaymentType.fromStringValue(_type)
    }
}