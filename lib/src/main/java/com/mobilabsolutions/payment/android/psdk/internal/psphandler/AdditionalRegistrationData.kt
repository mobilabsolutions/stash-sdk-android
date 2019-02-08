package com.mobilabsolutions.payment.android.psdk.internal.psphandler

/**
 * First go at modularizing, this is an attempt to future-proof cases where PSPs
 * have uncommon or hard to standardize requirements, or a PSP specific feature is needed
 *
 * The concrete class instances should be created by SDK through integration apis
 *
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
interface AdditionalRegistrationData{
    /**
     * Some PSPs might require data that we cannot foresee or standardise.
     * Such that should be contained in a map, that particular integration knows how to use.
     */
    fun getAdditionalData() : Map<String, String>
}