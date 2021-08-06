package com.ddona.memecommunity.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ddona.memecommunity.databinding.ItemImageUploadBinding
import com.ddona.memecommunity.model.ImageUpload

class ImageUploadAdapter(val inter: IFolder) :
    RecyclerView.Adapter<ImageUploadAdapter.ImageUploadHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageUploadHolder {
        return ImageUploadHolder(
            ItemImageUploadBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ImageUploadHolder, position: Int) {
        holder.binding.data = inter.getDataImage(position)
        holder.binding.btnDeleteImage.setOnClickListener {
            inter.onDeleteItemImage(holder.adapterPosition)
        }
    }

    override fun getItemCount(): Int {
        return inter.getCountImage()
    }

    interface IFolder {
        fun getCountImage(): Int
        fun getDataImage(position: Int): ImageUpload
        fun onDeleteItemImage(adapterPosition: Int)
    }

    class ImageUploadHolder(val binding: ItemImageUploadBinding) :
        RecyclerView.ViewHolder(binding.root)
}