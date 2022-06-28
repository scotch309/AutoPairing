package com.example.testble

import android.Manifest
import android.bluetooth.BluetoothManager
import android.bluetooth.le.*
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.cardiomood.android.controls.gauge.SpeedometerGauge
import java.security.AccessController.getContext

class MainActivity : AppCompatActivity() {
    private val requestEnableBT = 1
    private var manager:BluetoothManager? = null
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        manager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        if (manager?.adapter == null) {
            Toast.makeText(applicationContext, "端末サポートされてません", Toast.LENGTH_LONG).show()
            finish()
            return
        }
        if (!manager?.adapter?.isEnabled!!) {
            Toast.makeText(applicationContext, "Bluetoothが無効になっています", Toast.LENGTH_LONG).show()
            finish()
            return
        }
        setSpeedmeterGauge()
        setBtnAction()
    }
    private fun setBtnAction() {
        val bluetoothLeScanner: BluetoothLeScanner = manager?.adapter?.bluetoothLeScanner!!
        val scanFilter: ScanFilter = ScanFilter.Builder().build()
        val scanFilterList: ArrayList<ScanFilter> = ArrayList()
        scanFilterList.add(scanFilter)
        val scanSettings: ScanSettings = ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_BALANCED).build()
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_ADVERTISE,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_CONTACTS
            ), requestEnableBT)
        }
        val btnStart:Button = findViewById(R.id.btn_start)
        val btnStop:Button = findViewById(R.id.btn_stop)
        btnStop.isEnabled = false
        btnStart.setOnClickListener {
            btnStart.isEnabled = false
            bluetoothLeScanner.startScan(scanFilterList, scanSettings, scanCallback)
        }
        btnStop.setOnClickListener {
            btnStart.isEnabled = false
            btnStop.isEnabled = false
            bluetoothLeScanner.stopScan(scanCallback)
            btnStart.isEnabled = true
        }
    }
    //スキャンで見つかったデバイスが飛んでくる
    private val scanCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            Log.d("scanResult:", result.device.address)
        }
    }
    private fun setSpeedmeterGauge() {
        val speedMeter:SpeedometerGauge = findViewById(R.id.speedometer)
        speedMeter.maxSpeed = 300.0
        speedMeter.majorTickStep = 30.0
        speedMeter.minorTicks = 2
        speedMeter.addColoredRange(30.0,140.0, Color.GREEN)
        speedMeter.addColoredRange(140.0,180.0, Color.YELLOW)
        speedMeter.addColoredRange(180.0,400.0, Color.RED)
    }
}