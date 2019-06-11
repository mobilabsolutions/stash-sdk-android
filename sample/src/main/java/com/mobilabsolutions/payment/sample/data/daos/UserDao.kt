package com.mobilabsolutions.payment.sample.data.daos

import androidx.room.Dao
import androidx.room.Query
import com.mobilabsolutions.payment.sample.data.entities.User
import io.reactivex.Observable

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 26-04-2019.
 */
@Dao
abstract class UserDao : EntityDao<User> {
    @Query("SELECT COUNT(*) FROM user")
    abstract suspend fun userCount(): Int

    @Query("SELECT * FROM user")
    abstract fun entryObservable(): Observable<User>

    @Query("SELECT * FROM user")
    abstract suspend fun getUser(): User
}