package com.teamapt.monnify.sdk.util

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.graphics.Insets
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Build.VERSION_CODES.LOLLIPOP
import android.util.DisplayMetrics
import android.view.WindowInsets
import android.view.WindowMetrics
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
//import com.teamapt.monnify.sdk.BuildConfig
import java.util.regex.Pattern

class SdkUtils(private val context: Context) {
    private var isDebugMode = false;//BuildConfig.DEBUG

    fun showToastMessage(message: String) {
        if (isDebugMode) {
            val toast = Toast.makeText(context, message, Toast.LENGTH_LONG)
            toast.show()
        }
    }

    companion object {

        fun isANumber(c: Char): Boolean {
            return c in '0'..'9'
        }

        fun setVectorForPreLollipop(
            textView: AppCompatTextView,
            resourceId: Int,
            activity: Context,
            position: Int
        ) {
            val icon: Drawable? = if (Build.VERSION.SDK_INT < LOLLIPOP) {
                VectorDrawableCompat.create(
                    activity.resources, resourceId,
                    activity.theme
                )
            } else {
                ResourcesCompat.getDrawable(activity.resources, resourceId, activity.theme)
            }
            when (position) {
                Constants.DRAWABLE_LEFT ->
                    textView.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null)

                Constants.DRAWABLE_RIGHT ->
                    textView.setCompoundDrawablesWithIntrinsicBounds(null, null, icon, null)

                Constants.DRAWABLE_TOP ->
                    textView.setCompoundDrawablesWithIntrinsicBounds(null, icon, null, null)

                Constants.DRAWABLE_BOTTOM ->
                    textView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, icon)
            }
        }

        private fun isNetworkAvailable(context: Context): Boolean {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val nw = connectivityManager.activeNetwork ?: return false
                val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
                return when {
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    //for other device how are able to connect with Ethernet
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                    //for check internet over Bluetooth
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
                    else -> false
                }
            } else {
                val nwInfo = connectivityManager.activeNetworkInfo ?: return false
                return nwInfo.isConnected
            }
        }

        fun getScreenWidth(activity: Activity): Int {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val windowMetrics = activity.windowManager.currentWindowMetrics
                val bounds = windowMetrics.bounds
                val insets = windowMetrics.windowInsets.getInsetsIgnoringVisibility(
                    WindowInsets.Type.systemBars()
                )
                if (activity.resources.configuration.orientation
                    == Configuration.ORIENTATION_LANDSCAPE
                    && activity.resources.configuration.smallestScreenWidthDp < 600
                ) { // landscape and phone
                    val navigationBarSize = insets.right + insets.left
                    bounds.width() - navigationBarSize
                } else { // portrait or tablet
                    bounds.width()
                }
            } else {
                val outMetrics = DisplayMetrics()
                activity.windowManager.defaultDisplay.getMetrics(outMetrics)
                outMetrics.widthPixels
            }
        }


        fun getScreenHeight(@NonNull activity: Activity): Int {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val windowMetrics: WindowMetrics =
                    activity.getWindowManager().getCurrentWindowMetrics()
                val bounds: Rect = windowMetrics.getBounds()
                val insets: Insets = windowMetrics.getWindowInsets().getInsetsIgnoringVisibility(
                    WindowInsets.Type.systemBars()
                )
                if (activity.resources.configuration.orientation
                    == Configuration.ORIENTATION_LANDSCAPE
                    && activity.resources.configuration.smallestScreenWidthDp < 600
                ) { // landscape and phone
                    bounds.height()
                } else { // portrait or tablet
                    val navigationBarSize: Int = insets.bottom
                    bounds.height() - navigationBarSize
                }
            } else {
                val outMetrics = DisplayMetrics()
                activity.getWindowManager().getDefaultDisplay().getMetrics(outMetrics)
                outMetrics.heightPixels
            }
        }

        fun isValidUrl(url: String): Boolean {
            val regexp =
                Regex("(https?://(?:www\\.|(?!www))[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|www\\.[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|https?://(?:www\\.|(?!www))[a-zA-Z0-9]+\\.[^\\s]{2,}|www\\.[a-zA-Z0-9]+\\.[^\\s]{2,})")

            return regexp.matches(url)
        }

    }

}