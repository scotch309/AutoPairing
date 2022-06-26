package com.example.testble

import android.Manifest
import android.bluetooth.BluetoothManager
import android.bluetooth.le.*
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity() {
    private val requestEnableBT = 1
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val manager: BluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = manager.adapter
        // 端末がBluetoothサポートされてる？
        if (bluetoothAdapter == null) {
            Toast.makeText(applicationContext, "端末サポートされてません", Toast.LENGTH_LONG).show()
            finish()
            return
        }
        // bluetoothは　有効になってる？
        // 無効なら有効にしていいか許可をとるダイアログを表示する。
        if (!bluetoothAdapter.isEnabled) {
            Toast.makeText(applicationContext, "Bluetoothが無効になっています", Toast.LENGTH_LONG).show()
            finish()
            return
        }
        val bluetoothLeScanner: BluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
        val scanFilter: ScanFilter = ScanFilter.Builder().build()
        val scanFilterList: ArrayList<ScanFilter> = ArrayList()
        scanFilterList.add(scanFilter)
        val scanSettings: ScanSettings = ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_BALANCED).build()
        Log.d("TAG", "startScan set")
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN), requestEnableBT)
        }
        bluetoothLeScanner.startScan(scanFilterList, scanSettings, scanCallback)
    }
    //スキャンで見つかったデバイスが飛んでくる
    private val scanCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            Log.d("scanResult:", result.device.address)
        }
    }
}