package com.mobilabsolutions.payment.android.psdk.internal.psphandler.oldbspayone

import com.mobilabsolutions.payment.android.psdk.internal.api.oldbspayone.OldBsPayoneApi
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
class OldBsPayoneModule(private val bsPayoneUrl: String) {

    @Provides
    @Singleton
    @Named("bsOkHttpClient")
    fun provideBsPayoneHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        val bsPayoneOkHttpClient = OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .build()
        return bsPayoneOkHttpClient
    }

    @Provides
    @Singleton
    fun provideBSPayoneApi(
            @Named("bsOkHttpClient") bsPayoneOkHttpClient: OkHttpClient,
            simpleXmlConverterFactory: SimpleXmlConverterFactory,
            rxJava2CallAdapterFactory: RxJava2CallAdapterFactory
    ): OldBsPayoneApi {


        val bsPayoneRetrofit = Retrofit.Builder()
                .addConverterFactory(simpleXmlConverterFactory)
                .addCallAdapterFactory(rxJava2CallAdapterFactory)
                .client(bsPayoneOkHttpClient)
                .baseUrl(bsPayoneUrl)
                .build()

        return bsPayoneRetrofit.create(OldBsPayoneApi::class.java)
    }
}