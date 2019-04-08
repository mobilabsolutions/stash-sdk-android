package com.mobilabsolutions.payment.sample.main

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.mobilabsolutions.payment.sample.R
import com.mobilabsolutions.payment.sample.core.BaseActivity
import com.mobilabsolutions.payment.sample.databinding.ActivityMainBinding

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 08-04-2019.
 */
class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private val navController: NavController
        get() = (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        NavigationUI.setupWithNavController(binding.mainToolbar, navController)

    }
}