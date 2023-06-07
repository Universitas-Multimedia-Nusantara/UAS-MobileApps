package com.example.chilli.database


import androidx.room.Entity

import androidx.room.PrimaryKey

import androidx.room.TypeConverters
import java.sql.Timestamp




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
    @TypeConverters
    val pinTime: String?,
    @TypeConverters(TimestampConverter::class)
    val timestamp: Timestamp?,
    val sender: String?,
    val body: String?
): java.io.Serializable

@Entity(tableName = "Group")
data class Group(
    @PrimaryKey val groupId: String,
    val deskripsi: String,
    val foto: String?,
    val nama: String,
    @TypeConverters(UserConverter::class)
    var user: List<Map<String, String>>?
)


@Entity(tableName = "User")
data class User(
    @PrimaryKey
    var userId: String,
    val email: String,
    val foto: String? = null,
    @TypeConverters(Converter::class)
    var group: List<String>? = null,
    val name: String,
    val nickName: String
)

