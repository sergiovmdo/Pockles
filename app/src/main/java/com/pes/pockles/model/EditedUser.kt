package com.pes.pockles.model

import java.io.Serializable

data class EditedUser(
    var name: String,
    var profileImageUrl: String,
    var radiusVisibility: Float,
    var accentColor: String
): Serializable