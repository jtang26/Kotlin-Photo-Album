package com.example.photoalbum

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.photoalbum.Data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var picButton: Button
    private lateinit var createButton: Button
    private lateinit var logoutButton: Button
    private lateinit var userListButton: Button
    private lateinit var publicAlbum: Button
    private lateinit var privateAlbum: Button
    public lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        picButton = btn_camera
        createButton = btn_creator
        logoutButton = btn_logout
        userListButton = btn_userlist
        publicAlbum = btn_public_album
        privateAlbum = btn_private_album

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        if (auth.currentUser == null) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        } else {
//            Toast.makeText(this, "Already logged in", Toast.LENGTH_LONG).show()
        }

        picButton.setOnClickListener(){
            val intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
            finish()
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

        userListButton.setOnClickListener() {
            val id = auth.currentUser!!.uid
            var username: String?
            username = ""
            val usernameDoc = db.collection("users").whereEqualTo(id, true)
            usernameDoc.get().addOnSuccessListener { documentSnapshot ->
                var dataID = documentSnapshot.toObjects(User::class.java)
                if(dataID.size!=0) {
                    username = dataID[0].username
                }
                val intent = Intent(this, UserListActivity::class.java)
                intent.putExtra("username", username)
                startActivity(intent)
                finish()
            }
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
