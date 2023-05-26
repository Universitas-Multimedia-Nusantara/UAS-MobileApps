package com.example.chilli.broadcash

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

class broadcastViewModel(
    private val messageDao: MessagesDao,
    private val groupDao: GroupDao,
    application: Application
) : AndroidViewModel(application) {
    val group: MutableLiveData<List<String>> = MutableLiveData()
    val message: MutableLiveData<List<Messages>> = MutableLiveData()
    val updatedMessage: Flow<List<Messages>> = messageDao.getAllMessages()


    init {
        startCollectingData()
    }

//    fun startCollectingData() {
//        viewModelScope.launch {
//            withContext(Dispatchers.IO) {
//                updatedMessage.collect { data ->
//                    val broadcastList = mutableListOf<Messages>()
//                    for (singleData in data) {
//                        broadcastList.add(singleData)
//                    }
//
//                    message.value = broadcastList
//                }
//            }
//        }
//    }

    fun startCollectingData() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                updatedMessage.collect { data ->
                    Log.d("data", "$data")
                    val broadcastList = data.toMutableList()
                    message.postValue(broadcastList)
                }
            }
        }
    }


//    private fun initializeMessage() {
//        viewModelScope.launch {
//            updatedMessage.collect { messageData ->
//                val convertedList = ArrayList<broadcast>(messageData.size)
//                messageData.forEach { broadcastMessage ->
//                    convertedList.add(convertToBroadcast(broadcastMessage))
//                }
//                message.value = convertedList
//            }
//        }
//    }
//
//    private fun convertToBroadcast(broadcastMessage: Messages): broadcast {
//        val convertedBroadcast = broadcast()
//
//        return convertedBroadcast
//    }
}
