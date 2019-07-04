/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.payment.android.util

import org.threeten.bp.LocalDate

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
fun LocalDate.withLastDayOfMonth(): LocalDate {
    return LocalDate.of(year, month, month.length(isLeapYear))
}