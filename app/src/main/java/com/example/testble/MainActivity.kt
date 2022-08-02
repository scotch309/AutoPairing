package com.example.testble

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class Devices {
    private val devices = mutableMapOf<String, MutableList<Int>>()
    private val results = mutableListOf<ScanResult>()

    fun add(result: ScanResult) {
        if (this.devices[result.device.address] == null) {
            this.devices[result.device.address] = mutableListOf(result.rssi)
            this.results.add(result)
        } else {
            this.devices[result.device.address]?.add(result.rssi)
        }
    }
    fun getAverage(): Map<String, Double> {
        val averageDeviceDictionary = mutableMapOf<String, Double>()
        this.devices.forEach {(key,value)->
            averageDeviceDictionary[key] = value.average()
        }
        //return averageDeviceDictionary.toList().sortedBy { it.second }.reversed().toMap()
        return averageDeviceDictionary.toList().sortedBy { it.second }.toMap()
    }
    fun clear() {
        this.devices.clear()
        this.results.clear()
    }
    @RequiresApi(Build.VERSION_CODES.S)
    private fun checkPermission(context: Context, activity: Activity) {
        if (ActivityCompat.checkSelfPermission(
                context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                activity, arrayOf(
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_ADVERTISE,
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_CONTACTS
                ), 1
            )
        }
    }
    @RequiresApi(Build.VERSION_CODES.S)
    fun createBond(uuid:String, context: Context, activity: Activity) {
        this.checkPermission(context, activity)
        context.applicationContext.registerReceiver(
            this.broadcastReceiver,
            IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
        )
        for (result in this.results) {
            if (result.device.address == uuid) {
                result.device.createBond()
                break
            }
        }
    }
    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when(intent?.action) {
                BluetoothDevice.ACTION_BOND_STATE_CHANGED -> {
                    Toast.makeText(, "端末サポートされてません", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}

class MainActivity : AppCompatActivity() {
    private var scanner:AdvertiseScanner? = null
    private val progressDialog = ProgressDialog.newInstance("検索中")
    private val devices = Devices()

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.scanner = AdvertiseScanner(this, Context.BLUETOOTH_SERVICE)
        if (!this.scanner?.isSupported()!!) {
            Toast.makeText(applicationContext, "端末サポートされてません", Toast.LENGTH_LONG).show()
            finish()
            return
        }
        if (!this.scanner?.isEnabled()!!) {
            Toast.makeText(applicationContext, "Bluetoothが無効になっています", Toast.LENGTH_LONG).show()
            finish()
            return
        }
        setBtnAction()
    }
    @RequiresApi(Build.VERSION_CODES.S)
    private fun setBtnAction() {
        @SuppressLint("CutPasteId")
        val btnStart = findViewById<Button>(R.id.btn_start)
        btnStart.setOnClickListener {
            val deviceFilter = findViewById<EditText>(R.id.et_filter).text.toString()
            if (deviceFilter == "") {
                Toast.makeText(applicationContext, "検索するデバイス名を入力してください", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            btnStart.isEnabled = false
            this.progressDialog.show(supportFragmentManager, "TAG")
            this.scanner?.setFilter(deviceFilter)
            //this.scanner?.setFilter()
            this.scanner?.setSettings()
            this.scanner?.startScan(this,this, this.scanCallback)
            // 5秒後に停止
            Executors.newSingleThreadScheduledExecutor().schedule({
                this.scanner?.stopScan(this,this,this.scanCallback)
                this.progressDialog.dismiss()
                this.pairing()
                btnStart.isEnabled = true
            }, 5,TimeUnit.SECONDS)
        }
    }
    @RequiresApi(Build.VERSION_CODES.S)
    private fun pairing() {
        val devices: Map<String, Double> = this.devices.getAverage()
        for (key in devices.keys) {
            this.devices.createBond(key,this,this)
            break
        }
        this.devices.clear()
    }
    //スキャンで見つかったデバイスが飛んでくる
    private val scanCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            devices.add(result)
        }
    }
}