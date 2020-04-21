package com.example.photoalbum.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.photoalbum.Data.Album
import com.example.photoalbum.Data.User
import com.example.photoalbum.R

class ModListViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.mod_users, parent, false)) {
    private val username: TextView
    private val modButton: Button
    private val demodButton: Button
    private val banButton: Button


    init {
        username = itemView.findViewById(R.id.current_username)
        modButton = itemView.findViewById(R.id.mod_button)
        demodButton = itemView.findViewById(R.id.demod_button)
        banButton = itemView.findViewById(R.id.ban_button)

    }

    fun bind(event : Album, position: Int) {
        var positionPlus = position + 1
        username.text = positionPlus.toString() + ".  " + event.allowedUserList + ":  "
        modButton.setOnClickListener() {

        }
        demodButton.setOnClickListener() {

        }
        banButton.setOnClickListener() {
            
        }

//        if (event.username == owner) {
//
//        }
    }
}


class ModListAdapter(private val list: MutableList<Album>?) :
    RecyclerView.Adapter<ModListViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModListViewHolder {
        val inflater = LayoutInflater.from(parent.context)


        return ModListViewHolder(inflater, parent)
    }

    //bind the object
    override fun onBindViewHolder(holder: ModListViewHolder, position: Int) {
        val event: Album = list!!.get(position)

        holder.bind(event, position)
    }

    //set the count
    override fun getItemCount(): Int = list!!.size

}