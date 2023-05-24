package com.example.chilli.database


import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters

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
    @PrimaryKey val userId: String,
    val email: String,
    val foto: String?,
    @TypeConverters(Converter::class)
    val group: List<String>?,
    val name: String,
    val nickName: String
)


