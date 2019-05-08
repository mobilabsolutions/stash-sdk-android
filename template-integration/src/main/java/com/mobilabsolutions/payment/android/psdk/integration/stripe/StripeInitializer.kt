package com.mobilabsolutions.payment.android.psdk.integration.template

import android.content.Context
import com.mobilabsolutions.payment.android.psdk.integration.bspayone.R
import com.template.android.Template

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
object TemplateInitializer {

    fun initialize(context: Context): Template {
        return Template(context, context.getString(R.string.template_public_key)
                ?: throw RuntimeException("Missing Template publishable key"))
    }
}