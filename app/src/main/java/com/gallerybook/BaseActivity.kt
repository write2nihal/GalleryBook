package com.gallerybook

import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gallerybook.broadcast.NetworkStateReceiver
import com.gallerybook.broadcast.NetworkStateReceiverListener


open class BaseActivity : AppCompatActivity(), NetworkStateReceiverListener {

    private var networkStateReceiver: NetworkStateReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        networkStateReceiver = NetworkStateReceiver()
    }

    override fun onStart() {
        super.onStart()
        networkStateReceiver!!.addListener(this)
        this.registerReceiver(
            networkStateReceiver,
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )
    }

    override fun startActivity(intent: Intent?) {
        super.startActivity(intent)
        overridePendingTransition(R.anim.from_right_in, R.anim.from_left_out)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.from_left_in, R.anim.from_right_out)
    }


    override fun networkAvailable() {

    }

    override fun networkUnavailable() {

    }

    override fun onStop() {
        super.onStop()
        try {
            this.unregisterReceiver(networkStateReceiver)
        } catch (e: Exception) {
        }
    }
}