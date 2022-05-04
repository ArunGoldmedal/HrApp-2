package com.goldmedal.hrapp.common

interface ImageSelectionListener {


    fun choosePhotoFromGallery()
    fun takePhotoFromCamera()
    fun removeProfilePhoto(){}
}