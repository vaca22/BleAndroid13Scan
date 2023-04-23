package com.vaca.bleandroid13scan

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat


class MainActivity : AppCompatActivity() {

    private fun checkPermission(p_array: List<String>): Boolean {
        for (p in p_array) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    p
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val p_array = listOf<String>(
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH_CONNECT,
        )

        val requestVoicePermission = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) {
            start()
        }
        if (!checkPermission(p_array)
        ) {
            requestVoicePermission.launch(p_array.toTypedArray())
        } else {
            start()
        }

    }


    @SuppressLint("MissingPermission")
    fun start() {
        val bluetoothManager =
            getSystemService(AppCompatActivity.BLUETOOTH_SERVICE) as BluetoothManager
        val mBluetoothAdapter = bluetoothManager.adapter
        if (mBluetoothAdapter.isDiscovering) {
            mBluetoothAdapter.cancelDiscovery()
        }
        mBluetoothAdapter.startDiscovery()
        val mReceiver = SingBroadcastReceiver()
        val ifilter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        this.registerReceiver(mReceiver, ifilter)

    }

    private class SingBroadcastReceiver : BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context?, intent: Intent) {
            val action = intent.action //may need to chain this to a recognizing function
            if (BluetoothDevice.ACTION_FOUND == action) {
                val device =
                    intent.getParcelableExtra(
                        BluetoothDevice.EXTRA_DEVICE,
                        BluetoothDevice::class.java
                    )
                if (device!!.name.isNullOrEmpty()) {
                    return
                }
                val rssi =
                    intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE).toInt()
                val deviceType = device.type
                val derp = device.name + " - " + device.address+" - "+rssi
                if(device.name.startsWith("Check")==false){
                    return
                }
                if (deviceType == BluetoothDevice.DEVICE_TYPE_CLASSIC) {
                    Log.e("vaca", "CLASSIC device: $derp")
                } else if (deviceType ==  BluetoothDevice.DEVICE_TYPE_LE) {
                    Log.e("vaca", "LE device: $derp")
                } else if (deviceType == BluetoothDevice.DEVICE_TYPE_DUAL) {
                    Log.e("vaca", "DUAL device: $derp")
                }


            }
        }
    }
}