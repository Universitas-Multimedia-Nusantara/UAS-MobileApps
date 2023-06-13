package id.ac.umn.chilli.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import id.ac.umn.chilli.database.BroadcastMessage
import id.ac.umn.chilli.database.BroadcastMessageDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MessageGroupViewModel(private val messageGroupDao: BroadcastMessageDao,
                            application: Application
) : AndroidViewModel(application) {
    val message: MutableLiveData<List<String>> = MutableLiveData()
    fun updatedMessage(id: String): Flow<List<BroadcastMessage>> = messageGroupDao.getAllBroadcastMessages(id)



    fun startCollectingData(id: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                updatedMessage(id).collect { data ->
                    Log.d("data message", "$data")
                    val broadcastMessageIds = data.flatMap { it.messageId }
                    Log.d("list data", "$broadcastMessageIds")
                    message.postValue(broadcastMessageIds)
                }
            }
        }
    }
}