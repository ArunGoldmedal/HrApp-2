package com.goldmedal.hrapp.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Address
import android.location.Geocoder
import android.provider.Settings
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import java.io.IOException
import java.math.BigDecimal
import java.net.InetAddress
import java.net.NetworkInterface
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
    val formatter = SimpleDateFormat(format, locale)
    return formatter.format(this)
}


fun formatDateString(rawString: String, inputFormat: String, outputFormat: String, locale: Locale = Locale.getDefault()): String {
    val date: Date?
    val inputFormatter = SimpleDateFormat(inputFormat, locale)
    val outputFormatter = SimpleDateFormat(outputFormat, locale)
    try {
        date = inputFormatter.parse(rawString)
        date?.let {
            return outputFormatter.format(date)
        }
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    return ""
}

fun getCurrentDateTime(): Date {



    return Calendar.getInstance().time
}


/*Financial Year in India runs from 1st April to 31st March*/
fun getCurrentFiscalYear(): String {
    val FY: String
    val year = Calendar.getInstance()[Calendar.YEAR]
    val month = Calendar.getInstance()[Calendar.MONTH] + 1
    FY = if (month < 4) { //month less than 4 are Jan,Feb,Mar
        (year - 1).toString() + "-" + year
    } else {
        year.toString() + "-" + (year + 1)
    }
    return FY
}


fun getDateFromString(rawString: String, format: String, locale: Locale = Locale.getDefault()): Date? {
    try {
        val formatter = SimpleDateFormat(format, locale)
        return formatter.parse(rawString)
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    return Date()
}


fun  getAddressFromLatLong(mContext: Context?, latitude: Double, longitude: Double): String? {
    val addresses: List<Address>
    val geocoder = Geocoder(mContext, Locale.getDefault())
    var cityAdd: String? = ""
    try {
        addresses = geocoder.getFromLocation(latitude, longitude, 1) // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        if (addresses.isNotEmpty()) {
            cityAdd = addresses[0].getAddressLine(0)
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return cityAdd
}

fun getLocationFromAddress(context: Context?, strAddress: String?): LatLng? {
    val coder = Geocoder(context)
    val address: List<Address>?
    var p1: LatLng? = null
    try {
        // May throw an IOException
        address = coder.getFromLocationName(strAddress, 5)
        if (address == null) {
            return null
        }
        if (address.isEmpty()) {
            return null
        }
        val location = address[0]
        p1 = LatLng(location.latitude, location.longitude)
    } catch (ex: IOException) {
        ex.printStackTrace()
    }
    return p1
}


fun formatNumber(value: String?): String? {
    var strNumber = ""

    try {
        strNumber = if (value.isNullOrEmpty()) {
            ""
        } else {

            val f1 = DecimalFormat()
            f1.minimumFractionDigits = 0
            f1.maximumFractionDigits = 2
            f1.format(BigDecimal(value))
        }
    } catch (e: NumberFormatException) {

    }
    return strNumber
}


fun getIPAddress(useIPv4: Boolean): String {
    try {
        val interfaces: List<NetworkInterface> = Collections.list(NetworkInterface.getNetworkInterfaces())
        for (intf in interfaces) {
            val addrs: List<InetAddress> = Collections.list(intf.inetAddresses)
            for (addr in addrs) {
                if (!addr.isLoopbackAddress) {
                    val sAddr = addr.hostAddress
                    val isIPv4 = sAddr.indexOf(':') < 0
                    if (useIPv4) {
                        if (isIPv4) return sAddr
                    } else {
                        if (!isIPv4) {
                            val delim = sAddr.indexOf('%') // drop ip6 zone suffix
                            return if (delim < 0) sAddr.toUpperCase(Locale.getDefault()) else sAddr.substring(0, delim).toUpperCase(Locale.getDefault())
                        }
                    }
                }
            }
        }
    } catch (ignored: Exception) {
    } // for now eat exceptions
    return ""
}

fun getDeviceId(context: Context): String? {
    return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
}

fun bitmapDescriptorFromVector(context: Context?, resource: Int): BitmapDescriptor? {

    val height = 150
    val width = 150


    val vectorDrawable = ContextCompat.getDrawable(context!!, resource)
    vectorDrawable!!.setBounds(0, 0, width, height)
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    vectorDrawable.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}

fun getMinDateToApplyLeaves(year: Int, month: Int, dayOfMonth: Int): Int {
    var minDay = 1
    when (month) {
        1, 3, 5, 7, 8, 10, 12 -> {
            // month is of 31 days
            minDay = if (dayOfMonth < 28) {
                4 + dayOfMonth
            } else {
                dayOfMonth - 27
            }
        }
        4, 6, 9, 11 -> {
            // month is of 30 days
            minDay = if (dayOfMonth < 27) {
                4 + dayOfMonth
            } else {
                dayOfMonth - 26
            }
        }
        2 -> {
            // month of feb
            // leap year
            minDay = if (year % 4 == 0) {
                if (dayOfMonth < 26) {
                    4 + dayOfMonth
                } else {
                    dayOfMonth - 25
                }
            } else {
                if (dayOfMonth < 25) {
                    4 + dayOfMonth
                } else {
                    dayOfMonth - 24
                }
            }
        }
    }
    return minDay
}

fun isRegularizedAllowedData(regularizeDate: String): Boolean {
    val c = Calendar.getInstance()
    val currentYear = c[Calendar.YEAR]
    val currentMonth = c[Calendar.MONTH]
    val currentDay = c[Calendar.DAY_OF_MONTH]
    val currentTimeMillis = c.timeInMillis

    val currentMinDays = getMinDateToApplyLeaves(currentYear, currentMonth, currentDay)
    c.add(Calendar.DAY_OF_MONTH, -currentMinDays)
    val startTimeMillis = c.timeInMillis

    val regularizeTimeInMillis = getTimeInMillisForDate(regularizeDate)

    return if (regularizeTimeInMillis >= startTimeMillis && regularizeTimeInMillis <= currentTimeMillis) true else false
}

fun getTimeInMillisForDate(strDate: String): Long {
    val cal = Calendar.getInstance()
    val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    cal.time = sdf.parse(strDate) as Date
    return cal.timeInMillis
}