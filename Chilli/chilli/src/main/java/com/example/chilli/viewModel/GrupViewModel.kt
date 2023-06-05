package com.example.chilli.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.chilli.database.AppDatabase
import com.example.chilli.database.Group
import com.example.chilli.database.GroupDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GrupViewModel( private val grupDao: GroupDao, private val application: Application): AndroidViewModel(application) {
    var Data: MutableLiveData<List<Group>> = MutableLiveData()
    private val grupData: Flow<List<Group>> = grupDao.getAllGroups()
    val admin: MutableLiveData<Boolean> = MutableLiveData()
    fun syncGroup(){
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                    grupData.collect{ data->
                        Log.d("Group Data", "$data")
                        Data.postValue(data)
                    }
            }
        }
    }

    fun isAdmin(groupId: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val group = grupDao.getGroupById(groupId)
                val valid = group?.user?.any { it["type"] == "admin" && it["userId"] == auth.uid!!.toString() } == true
                admin.postValue(valid)
            }
        }
    }
}