package com.example.firebase_test

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

//import kotlinx.parcelize.Parcelize

@Parcelize
data class UserDTO(
    var uId : String? = null,
    var userId : String? = null,
    var token : String? = null,
    var age : String? = null,
    var height : String? = null,
    var weight : String? = null,
) : Parcelable


data class MessageDTO(
    var fromUid : String? = null,
    var toUid : String? = null,
    var content : String? = null,
    var timestamp : Long? = null
)

data class CalendarDTO(
    var health : String? = null,
    var otherhealth : String? = null,
    var day : String? = null

)

data class MatchDTO(
    var age : String? = null,
    var height : String? = null,
    var token : String? = null,
    var uid : String? = null,
    var userId : String? = null,
    var weight : String? = null,
)


/*
data class MatchDTO(
    var otherUid : String? = null


)*/
