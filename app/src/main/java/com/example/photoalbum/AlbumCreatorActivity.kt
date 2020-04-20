package com.example.photoalbum

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.photoalbum.Data.Album
import com.example.photoalbum.Data.Comments
import com.example.photoalbum.Data.User
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import kotlinx.android.synthetic.main.album_creator_layout.*

class AlbumCreatorActivity: AppCompatActivity() {

    lateinit var albumName: EditText
    lateinit var albumDescription: EditText
    lateinit var albumType: Switch
    lateinit var createAlbumButton: Button
    //creat instance of FirebaseFirestore
    lateinit var db : FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    lateinit var allowedUserListed: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.album_creator_layout)
        auth = FirebaseAuth.getInstance()
        //set instance of firestore
        db = FirebaseFirestore.getInstance()
        val settings = FirebaseFirestoreSettings.Builder()
            .setTimestampsInSnapshotsEnabled(true)
            .build()
        db.setFirestoreSettings(settings)

        albumName = album_name
        albumDescription = album_description
        albumType = switch1
        createAlbumButton = createButton
        var allowedUserListed = ArrayList<String>()

        var typePublic = true

        var public: String = "Public"
        var private: String = "Private"




        albumType.setOnCheckedChangeListener({ _ , isChecked ->
            typePublic = false
            albumType.text = private
            if (isChecked!=true){
                typePublic = true
                albumType.text = public
            }

        })




        createAlbumButton.setOnClickListener(){
            var name = albumName.text.toString()
            var description = albumDescription.text.toString()

            if(TextUtils.isEmpty(name) || TextUtils.isEmpty(description)) {
                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_LONG).show()
            }
            else{
                val userId:String = FirebaseAuth.getInstance().currentUser!!.uid


                var albumName = name
                var albumDesc = description
                var owner = auth.currentUser!!.email
                val isModList = ArrayList<String>()
                val comments = ArrayList<Comments>()
                val pictures = ArrayList<String>()
                //Stores userids or usernames that can view album
                db.collection("users").document(auth.currentUser!!.uid)
                    .get().addOnSuccessListener { documentSnapshot ->
                        var data = documentSnapshot.toObject(User::class.java)
                        var currentUserUsername = data!!.username
                        allowedUserListed.add(currentUserUsername as String)
                        var newAlbum = Album(albumDesc, albumName, null, allowedUserListed, comments, isModList, owner, pictures, typePublic)
                        db.collection("albums").document(albumName)
                            .set(newAlbum)

                            .addOnSuccessListener { Log.d(ContentValues.TAG, "DocumentSnapshot successfully written!")
                                Toast.makeText(this,"Album Created!",Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error writing document", e) }

                            .addOnFailureListener(OnFailureListener { e ->
                                Log.d("AlbumCreatorActivity", "Failed to insert data!")
                            })
                    }
                println("userlist")
                println(allowedUserListed)
                //Stores arraylist of userids or usernames that are mods
            }
        }

        back_button.setOnClickListener(){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }









    }
}