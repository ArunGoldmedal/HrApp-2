package com.goldmedal.hrapp.util

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.goldmedal.hrapp.common.ImageSelectionListener
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*


fun showPictureDialog(mContext: Context?, listener: ImageSelectionListener) {
    val pictureDialog = mContext?.let { AlertDialog.Builder(it) }
    pictureDialog?.setTitle("Choose")
    val pictureDialogItems = arrayOf("Select photo from gallery", "Capture photo from camera","Remove profile photo")
    pictureDialog?.setItems(pictureDialogItems
    ) { dialog, which ->
        when (which) {
            0 -> listener.choosePhotoFromGallery()
            1 -> listener.takePhotoFromCamera()
            2 -> listener.removeProfilePhoto()
        }
    }
    pictureDialog?.show()
}


fun saveImage(context: Context?, imageDirectory: String, myBitmap: Bitmap): String {
    val bytes = ByteArrayOutputStream()
    myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
    val wallpaperDirectory = File(
            (Environment.getExternalStorageDirectory()).toString() + imageDirectory)
//    val wallpaperDirectory = File(
//            (context?.getExternalFilesDir(null)).toString() + imageDirectory)
    // have the object build the directory structure, if needed.
    Log.d("fee", wallpaperDirectory.toString())
    if (!wallpaperDirectory.exists()) {

        wallpaperDirectory.mkdirs()
    }

    try {
        Log.d("heel", wallpaperDirectory.toString())
        val f = File(wallpaperDirectory, ((Calendar.getInstance()
                .timeInMillis).toString() + ".jpg"))
        f.createNewFile()
        val fo = FileOutputStream(f)
        fo.write(bytes.toByteArray())
        MediaScannerConnection.scanFile(context,
                arrayOf(f.path),
                arrayOf("image/jpeg"), null)
        fo.close()
        Log.d("TAG", "File Saved::--->" + f.absolutePath)

        return f.absolutePath
    } catch (e1: IOException) {
        e1.printStackTrace()
    }

    return ""
}


fun convertBitmapToBase64(bitmap: Bitmap?): String {

    val baos = ByteArrayOutputStream()
    bitmap?.compress(Bitmap.CompressFormat.JPEG, 95, baos)
    val data = baos.toByteArray()

    return Base64.encodeToString(data, Base64.NO_WRAP)

}


fun scaleDown(realImage: Bitmap, maxImageSize: Float,
              filter: Boolean): Bitmap? {
    val ratio = Math.min(
            maxImageSize / realImage.width,
            maxImageSize / realImage.height)
    if (ratio >= 1.0) {
        return realImage
    }
    val width = Math.round(ratio * realImage.width)
    val height = Math.round(ratio * realImage.height)
    return Bitmap.createScaledBitmap(realImage, width, height, filter)
}


fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
    val bytes = ByteArrayOutputStream()
    inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
    val path = MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "", null)
    return Uri.parse(path)
}