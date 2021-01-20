package com.pes.pockles.model

data class Pock(
    val id: String,
    val message: String,
    val media: List<String>?,
    val category: String,
    val chatAccess: Boolean,
    val location: Location,
    val dateInserted: Long = 0L,
    val username: String = "Carlos",
    val user: String,
    var likes: Int = 0,
    var liked: Boolean = false,
    var userProfileImage: String? = "",
    var reports: Int =0,
    var reported: Boolean = false,
    val editable: Boolean = false
)
