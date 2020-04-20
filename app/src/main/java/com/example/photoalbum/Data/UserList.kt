package com.example.photoalbum.Data

data class User (
    val email: String? = null,
    val username: String? = null,
    val userID: String? = null,
    val albumLink: String? = null
)


data class Album (
    val albumDescription: String? = null,
    val albumName: String? = null,
    val albumLink: String? = null,
    //Stores userids or usernames that can view album
    val allowedUserList: ArrayList<String> = ArrayList<String>(),
    //Stores links to Firebase Storage for each picture
    val comments:ArrayList<Comments> = ArrayList<Comments>(),
    //Stores arraylist of userids or usernames that are mods
    val isModList: ArrayList<String> = ArrayList<String>(),
    val owner: String? = null,
    val pictures: ArrayList<String> = ArrayList<String>(),
    //Source for @Field : https://stackoverflow.com/questions/46406376/kotlin-class-does-not-get-its-boolean-value-from-firebase
    @field:JvmField val isPublic: Boolean? = null

)






data class Comments (
    val commentBody: String? = null,
    val commentAuthor: String? = null
)
