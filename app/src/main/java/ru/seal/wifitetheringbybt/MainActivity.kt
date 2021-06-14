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
import android.widget.TextView

class MainActivity  : AppCompatActivity() {
    var spinInited = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val context = applicationContext
        // Check whether has the write settings permission or not.
        val settingsCanWrite = Settings.System.canWrite(context);
        if (!settingsCanWrite) {
            // If do not have write settings permission then open the Can modify system settings panel.
            val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            startActivity(intent);
        } else {
            findViewById<TextView>(R.id.textViewGrants).text =
                "You have system write settings permission now."
        }

        val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        val pairedDevices = mBluetoothAdapter.bondedDevices

        val btDevicesList: MutableList<String> = ArrayList()
        for (bt in pairedDevices) {
            btDevicesList.add(bt.name)
        }

        val spinner: Spinner = findViewById(R.id.spinner)

        val sharedPreferences = this.getSharedPreferences("MyBT", Context.MODE_PRIVATE)
        val arrayAdapter: ArrayAdapter<String> = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item, btDevicesList
        )

        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = arrayAdapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                arg0: AdapterView<*>?,
                arg1: View?,
                position: Int,
                id: Long
            ) {
                if (!spinInited) {
                    spinInited = true
                    val name = sharedPreferences.getString("SavedBTName", null)
                    if (name != null) {
                        spinner.setSelection(btDevicesList.indexOf(name))
                    } else {
                        spinner.setSelection(0)
                    }
                } else {
                    sharedPreferences.edit().putString("SavedBTName", btDevicesList[position])
                        .apply()
                }
            }

            override fun onNothingSelected(arg0: AdapterView<*>?) {
                // TODO Auto-generated method stub
            }
        }
    }
}