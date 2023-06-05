package com.example.chilli.viewModel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.chilli.database.GroupDao

class GrupViewModelFactory(private val grupDao: GroupDao, private val application: Application) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GrupViewModel::class.java)) {
            return GrupViewModel(grupDao, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
