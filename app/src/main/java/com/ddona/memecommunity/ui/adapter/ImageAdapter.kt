package com.ddona.memecommunity.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ddona.memecommunity.databinding.ItemImageFirebaseBinding
import com.ddona.memecommunity.databinding.ItemImageUploadBinding
import com.ddona.memecommunity.model.ImageFirebase
import com.ddona.memecommunity.model.ImageUpload
import kotlinx.android.synthetic.main.item_image_firebase.view.*

class ImageAdapter(val inter: IFolder) : RecyclerView.Adapter<ImageAdapter.ImageHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        return ImageHolder(
            ItemImageFirebaseBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ImageHolder, position: Int) {
        holder.binding.data = inter.getDataImage(position)
        holder.itemView.btnDownload.setOnClickListener {
            inter.downloadImage(holder.adapterPosition)
        }
    }

    override fun getItemCount(): Int {
        return inter.getCountImage()
    }

    interface IFolder {
        fun getCountImage(): Int
        fun getDataImage(position: Int): ImageFirebase
        fun downloadImage(position: Int)
    }

    class ImageHolder(val binding: ItemImageFirebaseBinding) : RecyclerView.ViewHolder(binding.root)
}