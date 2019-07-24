/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.adyen

import com.adyen.checkout.core.internal.PaymentHandlerImpl
import org.junit.Test

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class ReflectionDebug {

    @Test
    fun constructorInspection() {
        PaymentHandlerImpl::class.java.declaredConstructors.forEach {
            println("Constructor ${it.parameterTypes.contentToString()}")
        }
    }
}