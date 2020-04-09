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
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import java.io.ByteArrayOutputStream
import java.io.File


class CameraActivity: AppCompatActivity() {

    private lateinit var captureButton: Button
    private lateinit var privateButton: Button
    private lateinit var publicButton: Button
    private lateinit var photoView: ImageView
    private lateinit var storage: FirebaseStorage
    private lateinit var storageRef: StorageReference

    val userId: String = FirebaseAuth.getInstance().currentUser!!.uid

    //Code cited from "Camera Studio"

    val REQUEST_CAMERA_PERMISSIONS = 1;
    val IMAGE_CAPTURE_CODE = 2;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.camera_layout)

        storage = FirebaseStorage.getInstance()
        storageRef = storage.reference
        var publicImageRef = storageRef.child("albums/public")
        var privateImageRef = storageRef.child("albums/private")

        //Set Views
        captureButton = btn_capture_photo
        privateButton = btn_private
        publicButton = btn_public
        photoView = image

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
                    var picRef = storageRef.child("Albums/"+userId+ "/"+"Private/" + picName)
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
                        // ...
                    }
                }
                publicButton.setOnClickListener() {
                    var publicPicRef = storageRef.child("Albums/"+userId+ "/"+"Public/" + picName)
                    photoView.isDrawingCacheEnabled = true
                    photoView.buildDrawingCache()
                    val baos = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                    val data = baos.toByteArray()
                    println("before upload")
                    var uploadTask = publicPicRef.putBytes(data)
                    uploadTask.addOnFailureListener {
                        println("failure")
                        // Handle unsuccessful uploads
                    }.addOnSuccessListener {
                        println("Success")
                        // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
                        // ...
                    }
                }

            }
        }
    }
}