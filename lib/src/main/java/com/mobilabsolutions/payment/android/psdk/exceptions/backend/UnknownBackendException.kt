package com.mobilabsolutions.payment.android.psdk.exceptions.backend

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class UnknownBackendException(message : String, val providerMessage : String) : RuntimeException(message)