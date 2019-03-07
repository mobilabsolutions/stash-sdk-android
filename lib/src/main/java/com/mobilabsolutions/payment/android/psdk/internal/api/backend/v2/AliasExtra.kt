package com.mobilabsolutions.payment.android.psdk.internal.api.backend.v2

data class AliasExtra(
        val ccExpiry : String,
        val ccMask : String,
        val ccType : String,
        val email : String,
        val ibanMaskval : String
)