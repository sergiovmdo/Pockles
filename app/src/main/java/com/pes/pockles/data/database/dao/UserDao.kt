package com.pes.pockles.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.pes.pockles.model.User

@Dao
interface UserDao {
    @Query("select * from User")
    fun getUser(): LiveData<User>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: User)

    @Delete
    fun delete(user: User)

    @Query("delete from User")
    fun clean()
}