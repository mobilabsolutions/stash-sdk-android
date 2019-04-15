package com.mobilabsolutions.payment.sample.util;

import android.text.TextUtils;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 15-04-2019.
 */
public final class JavaUtils {

    private JavaUtils() {
    }

    private static String sCurrency = Currency.getInstance(Locale.GERMANY).getCurrencyCode();

    public static void setCurrency(String currency) {
        sCurrency = currency;
    }

    private static String getCurrency() {
        return sCurrency != null ? sCurrency : "EUR";
    }

    public static String formatCurrencyFromCents(int amountCents) {
        return formatCurrencyFromCents(String.valueOf(amountCents));
    }

    public static String formatCurrencyFromCents(String amountCents) {
        return getLocaleFormat(getCurrency()).format(formatAmount(amountCents));
    }

    private static double formatAmount(String amountCents) {
        BigDecimal amount;
        if (!TextUtils.isEmpty(amountCents) && TextUtils.isDigitsOnly(amountCents)) {
            amount = new BigDecimal(amountCents);
            amount = amount.movePointLeft(2);
        } else {
            amount = BigDecimal.ZERO;
        }
        return amount.doubleValue();
    }

    private static NumberFormat getLocaleFormat(String currency) {
        NumberFormat format = NumberFormat.getCurrencyInstance();
        format.setCurrency(Currency.getInstance(currency));
        return format;
    }
}

