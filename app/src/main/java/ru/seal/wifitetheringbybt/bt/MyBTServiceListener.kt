package com.example.mybtapp.bt

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.util.Log
import com.example.mybtapp.wifi.OreoWifiManager

class MyBTServiceListener(
    private val context: Context,
    private val deviceName: String
) : BluetoothProfile.ServiceListener {

    override fun onServiceConnected(profile: Int, proxy: BluetoothProfile) {
        for (device in proxy.connectedDevices) {
            if (device.name == deviceName) {
                OreoWifiManager(context).start()
            }
        }
        BluetoothAdapter.getDefaultAdapter().closeProfileProxy(profile, proxy)
    }

    override fun onServiceDisconnected(profile: Int) {
    }
}