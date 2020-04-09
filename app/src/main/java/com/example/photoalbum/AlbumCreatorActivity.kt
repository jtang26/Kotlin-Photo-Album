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
    lateinit var type:String
    //creat instance of FirebaseFirestore
    lateinit var db : FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.album_creator_layout)

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

        var type = "Public"


        albumType.setOnCheckedChangeListener({ _ , isChecked ->
            type = "Private"
            if (isChecked!=true){
                type = "Public"
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

                val userMap:MutableMap<String, Any> = HashMap()
                userMap["name"] = name
                userMap["description"] = description
                userMap["album_type"] = type

                db.collection("users").document(userId).collection(type).document()
                    .set(userMap)

                    .addOnSuccessListener { Log.d(ContentValues.TAG, "DocumentSnapshot successfully written!")
                       Toast.makeText(this,"Album Created!",Toast.LENGTH_SHORT).show()
                        }
                    .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error writing document", e) }

                    .addOnFailureListener(OnFailureListener { e ->
                        Log.d("AlbumCreatorActivity", "Failed to insert data!")
                    })
            }
        }









    }
}