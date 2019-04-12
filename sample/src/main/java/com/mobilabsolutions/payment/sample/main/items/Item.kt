package com.mobilabsolutions.payment.sample.main.items

import java.math.BigDecimal

/**
 * @author <a href="biju@mobilabsolutions.com">Biju Parvathy</a> on 10-04-2019.
 */

data class Item(
        var id: Int,
        var image: Int,
        var title: String,
        var desc: String,
        var price: BigDecimal
)