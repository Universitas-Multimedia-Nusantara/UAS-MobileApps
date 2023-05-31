package com.example.chilli.viewModel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.chilli.database.GroupDao
import com.example.chilli.database.MessagesDao

class MessageViewModelFactory(private val messageDao: MessagesDao, private val application: Application) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MessageViewModel::class.java)) {
            return MessageViewModel(messageDao, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
