/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.core.internal.api.backend

import com.mobilabsolutions.stash.core.internal.api.backend.model.UpdateAliasResponseDto
import com.mobilabsolutions.stash.core.internal.api.backend.model.VerifyAliasRequestDto
import com.mobilabsolutions.stash.core.internal.api.backend.v1.AliasResponse
import com.mobilabsolutions.stash.core.internal.api.backend.v1.AliasUpdateRequest
import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */

interface MobilabApi {
    @POST("v1/alias")
    fun createAlias(
        @Header("PSP-Type") psp: String,
        @Header("Idempotent-Key") idempotencyKey: String,
        @Body dynamicPspConfig: Map<String, String>
    ): Single<AliasResponse>

    @PUT("v1/alias/{aliasId}")
    fun updateAlias(
        @Path("aliasId") aliasId: String,
        @Body aliasUpdateRequest: AliasUpdateRequest
    ): Completable

    @POST("v1/alias")
    fun testcreateAlias(
        @Header("PSP-Type") psp: String,
        @Header("Idempotent-Key") idempotencyKey: String
    ): Single<AliasResponse>

    @PUT("v1/alias/{aliasId}")
    fun testupdateAlias(
        @Path("aliasId") aliasId: String,
        @Body aliasUpdateRequest: AliasUpdateRequest
    ): Single<UpdateAliasResponseDto>

    @POST("v1/alias/{aliasId}/verify")
    fun verify(
        @Path("aliasId") aliasId: String,
        @Body verifyAliasRequest: VerifyAliasRequestDto
    ): Single<UpdateAliasResponseDto>
}