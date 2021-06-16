package ru.seal.wifitetheringbybt

import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import ru.seal.wifitetheringbybt.Constants.APP_NAME
import ru.seal.wifitetheringbybt.Constants.SAVED_BT_NAME

class MainActivity : AppCompatActivity() {
    var spinInited = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val settingsCanWrite = Settings.System.canWrite(applicationContext)
        if (!settingsCanWrite) {
            val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
            startActivity(intent)
        }

        val btDevicesList: MutableList<String> = ArrayList()
        for (btDevice in BluetoothAdapter.getDefaultAdapter().bondedDevices) {
            btDevicesList.add(btDevice.name)
        }

        val spinner: Spinner = findViewById(R.id.spinner)
        val arrayAdapter: ArrayAdapter<String> = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            btDevicesList
        )

        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = arrayAdapter

        val sharedPreferences = this.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE)

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                arg0: AdapterView<*>?,
                arg1: View?,
                position: Int,
                id: Long
            ) {
                if (!spinInited) {
                    spinInited = true
                    val name = sharedPreferences.getString(SAVED_BT_NAME, null)
                    if (name != null) {
                        spinner.setSelection(btDevicesList.indexOf(name))
                    } else {
                        spinner.setSelection(0)
                    }
                } else {
                    sharedPreferences
                        .edit()
                        .putString(SAVED_BT_NAME, btDevicesList[position])
                        .apply()
                }
            }

            override fun onNothingSelected(arg0: AdapterView<*>?) {
                // TODO Auto-generated method stub
            }
        }
    }
}