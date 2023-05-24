package com.example.chilli
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chilli.database.*
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class Repository(
    private val firestore: FirebaseFirestore,
    private val broadcastDao: BroadcastMessageDao,
    private val messagesDao: MessagesDao,
    private val groupDao: GroupDao,
    private val userDao: UserDao
) : ViewModel() {
    private val userCollection: CollectionReference = firestore.collection("User")
    private val groupCollection: CollectionReference = firestore.collection("Group")
    private val messagesCollection: CollectionReference = firestore.collection("BroadcastMessages")

    fun syncUser(userId: String) {
        viewModelScope.launch {
            try {
                val userSnapshot = userCollection.document(userId).get().await()
                userSnapshot.toObject(User::class.java)?.let { user ->

                    setUser(user, userId)
                    val groupIDs = userSnapshot.get("group") as? List<String>
                    for (groupID in groupIDs!!) {
                        val groupSnapshot = groupCollection.document(groupID).get().await()
                        groupSnapshot.toObject(Group::class.java)?.let { group ->
                            setGroup(group, groupID)
                        }

                        val messagesSnapShot =
                            messagesCollection.document(groupID).collection("messages").get().await()
                        val messageIds = messagesSnapShot.documents.mapNotNull { it.id }
                        val broadcastMessage = BroadcastMessage(groupID, messageIds)
                        insertBroadcastMessage(broadcastMessage)

                        for (messagesDocument in messagesSnapShot.documents) {
                            messagesDocument.toObject(Messages::class.java)?.let { messages ->
                                setMessages(messages, messagesDocument.id)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.d("Repository", "Error: ${e.message}")
                // Handle the exception here (e.g., display an error message)
            }
        }
    }

    private suspend fun setUser(user: User, userId:String) {
        withContext(Dispatchers.IO) {
            try {
                val userWithId = user.copy(userId = userId)
//                Log.d("username:","${user.getString("name")}")
//                val createUser = User(userId, user?.getString("email")!!, user?.getString("foto")!!, user.get("group") as? List<String>, user?.getString("name")!!, user?.getString("nickName")!! )
                userDao.insertUser(userWithId)
            } catch (e: Exception) {
                Log.d("error","${e.message}")
            }
        }
    }

    private suspend fun setGroup(group: Group, groupId: String) {
        withContext(Dispatchers.IO) {
            try {
                val groupWithId = group.copy(groupId = groupId)
                groupDao.insertGroup(groupWithId)
            } catch (e: Exception) {
                // Handle the exception here
            }
        }
    }

    private suspend fun setMessages(messages: Messages, MessagesId:String) {
        withContext(Dispatchers.IO) {
            try {
                val messagesWithId = messages.copy(messageId = MessagesId)
                messagesDao.insertMessage(messagesWithId)
            } catch (e: Exception) {
                // Handle the exception here
            }
        }
    }

    suspend fun insertBroadcastMessage(broadcastMessage: BroadcastMessage) {
        withContext(Dispatchers.IO) {
            try {
                broadcastDao.insertBroadcastMessage(broadcastMessage)
            } catch (e: Exception) {
                // Handle the exception here
            }
        }
    }
}
