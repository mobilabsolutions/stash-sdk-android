package com.mobilabsolutions.payment.sample.data.entities


import android.content.res.Resources
import android.graphics.drawable.Drawable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.NumberFormat
import java.util.*

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 12-04-2019.
 */
@Entity(
        tableName = "product"
)
data class Product(
        @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") override val id: Long = 0,
        @ColumnInfo val name: String? = null,
        @ColumnInfo val image: Int = 0,
        @ColumnInfo val description: String? = null,
        @ColumnInfo val price: Int = 0
) : SampleEntity {
}