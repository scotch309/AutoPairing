package com.example.testble

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.bluetooth.le.*
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.cardiomood.android.controls.gauge.SpeedometerGauge

class MainActivity : AppCompatActivity() {
    private val requestEnableBT = 1
    private var manager:BluetoothManager? = null
    private var speedMeter:SpeedometerGauge? = null

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
    @RequiresApi(Build.VERSION_CODES.S)
    private fun setBtnAction() {
        @SuppressLint("CutPasteId")
        fun changeBtnState(isEnabled:Boolean) {
            findViewById<Button>(R.id.btn_start).isEnabled = isEnabled
            findViewById<Button>(R.id.btn_stop).isEnabled = isEnabled
        }
        val bluetoothLeScanner: BluetoothLeScanner = manager?.adapter?.bluetoothLeScanner!!
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
            changeBtnState(false)
            val etDeviceFilter:EditText = findViewById(R.id.et_filter)
            val deviceFilter = etDeviceFilter.text.toString()
            if (deviceFilter == "") {
                btnStart.isEnabled = true
                Toast.makeText(applicationContext, "検索するデバイス名を入力してください", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            val scanFilter: ScanFilter = ScanFilter.Builder().setDeviceName(deviceFilter).build()
            val scanFilterList: ArrayList<ScanFilter> = ArrayList()
            scanFilterList.add(scanFilter)
            bluetoothLeScanner.startScan(scanFilterList, scanSettings, scanCallback)
            btnStop.isEnabled = true
        }
        btnStop.setOnClickListener {
            changeBtnState(false)
            bluetoothLeScanner.stopScan(scanCallback)
            speedMeter?.speed = .0
            btnStart.isEnabled = true
        }
    }
    //スキャンで見つかったデバイスが飛んでくる
    private val scanCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            Log.d("scanResult:", result.rssi.toString())
            speedMeter?.speed = -1 * result.rssi.toDouble()
        }
    }
    private fun setSpeedmeterGauge() {
        speedMeter = findViewById(R.id.speedometer)
        speedMeter?.maxSpeed = 100.0
        speedMeter?.majorTickStep = 5.0
        speedMeter?.minorTicks = 1
        speedMeter?.addColoredRange(10.0,40.0, Color.GREEN)
        speedMeter?.addColoredRange(40.0,70.0, Color.YELLOW)
        speedMeter?.addColoredRange(70.0,100.0, Color.RED)
    }
}