package com.shahbaz.letstalk.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.shahbaz.letstalk.datamodel.UnregisteredUser
import com.shahbaz.letstalk.datamodel.UserProfile


//@Dao
//interface RoomDao {
////    @Insert(onConflict = OnConflictStrategy.REPLACE)
////    suspend fun insertUserProfile(userProfile : MutableList<UserProfile>)
////
////    @Query("SELECT * FROM UserProfile")
////    suspend fun getRegisterUserProfile():MutableList<UserProfile>
////
////    @Update
////    suspend fun updatetoken(userProfile: UserProfile)
//}