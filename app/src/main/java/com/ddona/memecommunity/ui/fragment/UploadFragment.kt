package com.ddona.memecommunity.ui.fragment

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.*
import android.widget.PopupMenu
import android.widget.Toast
import android.widget.VideoView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.ddona.memecommunity.R
import com.ddona.memecommunity.databinding.FragmentUploadBinding
import com.ddona.memecommunity.model.ImageUpload
import com.ddona.memecommunity.model.VideoUpload
import com.ddona.memecommunity.ui.adapter.ImageUploadAdapter
import com.ddona.memecommunity.ui.adapter.VideoUploadAdapter
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


class UploadFragment : Fragment(), PopupMenu.OnMenuItemClickListener, View.OnClickListener,
    ImageUploadAdapter.IFolder, VideoUploadAdapter.IFolder {
    private lateinit var binding: FragmentUploadBinding
    private val IMAGE_CODE = 101
    private val VIDEO_CODE = 102
    private var listImageUpload = arrayListOf<ImageUpload>()
    private var listVideoUpload = arrayListOf<VideoUpload>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUploadBinding.inflate(inflater, container, false)
        inits()

        binding.ibChooseUpload.setOnClickListener {
            showPopupMenu(it)
        }

        return binding.root
    }

    private fun inits() {
        binding.btnBackImage.setOnClickListener(this)
        binding.btnBackVideo.setOnClickListener(this)
        binding.btnSelectImage.setOnClickListener(this)
        binding.btnSelectVideo.setOnClickListener(this)
        binding.btnUploadImage.setOnClickListener(this)
        binding.btnUploadVideo.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btnBackImage -> {
                listImageUpload.clear()
                listVideoUpload.clear()
                binding.cardMenu.visibility = View.VISIBLE
                binding.rlImage.visibility = View.GONE
                binding.btnUploadImage.visibility = View.GONE
            }
            R.id.btnBackVideo -> {
                listImageUpload.clear()
                listVideoUpload.clear()
                binding.cardMenu.visibility = View.VISIBLE
                binding.rlVideo.visibility = View.GONE
                binding.btnUploadVideo.visibility = View.GONE
            }
            R.id.btnSelectImage -> {
                listImageUpload.clear()
                val intent = Intent(
                    Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                )
                intent.type = "image/*"
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                intent.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), IMAGE_CODE)
            }
            R.id.btnSelectVideo -> {
                listImageUpload.clear()
                val intent = Intent(
                    Intent.ACTION_PICK,
                    android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                )
                intent.type = "video/*"
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                intent.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(Intent.createChooser(intent, "Select Video"), VIDEO_CODE)
            }
            R.id.btnUploadImage -> {
                uploadImages()
            }
            R.id.btnUploadVideo -> {
                uploadVideos()
            }
        }
    }

    private fun showPopupMenu(v: View) {
        val popupMenu = PopupMenu(context, v)
        popupMenu.setOnMenuItemClickListener(this)
        popupMenu.inflate(R.menu.upload)
        popupMenu.show()
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.images -> {
                binding.cardMenu.visibility = View.GONE
                binding.rlImage.visibility = View.VISIBLE
                return true
            }
            R.id.videos -> {
                binding.cardMenu.visibility = View.GONE
                binding.rlVideo.visibility = View.VISIBLE
                return true
            }
            else -> {
                return false
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_CODE) {
            if (resultCode == RESULT_OK) {
                if (data == null) {
                    Toast.makeText(context, "Not image selected", Toast.LENGTH_SHORT).show()
                }
                val clipData = data?.clipData
                if (clipData != null) {
                    for (i in 0 until clipData.itemCount) {
                        val uri = clipData.getItemAt(i).uri

                        val imageName = getFileName(uri)
                        listImageUpload.add(
                            ImageUpload(
                                imageName,
                                uri
                            )
                        )
                    }
                    binding.btnUploadImage.visibility = View.VISIBLE
                    binding.rcImageUpload.adapter = ImageUploadAdapter(this)
                    binding.rcImageUpload.layoutManager =
                        GridLayoutManager(context, 2)
                    binding.rcImageUpload.adapter?.notifyDataSetChanged()
                } else {
                    val uri = data?.data
                    val imageName = getFileName(uri!!)
                    listImageUpload.add(
                        ImageUpload(
                            imageName,
                            uri
                        )
                    )
                    binding.btnUploadImage.visibility = View.VISIBLE
                    binding.rcImageUpload.adapter = ImageUploadAdapter(this)
                    binding.rcImageUpload.layoutManager =
                        StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
                    binding.rcImageUpload.adapter?.notifyDataSetChanged()
                }
            }
        }

        if (requestCode == VIDEO_CODE) {
            if (resultCode == RESULT_OK) {
                if (data == null) {
                    Toast.makeText(context, "Not image selected", Toast.LENGTH_SHORT).show()
                }

                val clipData = data?.clipData
                if (clipData != null) {
                    for (i in 0 until clipData.itemCount) {
                        val uris = clipData.getItemAt(i).uri
                        val videoName = getFileName(uris)
                        listVideoUpload.add(
                            VideoUpload(
                                videoName,
                                uris
                            )
                        )
                    }
                    binding.btnUploadVideo.visibility = View.VISIBLE
                    binding.rcVideoUpload.adapter = VideoUploadAdapter(this)
                    binding.rcVideoUpload.layoutManager =
                        LinearLayoutManager(context)
                    binding.rcVideoUpload.adapter?.notifyDataSetChanged()
                } else {
                    val uri = data?.data
                    val videoName = getFileName(uri!!)
                    listVideoUpload.add(
                        VideoUpload(
                            videoName,
                            uri
                        )
                    )
                    binding.btnUploadVideo.visibility = View.VISIBLE
                    binding.rcVideoUpload.adapter = VideoUploadAdapter(this)
                    binding.rcVideoUpload.layoutManager =
                        StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
                    binding.rcVideoUpload.adapter?.notifyDataSetChanged()
                }
            }
        }
    }

    private fun uploadImages() {
        if (listImageUpload.size > 4) {
            val builder1 = AlertDialog.Builder(context)
            builder1.setMessage("Only 4 images can be uploaded")
            builder1.setCancelable(true)
            builder1.setPositiveButton(
                "Ok"
            ) { dialog, id -> dialog.cancel() }

            val alert11 = builder1.create()
            alert11.show()
            alert11.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE)
        } else {
            val dialog = ProgressDialog(context)
            dialog.setTitle("File Uploading...")
            dialog.show()

            val storage = Firebase.storage
            val storageRef = storage.getReference()
            for (i in 0 until listImageUpload.size) {
                val uploader =
                    storageRef.child("images").child(listImageUpload[i].name)
                uploader.putFile(listImageUpload[i].pathUri)
                    .addOnSuccessListener {
                        dialog.dismiss()
                        binding.btnUploadImage.visibility = View.GONE
                        listImageUpload.clear()
                        binding.rcImageUpload.adapter?.notifyDataSetChanged()
                    }
                    .addOnProgressListener {
                        val percent = 100 * it.bytesTransferred / it.totalByteCount
                        dialog.setMessage("Uploaded : " + percent.toInt() + "%")
                    }
            }
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun uploadVideos() {
        for (i in 0 until listVideoUpload.size) {
            val mp = MediaPlayer.create(
                activity,
                Uri.parse(
                    listVideoUpload[i].pathUri.toString()
                )
            )
            val duration = mp.duration
            mp.release()

            if (duration > 30000) {
                val builder1 = AlertDialog.Builder(context)
                builder1.setMessage("Video should not exceed 30 seconds")
                builder1.setCancelable(true)
                builder1.setPositiveButton(
                    "Ok"
                ) { dialog, id -> dialog.cancel() }

                val alert11 = builder1.create()
                alert11.show()
                alert11.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE)
            } else if (listVideoUpload.size > 2) {
                val builder1 = AlertDialog.Builder(context)
                builder1.setMessage("Only 2 videos can be uploaded")
                builder1.setCancelable(true)
                builder1.setPositiveButton(
                    "Ok"
                ) { dialog, id -> dialog.cancel() }

                val alert11 = builder1.create()
                alert11.show()
                alert11.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE)
            } else {
                val dialog = ProgressDialog(context)
                dialog.setTitle("File Uploading...")
                dialog.show()

                val storage = Firebase.storage
                val storageRef = storage.getReference()
                val uploader =
                    storageRef.child("videos").child(listVideoUpload[i].name)
                uploader.putFile(listVideoUpload[i].pathUri)
                    .addOnSuccessListener {
                        dialog.dismiss()
                        binding.btnUploadVideo.visibility = View.GONE
                        listVideoUpload.clear()
                        binding.rcVideoUpload.adapter?.notifyDataSetChanged()
                    }
                    .addOnProgressListener {
                        val percent = 100 * it.bytesTransferred / it.totalByteCount
                        dialog.setMessage("Uploaded : " + percent.toInt() + "%")
                    }
            }
        }
    }

    @SuppressLint("Recycle")
    private fun getFileName(uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor: Cursor =
                activity?.getContentResolver()?.query(uri, null, null, null, null)!!
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                cursor.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result.substring(cut + 1)
            }
        }
        return result
    }

    override fun getCountImage() = listImageUpload.size

    override fun getDataImage(position: Int): ImageUpload {
        return listImageUpload[position]
    }

    override fun onDeleteItemImage(adapterPosition: Int) {
        listImageUpload.removeAt(adapterPosition)
        binding.rcImageUpload.adapter?.notifyItemRemoved(adapterPosition)
    }

    override fun getCountVideo() = listVideoUpload.size

    override fun getDataVideo(position: Int): VideoUpload {
        return listVideoUpload[position]
    }

    override fun onItemClickVideo(position: Int, _id: Int, videoView: VideoView) {
        videoView.setVideoURI(listVideoUpload[position].pathUri)
        videoView.start()
    }

    override fun onDeleteItemVideo(adapterPosition: Int) {
        listVideoUpload.removeAt(adapterPosition)
        binding.rcVideoUpload.adapter?.notifyItemRemoved(adapterPosition)
    }

    override fun onPause() {
        super.onPause()
        listImageUpload.clear()
        listVideoUpload.clear()
    }
}