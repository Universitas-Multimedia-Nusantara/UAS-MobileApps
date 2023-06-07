package com.example.chilli.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.chilli.database.AppDatabase
import com.example.chilli.database.Group
import com.example.chilli.database.GroupDao
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GrupViewModel( private val grupDao: GroupDao, private val application: Application): AndroidViewModel(application) {
    var Data: MutableLiveData<List<Group>> = MutableLiveData()
    var group: MutableLiveData<Group?> = MutableLiveData()
    private val grupData: Flow<List<Group>> = grupDao.getAllGroups()
    val admin: MutableLiveData<Boolean> = MutableLiveData()
    val auth = FirebaseAuth.getInstance()
    fun singleData(id: String):Group?{
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                   val newgroup = grupDao.getGroupById(id)
                    group.postValue(newgroup)
                    return@withContext newgroup
                }
            } catch (e: Exception) {
                // Handle the exception here
                // For example, you can log the error or show an error message
                Log.e("singleData", "Error retrieving group data: ${e.message}", e)
            }
        }
        return null
    }



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
                val valid = group?.user?.any { it["type"] == "admin" && it["userId"] == auth.uid.toString() } == true
                admin.postValue(valid)
            }
        }
    }


}