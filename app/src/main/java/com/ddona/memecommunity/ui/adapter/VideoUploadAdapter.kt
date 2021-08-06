package com.ddona.memecommunity.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import com.ddona.memecommunity.databinding.ItemVideoUploadBinding
import com.ddona.memecommunity.model.VideoUpload
import kotlinx.android.synthetic.main.item_video_upload.view.*

class VideoUploadAdapter (val inter: IFolder) : RecyclerView.Adapter<VideoUploadAdapter.VideoUploadHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoUploadHolder {
        return VideoUploadHolder(
            ItemVideoUploadBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: VideoUploadHolder, position: Int) {
        holder.binding.data = inter.getDataVideo(position)
        holder.itemView.setOnClickListener {
            inter.onItemClickVideo(holder.adapterPosition, position, holder.itemView.ivVideoUpload)
        }
        holder.binding.btnDeleteVideo.setOnClickListener {
            inter.onDeleteItemVideo(holder.adapterPosition)
        }
    }

    override fun getItemCount(): Int {
        return inter.getCountVideo()
    }

    interface IFolder {
        fun getCountVideo(): Int
        fun getDataVideo(position: Int): VideoUpload
        fun onItemClickVideo(position: Int, _id: Int, videoView: VideoView)
        fun onDeleteItemVideo(adapterPosition: Int)
    }

    class VideoUploadHolder(val binding: ItemVideoUploadBinding) : RecyclerView.ViewHolder(binding.root)
}