package com.ddona.memecommunity.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.ddona.memecommunity.R
import com.ddona.memecommunity.databinding.ActivityMainBinding
import com.ddona.memecommunity.ui.fragment.ImageFragment
import com.ddona.memecommunity.ui.fragment.UploadFragment
import com.ddona.memecommunity.ui.fragment.VideoFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val PERMISSION_CODE = 2021
    private val imageFragment = ImageFragment()
    private val videoFragment = VideoFragment()
    private val uploadFragment = UploadFragment()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        checkedInternet()
        if (!checkPermission()) {
            requestPermission()
        }
        replaceFragment(imageFragment)
        binding.navigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_image -> {
                    replaceFragment(imageFragment)
                }
                R.id.navigation_video -> {
                    replaceFragment(videoFragment)
                }
                R.id.navigation_upload -> {
                    replaceFragment(uploadFragment)
                }
            }
            true
        }
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.M)
    private fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                return true
            }
        }
        return false
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun checkedInternet() {
        if (isOnline(this) == false) {
            Toast.makeText(this, "Internet Disconnected", Toast.LENGTH_SHORT).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Well come to Meme Community", Toast.LENGTH_SHORT).show()
            } else {
                requestPermission()
            }
        }
    }

    private fun checkPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this@MainActivity,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            this@MainActivity,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            this@MainActivity,
            Manifest.permission.INTERNET
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        if (!checkPermission()) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.INTERNET
                ), PERMISSION_CODE
            )
        } else {
            requestPermission()
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        if (fragment != null) {
            val tran = supportFragmentManager.beginTransaction()
            when (fragment) {
                imageFragment -> {
                    val bundle = Bundle()
                    fragment.arguments = bundle
                }
                videoFragment -> {
                    val bundle = Bundle()
                    fragment.arguments = bundle
                }
                uploadFragment -> {
                    val bundle = Bundle()
                    fragment.arguments = bundle
                }
            }
            tran.apply {
                replace(R.id.frame_container, fragment)
                commit()
            }
        } else {
            return
        }
    }

}