package com.ddona.memecommunity.ui.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ddona.memecommunity.databinding.ItemVideoFirebaseBinding
import com.ddona.memecommunity.model.VideoFirebase

class VideoAdapter(val inter: IFolder) : RecyclerView.Adapter<VideoAdapter.VideoHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoHolder {
        return VideoHolder(
            ItemVideoFirebaseBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: VideoHolder, position: Int) {
        holder.binding.data = inter.getDataVideo(position)
        holder.binding.btnDownload.setOnClickListener {
            inter.downloadVideo(holder.adapterPosition)
        }
        holder.binding.ibPlay.setOnClickListener {
            holder.binding.vvVideo.setVideoURI(Uri.parse(inter.getDataVideo(position).pathUrlVideo))
            holder.binding.vvVideo.requestFocus()
            holder.binding.vvVideo.start()
        }
        holder.binding.ibStop.setOnClickListener {
            holder.binding.vvVideo.setVideoURI(Uri.parse(inter.getDataVideo(position).pathUrlVideo))
            holder.binding.vvVideo.requestFocus()
            holder.binding.vvVideo.stopPlayback()
        }
    }

    override fun getItemCount(): Int {
        return inter.getCountVideo()
    }

    interface IFolder {
        fun getCountVideo(): Int
        fun getDataVideo(position: Int): VideoFirebase
        fun downloadVideo(adapterPosition: Int)
    }

    class VideoHolder(val binding: ItemVideoFirebaseBinding) : RecyclerView.ViewHolder(binding.root)
}