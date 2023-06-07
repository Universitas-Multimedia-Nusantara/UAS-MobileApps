package com.example.chilli

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.chilli.database.*
import com.example.chilli.databinding.ActivityMainBinding
import com.example.chilli.searchGroup.SearchGroupFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var repository: Repository

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val broadcastMessageDao = AppDatabase.getInstance(application).broadcastMessageDao
        val messagesDao = AppDatabase.getInstance(application).messagesDao
        val groupDao = AppDatabase.getInstance(application).groupDao
        val userDao = AppDatabase.getInstance(application).userDao

        repository = Repository(this, firestore, broadcastMessageDao, messagesDao, groupDao, userDao)

        val userId = auth.currentUser?.uid!!
        repository.syncUser(userId)

        navController = findNavController(R.id.host_fragment)
        binding.bottomNavigation.setOnNavigationItemSelectedListener { menuItem ->
            val startDestination = navController.graph.startDestinationId

            if (menuItem.itemId == startDestination) {
                navController.popBackStack(startDestination, false)
            } else {
                navController.navigate(menuItem.itemId)
            }

            true
        }


//        binding.bottomNavigation.setupWithNavController(navController)



        setupPermission()
    }

        private fun setupPermission() {
            val permission = ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.POST_NOTIFICATIONS
            )

            if (permission != PackageManager.PERMISSION_GRANTED) {
                makeRequest()
            }
        }

        private fun makeRequest() {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                POST_NOTIFICATIONS_REQUEST_CODE
            )
        }

        companion object {
            private const val POST_NOTIFICATIONS_REQUEST_CODE = 1
        }

}
