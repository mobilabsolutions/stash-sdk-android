package com.mobilabsolutions.payment.sample.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 12-04-2019.
 */
@Entity(
        tableName = "product"
)
data class Product(
        @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") override val id: Long = 0,
        @ColumnInfo val name: String? = null,
        @ColumnInfo val description: String? = null,
        @ColumnInfo val price: Int = 0
) : SampleEntity