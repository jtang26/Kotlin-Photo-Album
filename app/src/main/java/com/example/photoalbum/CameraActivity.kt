package com.example.photoalbum

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class CameraActivity: AppCompatActivity() {

    //Code taken from "Camera Studio"

    val REQUEST_CAMERA_PERMISSIONS = 1;
    val IMAGE_CAPTURE_CODE = 2;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnTakePicture.setOnClickListener {

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
                capturedImage.setImageBitmap(bitmap)

                val fireImage = FirebaseVisionImage.fromBitmap(bitmap)

                val options = FirebaseVisionFaceDetectorOptions.Builder()
                    .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
                    .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
                    .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
                    .build()


            }
        }
    }
}