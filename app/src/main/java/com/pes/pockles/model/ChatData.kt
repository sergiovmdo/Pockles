package com.pes.pockles.model

import android.os.Parcel
import android.os.Parcelable

data class ChatData(
    var chatId: String?,
    var pockId: String?,
    var userName: String,
    var profileImageUrl: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString()!!,
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(chatId)
        parcel.writeString(pockId)
        parcel.writeString(userName)
        parcel.writeString(profileImageUrl)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ChatData> {
        override fun createFromParcel(parcel: Parcel): ChatData {
            return ChatData(parcel)
        }

        override fun newArray(size: Int): Array<ChatData?> {
            return arrayOfNulls(size)
        }
    }

}