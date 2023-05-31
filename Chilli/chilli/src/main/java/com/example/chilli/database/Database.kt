package com.example.chilli.database

import android.content.Context
import android.graphics.ColorSpace
import androidx.databinding.adapters.Converters
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [User::class, Messages::class, BroadcastMessage::class, Group::class], version = 5, exportSchema = false)
@TypeConverters(Converter::class, userConverter::class, TimestampConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract val userDao: UserDao
    abstract val messagesDao: MessagesDao
    abstract val broadcastMessageDao: BroadcastMessageDao
    abstract val groupDao: GroupDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "database_chilli2"
                    ).fallbackToDestructiveMigration().build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}
