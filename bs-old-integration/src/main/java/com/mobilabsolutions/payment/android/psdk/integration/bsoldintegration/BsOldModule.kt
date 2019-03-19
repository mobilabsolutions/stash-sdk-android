package com.mobilabsolutions.payment.android.psdk.integration.bsoldintegration

import com.mobilabsolutions.payment.android.psdk.integration.bsoldintegration.pspapi.OldBsPayoneApi
import com.mobilabsolutions.payment.android.psdk.internal.IntegrationScope
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import javax.inject.Named
import javax.inject.Singleton

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
@Module
class BsOldModule(private val bsOld: String) {

    @Provides
    @IntegrationScope
    @Named("bsOkHttpClient")
    fun provideBsOldHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        val bsPayoneOkHttpClient = OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .build()
        return bsPayoneOkHttpClient
    }

    @Provides
    @IntegrationScope
    fun provideBSOldApi(
            @Named("bsOkHttpClient") bsPayoneOkHttpClient: OkHttpClient,
            simpleXmlConverterFactory: SimpleXmlConverterFactory,
            rxJava2CallAdapterFactory: RxJava2CallAdapterFactory
    ): OldBsPayoneApi {


        val bsPayoneRetrofit = Retrofit.Builder()
                .addConverterFactory(simpleXmlConverterFactory)
                .addCallAdapterFactory(rxJava2CallAdapterFactory)
                .client(bsPayoneOkHttpClient)
                .baseUrl(bsOld)
                .build()

        return bsPayoneRetrofit.create(OldBsPayoneApi::class.java)
    }

}