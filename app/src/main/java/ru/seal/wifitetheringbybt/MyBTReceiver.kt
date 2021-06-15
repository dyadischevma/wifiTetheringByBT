package ru.seal.wifitetheringbybt

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothProfile
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import ru.seal.wifitetheringbybt.bt.MyBTServiceListener
import ru.seal.wifitetheringbybt.wifi.OreoWifiManager

class MyBTReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val sharedPreferences = context.getSharedPreferences("MyBT",  Context.MODE_PRIVATE)
        val name = sharedPreferences.getString("SavedBTName", null)?:""

        BluetoothAdapter.getDefaultAdapter().getProfileProxy(
            context,
            MyBTServiceListener(context, name),
            BluetoothProfile.HEADSET
        )

        if (intent.action == "android.bluetooth.device.action.ACL_CONNECTED") {
            Toast.makeText(context, "ACL_CONNECTED", Toast.LENGTH_LONG).show()
        }
        if (intent.action == "android.bluetooth.device.action.ACL_DISCONNECTED") {
            Toast.makeText(context, "ACL_DISCONNECTED", Toast.LENGTH_LONG).show()
            OreoWifiManager(context).stop()
        }
    }
}