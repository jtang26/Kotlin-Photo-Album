package com.example.photoalbum

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.example.photoalbum.Data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var createButton: Button
    private lateinit var logoutButton: Button
    private lateinit var publicAlbum: Button
    private lateinit var privateAlbum: Button
    public lateinit var db: FirebaseFirestore
    private lateinit var mainView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createButton = btn_creator
        logoutButton = btn_logout
        publicAlbum = btn_public_album
        privateAlbum = btn_private_album
        mainView = main_title_view

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()


        if (auth.currentUser == null) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            db.collection("users").document(auth.currentUser!!.uid)
            .get().addOnSuccessListener { documentSnapshot ->
                var data = documentSnapshot.toObject(User::class.java)
                var username = data?.username
                mainView.text = "Welcome: " + username
            }
        }

        createButton.setOnClickListener(){
            val intent = Intent(this, AlbumCreatorActivity::class.java)
            startActivity(intent)
            finish()
        }

        btn_logout.setOnClickListener() {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }


        publicAlbum.setOnClickListener() {
            val intent = Intent(this, PublicAlbumListActivity::class.java)
            startActivity(intent)
            finish()
        }

        privateAlbum.setOnClickListener() {
            val intent = Intent(this, PrivateAlbumListActivity::class.java)
            startActivity(intent)
            finish()
        }


    }
}
