package com.goldmedal.hrapp.ui.dialogs

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.goldmedal.hrapp.R

class SuccessMessageDialog : DialogFragment() {


    var callBack: OnDashboardRefresh? = null


    companion object {
        fun newInstance() = SuccessMessageDialog()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCanceledOnTouchOutside(false)
        isCancelable = false
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_success_msg, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val textViewPunchType = view.findViewById<TextView>(R.id.txt_punch_type)
        val textViewPunchTime = view.findViewById<TextView>(R.id.txt_punch_time)

        textViewPunchType.text   = arguments?.getString("punchType", "")
        textViewPunchTime.text   = arguments?.getString("punchTime", "")
        val btnOk = view.findViewById<TextView>(R.id.btn_ok)

        btnOk.setOnClickListener {

            callBack?.redirectToHome()

            dismiss()
        }
    }

    interface OnDashboardRefresh {
        fun redirectToHome()
    }
}