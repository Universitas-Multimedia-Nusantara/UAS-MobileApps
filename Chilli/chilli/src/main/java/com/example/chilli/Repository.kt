package com.example.chilli

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chilli.database.*
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.Timestamp
import com.google.firebase.Timestamp as Timestamp2

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
        try {
            userCollection.document(userId).addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    return@addSnapshotListener
                }

                val userData = snapshot?.data
                if (userData != null) {
                    viewModelScope.launch {
                        Log.d("user", "$userData")
                        setUser(userData, userId)


                        val groupIDs = userData["group"] as? List<String> ?: emptyList()
                        Log.d("group", "$groupIDs")
                        for (groupID in groupIDs) {
                            syncGroup(groupID)
                            syncMessages(groupID)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.d("Repository", "Error: ${e.message}")
            // Handle the exception here (e.g., display an error message)
        }
    }

    private fun syncGroup(groupID: String) {
        groupCollection.document(groupID).addSnapshotListener { snapshotGroup, exception ->
            val groupData = snapshotGroup?.data
            if (groupData != null) {
                viewModelScope.launch {
                    setGroup(groupData, groupID)
                }
            }
        }
    }

    private fun syncMessages(groupID: String) {
        messagesCollection.document(groupID).collection("Messages")
            .addSnapshotListener { snapshotMessages, exception ->
                val messageDocuments = snapshotMessages?.documents
                if (messageDocuments != null) {
                    val messageIds = messageDocuments.mapNotNull { it.id }
                    val broadcastMessage = BroadcastMessage(groupID, messageIds)

                    for (messagesDocument in messageDocuments) {
                        if (messagesDocument != null) {
                            viewModelScope.launch {
                                setMessages(messagesDocument.data, messagesDocument.id)
                            }
                        }
                    }

                    viewModelScope.launch {
                        insertBroadcastMessage(broadcastMessage)
                    }
                }
            }
    }



    private suspend fun setUser(userMap: Map<String, Any>?, userId: String) {
        withContext(Dispatchers.IO) {
            try {
                Log.d("set", "${ userMap}")
                val email = userMap?.get("email") as String
                val foto = userMap.get("foto") as String
                val group = userMap.get("group") as List<String>
                val name = userMap.get("name") as String
                val nickName = userMap.get("nickName") as String

                val user = User(userId, email, foto, group, name, nickName)

//                data class User(
//                    @PrimaryKey
//                    var userId: String,
//                    val email: String,
//                    val foto: String?,
//                    @TypeConverters(Converter::class)
//                    val group: List<String>?,
//                    val name: String,
//                    val nickName: String
//                )
                Log.d("adawe", "$user")

                    Log.d("Masuk insert", "Masuk")
                    userDao.insertUser(user)

            } catch (e: Exception) {
                Log.d("error", "${e.message}")
            }
        }
    }

    private suspend fun setGroup(group: Map<String, Any>?, groupId: String) {
        withContext(Dispatchers.IO) {
            try {
                Log.d("group","$group")
                val deskripsi = group?.get("deskripsi") as? String ?: ""
                val foto = group?.get("foto") as? String ?: ""
                val nama = group?.get("nama") as? String ?: ""
                val user = group?.get("user") as? List<Map<String, String>> ?: null
                val groupWithId = Group(groupId, deskripsi, foto, nama, user)
                groupDao.insertGroup(groupWithId)
            } catch (e: Exception) {
                // Handle the exception here
            }
        }
    }

    private suspend fun setMessages(messages: MutableMap<String, Any>?, id: String) {
        withContext(Dispatchers.IO) {
            try{
                Log.d("ada cok", "$messages")
                val body = messages?.get("body") as? String ?: ""
                val files = messages?.get("files") as? String ?: ""
                val pinTime = convertTimestampToSqlTimestamp(messages?.get("pinTime") as? Timestamp2?)
                val sender = messages?.get("sender") as? String ?: ""
                val timestamp = convertTimestampToSqlTimestamp(messages?.get("timestamp") as Timestamp2)
                val title = messages.get("title") as? String ?: ""
                Log.d("tanggal", "${timestamp}")
                val msg = Messages(id, title, files, pinTime, timestamp, sender, body)
                Log.d("msg", "$msg")
                messagesDao.insertMessage(msg)
            }catch (e: Exception) {
                // Handle the exception here
            }
        }
    }

    suspend fun insertBroadcastMessage(broadcastMessage: BroadcastMessage) {
        withContext(Dispatchers.IO) {

            try {
                Log.d("ada", "$broadcastMessage")
                broadcastDao.insertBroadcastMessage(broadcastMessage)
            } catch (e: Exception) {
                // Handle the exception here
            }
        }
    }

    fun convertTimestampToSqlTimestamp(firestoreTimestamp: Timestamp2?): Timestamp? {
        return if (firestoreTimestamp != null) {
            Timestamp(firestoreTimestamp.toDate().time)
        } else null
    }

}
