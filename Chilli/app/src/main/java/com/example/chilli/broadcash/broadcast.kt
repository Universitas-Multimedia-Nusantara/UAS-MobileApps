package com.example.chilli.broadcash

import com.google.firebase.Timestamp

data class broadcast(var title: String ?= null, var body: String ?= null, var timestamp: Timestamp? = null )
data class groupBroadcast(var name:String ?= null)