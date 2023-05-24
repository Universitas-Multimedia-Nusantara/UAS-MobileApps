package com.example.chilli.profile

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.chilli.database.UserDao


class ProfileViewModelFactory(private val userId:String,private val userDao: UserDao, private val application: Application) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            return ProfileViewModel(userId, userDao, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
