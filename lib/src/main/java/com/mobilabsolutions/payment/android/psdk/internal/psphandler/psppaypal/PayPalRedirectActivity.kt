package com.mobilabsolutions.payment.android.psdk.internal.psphandler.psppaypal

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.mobilabsolutions.payment.android.R
import com.mobilabsolutions.payment.android.psdk.internal.NewPaymentSdk
import com.mobilabsolutions.payment.android.psdk.internal.NewUiCustomizationManager
import io.reactivex.subjects.Subject
import kotlinx.android.synthetic.main.activity_paypal_redirect.*
import timber.log.Timber
import javax.inject.Inject

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class PayPalRedirectActivity : AppCompatActivity() {

    companion object {
        final val REDIRECT_URL_EXTRA = "RedirectUrlExtra"
    }

    lateinit var payPalActivityCustomization : PayPalActivityCustomization


    @Inject
    lateinit var redirectHandlerSubject : Subject<PayPalRedirectHandler.RedirectResult>

    @Inject
    internal lateinit var uiCustomizationManager: NewUiCustomizationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NewPaymentSdk.getInjector().inject(this)
        setContentView(R.layout.activity_paypal_redirect)
        payPalActivityCustomization = uiCustomizationManager.getPaypalRedirectActivityCustomizations()
        payPalActivityCustomization.apply {
            if (showAppBar) {
                paypalToolbar.visibility = View.VISIBLE
                setSupportActionBar(paypalToolbar)
                supportActionBar?.setDisplayHomeAsUpEnabled(showUpNavigation)
            } else {
                paypalToolbar.visibility = View.GONE
            }
        }

        val redirectUrl = intent.getStringExtra(REDIRECT_URL_EXTRA)
        Timber.d("Got redirect url: $redirectUrl")
        payPalWebView.webViewClient = object : WebViewClient() {
            @SuppressLint("NewApi")
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                Timber.d("Redirect API21: ${request.url}")
                redirectHandlerSubject.onNext(getResultFromRedirectUrl(request.url.toString()))
                return false
            }

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                Timber.d("Redirect API19: ${url.toString()}")
                redirectHandlerSubject.onNext(getResultFromRedirectUrl(url!!))
                return false
            }
        }
        payPalWebView.loadUrl(redirectUrl)



    }

    fun getResultFromRedirectUrl(redirectUrl : String) : PayPalRedirectHandler.RedirectResult{
        val redirectState = when {
            redirectUrl.contains("success") -> PayPalRedirectHandler.RedirectState.SUCCESS
            redirectUrl.contains("failure") -> PayPalRedirectHandler.RedirectState.FAILURE
            redirectUrl.contains("canceled") -> PayPalRedirectHandler.RedirectState.CANCELED
            else -> PayPalRedirectHandler.RedirectState.UNKNOWN
        }

        val code = redirectUrl.substringAfterLast("/")
        return PayPalRedirectHandler.RedirectResult(redirectState, code)
    }
}