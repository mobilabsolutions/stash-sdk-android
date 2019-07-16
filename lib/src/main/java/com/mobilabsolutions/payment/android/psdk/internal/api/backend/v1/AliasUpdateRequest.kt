/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.payment.android.psdk.internal.api.backend.v1

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
data class AliasUpdateRequest(
    val pspAlias: String? = null,
    val extra: AliasExtra? = null

)