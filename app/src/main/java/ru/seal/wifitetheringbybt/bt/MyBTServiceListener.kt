package ru.seal.wifitetheringbybt.bt

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothProfile
import android.content.Context
import ru.seal.wifitetheringbybt.Constants
import ru.seal.wifitetheringbybt.wifi.OreoWifiManager

class MyBTServiceListener(
    private val context: Context,
    private val deviceName: String?
) : BluetoothProfile.ServiceListener {

    override fun onServiceConnected(profile: Int, proxy: BluetoothProfile) {
        val sharedPreferences =
            context.getSharedPreferences(Constants.APP_NAME, Context.MODE_PRIVATE)
        val isEnabledTethering = sharedPreferences.getBoolean(Constants.IS_ENABLED_TETHERING, false)

        if (isConnected(deviceName, proxy)) {
            if (!isEnabledTethering) {
                OreoWifiManager(context).start()
            }
        } else {
            if (isEnabledTethering) {
                OreoWifiManager(context).stop()
            }
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