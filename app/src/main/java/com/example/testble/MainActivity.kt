package com.example.testble

import android.annotation.SuppressLint
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity

class Devices {
    private var devices: MutableMap<String, MutableList<Int>>? = null

    fun add(uuid:String, rssi:Int) {
        if (devices == null) {
            val list = mutableListOf<Int>(rssi)
        }
        else {

        }
    }
}

class MainActivity : AppCompatActivity() {
    private var scanner:AdvertiseScanner? = null
    private val progressDialog = ProgressDialog.newInstance("検索中")

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
            progressDialog.show(supportFragmentManager, "TAG")
            this.scanner?.setFilter(deviceFilter)
            //this.scanner?.setFilter()
            this.scanner?.setSettings()
            this.scanner?.startScan(this,this, this.scanCallback)
        }
    }
    //スキャンで見つかったデバイスが飛んでくる
    private val scanCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            Log.d("scanResult:", result.rssi.toString())
        }
    }
}