package com.mobilabsolutions.payment.sample.remote

import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Singleton

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 23-04-2019.
 */
@Module
class RemoteModule {
    companion object {
        private const val BASE_URL = "https://backend.utl"
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val okHttpClientBuilder = OkHttpClient().newBuilder()
        return okHttpClientBuilder.build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(BASE_URL)
                .build()
    }

    @Provides
    @Singleton
    fun provideMerchantApi(retrofit: Retrofit): MerchantApi = retrofit.create(MerchantApi::class.java)
}