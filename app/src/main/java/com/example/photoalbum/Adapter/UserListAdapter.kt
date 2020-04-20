package com.example.photoalbum

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.photoalbum.Data.User
import com.example.photoalbum.R

class UserListViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.invite_users, parent, false)) {
    private val username: TextView
    private val inviteButton: Button


    init {
        username = itemView.findViewById(R.id.leader_username)
        inviteButton = itemView.findViewById(R.id.invite_button)
    }

    fun bind(event :User, position: Int) {
        var positionPlus = position + 1
        username.text = positionPlus.toString() + ".  " + event.username + ":  "
        inviteButton.setOnClickListener() {

        }
//        if (event.username == owner) {
//
//        }
    }
}


class UserListAdapter(private val list: MutableList<User>?, private val owner: String) :
    RecyclerView.Adapter<UserListViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserListViewHolder {
        val inflater = LayoutInflater.from(parent.context)


        return UserListViewHolder(inflater, parent)
    }

    //bind the object
    override fun onBindViewHolder(holder: UserListViewHolder, position: Int) {
        val event: User = list!!.get(position)

        holder.bind(event, position)
    }

    //set the count
    override fun getItemCount(): Int = list!!.size

}