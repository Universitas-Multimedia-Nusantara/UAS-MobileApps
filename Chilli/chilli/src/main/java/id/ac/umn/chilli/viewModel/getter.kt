package id.ac.umn.chilli.viewModel

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner

import com.google.firebase.auth.FirebaseAuth

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import id.ac.umn.chilli.database.AppDatabase

val auth = FirebaseAuth.getInstance()

class getFirebase(){
    val firestore = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val userCollection: CollectionReference = firestore.collection("User")
    val groupCollection: CollectionReference = firestore.collection("Group")
    val messagesCollection: CollectionReference = firestore.collection("BroadcastMessages")
}

class getUser(context: ViewModelStoreOwner, application: Application) {
    val dataSource = AppDatabase.getInstance(application).userDao
    val factory = UserViewModelFactory(auth.uid!!,dataSource, application)
    val userViewModel = ViewModelProvider(context, factory)[UserViewModel::class.java]
}


class getGroup(context: ViewModelStoreOwner, application: Application){
    val dataSource = AppDatabase.getInstance(application).groupDao
    val factory = GrupViewModelFactory(dataSource, application)
    val ViewModel = ViewModelProvider(context, factory)[GrupViewModel::class.java]

    init {
        ViewModel.syncGroup()
    }

}

class getMessage(context: ViewModelStoreOwner, application: Application){
    val message = AppDatabase.getInstance(application).messagesDao
    val factory = MessageViewModelFactory(message, application)
    val ViewModel = ViewModelProvider(context, factory)[MessageViewModel::class.java]
}

class getGroupMessage(context: ViewModelStoreOwner, application: Application){
    val GroupMessage = AppDatabase.getInstance(application).broadcastMessageDao
    val factory2 = MessageGroupViewModelFactory(GroupMessage, application)
    val viewModel = ViewModelProvider(context, factory2)[MessageGroupViewModel::class.java]
}


