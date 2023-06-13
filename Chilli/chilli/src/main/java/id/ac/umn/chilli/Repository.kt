package id.ac.umn.chilli

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import id.ac.umn.chilli.database.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.Timestamp
import com.google.firebase.Timestamp as Timestamp2

class Repository(
    private val context: Context,
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
                        withContext(Dispatchers.IO) {
                            groupDao.deleteAllNotUserGroup(groupIDs)
                        }

                        Log.d("group", "$groupIDs")
                        for (groupID in groupIDs) {
                            syncGroup(groupID)
                            syncMessages(groupID)
                        }
                    }
                } else {
                    // Document has been deleted, remove corresponding data
                    viewModelScope.launch {
                        removeUser(userId)
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
            } else {
                // Document has been deleted, remove corresponding data
                viewModelScope.launch {
                    removeGroup(groupID)
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
                                if (messagesDocument.exists()) {
                                    setMessages(messagesDocument.data, messagesDocument.id)
                                } else {
                                    deleteMessage(messagesDocument.id)
                                }
                            }
                        }
                    }

                    viewModelScope.launch {
                        insertBroadcastMessage(broadcastMessage)
                    }
                }
            }
    }


    suspend fun setUser(userMap: Map<String, Any>?, userId: String) {
        withContext(Dispatchers.IO) {
            try {
                Log.d("set", "${userMap}")
                val email = userMap?.get("email") as String

                val foto = userMap.get("foto") as String? ?: null
                Log.d("adawe", "$email")
                val group = userMap.get("group") as List<String>
                val name = userMap.get("name") as String
                val nickName = userMap.get("nickName") as String


                val user = User(userId, email, foto, group, name, nickName)

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
                Log.d("group", "$group")
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
            try {
                Log.d("ada cok", "$messages")
                val body = messages?.get("body") as? String ?: ""
                val files = messages?.get("files") as? String ?: ""
                val pinTime = messages?.get("pinTime") as? String ?: ""
                val sender = messages?.get("sender") as? String ?: ""
                val timestamp = convertTimestampToSqlTimestamp(messages?.get("timestamp") as Timestamp2)
                val title = messages.get("title") as? String ?: ""
                Log.d("tanggal", "${timestamp}")
                val msg = Messages(id, title, files, pinTime, timestamp, sender, body)
                Log.d("msg", "$msg")

                val isNewMessage = messagesDao.getMessagesBySigleId(id)

                messagesDao.insertMessage(msg)

                if (isNewMessage == null) {
                    // Show notification for new message
                    if (timestamp != null) {
                        showNewMessageNotification( title, body, timestamp)
                    }
                }
            } catch (e: Exception) {
                // Handle the exception here
            }
        }
    }



    private suspend fun removeUser(userId: String) {
        withContext(Dispatchers.IO) {
            try {
                userDao.deleteUserById(userId)
                // Remove any other related data if needed
            } catch (e: Exception) {
                Log.d("removeUser", "Error: ${e.message}")
            }
        }
    }

    private suspend fun removeGroup(groupId: String) {
        withContext(Dispatchers.IO) {
            try {
                groupDao.deleteGroupById(groupId)
                // Remove any other related data if needed
            } catch (e: Exception) {
                Log.d("removeGroup", "Error: ${e.message}")
            }
        }
    }

    private suspend fun deleteMessage(messageId: String) {
        withContext(Dispatchers.IO) {
            try {
                messagesDao.deleteMessageById(messageId)
                // Handle any other related data removal if needed
            } catch (e: Exception) {
                Log.d("deleteMessage", "Error: ${e.message}")
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

    private fun showNewMessageNotification(title: String, body: String, timestamp: Timestamp) {
        val channelId = "chilli_notification_channel"
        val notificationId = 1

        val currentTime = System.currentTimeMillis()
        val messageTime = timestamp.time

        val timeDifferenceMinutes = (currentTime - messageTime) / (1000 * 60)

        if (timeDifferenceMinutes <= 5) {
            val intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            val pendingIntent = PendingIntent.getActivity(
                context,
                1,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

            // Create a notification builder
            val builder = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

            // Show the notification
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    channelId,
                    "New Message",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                notificationManager.createNotificationChannel(channel)
            }
            notificationManager.notify(notificationId, builder.build())
        }
    }

}

