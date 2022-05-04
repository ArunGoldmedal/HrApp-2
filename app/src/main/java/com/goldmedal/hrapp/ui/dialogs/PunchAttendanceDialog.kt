package com.goldmedal.hrapp.ui.dialogs

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.github.florent37.runtimepermission.kotlin.askPermission
import com.goldmedal.hrapp.R
import com.goldmedal.hrapp.common.ApiStageListener
import com.goldmedal.hrapp.common.ImageSelectionListener
import com.goldmedal.hrapp.databinding.DialogPunchAttendanceBinding
import com.goldmedal.hrapp.util.*
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.dialog_punch_attendance.*
import java.io.IOException

@AndroidEntryPoint
class PunchAttendanceDialog : DialogFragment(), ApiStageListener<Any>, ImageSelectionListener {




    private val viewModel: PunchAttendanceViewModel by viewModels()


    private lateinit var attBinding: DialogPunchAttendanceBinding


    var callBack: OnShowSuccessMsg? = null
    private val GALLERY = 1
    private val CAMERA = 2


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

        attBinding = DataBindingUtil.inflate(inflater, R.layout.dialog_punch_attendance, container, false)
        return attBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        attBinding.viewmodel = viewModel
        viewModel.apiListener = this
        viewModel.imageSelectionListener = this

        viewModel.strDeviceId = context?.let { getDeviceId(context = it) }




        viewModel.getLoggedInUser().observe(viewLifecycleOwner, Observer { user ->
            if (user != null) {
                viewModel.userId = user.UserID

            }
        })

        viewModel.strPunchType = arguments?.getString("calledFrom", "")

        if (viewModel.strPunchType.equals("OUT", ignoreCase = true)) {
            txt_punch_header.text = getString(R.string.str_punch_out)
        }


        val lastLocation = LatLng(requireArguments().getDouble("latitude"), requireArguments().getDouble("longitude"))
        viewModel.lastLocation = lastLocation


        val isWithinOfficeRadius = arguments?.getBoolean("isWithinOfficeRadius") ?: false

        if (isWithinOfficeRadius) {
            viewModel.strLocationAddress = arguments?.getString("officeAddress", getAddressFromLatLong(view.context, lastLocation.latitude, lastLocation.longitude))
        } else {
            viewModel.strLocationAddress = getAddressFromLatLong(view.context, lastLocation.latitude, lastLocation.longitude)
        }


        txt_punch_time.text = getCurrentDateTime().toString("hh:mm a")

    }

    override fun onStarted(callFrom: String) {
        progress_bar?.start()
    }

    override fun onSuccess(_object: List<Any?>, callFrom: String) {
        progress_bar?.stop()

        callBack?.showSuccessDialog(_object, viewModel.strPunchType)
        dismiss()


    }


    override fun onError(message: String, callFrom: String, isNetworkError: Boolean) {
        progress_bar?.stop()
        context?.toast(message)
    }


    companion object {
        fun newInstance() = PunchAttendanceDialog()
    }

    interface OnShowSuccessMsg {
        fun showSuccessDialog(_object: List<Any?>, punchType: String?)
    }

    override fun choosePhotoFromGallery() {

        val galleryIntent = Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        startActivityForResult(galleryIntent, GALLERY)

    }

    override fun takePhotoFromCamera() {


        askPermission(Manifest.permission.CAMERA) {
            //all permissions already granted or just granted

            // your action
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, CAMERA)

        }.onDeclined { e ->
            if (e.hasDenied()) {
                //  appendText(resultView, "Denied :")
                //the list of denied permissions
                e.denied.forEach {
                    //     appendText(resultView, it)
                }

                context?.let {
                    AlertDialog.Builder(it)
                            .setMessage(getString(R.string.str_camera_permission))
                            .setPositiveButton("CONTINUE") { dialog, which ->
                                e.askAgain()
                            } //ask again
                            .setNegativeButton("NOT NOW") { dialog, which ->
                                dialog.dismiss()
                            }
                            .show()
                }
            }

            if (e.hasForeverDenied()) {
                //the list of forever denied permissions, user has check 'never ask again'
                e.foreverDenied.forEach {
                    //    appendText(resultView, it)
                }
                // you need to open setting manually if you really need it
                e.goToSettings()
            }
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if (requestCode == GALLERY) {
            if (data != null) {
                val contentURI = data.data
                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver, contentURI)
                    val scaledBitmap = scaleDown(bitmap, 675f, true)


                    viewModel.strBase64Image = convertBitmapToBase64(scaledBitmap)


                    Toast.makeText(context, "Image Saved!", Toast.LENGTH_SHORT).show()
                    val displayedBitmap = scaleDown(bitmap, 150f, true)
                    imgUpload!!.setImageBitmap(displayedBitmap)


                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show()
                }

            }

        } else if (requestCode == CAMERA) {
            if (resultCode == Activity.RESULT_OK) {
                val thumbnail = data!!.extras!!.get("data") as Bitmap
                imgUpload!!.setImageBitmap(thumbnail)
                Toast.makeText(context, "Image Saved!", Toast.LENGTH_SHORT).show()
                val scaledBitmap = scaleDown(thumbnail, 675f, true)

                viewModel.strBase64Image = convertBitmapToBase64(scaledBitmap)
            }
        }
    }

    override fun onValidationError(message: String, callFrom: String) {
        context?.toast(message)
    }


}