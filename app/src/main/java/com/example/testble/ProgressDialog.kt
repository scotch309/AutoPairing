package com.example.testble

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.TextView
import androidx.fragment.app.DialogFragment

class ProgressDialog: DialogFragment() {
    companion object {
        fun newInstance(message: String): ProgressDialog {
            val instance = ProgressDialog()
            val arguments = Bundle()
            arguments.putString("message", message)
            instance.arguments = arguments
            return instance
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val mMessage = arguments?.getString("message")

        val builder = AlertDialog.Builder(activity)
        val inflater = activity?.layoutInflater
        val view = inflater?.inflate(R.layout.dialog_progress, null)
        val mMessageTextView = view?.findViewById(R.id.progress_message) as TextView
        mMessageTextView.text = mMessage
        builder.setView(view)
        return builder.create()
    }
}