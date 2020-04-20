package com.example.photoalbum

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.photoalbum.Data.Album
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.photo_view_layout.*

class PhotoViewActivity : AppCompatActivity() {


    lateinit var db: FirebaseFirestore
    lateinit var photoPic: ImageView
    lateinit var backButton: Button
    lateinit var deleteButton: Button
    lateinit var picList: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.photo_view_layout)

        photoPic = pic
        backButton = btn_back
        picList = ArrayList<String>()
        deleteButton = btn_delete


        db = FirebaseFirestore.getInstance()


        // Get album name
        val bundle = intent.extras
        val photoUrl: String = bundle!!.getString("url", "")
        val photoPosition: Int = bundle.getInt("position")
        val albumName: String = bundle.getString("album_name", "")


        Picasso.get().load(photoUrl).into(photoPic)



        backButton.setOnClickListener() {
            finish()
        }


        //Query Firebase for album name
        val document: Query = db.collection("albums").whereEqualTo("albumName", albumName)
        document.get().addOnSuccessListener { documentSnapshot ->

            var albumList = documentSnapshot.toObjects(Album::class.java)
            picList = albumList[0].pictures
            picList.removeAt(photoPosition)


        }


        deleteButton.setOnClickListener() {

            val builder = AlertDialog.Builder(this)

            builder.setMessage("Are you sure you want to delete this photo?")

            builder.setPositiveButton("Yes") { dialog, which ->

                val albumRef = db.collection("albums").document(albumName)
                albumRef
                    .update("pictures", picList)
                    .addOnSuccessListener { Toast.makeText(applicationContext, "Picture deleted!", Toast.LENGTH_SHORT).show()
                    }

               val intent = Intent(this,AlbumViewActivity::class.java)
                intent.putExtra("name",albumName)
                startActivity(intent)
                finish()

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





