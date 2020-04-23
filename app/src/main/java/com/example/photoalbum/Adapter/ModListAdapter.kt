package com.example.photoalbum.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.photoalbum.Data.Album
import com.example.photoalbum.Data.User
import com.example.photoalbum.ModListActivity
import com.example.photoalbum.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore






class ModListAdapter(private val list: MutableList<String>?, private val albumName: String) :
    RecyclerView.Adapter<ModListAdapter.ModListViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModListViewHolder {
        val inflater = LayoutInflater.from(parent.context)


        return ModListViewHolder(inflater, parent)
    }

    //bind the object
    override fun onBindViewHolder(holder: ModListViewHolder, position: Int) {
        val event: String? = list?.get(position)

        holder.bind(event, position, albumName)
    }

    //set the count
    override fun getItemCount(): Int = list?.size as Int

    inner class ModListViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.mod_users, parent, false)) {
        private val username: TextView
        private val status: TextView
        private val modButton: Button
        private val demodButton: Button
        private val banButton: Button
        var db = FirebaseFirestore.getInstance()
        var auth = FirebaseAuth.getInstance()


        init {
            username = itemView.findViewById(R.id.user_username)
            modButton = itemView.findViewById(R.id.mod_button)
            demodButton = itemView.findViewById(R.id.demod_button)
            banButton = itemView.findViewById(R.id.ban_button)
            status = itemView.findViewById(R.id.user_status)

        }

        fun bind(event : String?, position: Int, albumName: String) {
            var positionPlus = position + 1
            username.text = positionPlus.toString() + ".  " + event + ":  "
            db.collection("users").document(auth.currentUser!!.uid)
                .get().addOnSuccessListener { docSnap ->
                    var dat = docSnap.toObject(User::class.java)
                    var user = dat!!.username
                    db.collection("albums").document(albumName)
                        .get().addOnSuccessListener { dSnap ->
                            var data1 = dSnap.toObject(Album::class.java)
                            var albumOwner = data1!!.owner
                            var albumMods = data1!!.isModList
                            var userStats = data1!!.allowedUserStatus
                            println(albumMods)
                            if(auth.currentUser!!.email == albumOwner) {
                                modButton.visibility = View.VISIBLE
                                demodButton.visibility = View.VISIBLE
                                banButton.visibility = View.VISIBLE
                            }
                            if(albumMods.contains(user)) {
                                banButton.visibility = View.VISIBLE
                            }
                            status.text = userStats[position]
                        }
                }
            modButton.setOnClickListener() {
                val document = db.collection("albums").document(albumName)
                document.get()
                    .addOnSuccessListener { documentSnapshot ->
                        var data = documentSnapshot.toObject(Album::class.java)
                        var currentUserList = data!!.allowedUserList
                        var modList = data!!.isModList
                        if(!modList.contains(event.toString())) {
                            modList.add(event.toString())
                            val documentUpdate = db.collection("albums").document(albumName)
                            documentUpdate.update("isModList", modList)
                                .addOnSuccessListener { documentSnapshot ->
                                    Toast.makeText(
                                        modButton.context,
                                        "Successfully updated mod list!",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    notifyDataSetChanged()
                                }
                            var currentUserDetail = data.allowedUserStatus
                            currentUserDetail[position] = "mod"
                            db.collection("albums").document(albumName)
                                .update("allowedUserStatus", currentUserDetail)
                                .addOnSuccessListener { documentSnapshot ->
                                    Toast.makeText(
                                        modButton.context,
                                        "Successfully updated mod list!",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    notifyDataSetChanged()


                                }

                        }else{
                            Toast.makeText(modButton.context, "User has already been modded in the album!", Toast.LENGTH_LONG)
                        }
                    }
            }
            demodButton.setOnClickListener() {
                val document = db.collection("albums").document(albumName)
                document.get()
                    .addOnSuccessListener { documentSnapshot ->
                        var data = documentSnapshot.toObject(Album::class.java)
                        var currentUserList = data!!.allowedUserList
                        var modList = data!!.isModList
                        var currentUserStatus = data!!.allowedUserStatus
                        if(modList.contains(event.toString())) {
                            modList.remove(event.toString())
                            currentUserStatus[position] = "user"
                            val documentUpdate = db.collection("albums").document(albumName)
                            documentUpdate.update("isModList", modList)
                                .addOnSuccessListener { documentSnapshot ->
                                    Toast.makeText(
                                        modButton.context,
                                        "Successfully updated mod list!",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    notifyDataSetChanged()
                                }
                            db.collection("albums").document(albumName)
                                .update("allowedUserStatus", currentUserStatus)
                                .addOnSuccessListener { documentSnapshot ->
                                    Toast.makeText(
                                        modButton.context,
                                        "Successfully updated mod list!",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    notifyDataSetChanged()
                                }
                        }else{
                            Toast.makeText(modButton.context, "User is not a mod in the album!", Toast.LENGTH_LONG)
                        }
                    }
            }
            banButton.setOnClickListener() {
                db.collection("users").document(auth.currentUser!!.uid)
                    .get().addOnSuccessListener { documentSnapshot ->
                        var dat = documentSnapshot.toObject(User::class.java)
                        var currUsername = dat!!.username

                val document = db.collection("albums").document(albumName)
                document.get()
                    .addOnSuccessListener { documentSnapshot ->
                        var data = documentSnapshot.toObject(Album::class.java)
                        var currentUserList = data!!.allowedUserList
                        var currentUserStatus = data!!.allowedUserStatus
                        if(event!=currentUserList[0]) {
                            if (currentUserList.contains(event.toString())) {
                                currentUserList.remove(event.toString())
                                currentUserStatus.removeAt(position)
                                val documentUpdate = db.collection("albums").document(albumName)
                                documentUpdate.update("allowedUserList", currentUserList)
                                    .addOnSuccessListener { documentSnapshot ->
                                        Toast.makeText(
                                            modButton.context,
                                            "Successfully banned user!",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        notifyDataSetChanged()
                                    }
                                db.collection("albums").document(albumName)
                                    .update("allowedUserStatus", currentUserStatus)
                                    .addOnSuccessListener { documentSnapshot ->
                                        Toast.makeText(
                                            modButton.context,
                                            "Successfully updated mod list!",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        notifyDataSetChanged()
                                    }
                            } else {
                                Toast.makeText(
                                    modButton.context,
                                    "User does not have access to album!",
                                    Toast.LENGTH_LONG
                                )
                            }
                        }
                        else{
                            Toast.makeText(
                                modButton.context,
                                "Mods can't ban album owner!",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                    }
            }


        }
    }
}