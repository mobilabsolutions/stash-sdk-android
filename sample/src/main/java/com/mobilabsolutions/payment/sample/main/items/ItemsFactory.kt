package com.mobilabsolutions.payment.sample.main.items

import com.mobilabsolutions.payment.sample.R
import java.util.*


/**
 * A temporary class to supply items
 *
 * @author <a href="biju@mobilabsolutions.com">Biju Parvathy</a> on 10-04-2019.
 */

@Deprecated(
        message = "Will remove it when we connect it with respective API",
        level = DeprecationLevel.WARNING
)
class ItemsFactory {

    private val random = Random()

    private val items = arrayListOf<Item>(
            Item(R.mipmap.ic_launcher, "T Shirt", "Mobilab Print (Male)"),
            Item(R.mipmap.ic_launcher, "T Shirt", "Mobilab Print (Female)"),
            Item(-R.mipmap.ic_launcher, "Notebook", "Quadrille Pads"),
            Item(-R.mipmap.ic_launcher, "Sticker", "12 Sticker Pack"),
            Item(-R.mipmap.ic_launcher, "Pen", "Mobilab Pen (Blue)")
    )

    fun getItems(count: Int): List<Item> {
        val list = mutableListOf<Item>()
        repeat(count) {
            list.add(items[random.nextInt(4)])
        }
        return list
    }
}