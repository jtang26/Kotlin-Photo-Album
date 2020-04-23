package com.example.photoalbum

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.photoalbum.Data.Album
import com.example.photoalbum.Data.User
import com.example.photoalbum.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore





class UserListAdapter(private val list: MutableList<User>?, private val albumName: String) :
    RecyclerView.Adapter<UserListAdapter.UserListViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserListViewHolder {
        val inflater = LayoutInflater.from(parent.context)


        return UserListViewHolder(inflater, parent)
    }

    //bind the object
    override fun onBindViewHolder(holder: UserListViewHolder, position: Int) {
        val event: User = list!!.get(position)

        holder.bind(event, position, albumName)
    }

    //set the count
    override fun getItemCount(): Int = list!!.size

   inner class UserListViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.invite_users, parent, false)) {
        private val username: TextView
        private val inviteButton: Button
        var db = FirebaseFirestore.getInstance()
        var auth = FirebaseAuth.getInstance()
        init {
            username = itemView.findViewById(R.id.leader_username)
            inviteButton = itemView.findViewById(R.id.invite_button)
        }

        fun bind(event :User, position: Int, albumName: String) {
            var positionPlus = position + 1
            username.text = positionPlus.toString() + ".  " + event.username + ":  "

            inviteButton.setOnClickListener() {
                val document = db.collection("albums").document(albumName)
                document.get()
                    .addOnSuccessListener { documentSnapshot ->
                        var data = documentSnapshot.toObject(Album::class.java)
                        var currentUserList = data!!.allowedUserList
                        var currentUserStats = data!!.allowedUserStatus
                        var details = "User"
                        if(!currentUserList.contains(event.username.toString())) {
                            currentUserList.add(event.username.toString())
                            currentUserStats.add(details)
                            val documentUpdate = db.collection("albums").document(albumName)
                            documentUpdate.update("allowedUserList", currentUserList)
                                .addOnSuccessListener { documentSnapshot ->
                                    Toast.makeText(
                                        inviteButton.context,
                                        "Successfully invited user to album!",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    notifyDataSetChanged()
                                }
                            db.collection("albums").document(albumName)
                                .update("allowedUserStatus", currentUserStats)
                                .addOnSuccessListener { documentSnapshot ->
                                    Toast.makeText(
                                        inviteButton.context,
                                        "Successfully updated!",
                                        Toast.LENGTH_LONG
                                    )
                                    notifyDataSetChanged()
                                }
                        }else{
                            Toast.makeText(inviteButton.context, "User has already been invited to the album!", Toast.LENGTH_LONG).show()
                        }
                    }
            }

        }

    }

}