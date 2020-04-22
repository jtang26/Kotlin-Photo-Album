package com.example.photoalbum

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.photoalbum.Adapter.ModListAdapter
import com.example.photoalbum.Data.Album
import com.example.photoalbum.Data.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.mod_list_layout.*
import kotlinx.android.synthetic.main.user_list_layout.back_button

class ModListActivity : AppCompatActivity() {

    public lateinit var db: FirebaseFirestore
    private lateinit var modBackButton: Button
    private lateinit var allowedUsers: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mod_list_layout)
        val data = intent.extras
        val albumName = data!!.getString("albumNamed").toString()
        db = FirebaseFirestore.getInstance()
        modBackButton = mod_back_button
        allowedUsers = ArrayList<String>()

        val document = db.collection("albums").document(albumName)
        document.get().addOnSuccessListener { documentSnapshot ->
            var dataAlbum = documentSnapshot.toObject(Album::class.java)
            allowedUsers = dataAlbum!!.allowedUserList
            val adapter = ModListAdapter(allowedUsers, albumName)
           mod_recycler_view.adapter = adapter
            mod_recycler_view.layoutManager = LinearLayoutManager(this)
        }

        modBackButton.setOnClickListener() {
            val intent = Intent(this, AlbumViewActivity::class.java)
            intent.putExtra("name", albumName)
            startActivity(intent)
            finish()
        }

    }
}