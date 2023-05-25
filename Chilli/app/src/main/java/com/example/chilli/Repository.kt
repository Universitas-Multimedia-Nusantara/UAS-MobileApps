package com.example.chilli
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chilli.database.*
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SnapshotMetadata
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
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

    private lateinit var user: DocumentSnapshot

    fun syncUser(userId: String) {
        viewModelScope.launch {
            try {
//                observeUserData(userId)
                val userSnapshot = userCollection.document(userId).get().await()
                userCollection.document(userId).addSnapshotListener { snapshot, exception ->
                    if (exception != null) {
                        return@addSnapshotListener
                    }
                    if (snapshot != null && snapshot.exists()) {
                        snapshot.data?.let { user ->
                            viewModelScope.launch {
                                setUser(user, userId)
                            }
                        }
                    }
                }


//                userSnapshot.toObject(User::class.java)?.let { user ->
//                    setUser(user, userId)
//                }

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

            } catch (e: Exception) {
                Log.d("Repository", "Error: ${e.message}")
                // Handle the exception here (e.g., display an error message)
            }
        }
    }

    private suspend fun setUser(userMap: Map<String, Any>, userId: String) {
        withContext(Dispatchers.IO) {
            try {
                val email = userMap["email"] as? String ?: ""
                val foto = userMap["foto"] as? String
                val group = userMap["group"] as? List<String>
                val name = userMap["name"] as? String ?: ""
                val nickName = userMap["nickName"] as? String ?: ""

                val user = User(userId, email, foto, group, name, nickName)

                val existingUser = userDao.getAllUsers(userId)
                if (existingUser != null) {
                    userDao.updateUser(user)
                }else {
                    userDao.insertUser(user)
                }
            } catch (e: Exception) {
                Log.d("error", "${e.message}")
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
