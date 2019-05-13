package com.mobilabsolutions.payment.android.psdk.internal

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.mobilabsolutions.payment.android.psdk.UiCustomizationManager
import com.mobilabsolutions.payment.android.psdk.exceptions.ExceptionMapper
import com.mobilabsolutions.payment.android.psdk.internal.api.backend.MobilabApiV2
import com.mobilabsolutions.payment.android.psdk.internal.api.backend.PayoneSpecificData
import com.mobilabsolutions.payment.android.psdk.internal.api.backend.ProviderSpecificData
import com.mobilabsolutions.payment.android.psdk.internal.api.backend.RuntimeTypeAdapterFactory
import com.mobilabsolutions.payment.android.psdk.internal.api.backend.SketchSpecificData
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.Integration
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
open class PaymentSdkModule(
    private val publicKey: String,
    private val mobilabUrl: String,
    private val applicationContext: Application,
    private val integrationInitializers: List<IntegrationInitialization>,
    private val testMode: Boolean
) {
    val MOBILAB_TIMEOUT = 60L
    val TIMEOUT_UNIT = TimeUnit.SECONDS

    companion object {
        const val DEFAULT_SHARED_PREFERENCES_NAME = "DefaultSharedPreferences"
        const val IDEMPOTENCY_SHARED_PREFERENCES_NAME = "DefaultSharedPreferences"
    }

    private val redirectActivitySubject: PublishSubject<PayPalRedirectHandler.RedirectResult> = PublishSubject.create()

    private var uiCustomizationManager: UiCustomizationManager? = null

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
    fun provideMobilabApiV2(
        @Named("mobilabV2HttpClient")
        mobilabBackendOkHttpClient: OkHttpClient,
        @Named("mobilabBackendGsonConverterFactory")
        gsonConverterFactory: GsonConverterFactory,
        rxJava2CallAdapterFactory: RxJava2CallAdapterFactory
    ): MobilabApiV2 {
        val mobilabBackendRetrofit = Retrofit.Builder()
            .addConverterFactory(gsonConverterFactory)
            .addCallAdapterFactory(rxJava2CallAdapterFactory)
            .client(mobilabBackendOkHttpClient)
            .baseUrl(mobilabUrl)
            .build()

        val mobilabApiV2 = mobilabBackendRetrofit.create(MobilabApiV2::class.java)
        return mobilabApiV2
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
    @Singleton
    @Named("mobilabV2HttpClient")
    fun provideMobilabV2HttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        sslSupportPackage: SslSupportPackage
    ): OkHttpClient {

        val mobilabBackendOkHttpClientBuilder = OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor { chain ->
                val requestBuilder = chain.request().newBuilder()
                    .addHeader("Publishable-Key", publicKey)
                if (testMode) {
                    requestBuilder.addHeader("PSP-Test-Mode", "true")
                }
                val request = requestBuilder.build()
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
            .registerTypeHierarchyAdapter(Throwable::class.java, ThrowableSerializer())
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
    fun provideDefaultGson(): Gson = Gson()

    @Provides
    @Singleton
    @Named("default")
    fun provideDefaultSharedPreferences(): SharedPreferences = applicationContext.getSharedPreferences(
        DEFAULT_SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE
    )

    @Provides
    @Singleton
    @Named("idempotency")
    fun provideIdempotencySharedPreferences(): SharedPreferences = applicationContext.getSharedPreferences(
        IDEMPOTENCY_SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE
    )

    @Provides
    @Singleton
    fun provideExceptionMapper(gson: Gson): ExceptionMapper {
        return ExceptionMapper(gson)
    }

    @Provides
    @Singleton
    fun provideApplicationContext(): Application {
        return applicationContext
    }

    @Provides
    @Singleton
    fun provideContext(): Context {
        return applicationContext
    }

    @Provides
    @Singleton
    fun provideRedirectObservable(): Single<PayPalRedirectHandler.RedirectResult> {
        return redirectActivitySubject.firstElement().toSingle()
    }

    @Provides
    @Singleton
    fun provideRedirectSubject(): Subject<PayPalRedirectHandler.RedirectResult> {
        return redirectActivitySubject
    }

    @Provides
    @Singleton
    fun provideUiCustomizationManager(gson: Gson, @Named("default") sharedPreferences: SharedPreferences): UiCustomizationManager {
        if (uiCustomizationManager == null) {
            uiCustomizationManager = UiCustomizationManager(gson, sharedPreferences)
        }
        return uiCustomizationManager as UiCustomizationManager
    }

    @Provides
    @Singleton
    fun providePspIntegrationsRegistered(): Set<Integration> {
        return integrationInitializers.filter { it.initializedOrNull() != null }
            .map { it.initializedOrNull() as Integration }
            .toSet()
    }
}