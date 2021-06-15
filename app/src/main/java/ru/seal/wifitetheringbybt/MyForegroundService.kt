package ru.seal.wifitetheringbybt

import android.app.*
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothHeadset
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.NotificationCompat
import ru.seal.wifitetheringbybt.wifi.OreoWifiManager


class MyForegroundService : Service() {
    val CHANNEL_ID = "ForegroundServiceChannel"
    val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    var bluetoothHeadset: BluetoothHeadset? = null
    var bluetoothDevice: BluetoothDevice? = null

    val profileListener = object : BluetoothProfile.ServiceListener {

        override fun onServiceConnected(profile: Int, proxy: BluetoothProfile) {
            val sharedPreferences =
                applicationContext.getSharedPreferences("MyBT", Context.MODE_PRIVATE)
            val name = sharedPreferences.getString("SavedBTName", null)
            if (profile == BluetoothProfile.HEADSET) {
                bluetoothHeadset = proxy as BluetoothHeadset
            }
            for (device in proxy.connectedDevices) {
                if (device.name == name) {
                    bluetoothDevice = device
                    OreoWifiManager(applicationContext).start()
                }
            }
        }

        override fun onServiceDisconnected(profile: Int) {
            Toast.makeText(
                applicationContext,
                "onServiceDisconnectedFromForeground",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val input = intent.getStringExtra("inputExtra")
        createNotificationChannel()
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, 0
        )
        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Foreground Service")
            .setContentText(input)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .build()
        startForeground(1, notification)
        //do heavy work on a background thread
        //stopSelf();

        bluetoothAdapter?.getProfileProxy(
            applicationContext,
            profileListener,
            BluetoothProfile.HEADSET
        )
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        bluetoothAdapter?.closeProfileProxy(BluetoothProfile.HEADSET, bluetoothHeadset)
        super.onDestroy()
    }

    private fun createNotificationChannel() {
        val serviceChannel = NotificationChannel(
            CHANNEL_ID,
            "Foreground Service Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = getSystemService(
            NotificationManager::class.java
        )
        manager.createNotificationChannel(serviceChannel)
    }
}