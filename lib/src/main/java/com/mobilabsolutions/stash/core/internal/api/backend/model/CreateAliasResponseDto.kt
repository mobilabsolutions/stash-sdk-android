package com.mobilabsolutions.stash.core.internal.api.backend.model

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 08-08-2019.
 */
data class CreateAliasResponseDto(
    val aliasId: String,
    val psp: PspDto
)