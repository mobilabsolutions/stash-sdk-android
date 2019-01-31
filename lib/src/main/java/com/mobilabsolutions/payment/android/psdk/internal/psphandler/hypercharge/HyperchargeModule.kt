package com.mobilabsolutions.payment.android.psdk.internal.psphandler.hypercharge

import com.mobilabsolutions.payment.android.psdk.internal.api.hypercharge.HyperchargeApi
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
@Module
class HyperchargeModule {
    val HYPERCHARGE_TIMEOUT = 60L
    val TIMEOUT_UNIT = TimeUnit.SECONDS

    @Provides
    @Singleton
    @Named("HyperchargeOkHttp")
    fun provideHyperchargeOkHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        val hyperchargeOkHttpClient = OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .connectTimeout(HYPERCHARGE_TIMEOUT, TIMEOUT_UNIT)
                .readTimeout(HYPERCHARGE_TIMEOUT, TIMEOUT_UNIT)
                .writeTimeout(HYPERCHARGE_TIMEOUT, TIMEOUT_UNIT)
                .build()
        return hyperchargeOkHttpClient
    }

    @Provides
    @Singleton
    fun provideHyperchargeApi(
            @Named("HyperchargeOkHttp") hyperchargeOkHttpClient: OkHttpClient,
            simpleXmlConverterFactory: SimpleXmlConverterFactory,
            rxJava2CallAdapterFactory: RxJava2CallAdapterFactory
    ): HyperchargeApi {


        val hyperchargeRetrofit = Retrofit.Builder()
                .addConverterFactory(simpleXmlConverterFactory)
                .addCallAdapterFactory(rxJava2CallAdapterFactory)
                .client(hyperchargeOkHttpClient)
                .baseUrl("https://pd.mblb.net")//Note, this url is never used! urls are dynamic and set by response from hyperchage, we just need to calm down retrofit here
                .build()

        return hyperchargeRetrofit.create(HyperchargeApi::class.java)
    }
}