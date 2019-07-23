/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.template

import com.mobilabsolutions.stash.core.internal.IntegrationScope
import com.mobilabsolutions.stash.core.internal.StashComponent
import dagger.Component

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
@IntegrationScope
@Component(dependencies = arrayOf(StashComponent::class), modules = arrayOf(TemplateModule::class))
interface TemplateIntegrationComponent {
    fun inject(integration: TemplateIntegration)
}
