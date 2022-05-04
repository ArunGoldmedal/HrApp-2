package com.goldmedal.hrapp.common.customviews.crescento

import android.graphics.LinearGradient
import android.graphics.Shader


internal object GradientProvider {
    fun getShader(gradientStartColor: Int, gradientEndColor: Int, gradientDirection: Int, width: Float, height: Float): Shader {
        return when (gradientDirection) {
            CrescentoImageView.Gradient.TOP_TO_BOTTOM -> LinearGradient(0f, 0f, 0f, height, gradientStartColor, gradientEndColor, Shader.TileMode.CLAMP)
            CrescentoImageView.Gradient.BOTTOM_TO_TOP -> LinearGradient(0f, height, 0f, 0f, gradientStartColor, gradientEndColor, Shader.TileMode.CLAMP)
            CrescentoImageView.Gradient.LEFT_TO_RIGHT -> LinearGradient(0f, 0f, width, 0f, gradientStartColor, gradientEndColor, Shader.TileMode.CLAMP)
            CrescentoImageView.Gradient.RIGHT_TO_LEFT -> LinearGradient(width, 0f, 0f, 0f, gradientStartColor, gradientEndColor, Shader.TileMode.CLAMP)
            else -> LinearGradient(0f, 0f, height, 0f, gradientStartColor, gradientEndColor, Shader.TileMode.CLAMP)
        }
    }
}