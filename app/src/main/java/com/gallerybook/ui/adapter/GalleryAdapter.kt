package com.gallerybook.ui.adapter

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.gallerybook.databinding.GalleryLayoutBinding
import com.gallerybook.interfaces.OnImageClickListener
import com.gallerybook.room.GalleryScope
import com.gallerybook.ui.dashboard.DashBoardActivity
import com.gallerybook.utils.GalleryUtils
import java.io.File

/**
 * Created by Nihal Srivastava on 30/10/21.
 */

class GalleryAdapter(
    private var onImageClick: OnImageClickListener, private var mContext: DashBoardActivity
) : PagingDataAdapter<GalleryScope, GalleryAdapter.ImageViewHolder>(diffCallback) {


    inner class ImageViewHolder(
        val binding: GalleryLayoutBinding
    ) :
        ViewHolder(binding.root)

    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<GalleryScope>() {
            override fun areItemsTheSame(oldItem: GalleryScope, newItem: GalleryScope): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: GalleryScope, newItem: GalleryScope): Boolean {
                return oldItem == newItem
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(
            GalleryLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val data = getItem(position)
        holder.binding.apply {
            var imageLink=""
            if(data!!.image.startsWith("http")){
                 imageLink = data.image
            }else if(data.image_path.startsWith("/storage/")){
                val file = File(data.image_path)
                val imageUri: Uri = Uri.fromFile(file)
                 imageLink = imageUri.toString()
            }
            Glide.with(holder.binding.ivImage).load(imageLink).into(holder.binding.ivImage)
        }
        holder.itemView.setOnClickListener {
            onImageClick.onItemClick(data!!)
            Log.e("EMPTY_DATA", "onBindViewHolder: $data", )
        }
    }
}