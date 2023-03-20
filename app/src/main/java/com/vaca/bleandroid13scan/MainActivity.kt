package com.vaca.bleandroid13scan

import android.Manifest
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
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
//还是android 13的蓝牙权限是画蛇添足， 还是需要精确地理位置
        val p_array = listOf<String>(
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_ADVERTISE,
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

    private var leScanCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(
            callbackType: Int,
            result: ScanResult,
        ) {
            Log.d("vaca", "result: ${result.device.address}")
            super.onScanResult(callbackType, result)
        }
    }
    private val settings: ScanSettings = ScanSettings.Builder()
        .setScanMode(ScanSettings.SCAN_MODE_BALANCED)
        .build()

    fun start() {
        val bluetoothManager =
            getSystemService(AppCompatActivity.BLUETOOTH_SERVICE) as BluetoothManager
        val mBluetoothAdapter = bluetoothManager.adapter
        val scanner = mBluetoothAdapter.bluetoothLeScanner

        val builder = ScanFilter.Builder()
        val filter = builder.build()
        try {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_SCAN
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            if(scanner==null){
                Log.e("vacavaca", "scanner==null")
                return
            }
            scanner.startScan(listOf(filter), settings, leScanCallback)
            Log.e("vacavaca", "startScan")
        } catch (e: Exception) {

            e.printStackTrace()
        }

    }
}