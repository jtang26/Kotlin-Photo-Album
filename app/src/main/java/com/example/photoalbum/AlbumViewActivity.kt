package com.example.photoalbum

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.photoalbum.Adapter.PhotoAlbumAdapter
import com.example.photoalbum.Data.Album
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.album_layout.*
import android.R.attr.keySet
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import androidx.core.content.ContextCompat.startActivity


class AlbumViewActivity:AppCompatActivity() {

    lateinit var db: FirebaseFirestore
    lateinit var albumTitle: TextView
    lateinit var album: Album
    lateinit var backButton: Button
    lateinit var picList: ArrayList<String>
    private lateinit var userListButton: Button
    lateinit var albumNamed: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.album_layout)

        //Set Views
        albumTitle = album_title
        album = Album()
        backButton = back_btn
        picList = ArrayList<String>()
        userListButton = btn_userlist
        albumNamed = ""

        //set instance of firestore
        db = FirebaseFirestore.getInstance()

        // Get album name
        val bundle = intent.extras
        val albumName: String = bundle!!.getString("name","name")


        //Query Firebase for album name
        val document: Query = db.collection("albums").whereEqualTo("albumName",albumName)
        document.get().addOnSuccessListener { documentSnapshot ->
            var albumList = documentSnapshot.toObjects(Album::class.java)
            album = albumList[0]
            picList = album.pictures
            val recyclerView: RecyclerView = grid_view
            val adapter = PhotoAlbumAdapter(picList as ArrayList<String>)
            val numOfColumns = 2
            recyclerView.adapter = adapter
            recyclerView.layoutManager = GridLayoutManager(this,numOfColumns)
            albumTitle.text = album.albumName.toString()
            albumNamed = album.albumName.toString()
            val doc: Query = db.collection("albums").whereEqualTo("albumName", album.albumName.toString())
            doc.get().addOnSuccessListener { documentSnap ->
                var thisAlbum = documentSnap.toObjects(Album::class.java)
                if(thisAlbum[0].isPublic==false) {
                    userListButton.visibility = View.INVISIBLE

                }
            }
        }

        backButton.setOnClickListener(){
            finish()
        }

        userListButton.setOnClickListener() {
            val intent = Intent(this, UserListActivity::class.java)
            intent.putExtra("albumNamed", albumNamed)
            startActivity(intent)
            finish()
        }





    }
}