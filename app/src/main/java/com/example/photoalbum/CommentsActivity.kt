package com.example.photoalbum

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.photoalbum.Adapter.CommentsAdapter
import com.example.photoalbum.Data.Album
import com.example.photoalbum.Data.Comments
import com.example.photoalbum.Data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.comments_layout.*
import kotlinx.android.synthetic.main.user_list_layout.*
import kotlinx.android.synthetic.main.user_list_layout.leader_recycler_view
import kotlin.coroutines.coroutineContext

class CommentsActivity : AppCompatActivity() {
    public lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var commentString: String
    private lateinit var commentButton: Button
    private lateinit var commentBody: EditText
    private lateinit var commentBackButton: Button
    private lateinit var commentActivityContext: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.comments_layout)
        val data = intent.extras
        val albumName = data!!.getString("albumNamed").toString()
        db = FirebaseFirestore.getInstance()
        commentString = ""
        commentButton = comment_button
        commentBody = commentBox
        commentBackButton = comment_back_button
        auth = FirebaseAuth.getInstance()

        val document = db.collection("albums").document(albumName)
        document.get().addOnSuccessListener { documentSnapshot ->
            var data = documentSnapshot.toObject(Album::class.java)
            var commentList : ArrayList<Comments>
            commentList = data!!.comments
            val adapter = CommentsAdapter(commentList, albumName)
            comment_recycler_view.adapter = adapter
            comment_recycler_view.layoutManager = LinearLayoutManager(this)
        }

        commentBackButton.setOnClickListener() {
            val intent = Intent(this, AlbumViewActivity::class.java)
            intent.putExtra("name", albumName)
            startActivity(intent)
            finish()
        }

        commentButton.setOnClickListener() {
            commentString = commentBody.text.toString()
            db.collection("users").whereEqualTo("email", auth.currentUser!!.email)
                .get().addOnSuccessListener { docSnap ->
                    var userData = docSnap.toObjects(User::class.java)
                    var username = userData[0].username
                    db.collection("albums").document(albumName)
                        .get().addOnSuccessListener { docuSnap ->
                            var albumDat = docuSnap.toObject(Album::class.java)
                            var commentList = albumDat!!.comments
                            var commentObj = Comments(commentString,username)
                            commentList.add(commentObj)
                            val documentUpdate = db.collection("albums").document(albumName)
                            documentUpdate.update("comments", commentList)
                                .addOnSuccessListener { documentSnapshot ->
                                    Toast.makeText(
                                        commentButton.context,
                                        "Successfully added comment!",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    val intent = Intent(this, CommentsActivity::class.java)
                                    intent.putExtra("albumNamed", albumName)
                                    startActivity(intent)
                                    finish()
                                }
                        }
                }

        }

    }

}