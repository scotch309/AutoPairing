package com.example.testble

import android.annotation.SuppressLint
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.cardiomood.android.controls.gauge.SpeedometerGauge

class MainActivity : AppCompatActivity() {
    private var speedMeter:SpeedometerGauge? = null
    private var scanner:AdvertiseScanner? = null

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
        val btnStart:Button = findViewById(R.id.btn_start)
        val btnStop:Button = findViewById(R.id.btn_stop)
        val tvStatus:TextView = findViewById(R.id.tv_status)
        btnStart.setOnClickListener {
            changeBtnState(false)
            val deviceFilter = findViewById<EditText>(R.id.et_filter).text.toString()
            if (deviceFilter == "") {
                btnStart.isEnabled = true
                Toast.makeText(applicationContext, "検索するデバイス名を入力してください", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            tvStatus.text = getString(R.string.searching)
            this.scanner?.setFilter(deviceFilter)
            this.scanner?.setSettings()
            this.scanner?.startScan(this,this, this.scanCallback)
            btnStop.isEnabled = true
        }
        btnStop.setOnClickListener {
            changeBtnState(false)
            this.scanner?.stopScan(this,this, this.scanCallback)
            speedMeter?.speed = .0
            tvStatus.text = getString(R.string.stopping)
            btnStart.isEnabled = true
        }
        btnStop.isEnabled = false
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
    //スキャンで見つかったデバイスが飛んでくる
    private val scanCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            Log.d("scanResult:", result.rssi.toString())
            speedMeter?.speed = -1 * result.rssi.toDouble()
        }
    }
}