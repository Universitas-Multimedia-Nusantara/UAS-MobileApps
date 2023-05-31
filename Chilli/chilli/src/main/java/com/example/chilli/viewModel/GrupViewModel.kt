package com.example.chilli.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.chilli.database.Group
import com.example.chilli.database.GroupDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GrupViewModel(private val grupDao: GroupDao, private val application: Application): AndroidViewModel(application) {
    var Data: MutableLiveData<List<Group>> = MutableLiveData()
    private var grupData: Flow<List<Group>> = grupDao.getAllGroups()

    init {
        syncGroup()
    }

    private fun syncGroup(){
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                grupData.collect{data->
                    Data.postValue(data)
                }
            }
        }
    }

}