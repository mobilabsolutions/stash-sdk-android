package com.mobilabsolutions.payment.sample.data.resultentities

import androidx.room.Embedded
import androidx.room.Relation
import com.mobilabsolutions.payment.sample.data.entities.PaymentMethod
import com.mobilabsolutions.payment.sample.data.entities.User
import java.util.Objects

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 12-04-2019.
 */
class PaymentMethodWithUser {
    @Embedded
    var entry: PaymentMethod? = null

    @Relation(parentColumn = "product_id", entityColumn = "id")
    var relations: List<User> = emptyList()

    val user: User
        get() {
            assert(relations.size == 1)
            return relations[0]
        }

    override fun equals(other: Any?): Boolean = when {
        other === this -> true
        other is PaymentMethodWithUser -> entry == other.entry && relations == other.relations
        else -> false
    }

    override fun hashCode(): Int = Objects.hash(entry, relations)
}