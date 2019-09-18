package com.mobilabsolutions.stash.core.util

import android.os.Bundle
import androidx.lifecycle.LifecycleOwner
import com.airbnb.mvrx.BaseMvRxActivity
import com.airbnb.mvrx.MvRxView
import com.airbnb.mvrx.MvRxViewModelStore
import java.util.UUID

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 17-09-2019.
 */
abstract class BaseActivity : BaseMvRxActivity(), MvRxView {

    override val mvrxViewModelStore by lazy { MvRxViewModelStore(viewModelStore) }

    final override val mvrxViewId
        get() = mvrxPersistedViewId

    private lateinit var mvrxPersistedViewId: String

    override val subscriptionLifecycleOwner: LifecycleOwner
        get() = this

    override fun onCreate(savedInstanceState: Bundle?) {
        mvrxViewModelStore.restoreViewModels(this, savedInstanceState)
        super.onCreate(savedInstanceState)

        mvrxPersistedViewId = savedInstanceState?.getString(PERSISTED_VIEW_ID_KEY)
            ?: "${this::class.java.simpleName}_${UUID.randomUUID()}"
    }

    override fun onStart() {
        super.onStart()
        // This ensures that invalidate() is called for static screens that don't
        // subscribe to a ViewModel.
        postInvalidate()
    }

    companion object {
        private const val PERSISTED_VIEW_ID_KEY = "mvrx:persisted_view_id"
    }
}