package com.example.photoalbum

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.photoalbum.Adapter.AlbumListAdapter
import com.example.photoalbum.Adapter.RecyclerItemClickListener
import com.example.photoalbum.Data.Album
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.private_album_list_layout.*
import kotlinx.android.synthetic.main.signup.view.*


class PrivateAlbumListActivity:AppCompatActivity() {

    lateinit var db: FirebaseFirestore
    lateinit var albums: MutableList<Album>
    lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.private_album_list_layout)



        //set instance of firestore
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()


        val document: Query = db.collection("albums").whereEqualTo("isPublic",false)
            .whereEqualTo("owner", auth.currentUser!!.email)
        document.get().addOnSuccessListener { documentSnapshot ->
            var albumList = documentSnapshot.toObjects(Album::class.java)
            albums = albumList
            // set recycler view
            val decorator = DividerItemDecoration(applicationContext, LinearLayoutManager.VERTICAL)
            val recyclerView: RecyclerView = private_album_recycler_view
            val adapter = AlbumListAdapter(albumList)
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.addItemDecoration(decorator)
            Log.d("PublicAlbumListActivity", "Public Album List = " + albumList)

        }




        back_button.setOnClickListener(){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }



    private_album_recycler_view.addOnItemTouchListener(RecyclerItemClickListener(this, private_album_recycler_view, object : RecyclerItemClickListener.OnItemClickListener {

        override fun onItemClick(view: View, position: Int) {
            println("album clicked, position: " + position )

            val album: Album = albums.get(position)
            //Get info that i want to show in a player profile scree
            val albumName: String? = album.albumName


            val intent = Intent(baseContext,AlbumViewActivity::class.java)
            val bundle = Bundle()
            bundle.putString("name",albumName)
            intent.putExtras(bundle)

            startActivity(intent)
        }
        override fun onItemLongClick(view: View?, position: Int) {

        }
    }))

    }


}



