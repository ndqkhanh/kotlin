package com.example.kotlin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.slider.Slider
import java.text.DecimalFormat

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

fun thousandToDecimalFormat(value: Int): String {
    return DecimalFormat("#,###")
        .format(value)
        .replace(",", ".")
}

/**
 * A simple [Fragment] subclass.
 * Use the [PricingFilter.newInstance] factory method to
 * create an instance of this fragment.
 */
class PricingFilter : BottomSheetDialogFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var defaultPricing = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)

            defaultPricing = it.getInt("defaultPricing")
        }
    }

    var onApply: ((Pricing: Int) -> Unit)? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_pricing_filter, container, false)

        val pricingSeekBar = view.findViewById<Slider>(R.id.pricingSeekBar)
        val pricingValue = view.findViewById<TextView>(R.id.pricingValue)
        pricingSeekBar.value = defaultPricing.toFloat()
        pricingValue.text = thousandToDecimalFormat(defaultPricing) + "đ"

        pricingSeekBar.addOnChangeListener { slider, value, fromUser ->
            pricingValue.text = thousandToDecimalFormat(value.toInt()) + "đ"
        }

        val closeFilter = view.findViewById<TextView>(R.id.closeFilter)

        closeFilter.setOnClickListener {
            dismiss()
        }

        val applyPricingFilter = view.findViewById<TextView>(R.id.applyPricingFilter)
        applyPricingFilter.setOnClickListener {
            onApply?.invoke(pricingSeekBar.value.toInt())
            dismiss()
        }

        val removeFilter = view.findViewById<TextView>(R.id.removeFilter)
        removeFilter.setOnClickListener {
            onApply?.invoke(0)
            dismiss()
        }
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PricingFilter.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PricingFilter().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}