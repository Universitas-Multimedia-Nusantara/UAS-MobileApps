package id.ac.umn.chilli.broadcash

import com.google.firebase.Timestamp

data class broadcast(var title: String ?= null, var body: String ?= null, var timestamp: Timestamp? = null ) {
    fun contains(query: String, ignoreCase: Boolean) {

    }
}

data class groupBroadcast(var name:String ?= null)