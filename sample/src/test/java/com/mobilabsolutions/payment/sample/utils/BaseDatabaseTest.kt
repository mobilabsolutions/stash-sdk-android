package com.mobilabsolutions.payment.sample.utils

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import java.io.IOException

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 23-04-2019.
 */
@RunWith(RobolectricTestRunner::class)
abstract class BaseDatabaseTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    lateinit var db: TiviDatabase
        private set

    @Before
    open fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, TiviDatabase::class.java)
                .allowMainThreadQueries()
                .build()
    }

    @After
    @Throws(IOException::class)
    open fun closeDb() {
        db.close()
    }
}