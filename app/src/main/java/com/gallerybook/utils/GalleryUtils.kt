package com.gallerybook.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.kaopiz.kprogresshud.KProgressHUD
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by Nihal Srivastava on 30/10/21.
 */
class GalleryUtils {

    companion object {

        @SuppressLint("StaticFieldLeak")
        private var hud: KProgressHUD? = null
        fun showLoader(context: Context) {
            hud = KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
            hud!!.show()
        }

        fun hideLoader() {
            hud!!.dismiss()
        }

        fun showToast(context: Context, title: String) {
            Toast.makeText(context, title, Toast.LENGTH_SHORT).show()
        }

        fun getFormattedDate(timestampInMilliSeconds: Long): String {
            val date = Date()
            date.time = timestampInMilliSeconds
            return SimpleDateFormat("yyyy-MMM-dd").format(date)
        }

        fun showSnackBar(view: View, str: String) {
            val snackbar = Snackbar.make(view, str, Snackbar.LENGTH_LONG).setAction("Action", null)
            snackbar.setActionTextColor(Color.BLUE)
            val snackbarView = snackbar.view
            snackbarView.setBackgroundColor(Color.parseColor("#a0009688"))
            val textView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
            textView.setTextColor(Color.WHITE)
            textView.textSize = 16f
            snackbar.show()
        }
        fun hasInternet(context: Context): Boolean {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (cm != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    val ni = cm.activeNetworkInfo
                    if (ni != null) {
                        return ni.isConnected && (ni.type == ConnectivityManager.TYPE_WIFI || ni.type == ConnectivityManager.TYPE_MOBILE)
                    }
                } else {
                    val networkInfos = cm.allNetworks
                    for (tempNetworkInfo in networkInfos) {
                        if(isCapableNetwork(cm,tempNetworkInfo))
                            return  true
                    }
                }
            }
            return false
        }

        private fun isCapableNetwork(cm: ConnectivityManager, network: Network?): Boolean{
            cm.getNetworkCapabilities(network)?.also {
                if (it.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    return true
                } else if (it.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    return true
                }
            }
            return false
        }
    }
}