package com.mobilabsolutions.payment.android.psdk.internal.uicomponents

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.mobilabsolutions.payment.android.R

object SnackBarExtensions {

    operator fun invoke(body: SnackBarExtensions.() -> Unit): Unit = body.invoke(this)

    fun Throwable.getErrorSnackBar(view: View): Snackbar {
        val snackBar = Snackbar.make(view, "", Snackbar.LENGTH_INDEFINITE)
        snackBar.view.setBackgroundColor(ContextCompat.getColor(view.context, R.color.carnation))

        val snackBarView = snackBar.view as Snackbar.SnackbarLayout
        snackBarView.setPadding(0, 0, 0, 0)
        val snackView = LayoutInflater.from(view.context).inflate(R.layout.snackbar_layout, snackBarView, false)

        // TODO: Biju: Find a better way
        snackView.findViewById<TextView>(R.id.snackbar_title).text = this::class.simpleName?.replace("Exception", " Error")
        snackView.findViewById<TextView>(R.id.snackbar_text).text = this.message
        snackView.findViewById<ImageView>(R.id.close).setOnClickListener {
            snackBar.dismiss()
        }

        val params = snackBar.view.layoutParams
        if (params is CoordinatorLayout.LayoutParams) {
            with(params) {
                gravity = Gravity.TOP
                height = 135
                width = CoordinatorLayout.LayoutParams.MATCH_PARENT
                setMargins(0, 100, 0, 0)
            }
        } else {
            with(params as FrameLayout.LayoutParams) {
                gravity = Gravity.TOP
                height = 135
                width = FrameLayout.LayoutParams.MATCH_PARENT
                setMargins(0, 100, 0, 0)
            }
        }
        snackBar.view.layoutParams = params

        snackBarView.addView(snackView, 0)

        return snackBar
    }
}