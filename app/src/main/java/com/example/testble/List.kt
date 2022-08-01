package com.example.testble

import android.content.Context
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter

class Device(val uuid: String, val name: String, val rssi: Int)

class ListAdapter(context: Context, var deviceList: List<Device>): ArrayAdapter<Device>(context, 0, deviceList) {
    private val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val device = deviceList[position]

        var view = convertView
        if (convertView == null) {
        }
        return super.getView(position, convertView, parent)
    }
}