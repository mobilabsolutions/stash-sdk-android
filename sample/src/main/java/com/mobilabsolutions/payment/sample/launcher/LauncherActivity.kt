package com.mobilabsolutions.payment.sample.launcher

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.mobilabsolutions.payment.sample.R
import com.mobilabsolutions.payment.sample.main.MainActivity

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 08-04-2019.
 */
class LauncherActivity : AppCompatActivity() {
    companion object {
        private const val SPLASH_SCREEN_DURATION = 1000L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_launcher)

        Handler().postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, SPLASH_SCREEN_DURATION)
    }
}