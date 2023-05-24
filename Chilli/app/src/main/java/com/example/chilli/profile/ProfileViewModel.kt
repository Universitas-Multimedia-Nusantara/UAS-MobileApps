package com.example.chilli.profile

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.chilli.database.User
import com.example.chilli.database.UserDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileViewModel(private val userId: String, private val database: UserDao, application: Application) : AndroidViewModel(application) {
    val name: MutableLiveData<String> = MutableLiveData()
    val nickname: MutableLiveData<String> = MutableLiveData()
     val email: MutableLiveData<String> = MutableLiveData()

    init {
        initialProfile()
    }

    private fun initialProfile() {
        viewModelScope.launch {
            val userData = getUserData()
            name.value = userData?.name
            nickname.value = userData?.nickName
            email.value = userData?.email
            Log.d("Profile", "Name: ${name.value}")
            Log.d("Profile", "Nickname: ${nickname.value}")
            Log.d("Profile", "Email: ${email.value}")
        }
    }

    private suspend fun getUserData(): User? {
        return withContext(Dispatchers.IO) {
            database.getAllUsers(userId)
        }
    }
}
