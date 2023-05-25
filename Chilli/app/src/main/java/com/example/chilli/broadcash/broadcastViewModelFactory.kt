package com.example.chilli.broadcash

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.chilli.database.BroadcastMessageDao
import com.example.chilli.database.GroupDao
import com.example.chilli.database.MessagesDao
import com.example.chilli.database.UserDao
import com.example.chilli.profile.ProfileViewModel

class broadcastViewModelFactory(private val messageDao: MessagesDao, private val groupDao: GroupDao, private val application: Application) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(broadcastViewModel::class.java)) {
            return broadcastViewModel(messageDao, groupDao, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
