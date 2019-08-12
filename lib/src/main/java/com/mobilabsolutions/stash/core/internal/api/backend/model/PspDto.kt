package com.mobilabsolutions.stash.core.internal.api.backend.model

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 08-08-2019.
 */
data class PspDto(
    val type: String,
    val merchantId: String,
    val mode: String,
    val portalId: String,
    val request: String,
    val apiVersion: String,
    val responseType: String,
    val encoding: String,
    val hash: String,
    val accountId: String,
    val publicKey: String,
    val privateKey: String,
    val clientToken: String,
    val clientEncryptionKey: String
)