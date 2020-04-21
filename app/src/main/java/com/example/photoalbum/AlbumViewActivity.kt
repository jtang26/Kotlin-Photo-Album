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
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.startActivity
import com.example.photoalbum.Adapter.RecyclerItemClickListener


class AlbumViewActivity:AppCompatActivity() {

    lateinit var db: FirebaseFirestore
    lateinit var albumTitle: TextView
    lateinit var album: Album
    lateinit var backButton: Button
    lateinit var deleteButton: Button
    lateinit var picList: ArrayList<String>
    private lateinit var userListButton: Button
    private lateinit var commentsButton: Button
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
        deleteButton = btn_delete
        commentsButton = btn_comments

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
                    commentsButton.visibility = View.INVISIBLE
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

        commentsButton.setOnClickListener() {
            val intent = Intent(this, CommentsActivity::class.java)
            intent.putExtra("albumNamed", albumNamed)
            startActivity(intent)
            finish()
        }

        grid_view.addOnItemTouchListener(RecyclerItemClickListener(this, grid_view, object : RecyclerItemClickListener.OnItemClickListener {

            override fun onItemClick(view: View, position: Int) {
                println("photo clicked, position: " + position )

                val photoUrl: String = picList.get(position)
                val photoPosition = position


                val intent = Intent(baseContext,PhotoViewActivity::class.java)
                val bundle = Bundle()
                bundle.putString("url",photoUrl)
                bundle.putInt("position",photoPosition)
                bundle.putString("album_name",albumName)
                intent.putExtras(bundle)

                startActivity(intent)
            }
            override fun onItemLongClick(view: View?, position: Int) {

            }
        }))



        deleteButton.setOnClickListener(){
            val builder = AlertDialog.Builder(this)

            builder.setMessage("Are you sure you want to delete this photo album?")

            builder.setPositiveButton("Yes") { dialog, which ->

                val albumRef = db.collection("albums").document(albumName)
                albumRef
                    .delete()
                    .addOnSuccessListener { Toast.makeText(applicationContext, "Album deleted!", Toast.LENGTH_SHORT).show()
                    }


                if (album.isPublic==true){
                    val intent = Intent(this, PublicAlbumListActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                else{
                    val intent = Intent(this, PrivateAlbumListActivity::class.java)
                    startActivity(intent)
                    finish()
                }

            }
            builder.setNegativeButton("No") { dialog, which ->
                dialog.cancel()
            }

            val dialog: AlertDialog = builder.create()
            dialog.setCancelable(false)
            dialog.show()
        }



    }
}