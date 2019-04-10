package com.mobilabsolutions.payment.sample.main.items

import java.math.BigDecimal

/**
 * @author <a href="biju@mobilabsolutions.com">Biju Parvathy</a> on 10-04-2019.
 */

class Item(
        var image: Int = 0,
        var title: String = "",
        var desc: String = "",
        var price: BigDecimal = BigDecimal.ZERO
)