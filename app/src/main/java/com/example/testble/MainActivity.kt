package com.example.testble

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.bluetooth.le.*
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.content.PermissionChecker.checkSelfPermission

class MainActivity : AppCompatActivity() {

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val manager: BluetoothManager =
            getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val adapter = manager.adapter
        val bluetoothLeScanner: BluetoothLeScanner = adapter.bluetoothLeScanner


        var scanFilter: ScanFilter = ScanFilter.Builder()
            .build()
        var scanFilterList: ArrayList<ScanFilter> = ArrayList()
        scanFilterList.add(scanFilter)

        var scanSettings: ScanSettings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_BALANCED)
            .build()
        Log.d("TAG", "startScan set")
        bluetoothLeScanner.startScan(scanFilterList, scanSettings, scanCallback)
    }

    //スキャンで見つかったデバイスが飛んでくる
    private val scanCallback: ScanCallback = @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            Log.d("scanResult:", ",address,${result.device.address},rssi,${result.rssi}")
        }
    }
}