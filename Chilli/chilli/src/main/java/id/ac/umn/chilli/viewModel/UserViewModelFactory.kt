package id.ac.umn.chilli.viewModel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import id.ac.umn.chilli.database.UserDao

class UserViewModelFactory(private val userId:String, private val userDao: UserDao, private val application: Application) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            return UserViewModel(userId, userDao, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
