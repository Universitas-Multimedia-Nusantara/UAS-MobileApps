package id.ac.umn.chilli.viewModel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import id.ac.umn.chilli.database.BroadcastMessageDao

class MessageGroupViewModelFactory(private val messageDao: BroadcastMessageDao, private val application: Application) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MessageGroupViewModel::class.java)) {
            return MessageGroupViewModel(messageDao, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
