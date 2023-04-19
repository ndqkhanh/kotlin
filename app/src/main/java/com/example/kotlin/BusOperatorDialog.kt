package com.example.kotlin

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.media.Image
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.fragment.app.DialogFragment

class BusOperatorDialog: DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)

            val inflater = requireActivity().layoutInflater;
            val view = LayoutInflater.from(requireContext()).inflate(R.layout.bus_operator_dialog, null)
            builder.setView(inflater.inflate(R.layout.bus_operator_dialog, null))
            val image = view.findViewById<ImageView>(R.id.boDiaglogImgV)
            image.setImageResource(android.R.drawable.star_big_on)


            builder.setPositiveButton("Sign in", DialogInterface.OnClickListener { dialog, id ->

                })
                .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, id ->
                    dialog.cancel()
                })


            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}