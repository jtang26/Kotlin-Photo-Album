package com.example.photoalbum

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.photoalbum.Adapter.PhotoAlbumAdapter
import com.example.photoalbum.Data.Album
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.album_layout.*

class AlbumViewActivity:AppCompatActivity() {

    lateinit var db: FirebaseFirestore
    lateinit var albumTitle: TextView
    lateinit var album: Album
    lateinit var backButton: Button
    lateinit var picList: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.album_layout)

        //Set Views
        albumTitle = album_title
        album = Album()
        backButton = back_btn
        picList = ArrayList<String>()

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


        }

        backButton.setOnClickListener(){
            finish()
        }








    }
}