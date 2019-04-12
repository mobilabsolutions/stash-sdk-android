package com.mobilabsolutions.payment.sample.data.entities

import androidx.room.*

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 12-04-2019.
 */
@Entity(
        tableName = "cart",
        indices = [
            Index(value = ["product_id"], unique = true)
        ],
        foreignKeys = [
            ForeignKey(
                    entity = Product::class,
                    parentColumns = arrayOf("id"),
                    childColumns = arrayOf("product_id"),
                    onUpdate = ForeignKey.CASCADE,
                    onDelete = ForeignKey.CASCADE
            )
        ]
)
data class Cart(
        @PrimaryKey(autoGenerate = true) @ColumnInfo override val id: Long = 0,
        @ColumnInfo(name = "product_id") val productId: Long,
        @ColumnInfo val quantity: Int = 0
) : SampleEntity