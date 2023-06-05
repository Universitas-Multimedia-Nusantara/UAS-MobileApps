package com.example.chilli.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
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

    fun groupMessage(id: List<String>?): Flow<List<Messages>> = messageDao.getMessagesById(id)
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

    fun getGroupData(groups: List<String>?) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val broadcastList = mutableListOf<Messages>()
                if (groups != null) {
                    groupMessage(groups).collect { data ->
                        Log.d("groupMessage data", "$data")
                        broadcastList.addAll(data)
                        Log.d("data message semua", "$broadcastList")
                        message.postValue(broadcastList)
                    }

                }
            }
        }
    }
}
