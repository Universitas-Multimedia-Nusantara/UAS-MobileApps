package id.ac.umn.chilli.viewModel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import id.ac.umn.chilli.database.MessagesDao

class MessageViewModelFactory(private val messageDao: MessagesDao, private val application: Application) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MessageViewModel::class.java)) {
            return MessageViewModel(messageDao, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
