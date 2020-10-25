package com.example.imagesearch.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.imagesearch.R
import com.example.imagesearch.datas.PhotoInfo
import kotlinx.android.synthetic.main.item_layout.view.*

class FlickrPhotoAdapter(private val listener : TapListener) :
    PagingDataAdapter<PhotoInfo, FlickrPhotoAdapter.ViewHolder>(PHOTOS_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = getItem(position)

        if (data != null) {
            holder.displayImage(data)
        }

    }

    companion object {
        private val PHOTOS_COMPARATOR = object : DiffUtil.ItemCallback<PhotoInfo>() {
            override fun areItemsTheSame(oldItem: PhotoInfo, newItem: PhotoInfo): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: PhotoInfo, newItem: PhotoInfo): Boolean =
                oldItem == newItem
        }
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        val imageView: ImageView = itemView.image_view_item

        init {
            // here we will add on click lisners and stuffs
            imageView.setOnClickListener(this)
        }

        fun displayImage(photo: PhotoInfo) {
            val url =
                "https://live.staticflickr.com/${photo.server}/${photo.id}_${photo.secret}_b.jpg"
            val option = RequestOptions.placeholderOf(R.drawable.background_image_view)
                .error(R.drawable.background_image_view)

            Glide.with(itemView)
                .load(url)
                .apply(option)
                .centerCrop()
                //.fitCenter()
                //.transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView)

        }

        override fun onClick(p0: View?) {
            val position = bindingAdapterPosition
            val imageView = itemView.image_view_item
            imageView.transitionName = "itemShareImages"
            val item = getItem(position)
            if(item != null){
                if(position != RecyclerView.NO_POSITION){
                    listener.onTap(item, imageView)
                }
            }
        }
    }

    interface TapListener {
        fun onTap(item: PhotoInfo, imageView: ImageView)
    }
}