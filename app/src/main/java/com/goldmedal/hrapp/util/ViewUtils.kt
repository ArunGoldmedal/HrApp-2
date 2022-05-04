
package com.goldmedal.hrapp.util

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.goldmedal.hrapp.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.internal.managers.ViewComponentManager


fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun Context.shortToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun ProgressBar.show() {
    visibility = View.VISIBLE
}

fun ProgressBar.hide() {
    visibility = View.GONE
}

fun View.snackbar(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_LONG).also { snackbar ->
        snackbar.setAction("Ok") {
            snackbar.dismiss()
        }
    }.show()
}

fun Context.alertDialog(message: String) {

    MaterialAlertDialogBuilder(this)
            .setMessage(message)
            .setPositiveButton(resources.getString(R.string.str_ok)) { dialog, which ->
            }
            .show()

}


//Close/Hide virtual keyboard


fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun ViewGroup.inflate(layoutRes: Int): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, false)
}



fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}


fun generateRandomCaptcha(): String {
    val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    var passWord = ""
    for (i in 0..5) {
        passWord += chars[Math.floor(Math.random() * chars.length).toInt()]
    }
    return passWord
}


 fun activityContext(mContext: Context?): Context? {
     return if (mContext is ViewComponentManager.FragmentContextWrapper) {
        mContext.baseContext
    } else mContext
}

