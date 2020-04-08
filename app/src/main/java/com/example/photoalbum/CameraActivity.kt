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

class CameraActivity: AppCompatActivity() {

    private lateinit var captureButton: Button
    private lateinit var privateButton: Button
    private lateinit var publicButton: Button
    private lateinit var photoView: ImageView
    private lateinit var storage: FirebaseStorage



    //Code taken from "Camera Studio"

    val REQUEST_CAMERA_PERMISSIONS = 1;
    val IMAGE_CAPTURE_CODE = 2;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.camera_layout)

        storage = FirebaseStorage.getInstance()
        var storageRef = storage.reference
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

            }
        }
    }
}