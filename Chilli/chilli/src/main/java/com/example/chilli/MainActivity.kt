package com.example.chilli
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.room.Database
import com.example.chilli.database.*
import com.example.chilli.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var repository: Repository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(this.layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val broadcastMessageDao = AppDatabase.getInstance(application).broadcastMessageDao
        val messagesDao = AppDatabase.getInstance(application).messagesDao
        val groupDao = AppDatabase.getInstance(application).groupDao
        val userDao = AppDatabase.getInstance(application).userDao

        repository = Repository(firestore, broadcastMessageDao, messagesDao, groupDao, userDao)

        val userId = auth.currentUser?.uid!!
        repository.syncUser(userId)

        navController = findNavController(R.id.host_fragment)
        binding.bottomNavigation.setupWithNavController(navController)
    }
}
