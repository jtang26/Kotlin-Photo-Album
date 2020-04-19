package com.example.photoalbum.Adapter

import android.content.Context
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.photoalbum.Data.Album
import com.example.photoalbum.Data.User
import com.example.photoalbum.R

//define the binding for the view holder
class AlbumViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.album_item, parent, false)) {
    private val albumView: TextView


    init {
        albumView = itemView.findViewById(R.id.album_name)


    }


    fun bind(album: Album) {
        albumView?.text = (adapterPosition + 1).toString() + ". " + album.albumName

    }


}

// Class to allow activity to view recycler listener
class RecyclerItemClickListener(
    context: Context?,
    recyclerView: RecyclerView,
    private val mListener: OnItemClickListener?
) : RecyclerView.OnItemTouchListener {

    private val mGestureDetector: GestureDetector

    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int)

        fun onItemLongClick(view: View?, position: Int)
    }

    init {

        mGestureDetector =
            GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
                override fun onSingleTapUp(e: MotionEvent): Boolean {
                    return true
                }


            })
    }

    override fun onInterceptTouchEvent(view: RecyclerView, e: MotionEvent): Boolean {
        val childView = view.findChildViewUnder(e.x, e.y)

        if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
            mListener.onItemClick(childView, view.getChildAdapterPosition(childView))
        }

        return false
    }

    override fun onTouchEvent(view: RecyclerView, motionEvent: MotionEvent) {}

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}

}


//define the adapter for the recycler view
class AlbumListAdapter(private val list: MutableList<Album>?) :
    RecyclerView.Adapter<AlbumViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return AlbumViewHolder(inflater, parent)

    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        val album: Album = list!!.get(position)
        Log.d("AlbumListAdapter", " Album  = " + album)
        holder.bind(album)
    }

    override fun getItemCount(): Int = list!!.size


}

