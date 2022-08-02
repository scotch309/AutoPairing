package com.example.testble

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions

@RequiresApi(Build.VERSION_CODES.S)
class AdvertiseScanner(activity: Activity, serviceName: String) {
    private var adapter: BluetoothAdapter? =
        (activity.getSystemService(serviceName) as BluetoothManager).adapter
    private var scanner: BluetoothLeScanner? = null
    private var scanFilterList: ArrayList<ScanFilter> = ArrayList()
    private var scanSettings: ScanSettings? = null

    init {
        if (this.isSupported() && this.isEnabled()) {
            this.scanner = this.adapter!!.bluetoothLeScanner
            this.checkPermission(activity.applicationContext, activity)
        }
    }
    fun isSupported(): Boolean {
       return this.adapter != null
    }
    fun isEnabled(): Boolean {
        return if (this.isSupported()) {
            this.adapter!!.isEnabled
        } else {
            false
        }
    }
    fun setFilter(deviceName:String) {
        val filter: ScanFilter = ScanFilter.Builder().setDeviceName(deviceName).build()
        this.scanFilterList = ArrayList()
        this.scanFilterList.add(filter)
    }
    fun setFilter() {
        val filter: ScanFilter = ScanFilter.Builder().build()
        this.scanFilterList = ArrayList()
        this.scanFilterList.add(filter)
    }
    fun setSettings() {
        this.scanSettings = ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_BALANCED).build()
    }
    fun startScan(context: Context, activity: Activity, callback: ScanCallback) {
        if (!this.isSupported()) {
            return
        }
        this.checkPermission(context, activity)
        this.scanner!!.startScan(this.scanFilterList, this.scanSettings, callback)
    }
    private fun checkPermission(context: Context, activity: Activity) {
        if (ActivityCompat.checkSelfPermission(
                context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(activity, arrayOf(
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_ADVERTISE,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_CONTACTS
            ), 1)
        }
    }
    fun stopScan(context: Context, activity: Activity, callback:ScanCallback) {
        if (!this.isSupported()) {
            return
        }
        this.checkPermission(context, activity)
        this.scanner!!.stopScan(callback)
    }
}