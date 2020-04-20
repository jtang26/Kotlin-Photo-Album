package com.example.photoalbum

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.photoalbum.Data.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.user_list_layout.*

class UserListActivity: AppCompatActivity() {

    public lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_list_layout)
        val data = intent.extras
        val albumName = data!!.getString("albumNamed").toString()
        db = FirebaseFirestore.getInstance()

        val document = db.collection("users").orderBy("username", Query.Direction.ASCENDING)
        document.get().addOnSuccessListener { documentSnapshot ->
            var data = documentSnapshot.toObjects(User::class.java)
            val adapter = UserListAdapter(data, albumName)
            leader_recycler_view.adapter = adapter
            leader_recycler_view.layoutManager = LinearLayoutManager(this)
        }

        back_button.setOnClickListener() {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }


}