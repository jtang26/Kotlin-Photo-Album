package com.example.photoalbum

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.photoalbum.Adapter.ModListAdapter
import com.example.photoalbum.Data.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.mod_list_layout.*
import kotlinx.android.synthetic.main.user_list_layout.back_button

class ModListActivity : AppCompatActivity() {

    public lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mod_list_layout)
        val data = intent.extras
        val username = data!!.getString("username").toString()
        db = FirebaseFirestore.getInstance()

        val document = db.collection("album").orderBy("allowedUserList", Query.Direction.ASCENDING)
        document.get().addOnSuccessListener { documentSnapshot ->
            var data = documentSnapshot.toObjects(User::class.java)
            val adapter = ModListAdapter(data, username)
           mod_recycler_view.adapter = adapter
            mod_recycler_view.layoutManager = LinearLayoutManager(this)
        }

        back_button.setOnClickListener() {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
}