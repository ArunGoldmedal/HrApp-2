package com.goldmedal.hrapp.ui.auth


interface AuthListener<T> {
    fun onStarted()
    fun onSuccess(_object: List<T?>)
    fun onFailure(message: String)
    fun setCaptcha(strCaptcha: String)
}
