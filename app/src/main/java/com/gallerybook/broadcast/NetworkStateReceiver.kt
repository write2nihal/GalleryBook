package com.gallerybook.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.gallerybook.interfaces.OnWifiConnectivityCheck


import java.util.*


/**
 * Created by Nihal Srivastava on 30/10/21.
 */
class NetworkStateReceiver : BroadcastReceiver() {
    private var listeners: MutableSet<NetworkStateReceiverListener> = HashSet()
    private var wifiListener: OnWifiConnectivityCheck? = null
    private var connected: Boolean?
    private var isWifiConnected: Boolean = false
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.extras == null) return
        connected = ConnectionDetector().isConnectingToInternet(context)
        isWifiConnected = ConnectionDetector().checkNetworkStatus(context)
        notifyStateToAll()
    }


    private fun notifyStateToAll() {
        for (listener in listeners) {
            notifyState(listener)
        }
        if (wifiListener != null) {
            notifyWIfIConnection()
        }
    }

    private fun notifyState(listener: NetworkStateReceiverListener?) {
        if (connected == null || listener == null) return
        if (connected as Boolean) listener.networkAvailable() else listener.networkUnavailable()
    }

    fun addListener(l: NetworkStateReceiverListener) {
        listeners.add(l)
        notifyState(l)
    }

    fun addWifiConnectionListener(listener: OnWifiConnectivityCheck) {
        wifiListener = listener
    }

    private fun notifyWIfIConnection() {
        if (wifiListener != null) {
            if (isWifiConnected != null) {
                if (isWifiConnected) {
                    wifiListener?.isWifiConnected(true)
                } else {
                    wifiListener?.isWifiConnected(false)
                }
            } else {
                wifiListener?.isWifiConnected(false)
            }
        }

    }

    fun removeListener(l: NetworkStateReceiverListener?) {
        listeners.remove(l)
    }

    init {
        connected = null
    }
}