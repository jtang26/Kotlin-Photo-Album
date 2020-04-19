package com.example.photoalbum.Adapter

import android.content.Context
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.photoalbum.R
import com.squareup.picasso.Picasso


//define the binding for the view holder
class PhotoViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.photo_item, parent, false)) {
   // private val photoNameView: TextView
    private val photoPic: ImageView
    private var  imgUrl : String = ""



    init {
        photoPic = itemView.findViewById(R.id.pic)

    }


    fun bind(url: String) {

        imgUrl = url

        Picasso.get().load(imgUrl).into(photoPic);
    }


}





//define the adapter for the recycler view
class PhotoAlbumAdapter(private val list: ArrayList<String> )
    : RecyclerView.Adapter<PhotoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return PhotoViewHolder(inflater, parent)

    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val url: String = list[position]
        holder.bind(url)
    }

    override fun getItemCount(): Int = list.size


}
