package com.pes.pockles.model

import android.os.Parcel
import android.os.Parcelable

data class CreateUser(
    val id: String,
    var name: String?,
    var birthDate: String?,
    var mail: String,
    var profileImageUrl: String,
    var radiusVisibility: Float,
    var accentColor: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString(),
        parcel.readString(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readFloat(),
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(birthDate)
        parcel.writeString(mail)
        parcel.writeString(profileImageUrl)
        parcel.writeFloat(radiusVisibility)
        parcel.writeString(accentColor)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CreateUser> {
        override fun createFromParcel(parcel: Parcel): CreateUser {
            return CreateUser(parcel)
        }

        override fun newArray(size: Int): Array<CreateUser?> {
            return arrayOfNulls(size)
        }
    }

}