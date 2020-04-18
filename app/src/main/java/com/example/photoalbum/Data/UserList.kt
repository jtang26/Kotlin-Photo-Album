package com.example.photoalbum.Data

data class User (
    val email: String? = null,
    val username: String? = null,
    val albumLink: String? = null
)

data class Album (
    val owner: String? = null,
    val comments: ArrayList<Comments>,
    //Stores links to Firebase Storage for each picture
    val pictures: ArrayList<String>,
    //Stores userids or usernames that can view album
    val allowedUserList: ArrayList<String>,
    //Stores arraylist of userids or usernames that are mods
    val isModList: ArrayList<String>,
    val isPublic: Boolean? = null,
    val albumName: String? = null,
    val albumLink: String? = null,
    val albumDescription: String? = null
)

data class Comments (
    val commentBody: String? = null,
    val commentAuthor: String? = null
)
