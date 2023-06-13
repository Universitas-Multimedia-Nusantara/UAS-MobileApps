package id.ac.umn.chilli.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BroadcastMessageDao {
    @Query("SELECT * FROM BroadcastMessages WHERE groupId = :id")
    fun getAllBroadcastMessages(id: String): Flow<List<BroadcastMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun insertBroadcastMessage(broadcastMessage: BroadcastMessage)
}

@Dao
interface MessagesDao {
    @Query("SELECT * FROM Messages ORDER BY timestamp DESC")
    fun getAllMessages(): Flow<List<Messages>>

    @Query("SELECT * FROM Messages WHERE messageId IN (:ids) ORDER BY timestamp DESC")
    fun getMessagesById(ids: List<String>?): Flow<List<Messages>>

    @Query("SELECT * FROM Messages WHERE messageId = :ids ORDER BY timestamp DESC")
    fun getMessagesBySigleId(ids: String): Flow<List<Messages>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMessage(message: Messages)

    @Query("DELETE FROM Messages WHERE messageId = :messageId")
    fun deleteMessageById(messageId: String)

    @Query("DELETE FROM Messages WHERE messageId NOT IN(:messageIds)")
    fun deleteAllNotMessage(messageIds: List<String>)

}

@Dao
interface GroupDao {
    @Query("SELECT * FROM `Group`")
    fun getAllGroups(): Flow<List<Group>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun insertGroup(group: Group)
     @Query("DELETE FROM `Group` WHERE groupId = :groupId ")
     fun deleteGroupById(groupId: String)

    @Query("DELETE FROM `Group` WHERE groupId NOT IN(:groupIds)")
    fun deleteAllNotUserGroup(groupIds: List<String>)

    @Query("SELECT * FROM `Group` WHERE groupId = :groupId")
    fun getGroupById(groupId: String): Group?
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

    @Query("DELETE FROM User WHERE userId = :userId")
    fun deleteUserById(userId: String)

}

