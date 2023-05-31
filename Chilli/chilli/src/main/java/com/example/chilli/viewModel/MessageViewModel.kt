package com.example.chilli.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.chilli.database.GroupDao
import com.example.chilli.database.Messages
import com.example.chilli.database.MessagesDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MessageViewModel(
    private val messageDao: MessagesDao,
    application: Application
) : AndroidViewModel(application) {

    val message: MutableLiveData<List<Messages>> = MutableLiveData()
    val updatedMessage: Flow<List<Messages>> by lazy() { messageDao.getAllMessages() }
    val singleMessage: Flow<List<Messages>> by lazy() { messageDao.getAllMessages() }

    init {
        startCollectingData()
    }


    fun startCollectingData() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                updatedMessage.collect { data ->
                    Log.d("data message", "$data")
                    val broadcastList = data.toMutableList()
                    message.postValue(broadcastList)
                }
            }
        }
    }
}
