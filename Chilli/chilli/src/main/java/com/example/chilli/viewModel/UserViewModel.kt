package com.example.chilli.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.chilli.database.User
import com.example.chilli.database.UserDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserViewModel(private val userId: String, private val database: UserDao, application: Application) : AndroidViewModel(application) {
    val name: MutableLiveData<String> = MutableLiveData()
    val nickname: MutableLiveData<String> = MutableLiveData()
    val email: MutableLiveData<String> = MutableLiveData()
    private val updatedDataFlow: Flow<User> = database.getUser(userId)

    init {
        startCollectingData()
    }

    fun startCollectingData() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                updatedDataFlow.collect { data ->
                    Log.d("data", "$data")
                    name.postValue(data.name)
                    nickname.postValue(data.nickName)
                    email.postValue(data.nickName)
                }
            }
        }
    }

}
