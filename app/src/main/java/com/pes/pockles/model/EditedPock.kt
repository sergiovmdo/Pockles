package com.pes.pockles.model

import java.io.Serializable

data class EditedPock(
    val message: String,
    val category: String,
    val chatAccess: Boolean,
    val media: List<String>?
): Serializable