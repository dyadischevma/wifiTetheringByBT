package ru.seal.wifitetheringbybt.bt

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothProfile
import android.content.Context
import ru.seal.wifitetheringbybt.wifi.OreoWifiManager

class MyBTServiceListener(
    private val context: Context,
    private val deviceName: String?
) : BluetoothProfile.ServiceListener {

    override fun onServiceConnected(profile: Int, proxy: BluetoothProfile) {
        if (isConnected(deviceName, proxy)) {
            OreoWifiManager(context).start()
        } else {
            OreoWifiManager(context).stop()
        }
        BluetoothAdapter.getDefaultAdapter().closeProfileProxy(profile, proxy)
    }

    override fun onServiceDisconnected(profile: Int) {
    }

    private fun isConnected(deviceName: String?, proxy: BluetoothProfile): Boolean {
        for (device in proxy.connectedDevices) {
            if (device.name == deviceName) {
                return true
            }
        }
        return false
    }
}