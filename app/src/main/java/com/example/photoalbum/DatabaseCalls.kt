package com.example.photoalbum

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.photoalbum.Data.Album
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class DatabaseCalls:AppCompatActivity() {


    fun addUserToAllowedUsers() {
        var db: FirebaseFirestore
        db = FirebaseFirestore.getInstance()
        var auth: FirebaseAuth
        auth = FirebaseAuth.getInstance()
        val document = db.collection("albums").document(auth.currentUser!!.uid)
        document.get()
            .addOnSuccessListener { documentSnapshot ->
                var data = documentSnapshot.toObject(Album::class.java)
                var currentUserList = data!!.allowedUserList
            }


    }

}