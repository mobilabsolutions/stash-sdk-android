package com.mobilabsolutions.payment.android.psdk.internal

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.mobilabsolutions.payment.android.psdk.UiCustomizationManager
import com.mobilabsolutions.payment.android.psdk.exceptions.backend.BackendExceptionMapper
import com.mobilabsolutions.payment.android.psdk.internal.api.backend.*
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.psppaypal.PayPalRedirectHandler
import dagger.Module
import dagger.Provides
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.TlsVersion
import okhttp3.logging.HttpLoggingInterceptor
import org.simpleframework.xml.convert.AnnotationStrategy
import org.simpleframework.xml.core.Persister
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
@Module
open class PaymentSdkModule(private val publicKey: String, private val mobilabUrl: String,
                            private val applicationContext: Application) {
    val MOBILAB_TIMEOUT = 60L
    val TIMEOUT_UNIT = TimeUnit.SECONDS

    companion object {
        val SHARED_PREFERENCES_NAME = "DefaultSharedPreferences"
    }



    internal val redirectActivitySubject : PublishSubject<PayPalRedirectHandler.RedirectResult> = PublishSubject.create()

    internal var newUiCustomizationManager : NewUiCustomizationManager? = null

    @Provides
    fun provideHttpLoggingInterceptor(@Named("isLogging") isLogging: Boolean): HttpLoggingInterceptor {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        if (isLogging) {
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        } else {
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.NONE
        }
        return httpLoggingInterceptor
    }

    @Provides
    @Singleton
    fun provideMobilabApi(
            mobilabBackendOkHttpClient: OkHttpClient,
            @Named("mobilabBackendGsonConverterFactory")
            gsonConverterFactory: GsonConverterFactory,
            rxJava2CallAdapterFactory: RxJava2CallAdapterFactory
    ): MobilabApi {
        val mobilabBackendRetrofit = Retrofit.Builder()
                .addConverterFactory(gsonConverterFactory)
                .addCallAdapterFactory(rxJava2CallAdapterFactory)
                .client(mobilabBackendOkHttpClient)
                .baseUrl(mobilabUrl)
                .build()

        val mobilabApi = mobilabBackendRetrofit.create(MobilabApi::class.java)
        return mobilabApi

    }

    @Provides
    @Singleton
    fun provideMobilabHttpClient(
            httpLoggingInterceptor: HttpLoggingInterceptor,
            sslSupportPackage: SslSupportPackage
    ): OkHttpClient {

        val mobilabBackendOkHttpClientBuilder = OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                            .addHeader("Authorization", "Bearer " + Base64.encodeToString(publicKey.toByteArray(StandardCharsets.UTF_8), Base64.DEFAULT).trim { it <= ' ' })
                            .build()
                    chain.proceed(request)
                }
                .connectTimeout(MOBILAB_TIMEOUT, TIMEOUT_UNIT)
                .readTimeout(MOBILAB_TIMEOUT, TIMEOUT_UNIT)
                .writeTimeout(MOBILAB_TIMEOUT, TIMEOUT_UNIT)
        if (sslSupportPackage.useCustomSslSocketFactory) {
            mobilabBackendOkHttpClientBuilder.sslSocketFactory(sslSupportPackage.sslSocketFactory!!, sslSupportPackage.x509TrustManager!!)
            val connectionSpec = ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                    .tlsVersions(TlsVersion.TLS_1_2)
                    .build()
            mobilabBackendOkHttpClientBuilder.connectionSpecs(listOf(connectionSpec))
        }
        return mobilabBackendOkHttpClientBuilder.build()
    }

    @Provides
    @Named("isLogging")
    fun provideLoggingFlag(): Boolean {
        return true // Todo set based on build configuration
    }

    @Provides
    @Singleton
    fun provideGsonConverterFactory() = GsonConverterFactory.create()

    @Named("mobilabBackendGsonConverterFactory")
    @Provides
    @Singleton
    fun provideBackendGsonConverterFactory(): GsonConverterFactory {
        val runtimeTypeAdapterFactory = RuntimeTypeAdapterFactory
                .of(ProviderSpecificData::class.java, "psp")
                .registerSubtype(PayoneSpecificData::class.java, "payone")
                .registerSubtype(SketchSpecificData::class.java, "sketch")

        val gson = GsonBuilder()
                .registerTypeAdapterFactory(runtimeTypeAdapterFactory)
                .create()
        return GsonConverterFactory.create(gson)
    }

    @Provides
    @Singleton
    fun providesRxJava2CallAdapter() = RxJava2CallAdapterFactory.create()

    @Provides
    @Singleton
    fun provideSimpleXmlConverterFactory() = SimpleXmlConverterFactory.createNonStrict(
            Persister(AnnotationStrategy()
            )
    )

    @Provides
    @Singleton
    fun providePaymentProviderType() = NewPaymentSdk.getProviderFromKey(publicKey)

    @Provides
    @Singleton
    fun provideDefaultGson() : Gson = Gson()

    @Provides
    @Singleton
    fun provideSharedPreferences() : SharedPreferences = applicationContext.getSharedPreferences(
            SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE
    )

    @Provides
    @Singleton
    fun provideExceptionMapper(gson : Gson): BackendExceptionMapper {
        return BackendExceptionMapper(gson)
    }

    @Provides
    @Singleton
    fun provideApplicationContext() : Application {
        return applicationContext
    }

    @Provides
    @Singleton
    fun provideRedirectObservable() : Single<PayPalRedirectHandler.RedirectResult> {
        return redirectActivitySubject.firstElement().toSingle()
    }

    @Provides
    @Singleton
    fun provideRedirectSubject() : Subject<PayPalRedirectHandler.RedirectResult> {
        return redirectActivitySubject
    }

    @Provides
    @Singleton
    fun provideUiCustomizationManager(gson : Gson, sharedPreferences: SharedPreferences) : UiCustomizationManager {
        if (newUiCustomizationManager == null){
            newUiCustomizationManager = NewUiCustomizationManager(gson, sharedPreferences)
        }
        return newUiCustomizationManager as UiCustomizationManager
    }

    @Provides
    @Singleton
    internal fun provideNewUiCustomizationManager(gson : Gson, sharedPreferences: SharedPreferences) : NewUiCustomizationManager {
        if (newUiCustomizationManager == null){
            newUiCustomizationManager = NewUiCustomizationManager(gson, sharedPreferences)
        }
        return newUiCustomizationManager as NewUiCustomizationManager
    }


}