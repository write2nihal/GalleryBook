package com.gallerybook.broadcast

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build

/**
 * Created by Nihal Srivastava on 30/10/21.
 */

class ConnectionDetector {
    fun isConnectingToInternet(context: Context): Boolean {
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

    fun isCapableNetwork(cm: ConnectivityManager, network: Network?): Boolean{
        cm.getNetworkCapabilities(network)?.also {
            if (it.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                return true
            } else if (it.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                return true
            }
        }
        return false
    }
    fun checkNetworkStatus(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val ni = cm.activeNetworkInfo
            if (ni != null) {
                if (ni.type === ConnectivityManager.TYPE_WIFI) { // connected to wifi
                    return true
                }
            }
            return false
        } else {
            val networkInfos = cm.allNetworks
            for (tempNetworkInfo in networkInfos) {
                cm.getNetworkCapabilities(tempNetworkInfo)?.also {
                    if (it.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        return true
                    } else if (it.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        return false
                    }
                }
            }
        }
        return false
    }

    fun networkInfo(mContext: Context): Boolean {
        val connectivityManager = mContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var activeNetworkInfo: NetworkInfo? = null
        if (connectivityManager != null) {
            activeNetworkInfo = connectivityManager.activeNetworkInfo
        }
        return activeNetworkInfo != null
    }

}