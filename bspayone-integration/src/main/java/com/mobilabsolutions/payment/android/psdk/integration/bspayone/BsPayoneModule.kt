package com.mobilabsolutions.payment.android.psdk.integration.bspayone

import com.google.gson.GsonBuilder
import com.mobilabsolutions.payment.android.psdk.internal.api.backend.RuntimeTypeAdapterFactory
import com.mobilabsolutions.payment.android.psdk.internal.api.payone.*
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.threeten.bp.LocalDate
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
@Module
class BsPayoneModule(private val newBsPayoneUrl : String) {

    @Provides
    @Singleton
    fun provideNewBsPayoneConverterFactory() : PayoneKeyPairConverterFactory {
        return PayoneKeyPairConverterFactory.create()
    }


    @Provides
    @Singleton
    @Named("newBsPayoneOkHttpClient")
    fun provideBsPayoneHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        val bsPayoneOkHttpClient = OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .build()
        return bsPayoneOkHttpClient
    }

    @Provides
    @Singleton
    fun provideBSPayoneApi(
            @Named("newBsPayoneOkHttpClient") bsPayoneOkHttpClient: OkHttpClient,
            payoneKeyPairConverterFactory: PayoneKeyPairConverterFactory,
            @Named("bsPayoneGsonConverterFactory") gsonConverterFactory: GsonConverterFactory,
            rxJava2CallAdapterFactory: RxJava2CallAdapterFactory
    ): BsPayoneApi {


        val bsPayoneRetrofit = Retrofit.Builder()
                .addConverterFactory(payoneKeyPairConverterFactory)
                .addConverterFactory(gsonConverterFactory)
                .addCallAdapterFactory(rxJava2CallAdapterFactory)
                .client(bsPayoneOkHttpClient)
                .baseUrl(newBsPayoneUrl)
                .build()

        return bsPayoneRetrofit.create(BsPayoneApi::class.java)
    }

    @Named("bsPayoneGsonConverterFactory")
    @Provides
    @Singleton
    fun provideBackendGsonConverterFactory(): GsonConverterFactory {
        val runtimeTypeAdapterFactory = RuntimeTypeAdapterFactory
                .of(BsPayoneVerificationBaseResponse::class.java, "status")
                .registerSubtype(BsPayoneVerificationSuccessResponse::class.java, "VALID")
                .registerSubtype(BsPayoneVerificationErrorResponse::class.java, "ERROR")
                .registerSubtype(BsPayoneVerificationInvalidResponse::class.java, "INVALID")

        val gson = GsonBuilder()
                .registerTypeAdapterFactory(runtimeTypeAdapterFactory)
                .registerTypeAdapter(LocalDate::class.java, BsPayoneExpiryDateConverter())
                .create()
        return GsonConverterFactory.create(gson)
    }

}