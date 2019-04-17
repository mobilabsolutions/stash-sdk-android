package com.mobilabsolutions.payment.sample.data

import android.content.Context
import android.os.Debug
import androidx.room.Room
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 12-04-2019.
 */
@Module
class DataModule {
    @Singleton
    @Provides
    fun provideDatabase(context: Context): SampleDatabase {
        val builder = Room.databaseBuilder(context, SampleDatabase::class.java, "sample-db")
                .fallbackToDestructiveMigration()
        if (Debug.isDebuggerConnected()) {
            builder.allowMainThreadQueries()
        }
        return builder.build()
    }

    @Singleton
    @Provides
    fun provideDatabaseTransactionRunner(db: SampleDatabase): DatabaseTransactionRunner = RoomTransactionRunner(db)

    @Provides
    fun provideProductDao(db: SampleDatabase) = db.productDao()

    @Provides
    fun provideCartDao(db: SampleDatabase) = db.cartDao()

    @Provides
    fun providePaymentMethodDao(db: SampleDatabase) = db.paymentMethodDao()
}