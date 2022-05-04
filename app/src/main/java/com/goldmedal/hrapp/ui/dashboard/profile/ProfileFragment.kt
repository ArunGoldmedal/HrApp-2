package com.goldmedal.hrapp.ui.dashboard.profile

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.ContentResolver
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.github.florent37.runtimepermission.kotlin.askPermission
import com.goldmedal.hrapp.R
import com.goldmedal.hrapp.common.ApiStageListener
import com.goldmedal.hrapp.common.ImageSelectionListener
import com.goldmedal.hrapp.databinding.ProfileFragmentBinding
import com.goldmedal.hrapp.util.convertBitmapToBase64
import com.goldmedal.hrapp.util.scaleDown
import com.goldmedal.hrapp.util.snackbar
import com.yalantis.ucrop.UCrop
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.profile_fragment.*
import java.io.File
import java.io.IOException

@AndroidEntryPoint
class ProfileFragment : Fragment(),  ImageSelectionListener, ApiStageListener<Any> {





    private val viewModel: ProfileViewModel by viewModels()

    private lateinit var profileFragmentBinding: ProfileFragmentBinding


    private val GALLERY = 1
    private val CAMERA = 2
    private var userId: Int? = 0

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        profileFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.profile_fragment, container, false)
        return profileFragmentBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        profileFragmentBinding.viewmodelProfile = viewModel
        viewModel.imageSelectionListener = this
        viewModel.apiListener = this

        viewModel.getLoggedInUser().observe(viewLifecycleOwner, { user ->
            if (user != null) {
                userId = user.UserID

                if (!user.MobileNumber.isNullOrEmpty()) {

                    viewModel.strMobileNo = user.MobileNumber
                } else if (!user.Officialemail.isNullOrEmpty()) {
                    viewModel.strMobileNo = user.Officialemail
                }



                tvUserName.text = user.EmployeeFullName
                tvUserCompanyName.text = user.MobileNumber
                tvUserPost.text = user.Department + getString(R.string.dash) + user.Designation


                tv_employee_code?.text = user.EmployeeCode
                tv_office_email?.text = user.Officialemail
                tv_joining_date?.text = user.joiningDate
                tv_DOB?.text = user.DateOfBirth
                tv_reporting_person?.text = user.ReportingPerson
                tv_home_address?.text = user.HomeAddress
                tv_office_address?.text = user.OfficeAddress
                tv_branch?.text = user.BranchName


                tv_locality?.text = "${getString(R.string.locality)} ${user?.Location ?: "-"}"
                tv_sub_locality?.text = "${getString(R.string.sub_locality)} ${user?.Sublocation ?: "-"}"


                val avatar = if (user.Genderid.equals("1")) R.drawable.male_avatar else R.drawable.female_avatar

                Glide.with(this)
                        .load(user.ProfilePicture)
                        .fitCenter()
                        .placeholder(avatar)
                        .into(imgprofile)
            }
        })

        imv_edit_profile?.setOnClickListener {
            EditProfileActivity.start(requireContext())
        }


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

                //the list of denied permissions
                e.denied.forEach {

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


    override fun removeProfilePhoto() {
        viewModel.strBase64Image = "-"


        viewModel.updateProfilePic(userId)
    }


    private fun queryName(resolver: ContentResolver, uri: Uri): String {
        val returnCursor: Cursor? =
                resolver.query(uri, null, null, null, null)

        val nameIndex = returnCursor?.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor?.moveToFirst()
        val name: String = nameIndex?.let { returnCursor.getString(it) } ?: ""
        returnCursor?.close()
        return name
    }

    private fun cropImage(sourceUri: Uri?) {
        val destinationUri: Uri = Uri.fromFile(File(context?.cacheDir, context?.contentResolver?.let { sourceUri?.let { it1 -> queryName(it, it1) } }))
        val options = UCrop.Options()
        options.setCompressionQuality(80)
        context?.let { ContextCompat.getColor(it, R.color.colorPrimary) }?.let { options.setToolbarColor(it) }
        context?.let { ContextCompat.getColor(it, R.color.colorPrimary) }?.let { options.setStatusBarColor(it) }
        context?.let { ContextCompat.getColor(it, R.color.colorPrimary) }?.let { options.setActiveWidgetColor(it) }

        options.withMaxResultSize(1000, 1000)
        activity?.let {
            sourceUri?.let { it1 ->
                UCrop.of(it1, destinationUri)
                        .withOptions(options)
                        .start(it, this)
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GALLERY) {
            if (data != null) {
                val contentURI = data.data
                try {
                    cropImage(contentURI)
                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show()
                }

            }

        } else if (requestCode == CAMERA) {
            if (data != null) {
                val thumbnail = data.extras!!.get("data") as Bitmap


                prepareImgUpload(thumbnail)

            }
        } else if (requestCode == UCrop.REQUEST_CROP) {
            if (resultCode == RESULT_OK) {
                handleUCropResult(data!!)
            } else {

            }
        } else if (requestCode == UCrop.RESULT_ERROR) {

            val cropError: Throwable? = UCrop.getError(data!!)
            Log.e("TAG", "Crop error: " + cropError);

        }

    }

    private fun handleUCropResult(data: Intent) {

        val resultUri: Uri? = UCrop.getOutput(data)


        val bitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver, resultUri)


        prepareImgUpload(bitmap)


    }

    private fun prepareImgUpload(bitmap: Bitmap) {
        val scaledBitmap = scaleDown(bitmap, 675f, true)


        viewModel.strBase64Image = convertBitmapToBase64(scaledBitmap)



        viewModel.updateProfilePic(userId)


    }

    override fun onStarted(callFrom: String) {

        progress_bar?.start()

    }

    override fun onSuccess(_object: List<Any?>, callFrom: String) {


        progress_bar?.stop()
        root_layout?.snackbar("Profile Picture Updated")
    }

    override fun onError(message: String, callFrom: String, isNetworkError: Boolean) {


        progress_bar?.stop()
        root_layout?.snackbar(message)

    }

    override fun onValidationError(message: String, callFrom: String) {

    }


}