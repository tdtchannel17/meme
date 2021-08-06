package com.ddona.memecommunity.utils

import android.net.Uri
import android.widget.*
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions


object Utils {
    @JvmStatic
    @BindingAdapter("setEditText")
    fun setEditText(tv: EditText, content: String) {
        tv.setText(content)
    }

    @JvmStatic
    @BindingAdapter("setVideo")
    fun setVideo(videoView: VideoView, uri: Uri) {
        if (uri == null) {
            return
        }
        videoView.setVideoURI(uri)
        videoView.requestFocus()
        videoView.start()
    }

    @JvmStatic
    @BindingAdapter("setVideoUrl")
    fun setVideoUrl(videoView: VideoView, url: String) {
        if (url == null) {
            return
        }
        videoView.setVideoURI(Uri.parse(url))
        videoView.seekTo(1)
    }

    @JvmStatic
    @BindingAdapter("setImage")
    fun setImage(iv: ImageView, uri: Uri?) {
        if (uri == null) {
            return
        }
        Glide.with(iv.context)
            .load(uri)
            .into(iv)
    }

    @JvmStatic
    @BindingAdapter("setImageLink")
    fun setImageLink(iv: ImageView, link: String) {
        if (link == null) {
            return
        }

        val requestOptions = RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
        Glide.with(iv.context)
            .load(link)
            .thumbnail(0.25f)
            .apply(requestOptions)
            .into(iv)
    }
}