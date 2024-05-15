package com.shahbaz.letstalk.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.shahbaz.letstalk.helper.FirebasseUtils
import com.shahbaz.letstalk.room.RoomDao
import com.shahbaz.letstalk.room.RoomDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }


    @Provides
    @Singleton
    fun provideFirebaseDatabase(): FirebaseDatabase {
        return FirebaseDatabase.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage {
        return FirebaseStorage.getInstance()
    }


    @Provides
    @Singleton
    fun provideFirestore():FirebaseFirestore{
        return FirebaseFirestore.getInstance()
    }


    @Provides
    fun provideFirebasseUtils(
        firestore: FirebaseFirestore,
        firebaseAuth: FirebaseAuth
    ): FirebasseUtils {
        return FirebasseUtils(firestore, firebaseAuth)
    }

    //for the room databse instance
    @Singleton
    @Provides
    fun provideRoomDatabase(@ApplicationContext applicationContext: Context): RoomDatabase {
        return Room.databaseBuilder(
            applicationContext,
            RoomDatabase::class.java,
            "UserDatabase"
        )
            .build()
    }


    //provide the instance of the dao
    @Provides
    fun provideDao(database: RoomDatabase): RoomDao {
        return database.getDao()
    }

}


