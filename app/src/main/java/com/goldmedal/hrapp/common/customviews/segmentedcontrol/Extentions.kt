package com.goldmedal.hrapp.common.customviews.segmentedcontrol

import android.content.res.Resources

inline fun <R> R?.orElse(block: () -> R): R {
    return this ?: block()
}

fun Float.toPx() = (this * Resources.getSystem().displayMetrics.density)