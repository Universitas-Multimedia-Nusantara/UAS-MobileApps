package com.example.chilli.database

import android.provider.ContactsContract.CommonDataKinds.Nickname
import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BroadcastMessageDao {
    @Query("SELECT * FROM BroadcastMessages")
    fun getAllBroadcastMessages(): Flow<List<BroadcastMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun insertBroadcastMessage(broadcastMessage: BroadcastMessage)
}

@Dao
interface MessagesDao {
    @Query("SELECT * FROM Messages ORDER BY timestamp DESC")
    fun getAllMessages(): Flow<List<Messages>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMessage(message: Messages)
}

@Dao
interface GroupDao {
    @Query("SELECT * FROM `Group`")
    fun getAllGroups(): Flow<List<Group>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun insertGroup(group: Group)
}

@Dao
interface UserDao {
    @Transaction
    @Query("SELECT * FROM User WHERE userId = :userId")
    fun getUser(userId: String):Flow<User>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: User)

    @Update
    fun updateUser(user: User)

}

