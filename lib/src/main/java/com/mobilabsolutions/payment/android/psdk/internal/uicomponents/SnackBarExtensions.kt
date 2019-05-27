package com.mobilabsolutions.payment.android.psdk.internal.uicomponents

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.mobilabsolutions.payment.android.R
import com.mobilabsolutions.payment.android.psdk.exceptions.base.BasePaymentException
import com.mobilabsolutions.payment.android.psdk.px
import kotlinx.android.synthetic.main.snackbar_layout.view.close
import kotlinx.android.synthetic.main.snackbar_layout.view.snackbar_text
import kotlinx.android.synthetic.main.snackbar_layout.view.snackbar_title

object SnackBarExtensions {

    operator fun invoke(body: SnackBarExtensions.() -> Unit): Unit = body.invoke(this)

    val TOP_MARGIN = 0.px
    val WIDTH = 64.px

    fun Throwable.getErrorSnackBar(view: View): Snackbar {
        val snackBar = Snackbar.make(view, "", Snackbar.LENGTH_INDEFINITE)
        snackBar.view.setBackgroundColor(ContextCompat.getColor(view.context, R.color.carnation))

        val snackBarView = snackBar.view as Snackbar.SnackbarLayout
        snackBarView.setPadding(0, 0, 0, 0)
        val snackView = LayoutInflater.from(view.context).inflate(R.layout.snackbar_layout, snackBarView, false)

        if (this is BasePaymentException && this.errorTitle != null) {
            snackView.snackbar_title.text = this.errorTitle
        } else {
            // TODO: Biju: Find a better way
            snackView.snackbar_title.text = this.javaClass.simpleName.replace("Exception", " Error")
        }
        snackView.snackbar_text.text = this.message
        snackView.close.setOnClickListener {
            snackBar.view.visibility = View.GONE // We do this to prevent dismissal animation
            snackBar.dismiss()
        }

        val params = snackBar.view.layoutParams
        if (params is CoordinatorLayout.LayoutParams) {
            with(params) {
                gravity = Gravity.TOP
                height = WIDTH
                width = CoordinatorLayout.LayoutParams.MATCH_PARENT
                setMargins(0, TOP_MARGIN, 0, 0)
            }
        } else {
            with(params as FrameLayout.LayoutParams) {
                gravity = Gravity.TOP
                height = WIDTH
                width = FrameLayout.LayoutParams.MATCH_PARENT
                setMargins(0, TOP_MARGIN, 0, 0)
            }
        }
        snackBar.view.layoutParams = params
        snackBar.view.elevation = 0f

        snackBarView.addView(snackView, 0)
        // We don't want to show snackbar showing animation, because it looks broken, since our
        // snackbar is on top
        snackBar.view.visibility = View.INVISIBLE
        snackBar.addCallback(
            object : Snackbar.Callback() {
                override fun onShown(snackbar: Snackbar?) {
                    super.onShown(snackbar)
                    snackbar!!.view.visibility = View.VISIBLE
                }
            }
        )

        return snackBar
    }
}