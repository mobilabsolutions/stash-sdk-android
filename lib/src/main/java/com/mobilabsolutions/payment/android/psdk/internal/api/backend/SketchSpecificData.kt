package com.mobilabsolutions.payment.android.psdk.internal.api.backend

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
data class SketchSpecificData(override val psp : String, val somethingElse : String) : ProviderSpecificData()