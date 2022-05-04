package com.goldmedal.hrapp.common


interface ApiStageListener<T> {
    fun onStarted(callFrom:String)
    fun onSuccess(_object: List<T?>,callFrom:String)
    fun onError(message: String, callFrom: String, isNetworkError: Boolean)
    fun onValidationError(message: String, callFrom: String)
}