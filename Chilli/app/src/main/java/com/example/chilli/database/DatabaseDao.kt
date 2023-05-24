package com.example.chilli.database

import android.provider.ContactsContract.CommonDataKinds.Nickname
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface BroadcastMessageDao {
    @Query("SELECT * FROM BroadcastMessages")
    fun getAllBroadcastMessages(): Flow<BroadcastMessage>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun insertBroadcastMessage(broadcastMessage: BroadcastMessage)
}

@Dao
interface MessagesDao {
    @Query("SELECT * FROM Messages ORDER BY timestamp DESC")
    fun getAllMessages(): Flow<Messages>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMessage(message: Messages)
}

@Dao
interface GroupDao {
    @Query("SELECT * FROM `Group`")
    fun getAllGroups(): Flow<Group>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun insertGroup(group: Group)
}

@Dao
interface UserDao {
    @Transaction
    @Query("SELECT * FROM User WHERE userId = :userId")
    fun getAllUsers(userId: String): User?

    @Insert
    fun insertUser(user: User)
}

