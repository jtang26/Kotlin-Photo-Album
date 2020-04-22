package com.example.photoalbum.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.photoalbum.CommentsActivity
import com.example.photoalbum.Data.Album
import com.example.photoalbum.Data.Comments
import com.example.photoalbum.Data.User
import com.example.photoalbum.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CommentsViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.add_comment, parent, false)) {
    private val commentSpace: TextView
    private val deleteButton: Button
    var db = FirebaseFirestore.getInstance()
    var auth = FirebaseAuth.getInstance()
    init {
        commentSpace = itemView.findViewById(R.id.comment_body)
        deleteButton = itemView.findViewById(R.id.comment_delete_button)
    }

    fun bind(event : Comments, position: Int, albumName: String) {
        var positionPlus = position + 1
        commentSpace.text = positionPlus.toString() + ".  " + event.commentAuthor + ":  " + event.commentBody


        deleteButton.setOnClickListener() {
            db.collection("users").document(auth.currentUser!!.uid)
                .get().addOnSuccessListener { docSnap ->
                    var dat = docSnap.toObject(User::class.java)
                    var user = dat!!.username
                    val document = db.collection("albums").document(albumName)
                        document.get()
                        .addOnSuccessListener { documentSnapshot ->
                            var data = documentSnapshot.toObject(Album::class.java)
                            var currentCommentList = data!!.comments
                            var currentComment = Comments(event.commentBody, event.commentAuthor)
                            var albumOwner = data!!.owner
                            var albumMods = data!!.isModList
                            if (auth.currentUser!!.email == albumOwner) {
                                if (currentCommentList.contains(currentComment)) {
                                    currentCommentList.remove(currentComment)
                                    val documentUpdate = db.collection("albums").document(albumName)
                                    documentUpdate.update("comments", currentCommentList)
                                        .addOnSuccessListener { documentSnapshot ->
                                            Toast.makeText(
                                                deleteButton.context,
                                                "Successfully removed comment!",
                                                Toast.LENGTH_LONG
                                            )
//                                    val intent = Intent(deleteButton.context, CommentsActivity::class.java)
//                                    intent.putExtra("albumNamed", albumName)
//                                    startActivity(intent)
//                                    finish()
                                        }
                                } else {
                                    Toast.makeText(
                                        deleteButton.context,
                                        "Comment does not exist in Album!",
                                        Toast.LENGTH_LONG
                                    )
                                }
                            }
                            if (albumMods.contains(user) && event.commentAuthor!=albumOwner) {
                                if (currentCommentList.contains(currentComment)) {
                                    currentCommentList.remove(currentComment)
                                    val documentUpdate = db.collection("albums").document(albumName)
                                    documentUpdate.update("comments", currentCommentList)
                                        .addOnSuccessListener { documentSnapshot ->
                                            Toast.makeText(
                                                deleteButton.context,
                                                "Successfully removed comment!",
                                                Toast.LENGTH_LONG
                                            )
//                                    val intent = Intent(deleteButton.context, CommentsActivity::class.java)
//                                    intent.putExtra("albumNamed", albumName)
//                                    startActivity(intent)
//                                    finish()
                                        }
                                } else {
                                    Toast.makeText(
                                        deleteButton.context,
                                        "Mods cannot remove Album Owner's comments!",
                                        Toast.LENGTH_LONG
                                    )
                                }
                            }
                            if (!albumMods.contains(user) && event.commentAuthor!=albumOwner && event.commentAuthor==albumOwner) {
                                if (currentCommentList.contains(currentComment)) {
                                    currentCommentList.remove(currentComment)
                                    val documentUpdate = db.collection("albums").document(albumName)
                                    documentUpdate.update("comments", currentCommentList)
                                        .addOnSuccessListener { documentSnapshot ->
                                            Toast.makeText(
                                                deleteButton.context,
                                                "Successfully removed comment!",
                                                Toast.LENGTH_LONG
                                            )
//                                    val intent = Intent(deleteButton.context, CommentsActivity::class.java)
//                                    intent.putExtra("albumNamed", albumName)
//                                    startActivity(intent)
//                                    finish()
                                        }
                                } else {
                                    Toast.makeText(
                                        deleteButton.context,
                                        "Users cannot remove comments other than their own!",
                                        Toast.LENGTH_LONG
                                    )
                                }
                            }
                        }

                }


        }

    }
}


class CommentsAdapter(private val list: MutableList<Comments>?, private val albumName: String) :
    RecyclerView.Adapter<CommentsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentsViewHolder {
        val inflater = LayoutInflater.from(parent.context)


        return CommentsViewHolder(inflater, parent)
    }

    //bind the object
    override fun onBindViewHolder(holder: CommentsViewHolder, position: Int) {
        val event: Comments = list!!.get(position)

        holder.bind(event, position, albumName)
    }

    //set the count
    override fun getItemCount(): Int = list!!.size

}