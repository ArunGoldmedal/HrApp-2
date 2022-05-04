package com.goldmedal.hrapp.ui.dialogs

import android.app.Dialog
import android.app.TimePickerDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.format.DateFormat
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TimePicker
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.goldmedal.hrapp.common.ApiStageListener
import com.goldmedal.hrapp.data.model.DefaultMessageData
import com.goldmedal.hrapp.databinding.RegularizeAttendanceDialogBinding
import com.goldmedal.hrapp.ui.dashboard.attendance.AttendanceViewModel
import com.goldmedal.hrapp.util.formatDateString
import com.goldmedal.hrapp.util.toast
import dagger.hilt.android.AndroidEntryPoint
import org.angmarch.views.OnSpinnerItemSelectedListener
import java.text.SimpleDateFormat
import java.util.*


@AndroidEntryPoint
class RegularizeAttendanceDialog : DialogFragment(), ApiStageListener<Any>, TimePickerDialog.OnTimeSetListener {


    private val dataset: List<String> = LinkedList(listOf("Select Reason", "Biometric not working", "Forgot to punch", "Work from home", "Other"))


    private var _binding: RegularizeAttendanceDialogBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AttendanceViewModel by viewModels()

    private var punchInHour: Int = 0
    private var punchInMin: Int = 0
    private var punchOutHour: Int = 0
    private var punchOutMin: Int = 0
    private var oldPunchInHour: Int = 0
    private var oldPunchInMinute: Int = 0


    private var oldPunchOutHour: Int = 0
    private var oldPunchOutMinute: Int = 0


    private var regularizationReasonId = "-1"
    private var userId: Int? = null
    private var punchDate: String? = null


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

    override fun onResume() {
        super.onResume()

        val metrics = requireContext().resources.displayMetrics
        val screenWidth = (metrics.widthPixels * 0.85).toInt()
        dialog?.window?.setLayout(screenWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog?.window?.setGravity(Gravity.CENTER)
        dialog?.setCanceledOnTouchOutside(true)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = RegularizeAttendanceDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewModel.apiListener = this
        viewModel.getLoggedInUser().observe(viewLifecycleOwner, { user ->
            if (user != null) {
                userId = user.UserID
            }
        })


        punchDate = arguments?.getString("punchDate")
        val oldPunchInTime = arguments?.getString("oldPunchInTime")
        val oldPunchOutTime = arguments?.getString("oldPunchOutTime")
        binding.txtPunchDate.text = punchDate
        punchDate = formatDateString(punchDate ?: "", "dd-MM-yyyy", "MM/dd/yyyy")


        val formatInTimeTo24Hr = formatDateString(oldPunchInTime ?: "", "hh:mma", "HH:mm")
        val formatOutTimeTo24Hr = formatDateString(oldPunchOutTime ?: "", "hh:mma", "HH:mm")


        val inTime = formatInTimeTo24Hr.split(":")
        val outTime = formatOutTimeTo24Hr.split(":")


        if (inTime[0].isNotEmpty()) {
            oldPunchInHour = inTime[0].toInt()
            oldPunchInMinute = inTime[1].toInt()


            punchInHour = oldPunchInHour
            punchInMin = oldPunchInMinute
        }

        if (outTime[0].isNotEmpty()) {
            oldPunchOutHour = outTime[0].toInt()
            oldPunchOutMinute = outTime[1].toInt()

            punchOutHour = oldPunchOutHour
            punchOutMin = oldPunchOutMinute
        }


        binding.txtPunchInTime.text = oldPunchInTime
        binding.txtPunchOutTime.text = oldPunchOutTime
        initSpinner()
        clickListeners()

    }

    private fun clickListeners() {


        binding.txtPunchInTime.setOnClickListener {

            showPunchInTimePicker()

        }

        binding.txtPunchOutTime.setOnClickListener {
            showPunchOutTimePicker()
        }

        binding.btnOk.setOnClickListener {


            if (punchInHour > punchOutHour) {
                requireContext().toast("Start time should be less than end time")
                return@setOnClickListener

            } else if (punchInHour == punchOutHour) {
                if (punchInMin > punchOutMin) {
                    requireContext().toast("Start time should be less than end time")
                    return@setOnClickListener
                }
            }
            viewModel.insertAttendanceRegularization(userId = userId, punchDate = punchDate, oldPunchIn = String.format("%02d:%02d", oldPunchInHour, oldPunchInMinute),
                    oldPunchOut = String.format("%02d:%02d", oldPunchOutHour, oldPunchOutMinute), newPunchIn = String.format("%02d:%02d", punchInHour, punchInMin),
                    newPunchOut = String.format("%02d:%02d", punchOutHour, punchOutMin), reason = regularizationReasonId, remark = binding.edtRemarks.text.toString())
        }


    }

    private fun initSpinner() {
        binding.spinnerReason.attachDataSource(dataset)

        binding.spinnerReason.onSpinnerItemSelectedListener =
                OnSpinnerItemSelectedListener { parent, view, position, id ->
                    val item: String = parent.getItemAtPosition(position) as String

                    when (item) {
                        "Select Reason" -> {
                            regularizationReasonId = "-1"
                        }
                        "Biometric not working" -> {
                            regularizationReasonId = "1"
                        }
                        "Forgot to punch" -> {
                            regularizationReasonId = "2"
                        }
                        "Work from home" -> {
                            regularizationReasonId = "3"
                        }
                        "Other" -> {
                            regularizationReasonId = "4"
                        }
                        else -> {
                            regularizationReasonId = "-1"
                        }
                    }
                }
    }

    private fun showPunchInTimePicker() {

        val previewPunchInHour: Int
        val previewPunchInMinute: Int
        if (oldPunchInHour == 0) {
            val c = Calendar.getInstance()
            previewPunchInHour = c[Calendar.HOUR_OF_DAY]
            previewPunchInMinute = c[Calendar.MINUTE]
        } else {
            previewPunchInHour = oldPunchInHour
            previewPunchInMinute = oldPunchInMinute
        }

        val timePickerDialog = TimePickerDialog(requireContext(), { _, hourOfDay, minute ->
            punchInHour = hourOfDay
            punchInMin = minute
            val calendar = Calendar.getInstance()
            calendar[Calendar.HOUR_OF_DAY] = hourOfDay
            calendar[Calendar.MINUTE] = minute
            binding.txtPunchInTime.text = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(calendar.time)
        }, previewPunchInHour, previewPunchInMinute, DateFormat.is24HourFormat(requireContext()))
        timePickerDialog.show()
    }


    private fun showPunchOutTimePicker() {

        val previewPunchOutHour: Int
        val previewPunchOutMinute: Int
        if (oldPunchInHour == 0) {
            val c = Calendar.getInstance()
            previewPunchOutHour = c[Calendar.HOUR_OF_DAY]
            previewPunchOutMinute = c[Calendar.MINUTE]
        } else {
            previewPunchOutHour = oldPunchInHour
            previewPunchOutMinute = oldPunchInMinute
        }


        val timePickerDialog = TimePickerDialog(requireContext(), { _, hourOfDay, minute ->
            punchOutHour = hourOfDay
            punchOutMin = minute
            val calendar = Calendar.getInstance()
            calendar[Calendar.HOUR_OF_DAY] = hourOfDay
            calendar[Calendar.MINUTE] = minute
            binding.txtPunchOutTime.text = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(calendar.time)
        }, previewPunchOutHour, previewPunchOutMinute, DateFormat.is24HourFormat(requireContext()))

        timePickerDialog.show()
    }


    override fun onTimeSet(picker: TimePicker?, hourOfDay: Int, minute: Int) {
        punchInHour = hourOfDay
        punchInMin = minute


    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStarted(callFrom: String) {
        binding.progressBar.start()
    }

    override fun onSuccess(_object: List<Any?>, callFrom: String) {
        binding.progressBar.stop()

        val data = _object as List<DefaultMessageData?>
        requireContext().toast((data[0]?.StatusMessage
                ?: "Regularization request sent successfully!"))
        dismiss()


    }

    override fun onError(message: String, callFrom: String, isNetworkError: Boolean) {
        binding.progressBar.stop()
        requireContext().toast(message)
    }

    override fun onValidationError(message: String, callFrom: String) {
        requireContext().toast(message)
    }

    companion object {
        fun newInstance() = RegularizeAttendanceDialog()
    }


}