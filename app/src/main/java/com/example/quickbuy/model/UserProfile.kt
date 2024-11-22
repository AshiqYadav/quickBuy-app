package com.example.quickbuy.model

data class UserProfile(
    var id : String = "",
    var username : String = "",
    var email : String = "",
    var firstName : String = "",
    var lastName : String = "",
    var phoneNo : String = "",
    var address : String = "",
    var profilePictureUrl: String? = null
)
