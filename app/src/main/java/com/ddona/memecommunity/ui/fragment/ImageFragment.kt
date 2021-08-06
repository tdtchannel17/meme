package com.ddona.memecommunity.ui.fragment

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.ddona.memecommunity.databinding.FragmentImageBinding
import com.ddona.memecommunity.model.ImageFirebase
import com.ddona.memecommunity.ui.adapter.ImageAdapter
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream


class ImageFragment : Fragment(), ImageAdapter.IFolder, SwipeRefreshLayout.OnRefreshListener {
    private lateinit var binding: FragmentImageBinding
    private var listImage = arrayListOf<ImageFirebase>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentImageBinding.inflate(inflater, container, false)
        getDataImageFirebase()
        animation1()
        animation2()
        inits()
        return binding.root
    }

    private fun inits() {
        binding.swipeRefreshLayout.setOnRefreshListener(this)
        binding.rcImage.adapter = ImageAdapter(this)
        binding.rcImage.layoutManager =
            GridLayoutManager(context, 2)
    }

    private fun animation1() {
        val scaleX = ObjectAnimator.ofFloat(binding.ivImageCool, "scaleX", 0.9f, 1.1f)
        val scaleY = ObjectAnimator.ofFloat(binding.ivImageCool, "scaleY", 0.9f, 1.1f)
        scaleX.repeatCount = ObjectAnimator.INFINITE
        scaleX.repeatMode = ObjectAnimator.REVERSE
        scaleY.repeatCount = ObjectAnimator.INFINITE
        scaleY.repeatMode = ObjectAnimator.REVERSE
        val scaleAnim = AnimatorSet()
        scaleAnim.duration = 1000
        scaleAnim.play(scaleX).with(scaleY)
        scaleAnim.start()

        val animation = TranslateAnimation(
            0.0f,
            400.0f,
            0.0f,
            0.0f
        )
    }

    private fun animation2() {
        val anim = TranslateAnimation(0f, 100f, 0f, 0f)
        anim.duration = 1500
        anim.repeatCount = Animation.INFINITE
        anim.repeatMode = Animation.REVERSE
        binding.ivImageSurprised.setAnimation(anim)
    }

    private fun getDataImageFirebase() {
        binding.progress.visibility = View.VISIBLE
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference.child("images/")

        val listAllTask = storageRef.listAll()
        listAllTask.addOnCompleteListener {
            try {
                val item = it.result?.items
                item?.forEachIndexed { index, item ->
                    item.downloadUrl.addOnSuccessListener {
                        addDataToListImage(it.toString(), item.path)
                        binding.rcImage.adapter?.notifyItemChanged(index)
                    }
                }
            } catch (e: Exception) {
                binding.progress.visibility = View.GONE
                binding.llError.visibility = View.VISIBLE
            }
        }
    }

    private fun addDataToListImage(pathUrlImage: String, pathDownload: String) {
        listImage.add(
            ImageFirebase(
                pathUrlImage,
                pathDownload
            )
        )
        binding.progress.visibility = View.GONE
    }

    override fun getCountImage(): Int {
        return listImage.size
    }

    override fun getDataImage(position: Int): ImageFirebase {
        return listImage[position]
    }

    override fun downloadImage(adapterPosition: Int) {
        val storege = FirebaseStorage.getInstance()
        val imageRef = storege.reference.child(listImage[adapterPosition].pathDownload)
        imageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)

            var fos: OutputStream? = null
            val filename = "${System.currentTimeMillis()}.jpg"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                activity?.contentResolver?.also { resolver ->
                    val contentValues = ContentValues().apply {
                        put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                    }

                    val imageUri: Uri? =
                        resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                    fos = imageUri?.let { resolver.openOutputStream(it) }
                }
            } else {
                val imagesDir =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                val image = File(imagesDir, filename)
                fos = FileOutputStream(image)
            }

            fos?.use {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                Toast.makeText(context, "Saved Image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        listImage.clear()
    }

    override fun onPause() {
        super.onPause()
        listImage.clear()
    }

    override fun onRefresh() {
        listImage.clear()
        getDataImageFirebase()
        binding.swipeRefreshLayout.isRefreshing = false
    }
}