package com.goldmedal.hrapp.ui.leftdrawer

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.github.florent37.runtimepermission.kotlin.askPermission
import com.goldmedal.hrapp.R
import com.goldmedal.hrapp.common.ApiStageListener
import com.goldmedal.hrapp.common.ImageSelectionListener
import com.goldmedal.hrapp.data.model.AddCompanyData
import com.goldmedal.hrapp.data.model.CommomImageUploadData
import com.goldmedal.hrapp.data.model.LeaveTypeData
import com.goldmedal.hrapp.data.network.GlobalConstant
import com.goldmedal.hrapp.databinding.ActivitySourcingDataBinding
import com.goldmedal.hrapp.ui.leave.ApplyLeaveFragment
import com.goldmedal.hrapp.ui.leave.LeaveTypeActivity
import com.goldmedal.hrapp.util.convertBitmapToBase64
import com.goldmedal.hrapp.util.saveImage
import com.goldmedal.hrapp.util.scaleDown
import com.goldmedal.hrapp.util.snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.apply_leave_detail.*
import java.io.IOException

@AndroidEntryPoint
class SourcingDataActivity : AppCompatActivity(), ApiStageListener<Any>, ImageSelectionListener {
    private val sourcingDataViewModel: SourcingDataViewModel by viewModels()
    private lateinit var mBinding: ActivitySourcingDataBinding
    private var mSelectedView = UploadView.VISITING_CARD_1
    private var mUserId = 0

    companion object {
        const val GALLERY = 1
        const val CAMERA = 2

        enum class UploadView {
            VISITING_CARD_1,
            VISITING_CARD_2,
            PRODUCT_1,
            PRODUCT_2,
            PRODUCT_3,
            PRODUCT_4,
            PRODUCT_5
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_sourcing_data)
        setContentView(mBinding.root)

        mBinding.sourcingDataViewModel = sourcingDataViewModel
        sourcingDataViewModel.apiListener = this
        sourcingDataViewModel.imageSelectionListener = this

        setClickListeners()

        sourcingDataViewModel.getLoggedInUser().observe(this, Observer { user ->
            if (user != null) {
                mUserId = user.UserID ?: 0
            }
        })
    }

    private fun setClickListeners() {
        mBinding.btnSubmit.setOnClickListener {
            val name = mBinding.editTextOrganisationName.text.toString()
            val address = mBinding.editTextOrgAddress.text.toString()

            sourcingDataViewModel.visitorCardList.clear()
            if (sourcingDataViewModel.visitorCardImage1.isNotEmpty()) {
                sourcingDataViewModel.visitorCardList.add(sourcingDataViewModel.visitorCardImage1)
            }
            if (sourcingDataViewModel.visitorCardImage2.isNotEmpty()) {
                sourcingDataViewModel.visitorCardList.add(sourcingDataViewModel.visitorCardImage2)
            }

            sourcingDataViewModel.productList.clear()
            if (sourcingDataViewModel.productImage1.isNotEmpty()) {
                sourcingDataViewModel.productList.add(sourcingDataViewModel.productImage1)
            }
            if (sourcingDataViewModel.productImage2.isNotEmpty()) {
                sourcingDataViewModel.productList.add(sourcingDataViewModel.productImage2)
            }
            if (sourcingDataViewModel.productImage3.isNotEmpty()) {
                sourcingDataViewModel.productList.add(sourcingDataViewModel.productImage3)
            }
            if (sourcingDataViewModel.productImage4.isNotEmpty()) {
                sourcingDataViewModel.productList.add(sourcingDataViewModel.productImage4)
            }
            if (sourcingDataViewModel.productImage5.isNotEmpty()) {
                sourcingDataViewModel.productList.add(sourcingDataViewModel.productImage5)
            }

            val visitorCardImages = sourcingDataViewModel.visitorCardList.joinToString(",")
            val productImages = sourcingDataViewModel.productList.joinToString(",")

            sourcingDataViewModel.addCompanyDetailsApi(mUserId, name, address, visitorCardImages, productImages)
        }

        mBinding.ivUploadVisitingCard1.setOnClickListener {
            mSelectedView = UploadView.VISITING_CARD_1
            sourcingDataViewModel.onImageUploadButtonClick(it)
        }

        mBinding.ivUploadVisitingCard2.setOnClickListener {
            mSelectedView = UploadView.VISITING_CARD_2
            sourcingDataViewModel.onImageUploadButtonClick(it)
        }

        mBinding.ivUploadProduct1.setOnClickListener {
            mSelectedView = UploadView.PRODUCT_1
            sourcingDataViewModel.onImageUploadButtonClick(it)
        }

        mBinding.ivUploadProduct2.setOnClickListener {
            mSelectedView = UploadView.PRODUCT_2
            sourcingDataViewModel.onImageUploadButtonClick(it)
        }

        mBinding.ivUploadProduct3.setOnClickListener {
            mSelectedView = UploadView.PRODUCT_3
            sourcingDataViewModel.onImageUploadButtonClick(it)
        }

        mBinding.ivUploadProduct4.setOnClickListener {
            mSelectedView = UploadView.PRODUCT_4
            sourcingDataViewModel.onImageUploadButtonClick(it)
        }

        mBinding.ivUploadProduct5.setOnClickListener {
            mSelectedView = UploadView.PRODUCT_5
            sourcingDataViewModel.onImageUploadButtonClick(it)
        }
    }

    override fun choosePhotoFromGallery() {
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
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
                e.denied.forEach {}

                    AlertDialog.Builder(this)
                        .setMessage(getString(R.string.str_camera_permission))
                        .setPositiveButton("CONTINUE") { dialog, which ->
                            e.askAgain()
                        } //ask again
                        .setNegativeButton("NOT NOW") { dialog, which ->
                            dialog.dismiss()
                        }
                        .show()

            }

            if (e.hasForeverDenied()) {
                // appendText(resultView, "ForeverDenied :")
                //the list of forever denied permissions, user has check 'never ask again'
                e.foreverDenied.forEach {
                    //    appendText(resultView, it)
                }
                // you need to open setting manually if you really need it
                e.goToSettings()
            }
        }
    }

    override fun onStarted(callFrom: String) {
        mBinding.progressBar.start()
    }

    override fun onError(message: String, callFrom: String, isNetworkError: Boolean) {
        mBinding.progressBar.stop()
        mBinding.rootLayout.snackbar(message)
    }

    override fun onValidationError(message: String, callFrom: String) {
        mBinding.rootLayout.snackbar(message)
    }

    override fun onSuccess(_object: List<Any?>, callFrom: String) {
        mBinding.progressBar.stop()

        if (callFrom == GlobalConstant.COMMON_IMAGE_UPLOAD_API) {
            val imageUploadData = _object as List<CommomImageUploadData>
            val uploadedFileName = imageUploadData[0].UploadedImagePathName
            mBinding.rootLayout.snackbar("Image Uploaded Successfully")

            when (mSelectedView) {
                UploadView.VISITING_CARD_1 -> {
                    sourcingDataViewModel.visitorCardImage1 = uploadedFileName
                }
                UploadView.VISITING_CARD_2 -> {
                    sourcingDataViewModel.visitorCardImage2 = uploadedFileName
                }
                UploadView.PRODUCT_1 -> {
                    sourcingDataViewModel.productImage1 = uploadedFileName
                }
                UploadView.PRODUCT_2 -> {
                    sourcingDataViewModel.productImage2 = uploadedFileName
                }
                UploadView.PRODUCT_3 -> {
                    sourcingDataViewModel.productImage3 = uploadedFileName
                }
                UploadView.PRODUCT_4 -> {
                    sourcingDataViewModel.productImage4 = uploadedFileName
                }
                UploadView.PRODUCT_5 -> {
                    sourcingDataViewModel.productImage5 = uploadedFileName
                }
            }
        } else if (callFrom == GlobalConstant.ADD_COMPANY_DETAILS_API) {
            val addCompanyData = _object as List<AddCompanyData>
            val message = addCompanyData[0].StatusMessage
            mBinding.rootLayout.snackbar(message)
            clearScreen()
        }
    }

    private fun clearScreen() {
        mBinding.apply {
            editTextOrganisationName.text?.clear()
            editTextOrgAddress.text?.clear()

            sourcingDataViewModel?.visitorCardImage1 = ""
            sourcingDataViewModel?.visitorCardImage2 = ""

            sourcingDataViewModel?.productImage1 = ""
            sourcingDataViewModel?.productImage2 = ""
            sourcingDataViewModel?.productImage3 = ""
            sourcingDataViewModel?.productImage4 = ""
            sourcingDataViewModel?.productImage5 = ""

            ivUploadVisitingCard1.setImageDrawable(resources.getDrawable(R.drawable.image_upload))
            ivUploadVisitingCard2.setImageDrawable(resources.getDrawable(R.drawable.image_upload))
            ivUploadProduct1.setImageDrawable(resources.getDrawable(R.drawable.image_upload))
            ivUploadProduct2.setImageDrawable(resources.getDrawable(R.drawable.image_upload))
            ivUploadProduct3.setImageDrawable(resources.getDrawable(R.drawable.image_upload))
            ivUploadProduct4.setImageDrawable(resources.getDrawable(R.drawable.image_upload))
            ivUploadProduct5.setImageDrawable(resources.getDrawable(R.drawable.image_upload))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if (requestCode == GALLERY) {
            if (data != null) {
                val contentURI = data.data
                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, contentURI)
                    val scaledBitmap = scaleDown(bitmap, 675f, true)

                    //saveImage(this, GlobalConstant.IMAGE_DIRECTORY, bitmap)
                    //Toast.makeText(this, "Image Saved!", Toast.LENGTH_SHORT).show()
                    val displayedBitmap = scaleDown(bitmap, 150f, true)
                    displayedBitmap?.let { setImageUi(it) }

                    val base64 = convertBitmapToBase64(scaledBitmap)
                    if (mSelectedView == UploadView.VISITING_CARD_1 || mSelectedView == UploadView.VISITING_CARD_2) {
                        sourcingDataViewModel.commonUploadImage("VisitingCard", base64)
                    } else {
                        sourcingDataViewModel.commonUploadImage("ProductImage", base64)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(this, "Failed!", Toast.LENGTH_SHORT).show()
                }

            }

        } else if (requestCode == CAMERA) {
            if (resultCode == RESULT_OK) {


                val thumbnail = data!!.extras!!.get("data") as Bitmap
                setImageUi(thumbnail)
                //saveImage(this, GlobalConstant.IMAGE_DIRECTORY, thumbnail)
                //Toast.makeText(this, "Image Saved!", Toast.LENGTH_SHORT).show()
                val scaledBitmap = scaleDown(thumbnail, 675f, true)

                val strBase64Image = convertBitmapToBase64(scaledBitmap)
                if (mSelectedView == UploadView.VISITING_CARD_1 || mSelectedView == UploadView.VISITING_CARD_2) {
                    sourcingDataViewModel.commonUploadImage("VisitingCard", strBase64Image)
                } else {
                    sourcingDataViewModel.commonUploadImage("ProductImage", strBase64Image)
                }
            }
        }


    }

    private fun setImageUi(bitmap: Bitmap) {
        when (mSelectedView) {
            UploadView.VISITING_CARD_1 -> {
                mBinding.ivUploadVisitingCard1.setImageBitmap(bitmap)
            }
            UploadView.VISITING_CARD_2 -> {
                mBinding.ivUploadVisitingCard2.setImageBitmap(bitmap)
            }
            UploadView.PRODUCT_1 -> {
                mBinding.ivUploadProduct1.setImageBitmap(bitmap)
            }
            UploadView.PRODUCT_2 -> {
                mBinding.ivUploadProduct2.setImageBitmap(bitmap)
            }
            UploadView.PRODUCT_3 -> {
                mBinding.ivUploadProduct3.setImageBitmap(bitmap)
            }
            UploadView.PRODUCT_4 -> {
                mBinding.ivUploadProduct4.setImageBitmap(bitmap)
            }
            UploadView.PRODUCT_5 -> {
                mBinding.ivUploadProduct5.setImageBitmap(bitmap)
            }
        }
    }
}