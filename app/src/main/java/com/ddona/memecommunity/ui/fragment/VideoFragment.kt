package com.ddona.memecommunity.ui.fragment

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.DownloadManager
import android.content.Context.DOWNLOAD_SERVICE
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.ddona.memecommunity.databinding.FragmentVideoBinding
import com.ddona.memecommunity.model.VideoFirebase
import com.ddona.memecommunity.ui.adapter.VideoAdapter
import com.google.firebase.storage.FirebaseStorage


class VideoFragment : Fragment(), VideoAdapter.IFolder, SwipeRefreshLayout.OnRefreshListener{
    private lateinit var binding: FragmentVideoBinding
    private var listVideo = arrayListOf<VideoFirebase>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentVideoBinding.inflate(inflater, container, false)
        getDataVideoFirebase()
        animation1()
        animation2()
        inits()
        return binding.root
    }

    private fun inits() {
        binding.swipeRefreshLayout.setOnRefreshListener(this)
        binding.rcVideo.setHasFixedSize(true)
        binding.rcVideo.adapter = VideoAdapter(this)
        binding.rcVideo.layoutManager =
            LinearLayoutManager(context)
    }

    private fun animation1() {
        val scaleX = ObjectAnimator.ofFloat(binding.ivSurprised1, "scaleX", 0.9f, 1.1f)
        val scaleY = ObjectAnimator.ofFloat(binding.ivSurprised1, "scaleY", 0.9f, 1.1f)
        scaleX.repeatCount = ObjectAnimator.INFINITE
        scaleX.repeatMode = ObjectAnimator.REVERSE
        scaleY.repeatCount = ObjectAnimator.INFINITE
        scaleY.repeatMode = ObjectAnimator.REVERSE
        val scaleAnim = AnimatorSet()
        scaleAnim.duration = 1000
        scaleAnim.play(scaleX).with(scaleY)
        scaleAnim.start()
    }

    private fun animation2() {
        val scaleX = ObjectAnimator.ofFloat(binding.ivSurprised2, "scaleX", 0.9f, 1.1f)
        val scaleY = ObjectAnimator.ofFloat(binding.ivSurprised2, "scaleY", 0.9f, 1.1f)
        scaleX.repeatCount = ObjectAnimator.INFINITE
        scaleX.repeatMode = ObjectAnimator.REVERSE
        scaleY.repeatCount = ObjectAnimator.INFINITE
        scaleY.repeatMode = ObjectAnimator.REVERSE
        val scaleAnim2 = AnimatorSet()
        scaleAnim2.duration = 1200
        scaleAnim2.play(scaleX).with(scaleY)
        scaleAnim2.start()
    }

    private fun getDataVideoFirebase() {
        binding.progress.visibility = View.VISIBLE
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference.child("videos/")

        val listAllTask = storageRef.listAll()
        listAllTask.addOnCompleteListener {
            try {
                val item = it.result?.items
                item?.forEachIndexed { index, item ->
                    item.downloadUrl.addOnSuccessListener {
                        addDataToListVideo(it.toString(), item.path)
                        binding.rcVideo.adapter?.notifyItemChanged(index)
                    }
                }
            } catch (e: Exception) {
                binding.progress.visibility = View.GONE
                binding.llError.visibility = View.VISIBLE
            }
        }
    }

    private fun addDataToListVideo(pathUrlVideo: String, pathDownload: String) {
        listVideo.add(
            VideoFirebase(
                pathUrlVideo,
                pathDownload
            )
        )
        binding.progress.visibility = View.GONE
    }

    override fun getCountVideo(): Int {
        return listVideo.size
    }

    override fun getDataVideo(position: Int): VideoFirebase {
        return listVideo[position]
    }

    override fun downloadVideo(adapterPosition: Int) {
        val fileDirectory = Environment.DIRECTORY_MOVIES
        val fileName = "${System.currentTimeMillis()}.mp4"
        var downloadManager = context?.getSystemService(DOWNLOAD_SERVICE) as DownloadManager

        val uri = Uri.parse(listVideo[adapterPosition].pathUrlVideo)
        val request = DownloadManager.Request(uri)
        request.setDestinationInExternalPublicDir("" + fileDirectory, "" + fileName)
        downloadManager.enqueue(request)
    }

    override fun onStart() {
        super.onStart()
        listVideo.clear()
    }

    override fun onPause() {
        super.onPause()
        listVideo.clear()
    }

    override fun onRefresh() {
        listVideo.clear()
        getDataVideoFirebase()
        binding.swipeRefreshLayout.isRefreshing = false
    }
}