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

//    fun syncUser(userId: String) {
//        try {
//            userCollection.document(userId).addSnapshotListener { snapshot, exception ->
//                if (exception != null) {
//                    return@addSnapshotListener
//                }
//                if (snapshot != null && snapshot.exists()) {
//                    snapshot.data?.let { user ->
//                        viewModelScope.launch {
//                            Log.d("user", "${user}")
//                            setUser(user, userId)
//                        }
//
//                        val groupIDs = user["group"] as? List<String>
//
//                            for (groupID in groupIDs.orEmpty()) {
//                                groupCollection.document(groupID)
//                                    .addSnapshotListener { snapshotGroup, exception ->
//                                        snapshotGroup?.data?.let { group ->
//                                            viewModelScope.launch {
//                                                setGroup(group, groupID)
//                                            }
//                                        }
//
//                                        messagesCollection.document(groupID).collection("messages")
//                                            .addSnapshotListener { snapshotMessages, exception ->
//                                                val messageIds =
//                                                    snapshotMessages?.documents?.mapNotNull { it.id }
//                                                val broadcastMessage =
//                                                    BroadcastMessage(groupID, messageIds.orEmpty())
//
//                                                for (messagesDocument in snapshotMessages?.documents.orEmpty()) {
//                                                    messagesDocument.toObject(Messages::class.java)
//                                                        ?.let { messages ->
//                                                            viewModelScope.launch {
//                                                                setMessages(
//                                                                    messages,
//                                                                    messagesDocument.id
//                                                                )
//                                                            }
//                                                        }
//                                                }
//
//                                                viewModelScope.launch {
//                                                    insertBroadcastMessage(broadcastMessage)
//                                                }
//                                            }
//                                    }
//                            }
//
//                    }
//                }
//            }
//        } catch (e: Exception) {
//            Log.d("Repository", "Error: ${e.message}")
//            // Handle the exception here (e.g., display an error message)
//        }
//    }

    fun syncUser(userId: String) {
        try {
            userCollection.document(userId).addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    snapshot.data?.let { user ->
                        viewModelScope.launch {
                            Log.d("user", "${user}")
                            setUser(user, userId)
                        }

                        val groupIDs = user["group"] as? List<String>

                        for (groupID in groupIDs.orEmpty()) {
                            groupCollection.document(groupID)
                                .addSnapshotListener { snapshotGroup, exception ->
                                    snapshotGroup?.data?.let { group ->
                                        viewModelScope.launch {
                                            setGroup(group, groupID)
                                        }
                                    }
                                }

                            Log.d("jalan","jalan")

                            messagesCollection.document(groupID).collection("Messages")
                                .addSnapshotListener { snapshotMessages, exception ->
                                    Log.d("jalan","$snapshotMessages")



                                        val messageIds = snapshotMessages?.documents?.mapNotNull { it.id }
                                        val broadcastMessage = BroadcastMessage(groupID, messageIds!!)

                                        for (messagesDocument in snapshotMessages?.documents!!) {
                                            messagesDocument.toObject(Messages::class.java)
                                                ?.let { messages ->
                                                    viewModelScope.launch {
                                                        setMessages(
                                                            messages,
                                                            messagesDocument.id
                                                        )
                                                    }
                                                }


                                        viewModelScope.launch {
                                            insertBroadcastMessage(broadcastMessage)
                                        }
                                    }
                                }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.d("Repository", "Error: ${e.message}")
            // Handle the exception here (e.g., display an error message)
        }
    }


    private suspend fun setUser(userMap: Map<String, Any>?, userId: String) {
        withContext(Dispatchers.IO) {
            try {
                Log.d("set", "${ userMap?.get("email")}")
                val email = userMap?.get("email")
                val foto = userMap?.get("foto")
                val group = userMap?.get("group")
                val name = userMap?.get("name")
                val nickName = userMap?.get("nickName")

                val user = User(userId, email as String, foto as String?,
                    group as List<String>?, name as String, nickName as String
                )

                val existingUser = userDao.getUser(userId)
                if (existingUser != null) {
                    userDao.updateUser(user)
                } else {
                    userDao.insertUser(user)
                }
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
                val user = group?.get("user") as? List<Map<String, String>>?
                val groupWithId = Group(groupId, deskripsi, foto, nama, user)
                groupDao.insertGroup(groupWithId)
            } catch (e: Exception) {
                // Handle the exception here
            }
        }
    }

    private suspend fun setMessages(messages: Messages, messagesId: String) {
        withContext(Dispatchers.IO) {
            try {
                Log.d("message","$messages")
                val messagesWithId = messages.copy(messageId = messagesId)
                messagesDao.insertMessage(messagesWithId)
            } catch (e: Exception) {
                // Handle the exception here
            }
        }
    }

    suspend fun insertBroadcastMessage(broadcastMessage: BroadcastMessage) {
        withContext(Dispatchers.IO) {
            Log.d("ada", "ada")
            try {
                broadcastDao.insertBroadcastMessage(broadcastMessage)
            } catch (e: Exception) {
                // Handle the exception here
            }
        }
    }
}
