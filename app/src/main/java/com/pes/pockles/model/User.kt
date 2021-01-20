package com.pes.pockles.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "User", indices = [Index("id")])
data class User(
    @PrimaryKey
    val id: String,
    val name: String,
    val birthDate: String,
    val mail: String,
    @SerializedName("profileImageUrl")
    val profileImage: String,
    val radiusVisibility: Float,
    val accentColor: String,
    val badge: Int, // todo: change to badge once available
    @SerializedName("pocks")
    val createdPocksNumber: Int
)