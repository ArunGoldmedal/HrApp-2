package com.goldmedal.hrapp

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

open class BaseActivity : AppCompatActivity() {

    private var statusBarOverlay: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //        WindowCompat.setDecorFitsSystemWindows(window, true)
        enableEdgeToEdge()
        setContentView(R.layout.activity_base)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val api = Build.VERSION.SDK_INT
        //Log.d("API_CHECK", "Running on API $api")

        if (api >= 35) {
            // 1. Make content draw behind system bars
            WindowCompat.setDecorFitsSystemWindows(window, false)
            window.statusBarColor = Color.TRANSPARENT

            // 2. Create a view to act as status bar background
            statusBarOverlay = View(this).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    0 // We’ll set height later
                )
                setBackgroundColor(ContextCompat.getColor(this@BaseActivity, R.color.colorPrimary))
            }

            // 3. Add the view to the decorView
            (window.decorView as? ViewGroup)?.addView(statusBarOverlay)

            // 4. Dynamically apply real status bar height
            ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { _, insets ->
                val topInset = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
                statusBarOverlay?.layoutParams?.height = topInset
                statusBarOverlay?.requestLayout()
                insets
            }
        } else {
            // Fallback for older versions (API 21–32)
            window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        }


    }

}
