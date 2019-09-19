package com.mobilabsolutions.stash.core.ui

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.airbnb.mvrx.viewModel
import com.airbnb.mvrx.withState
import com.mobilabsolutions.stash.core.R
import com.mobilabsolutions.stash.core.databinding.ActivityHostBinding
import com.mobilabsolutions.stash.core.internal.StashImpl
import com.mobilabsolutions.stash.core.util.BaseActivity
import javax.inject.Inject

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 17-09-2019.
 */
class HostActivity : BaseActivity() {
    @Inject
    lateinit var hostViewModelFactory: HostViewModel.Factory

    private val viewModel: HostViewModel by viewModel()
    private lateinit var binding: ActivityHostBinding
    private val navController: NavController
        get() = (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController

    override fun onCreate(savedInstanceState: Bundle?) {
        StashImpl.getInjector().inject(this)
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_host)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (navController.currentDestination?.id != destination.id) {
                navController.navigate(destination.id)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.subscribe(this) { postInvalidate() }
    }

    override fun invalidate() {
        withState(viewModel) {
        }
    }
}