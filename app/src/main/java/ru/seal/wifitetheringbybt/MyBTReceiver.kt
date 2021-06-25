package ru.seal.wifitetheringbybt

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothProfile
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import ru.seal.wifitetheringbybt.Constants.ACL_CONNECTED
import ru.seal.wifitetheringbybt.Constants.ACL_DISCONNECTED
import ru.seal.wifitetheringbybt.Constants.APP_NAME
import ru.seal.wifitetheringbybt.Constants.SAVED_BT_NAME
import ru.seal.wifitetheringbybt.bt.MyBTServiceListener

class MyBTReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val sharedPreferences = context.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE)
        val name = sharedPreferences.getString(SAVED_BT_NAME, null)

        if (intent.action == ACL_CONNECTED || intent.action == ACL_DISCONNECTED) {
            BluetoothAdapter.getDefaultAdapter().getProfileProxy(
                context,
                MyBTServiceListener(context, name),
                BluetoothProfile.HEADSET
            )
        }
    }
}