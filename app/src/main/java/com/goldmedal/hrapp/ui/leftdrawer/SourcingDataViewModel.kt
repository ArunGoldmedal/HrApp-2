package com.goldmedal.hrapp.ui.leftdrawer

import android.view.View
import androidx.lifecycle.ViewModel
import com.goldmedal.hrapp.common.ApiStageListener
import com.goldmedal.hrapp.common.ImageSelectionListener
import com.goldmedal.hrapp.data.network.GlobalConstant
import com.goldmedal.hrapp.data.repositories.HomeRepository
import com.goldmedal.hrapp.util.ApiException
import com.goldmedal.hrapp.util.Coroutines
import com.goldmedal.hrapp.util.NoInternetException
import com.goldmedal.hrapp.util.showPictureDialog
import dagger.hilt.android.lifecycle.HiltViewModel
import java.net.SocketTimeoutException
import javax.inject.Inject

@HiltViewModel
class SourcingDataViewModel @Inject constructor(
    private val repository: HomeRepository
) : ViewModel() {
    var apiListener: ApiStageListener<Any>? = null
    var imageSelectionListener: ImageSelectionListener? = null
    var visitorCardImage1 = ""
    var visitorCardImage2 = ""
    var productImage1 = ""
    var productImage2 = ""
    var productImage3 = ""
    var productImage4 = ""
    var productImage5 = ""
    var visitorCardList = arrayListOf<String>()
    var productList = arrayListOf<String>()

    fun getLoggedInUser() = repository.getLoggedInUser()

    fun onImageUploadButtonClick(view: View) {
        imageSelectionListener?.let { showPictureDialog(view.context, it) }
    }

    fun commonUploadImage(moduleName: String, imgBlob: String) {
        apiListener?.onStarted(GlobalConstant.COMMON_IMAGE_UPLOAD_API)

        Coroutines.main {
            try {
                val outputResponse = repository.commonImageUpload(moduleName, imgBlob)
                if (outputResponse.StatusCode == 200) {
                    if (outputResponse.Data?.isNotEmpty()!!) {
                        outputResponse.Data.let {
                            apiListener?.onSuccess(it, GlobalConstant.COMMON_IMAGE_UPLOAD_API)
                            return@main
                        }
                    }
                } else {
                    val errorResponse = outputResponse.Errors
                    if (errorResponse?.isNotEmpty()!!) {
                        errorResponse[0].ErrorMsg?.let {
                            apiListener?.onError(
                                it,
                                GlobalConstant.COMMON_IMAGE_UPLOAD_API,
                                false
                            )
                        }
                    }
                }

            } catch (e: ApiException) {
                apiListener?.onError(e.message!!, GlobalConstant.COMMON_IMAGE_UPLOAD_API, true)
            } catch (e: NoInternetException) {
                apiListener?.onError(e.message!!, GlobalConstant.COMMON_IMAGE_UPLOAD_API, true)
                print("Internet not available")
            } catch (e: SocketTimeoutException) {
                apiListener?.onError(e.message!!, GlobalConstant.COMMON_IMAGE_UPLOAD_API, true)
            }
        }
    }

    fun addCompanyDetailsApi(
        userId: Int,
        companyName: String,
        companyAddress: String,
        visitingCardImages: String,
        productImages: String
    ) {
        if (companyName.isEmpty()){
            apiListener?.onValidationError("Please enter company name.", GlobalConstant.ADD_COMPANY_DETAILS_API)
            return
        } else if (companyAddress.isEmpty()){
            apiListener?.onValidationError("Please enter company address.", GlobalConstant.ADD_COMPANY_DETAILS_API)
            return
        } else if (visitingCardImages.isEmpty()){
            apiListener?.onValidationError("Please upload atleast one visiting card image.", GlobalConstant.ADD_COMPANY_DETAILS_API)
            return
        } else if (productImages.isEmpty()){
            apiListener?.onValidationError("Please upload atleast product image.", GlobalConstant.ADD_COMPANY_DETAILS_API)
            return
        }

        apiListener?.onStarted(GlobalConstant.ADD_COMPANY_DETAILS_API)

        Coroutines.main {
            try {
                val outputResponse = repository.addCompanyDetails(userId, companyName, companyAddress, visitingCardImages, productImages)
                if (outputResponse.StatusCode == 200) {
                    if (outputResponse.Data?.isNotEmpty()!!) {
                        outputResponse.Data.let {
                            apiListener?.onSuccess(it, GlobalConstant.ADD_COMPANY_DETAILS_API)
                            return@main
                        }
                    }
                } else {
                    val errorResponse = outputResponse.Errors
                    if (errorResponse?.isNotEmpty()!!) {
                        errorResponse[0].ErrorMsg?.let {
                            apiListener?.onError(
                                it,
                                GlobalConstant.ADD_COMPANY_DETAILS_API,
                                false
                            )
                        }
                    }
                }

            } catch (e: ApiException) {
                apiListener?.onError(e.message!!, GlobalConstant.ADD_COMPANY_DETAILS_API, true)
            } catch (e: NoInternetException) {
                apiListener?.onError(e.message!!, GlobalConstant.ADD_COMPANY_DETAILS_API, true)
                print("Internet not available")
            } catch (e: SocketTimeoutException) {
                apiListener?.onError(e.message!!, GlobalConstant.ADD_COMPANY_DETAILS_API, true)
            }
        }
    }

    fun getCompanyDetailsApi(userId: Int) {

        apiListener?.onStarted(GlobalConstant.GET_COMPANY_DETAILS_API)

        Coroutines.main {
            try {
                val outputResponse = repository.getCompanyDetails(userId)
                if (outputResponse.StatusCode == 200) {
                    if (outputResponse.Data?.isNotEmpty()!!) {
                        outputResponse.Data.let {
                            apiListener?.onSuccess(it, GlobalConstant.GET_COMPANY_DETAILS_API)
                            return@main
                        }
                    }
                } else {
                    val errorResponse = outputResponse.Errors
                    if (errorResponse?.isNotEmpty()!!) {
                        errorResponse[0].ErrorMsg?.let {
                            apiListener?.onError(
                                it,
                                GlobalConstant.GET_COMPANY_DETAILS_API,
                                false
                            )
                        }
                    }
                }

            } catch (e: ApiException) {
                apiListener?.onError(e.message!!, GlobalConstant.GET_COMPANY_DETAILS_API, true)
            } catch (e: NoInternetException) {
                apiListener?.onError(e.message!!, GlobalConstant.GET_COMPANY_DETAILS_API, true)
                print("Internet not available")
            } catch (e: SocketTimeoutException) {
                apiListener?.onError(e.message!!, GlobalConstant.GET_COMPANY_DETAILS_API, true)
            }
        }
    }

    fun deleteCompanyDetailsApi(companyId: Int, userId: Int) {

        apiListener?.onStarted(GlobalConstant.DELETE_COMPANY_DETAILS_API)

        Coroutines.main {
            try {
                val outputResponse = repository.deleteCompanyDetails(companyId, userId)
                if (outputResponse.StatusCode == 200) {
                    if (outputResponse.Data?.isNotEmpty()!!) {
                        outputResponse.Data.let {
                            apiListener?.onSuccess(it, GlobalConstant.DELETE_COMPANY_DETAILS_API)
                            return@main
                        }
                    }
                } else {
                    val errorResponse = outputResponse.Errors
                    if (errorResponse?.isNotEmpty()!!) {
                        errorResponse[0].ErrorMsg?.let {
                            apiListener?.onError(
                                it,
                                GlobalConstant.DELETE_COMPANY_DETAILS_API,
                                false
                            )
                        }
                    }
                }

            } catch (e: ApiException) {
                apiListener?.onError(e.message!!, GlobalConstant.DELETE_COMPANY_DETAILS_API, true)
            } catch (e: NoInternetException) {
                apiListener?.onError(e.message!!, GlobalConstant.DELETE_COMPANY_DETAILS_API, true)
                print("Internet not available")
            } catch (e: SocketTimeoutException) {
                apiListener?.onError(e.message!!, GlobalConstant.DELETE_COMPANY_DETAILS_API, true)
            }
        }
    }
}