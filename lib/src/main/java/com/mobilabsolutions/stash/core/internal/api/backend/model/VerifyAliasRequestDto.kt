package com.mobilabsolutions.stash.core.internal.api.backend.model

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 13-08-2019.
 */
data class VerifyAliasRequestDto(
    val challengeResult: String? = null,
    val fingerprintResult: String? = null
)