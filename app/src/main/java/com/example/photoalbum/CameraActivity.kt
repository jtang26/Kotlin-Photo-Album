package com.example.photoalbum

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.camera_layout.*
import androidx.annotation.NonNull
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.storage.UploadTask
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.StorageReference
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.photoalbum.Adapter.AlbumListAdapter
import com.example.photoalbum.Adapter.RecyclerItemClickListener
import com.example.photoalbum.Data.Album
import com.example.photoalbum.Data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.private_album_list_layout.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter


class CameraActivity: AppCompatActivity() {

    private lateinit var captureButton: Button
    private lateinit var privateButton: Button
    private lateinit var publicButton: Button
    private lateinit var photoView: ImageView
    private lateinit var storage: FirebaseStorage
    private lateinit var storageRef: StorageReference
    lateinit var db: FirebaseFirestore
    lateinit var albums: MutableList<Album>
    lateinit var backButton: Button
    private lateinit var username1: String
    lateinit var auth: FirebaseAuth

    val userId: String = FirebaseAuth.getInstance().currentUser!!.uid
    val email: String? = FirebaseAuth.getInstance().currentUser!!.email

    //Code cited from "Camera Studio"

    val REQUEST_CAMERA_PERMISSIONS = 1;
    val IMAGE_CAPTURE_CODE = 2;
    var typeSelected: Boolean = false
    var publicAlbum: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.camera_layout)
        val bundle = intent.extras
        val albumName: String = bundle!!.getString("albumNamed", "name")

        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        storageRef = storage.reference



        //Set Views
        captureButton = btn_capture_photo
        privateButton = btn_private
        publicButton = btn_public
        photoView = image
        backButton = back_button_1
        username1 = ""

        captureButton.setOnClickListener {

            var permission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)

            if(permission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.CAMERA),REQUEST_CAMERA_PERMISSIONS)
            } else {
                val cIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(cIntent, IMAGE_CAPTURE_CODE)
            }
        }

        backButton.setOnClickListener(){
            val intent = Intent(this,AlbumViewActivity::class.java)
            intent.putExtra("name", albumName)
            startActivity(intent)
            finish()
        }



    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == IMAGE_CAPTURE_CODE) {
            if(resultCode == Activity.RESULT_OK) {
                val bitmap = data!!.extras!!["data"] as Bitmap
                photoView.setImageBitmap(bitmap)
                privateButton.visibility = View.VISIBLE
                publicButton.visibility = View.VISIBLE
                val currentTimestamp = System.currentTimeMillis()
                var picName = ""
                picName += currentTimestamp.toString()
                picName += ".jpg"

                privateButton.setOnClickListener() {
                    publicAlbum = false
                    //set instance of firestore
                    db = FirebaseFirestore.getInstance()


                    //Query to get list of albums matching users selection
                        val document: Query =
                            db.collection("albums").whereEqualTo("isPublic", publicAlbum)
                                .whereEqualTo("owner", auth.currentUser!!.email)
                        document.get().addOnSuccessListener { documentSnapshot ->
                            var albumList = documentSnapshot.toObjects(Album::class.java)
                            albums = albumList
                            // set recycler view
                            val decorator =
                                DividerItemDecoration(
                                    applicationContext,
                                    LinearLayoutManager.VERTICAL
                                )
                            val recyclerView: RecyclerView = album_recycler_view
                            val adapter = AlbumListAdapter(albumList)
                            recyclerView.adapter = adapter
                            recyclerView.layoutManager = LinearLayoutManager(this)
                            recyclerView.addItemDecoration(decorator)
                    }

                    album_recycler_view.addOnItemTouchListener(
                        RecyclerItemClickListener(
                            this,
                            album_recycler_view,
                            object : RecyclerItemClickListener.OnItemClickListener {

                                override fun onItemClick(view: View, position: Int) {
                                    println("album clicked, position: " + position)

                                    val album: Album = albums.get(position)
                                    var pictures: MutableList<String> = album.pictures

                                    var picRef = storageRef.child("albums/"+ email+ "/"+ album.albumName +"/" + picName)
                                    // Get the data from an ImageView as bytes
                                    photoView.isDrawingCacheEnabled = true
                                    photoView.buildDrawingCache()
                                    val baos = ByteArrayOutputStream()
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                                    val data = baos.toByteArray()
                                    println("before upload")
                                    var uploadTask = picRef.putBytes(data)
                                    uploadTask.addOnFailureListener {
                                        println("failure")
                                        // Handle unsuccessful uploads
                                    }.addOnSuccessListener {
                                        println("Success")
                                        // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
                                        //get download URi
                                     val result=  it.task.snapshot.metadata!!.reference!!.downloadUrl
                                      val time =  it.task.snapshot.metadata!!.creationTimeMillis
                                        val pattern = "yyyy-MM-dd";
                                        val format = SimpleDateFormat(pattern)
                                        val date = format.format(time)
                                        Log.d("CameraActivity","date picture taken: " + date)
                                        result.addOnSuccessListener {
                                            var imageLink = it.toString()
                                            pictures.add(imageLink)
                                            if (album.albumName!=null){
                                                //Query album to get string of pics
                                                val albumRef = db.collection("albums").document(album.albumName)

                                                //Update album pic List
                                                albumRef
                                                    .update("pictures", pictures)
                                                    .addOnSuccessListener {  Log.d("CameraActivity", "Snapshot succesfully updated!")
                                                        Toast.makeText(applicationContext, "Pictured added to album!", Toast.LENGTH_SHORT).show()}
                                                    .addOnFailureListener { e -> Log.w( "Error updating document", e) }

                                                db.collection("users").document(auth.currentUser!!.uid)
                                                    .get().addOnSuccessListener { docuSnapshot ->
                                                        var data1 = docuSnapshot.toObject(User::class.java)
                                                        var username = data1!!.username
                                                        db.collection("albums")
                                                            .document(album.albumName)
                                                            .get()
                                                            .addOnSuccessListener { documentSnapshot ->
                                                                var data =
                                                                    documentSnapshot.toObject(Album::class.java)
                                                                var picOwners = data!!.picOwners
                                                                picOwners.add(username as String)
                                                                albumRef
                                                                    .update("picOwners", picOwners)
                                                                    .addOnSuccessListener {
                                                                        Log.d(
                                                                            "CameraActivity",
                                                                            "Snapshot owner succesfully updated!"
                                                                        )
                                                                        Toast.makeText(
                                                                            applicationContext,
                                                                            "Picture Owner added to album!",
                                                                            Toast.LENGTH_SHORT
                                                                        ).show()
                                                                    }
                                                                    .addOnFailureListener { e ->
                                                                        Log.w(
                                                                            "Error updating document",
                                                                            e
                                                                        )
                                                                    }
                                                            }
                                                    }
                                            }
                                        }
                                    }
                                }

                                override fun onItemLongClick(view: View?, position: Int) {

                                }
                            })
                    )

                }
                publicButton.setOnClickListener() {
                    publicAlbum = true
                    //set instance of firestore
                    db = FirebaseFirestore.getInstance()

                    val doc: Query = db.collection("users").whereEqualTo("userID", auth.currentUser!!.uid)
                    doc.get().addOnSuccessListener { docSnapshot ->
                        var userData = docSnapshot.toObjects(User::class.java)
                        username1 = userData[0].username as String
                        val document: Query =
                            db.collection("albums").whereEqualTo("isPublic", publicAlbum)
                                .whereArrayContains("allowedUserList", username1)
                        document.get().addOnSuccessListener { documentSnapshot ->
                            var albumList = documentSnapshot.toObjects(Album::class.java)
                            albums = albumList
                            // set recycler view
                            val decorator =
                                DividerItemDecoration(
                                    applicationContext,
                                    LinearLayoutManager.VERTICAL
                                )
                            val recyclerView: RecyclerView = album_recycler_view
                            val adapter = AlbumListAdapter(albumList)
                            recyclerView.adapter = adapter
                            recyclerView.layoutManager = LinearLayoutManager(this)
                            recyclerView.addItemDecoration(decorator)

                        }
                    }

                    album_recycler_view.addOnItemTouchListener(
                        RecyclerItemClickListener(
                            this,
                            album_recycler_view,
                            object : RecyclerItemClickListener.OnItemClickListener {
                                override fun onItemClick(view: View, position: Int) {
                                    println("album clicked, position: " + position)

                                    val album: Album = albums.get(position)
                                    var pictures: MutableList<String> = album.pictures

                                    var picRef = storageRef.child("albums/"+ email+ "/"+ album.albumName +"/" + picName)
                                    // Get the data from an ImageView as bytes
                                    photoView.isDrawingCacheEnabled = true
                                    photoView.buildDrawingCache()
                                    val baos = ByteArrayOutputStream()
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                                    val data = baos.toByteArray()
                                    println("before upload")
                                    var uploadTask = picRef.putBytes(data)
                                    uploadTask.addOnFailureListener {
                                        println("failure")
                                        // Handle unsuccessful uploads
                                    }.addOnSuccessListener {
                                        println("Success")
                                        // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
                                        //get download URi
                                        val result=  it.task.snapshot.metadata!!.reference!!.downloadUrl
                                        result.addOnSuccessListener {
                                            var imageLink = it.toString()
                                            pictures.add(imageLink)
                                            if (album.albumName!=null){
                                                //Query album to get string of pics
                                                val albumRef = db.collection("albums").document(album.albumName)
                                                //Update album pic List
                                                albumRef
                                                    .update("pictures", pictures)
                                                    .addOnSuccessListener {  Log.d("CameraActivity", "Snapshot succesfully updated!")
                                                        Toast.makeText(applicationContext, "Pictured added to album!", Toast.LENGTH_SHORT).show()}
                                                    .addOnFailureListener { e -> Log.w( "Error updating document", e) }

                                                db.collection("users").document(auth.currentUser!!.uid)
                                                    .get().addOnSuccessListener { docuSnapshot ->
                                                        var data1 = docuSnapshot.toObject(User::class.java)
                                                        var username = data1!!.username
                                                        db.collection("albums")
                                                            .document(album.albumName)
                                                            .get()
                                                            .addOnSuccessListener { documentSnapshot ->
                                                                var data =
                                                                    documentSnapshot.toObject(Album::class.java)
                                                                var picOwners = data!!.picOwners
                                                                picOwners.add(username as String)
                                                                albumRef
                                                                    .update("picOwners", picOwners)
                                                                    .addOnSuccessListener {
                                                                        Log.d(
                                                                            "CameraActivity",
                                                                            "Snapshot owner succesfully updated!"
                                                                        )
                                                                        Toast.makeText(
                                                                            applicationContext,
                                                                            "Picture Owner added to album!",
                                                                            Toast.LENGTH_SHORT
                                                                        ).show()
                                                                    }
                                                                    .addOnFailureListener { e ->
                                                                        Log.w(
                                                                            "Error updating document",
                                                                            e
                                                                        )
                                                                    }
                                                            }
                                                    }
                                            }
                                        }
                                    }

                                }
                                override fun onItemLongClick(view: View?, position: Int) {

                                }
                            })
                    )
                }


            }
        }
    }
}