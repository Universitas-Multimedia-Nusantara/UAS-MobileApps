package com.example.chilli.database


import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

import androidx.room.TypeConverters
import com.example.chilli.profile.ProfileFragment
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties

@Entity(tableName = "BroadcastMessages")
data class BroadcastMessage(
    @PrimaryKey val groupId: String,
    @TypeConverters(Converter::class)
    val messageId: List<String>
)

@Entity(tableName = "Messages")
data class Messages(
    @PrimaryKey val messageId: String,
    val title: String,
    val files: String?,
    val pinTime: String,
    val timestamp: String,
    val sender: String,
    val body: String
)

@Entity(tableName = "Group")
data class Group(
    @PrimaryKey val groupId: String,
    val deskripsi: String,
    val foto: String?,
    val nama: String,
    @TypeConverters(userConverter::class)
    val user: List<Map<String, String>>?
)


@Entity(tableName = "User")
data class User(
    @PrimaryKey
    var userId: String,
    val email: String = "",
    val foto: String? = null,
    @TypeConverters(Converter::class)
    val group: List<String>? = null,
    val name: String = "",
    val nickName: String = ""
)

