package com.goldmedal.hrapp.ui.map



import android.Manifest
import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.text.Html
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.goldmedal.hrapp.R
import com.goldmedal.hrapp.common.GoogleMapInfoAdapter
import com.goldmedal.hrapp.data.model.InsertPunchData
import com.goldmedal.hrapp.geofence.GeofenceTransitionService
import com.goldmedal.hrapp.ui.dialogs.PunchAttendanceDialog
import com.goldmedal.hrapp.ui.dialogs.SuccessMessageDialog
import com.goldmedal.hrapp.util.*
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.GoogleMap.*
import com.google.android.gms.maps.model.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_maps.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class MapsActivity : FragmentActivity(), View.OnClickListener, ConnectionCallbacks, OnConnectionFailedListener, LocationListener, OnMapReadyCallback, SuccessMessageDialog.OnDashboardRefresh, OnMapClickListener, OnMarkerClickListener, ResultCallback<Status>, PunchAttendanceDialog.OnShowSuccessMsg {
    private var map: GoogleMap? = null
    private var googleApiClient: GoogleApiClient? = null
    private var lastLocation: Location? = null
    private var geoFenceMarker: Marker? = null

    private var officeLatitude: String? = null
    private var officeLongitude: String? = null
    private var ODSubLocation: String? = null
    private var address: String? = null
    private var isGeoFenceLock: Boolean? = true


    private lateinit var locationManager: LocationManager
    var GpsStatus = false

    private var isCameraZoomed = false

    companion object {
        const val KEY_TASK_DESC = "key_task_desc"
        private val TAG = MapsActivity::class.java.simpleName
        private const val NOTIFICATION_MSG = "NOTIFICATION MSG"

        // Create a Intent send by the notification
        fun makeNotificationIntent(context: Context?, msg: String?): Intent {
            val intent = Intent(context, MapsActivity::class.java)
            intent.putExtra(NOTIFICATION_MSG, msg)
            return intent
        }

        private const val GEO_DURATION = 60 * 60 * 1000.toLong()
        private const val GEOFENCE_REQ_ID = "My Geofence"
        private const val GEOFENCE_RADIUS = 500.0f // in meters
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)


        // initialize GoogleMaps
        initGMaps()
        // create GoogleApiClient
        createGoogleApi()


        val punchType = intent.getStringExtra("punchType")


        officeLatitude = intent.getStringExtra("officeLatitude")
        officeLongitude = intent.getStringExtra("officeLongitude")
        ODSubLocation = intent.getStringExtra("ODSubLocation")
        address = intent.getStringExtra("Address")


        isGeoFenceLock = intent.getBooleanExtra("isGeoFenceLock", true)


        buttonAddress?.setOnClickListener(this)

        if (punchType == "IN") {
            btnCheckIn.visibility = View.VISIBLE
            btnCheckOut.visibility = View.GONE
            btnCheckIn.setOnClickListener(this)
        } else {
            btnCheckIn.visibility = View.GONE
            btnCheckOut.visibility = View.VISIBLE
            btnCheckOut.setOnClickListener(this)
        }
    }

    // Create GoogleApiClient instance
    private fun createGoogleApi() {
        Log.d(TAG, "createGoogleApi()")
        if (googleApiClient == null) {
            googleApiClient = GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build()
        }
    }

    private fun checkGpsStatus(): Boolean {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (locationManager != null) {
            GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            if (GpsStatus === true) {

            } else {
                buildAlertMessageNoGps()
            }
            return GpsStatus
        }
        return false

    }

    private fun buildAlertMessageNoGps() {

        val locationMsg = "<b>LOCATION</b>"
        MaterialAlertDialogBuilder(this@MapsActivity)
                .setMessage(Html.fromHtml("Please turn on $locationMsg to access features in our app"))
                .setPositiveButton(resources.getString(R.string.str_ok)) { dialog, which ->
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
                .show()


    }


    override fun onResume() {
        super.onResume()
        checkGpsStatus()
    }

    override fun onStart() {
        super.onStart()
        // Call GoogleApiClient connection when starting the Activity
        googleApiClient!!.connect()
    }

    override fun onStop() {
        super.onStop()
        // Disconnect GoogleApiClient when stopping Activity
        googleApiClient!!.disconnect()
    }

    private val REQ_PERMISSION = 999

    // Check for permission to access Location
    private fun checkPermission(): Boolean {
        Log.d(TAG, "checkPermission()")
        // Ask for permission if it wasn't granted yet
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)
    }

    // Asks for permission
    private fun askPermission() {
        Log.d(TAG, "askPermission()")
        ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQ_PERMISSION
        )
    }

    // Verify user's response of the permission requested
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        Log.d(TAG, "onRequestPermissionsResult()")
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQ_PERMISSION) {
            if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) { // Permission granted
                lastKnownLocation
            } else { // Permission denied
                permissionsDenied()
            }
        }
    }

    // App cannot work without the permissions
    private fun permissionsDenied() {
        Log.w(TAG, "permissionsDenied()")
    }

    // Initialize GoogleMaps
    private fun initGMaps() {
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
    }

    override fun onClick(v: View?) {
        val id = v?.id


        if (id == R.id.buttonAddress) {
            isCameraZoomed = false
            moveToAddressLocation()
        } else {
            val isGPSOn = checkGpsStatus()

            if (!isGPSOn) {
                return
            }

            if (lastLocation?.latitude == null && lastLocation?.longitude == null) {
                toast("Unable to retrieve your location at this time,please try again")
                return
            }


            if (lastLocation?.isFromMockProvider!!) {
                showMockLocationAlert()
                return
            }


            if (isGeoFenceLock == true) {
                if (officeLatitude.isNullOrEmpty() || officeLatitude.isNullOrEmpty()) {
                    toast("Office Location cannot be found,Please contact Admin")
                    return
                }

                if (checkIfWithinGeofenceRange()) {
                    showPunchDialog(id,isWithinOfficeRadius = true)
                } else {
                    if (id == R.id.btnCheckIn) {
                        alertDialog(if (ODSubLocation.isNullOrEmpty()) getString(R.string.str_check_in_alert) else getString(R.string.str_od_check_in_alert))
                    } else if (id == R.id.btnCheckOut) {
                        alertDialog(if (ODSubLocation.isNullOrEmpty()) getString(R.string.str_check_out_alert) else getString(R.string.str_od_check_out_alert))
                    }
                }
            } else {
                showPunchDialog(id,isWithinOfficeRadius = checkIfWithinGeofenceRange())
            }

        }
    }

// Callback called when Map is ready
    override fun onMapReady(googleMap: GoogleMap) {
        Log.d(TAG, "onMapReady()")
        map = googleMap
        map?.setOnMapClickListener(this)
        map?.setOnMarkerClickListener(this)

    }

    override fun onMapClick(latLng: LatLng) {
        Log.d(TAG, "onMapClick($latLng)")
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        Log.d(TAG, "onMarkerClickListener: " + marker.position)
        return false
    }

    private var locationRequest: LocationRequest? = null

    // Defined in mili seconds.
// This number in extremely low, and should be used only for debug
    private val UPDATE_INTERVAL = 15000 //15 secs 10 * 1000
    private val FASTEST_INTERVAL = 10000 // 10 secs

    // Start location Updates
    private fun startLocationUpdates() {
        Log.i(TAG, "startLocationUpdates()")
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL.toLong())
                .setFastestInterval(FASTEST_INTERVAL.toLong())
        if (checkPermission()) {
            map!!.isMyLocationEnabled = true


            //set view for info window
            map!!.setInfoWindowAdapter(GoogleMapInfoAdapter(this))

            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this)
        }
    }

    override fun onLocationChanged(location: Location) {
        Log.d(TAG, "onLocationChanged [$location]")
        lastLocation = location
        writeActualLocation(location)
    }

    // GoogleApiClient.ConnectionCallbacks connected
    override fun onConnected(bundle: Bundle?) {
        Log.i(TAG, "onConnected()")
        lastKnownLocation

        saveGeofence()
        recoverGeofenceMarker()
    }

    // GoogleApiClient.ConnectionCallbacks suspended
    override fun onConnectionSuspended(i: Int) {
        Log.w(TAG, "onConnectionSuspended()")
    }

    // GoogleApiClient.OnConnectionFailedListener fail
    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        Log.w(TAG, "onConnectionFailed()")
    }

    // Get last known location
    private val lastKnownLocation: Unit
        get() {
            Log.d(TAG, "getLastKnownLocation()")
            if (checkPermission()) {
                lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient)
                if (lastLocation != null) {
                    Log.i(TAG, "LasKnown location. " +
                            "Long: " + lastLocation!!.longitude +
                            " | Lat: " + lastLocation!!.latitude)
                    writeLastLocation()
                    startLocationUpdates()
                } else {
                    Log.w(TAG, "No location retrieved yet")
                    startLocationUpdates()
                }
            } else askPermission()
        }

    private fun writeActualLocation(location: Location?) {
        markerLocation(LatLng(location!!.latitude, location.longitude))
    }

    private fun writeLastLocation() {
        writeActualLocation(lastLocation)
    }


    private fun markerLocation(latLng: LatLng) {
        Log.i(TAG, "markerLocation($latLng)")
        if (map != null) {
            val zoom = 15f
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoom)
            map!!.animateCamera(cameraUpdate, object : CancelableCallback {

                override fun onFinish() {
                    if (!isCameraZoomed) {
                        geoFenceMarker?.showInfoWindow()
                    }
                    isCameraZoomed = true
                }

                override fun onCancel() {}
            })
        }
    }

    private fun markerForGeofence(latLng: LatLng) {
        Log.i(TAG, "markerForGeofence($latLng)")

        // Define marker options
        val markerOptions = MarkerOptions()
                .position(latLng)
                .snippet(address)
                .icon(bitmapDescriptorFromVector(this, R.drawable.map_pin))
                .title(if (ODSubLocation.isNullOrEmpty()) "Office Address" else "Outdoor Address")
        if (map != null) { // Remove last geoFenceMarker
            if (geoFenceMarker != null) geoFenceMarker!!.remove()

            geoFenceMarker = map!!.addMarker(markerOptions)
            startGeofence()
        }
    }

    // Start Geofence creation process
    private fun startGeofence() {
        Log.i(TAG, "startGeofence()")
        if (geoFenceMarker != null) {
            val geofence = createGeofence(geoFenceMarker!!.position, GEOFENCE_RADIUS)
            val geofenceRequest = createGeofenceRequest(geofence)
            addGeofence(geofenceRequest)
        } else {
            Log.e(TAG, "Geofence marker is null")
        }
    }

    // Create a Geofence
    private fun createGeofence(latLng: LatLng, radius: Float): Geofence {
        Log.d(TAG, "createGeofence")
        return Geofence.Builder()
                .setRequestId(GEOFENCE_REQ_ID)
                .setCircularRegion(latLng.latitude, latLng.longitude, radius)
                .setExpirationDuration(GEO_DURATION)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER
                        or Geofence.GEOFENCE_TRANSITION_EXIT)
                .build()
    }

    // Create a Geofence Request
    private fun createGeofenceRequest(geofence: Geofence): GeofencingRequest {
        Log.d(TAG, "createGeofenceRequest")
        return GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofence(geofence)
                .build()
    }

    private val geoFencePendingIntent: PendingIntent? = null
    private val GEOFENCE_REQ_CODE = 0
    private fun createGeofencePendingIntent(): PendingIntent {
        Log.d(TAG, "createGeofencePendingIntent")
        if (geoFencePendingIntent != null) return geoFencePendingIntent
        val intent = Intent(this, GeofenceTransitionService::class.java)
        return PendingIntent.getService(
                this, GEOFENCE_REQ_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    // Add the created GeofenceRequest to the device's monitoring list
    private fun addGeofence(request: GeofencingRequest) {
        Log.d(TAG, "addGeofence")
        if (checkPermission()) LocationServices.GeofencingApi.addGeofences(
                googleApiClient,
                request,
                createGeofencePendingIntent()
        ).setResultCallback(this)
    }

    override fun onResult(status: Status) {
        Log.i(TAG, "onResult: $status")
        if (status.isSuccess) {
            saveGeofence()
            drawGeofence()
        } else { // inform about fail
        }
    }

    // Draw Geofence circle on GoogleMap
    private var geoFenceLimits: Circle? = null

    private fun drawGeofence() {
        Log.d(TAG, "drawGeofence()")
        if (geoFenceLimits != null) geoFenceLimits!!.remove()

        val random = Random()
        val circleOptions = CircleOptions()
                .center(geoFenceMarker!!.position)
                .strokeColor(Color.argb(50, 70, 70, 70))
                .strokeWidth(2f)
                .fillColor(Color.argb(70, random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .radius(GEOFENCE_RADIUS.toDouble())
        geoFenceLimits = map!!.addCircle(circleOptions)
    }

    private val KEY_GEOFENCE_LAT = "GEOFENCE LATITUDE"
    private val KEY_GEOFENCE_LON = "GEOFENCE LONGITUDE"

    // Saving GeoFence marker with prefs mng
    private fun saveGeofence() {
        Log.d(TAG, "saveGeofence()")
        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putLong(KEY_GEOFENCE_LAT, java.lang.Double.doubleToRawLongBits(officeLatitude?.toDoubleOrNull()
                ?: 0.0))
        editor.putLong(KEY_GEOFENCE_LON, java.lang.Double.doubleToRawLongBits(officeLongitude?.toDoubleOrNull()
                ?: 0.0))

        editor.apply()
    }

    // Recovering last Geofence marker
    private fun recoverGeofenceMarker() {
        Log.d(TAG, "recoverGeofenceMarker")
        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        if (sharedPref.contains(KEY_GEOFENCE_LAT) && sharedPref.contains(KEY_GEOFENCE_LON)) {
            val lat = java.lang.Double.longBitsToDouble(sharedPref.getLong(KEY_GEOFENCE_LAT, -1))
            val lon = java.lang.Double.longBitsToDouble(sharedPref.getLong(KEY_GEOFENCE_LON, -1))

            if (lat == 0.0 && lon == 0.0) {
                Log.d("MapsActivity", "System can't track regions")
                return
            }
            val latLng = LatLng(lat, lon)
            markerForGeofence(latLng)
            drawGeofence()
        }
    }


    private fun moveToAddressLocation() {
        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        if (sharedPref.contains(KEY_GEOFENCE_LAT) && sharedPref.contains(KEY_GEOFENCE_LON)) {
            val lat = java.lang.Double.longBitsToDouble(sharedPref.getLong(KEY_GEOFENCE_LAT, -1))
            val lon = java.lang.Double.longBitsToDouble(sharedPref.getLong(KEY_GEOFENCE_LON, -1))

            if (lat == 0.0 && lon == 0.0) {
                Log.d("MapsActivity", "System can't track regions")
                return
            }
            val latLng = LatLng(lat, lon)

            markerLocation(latLng)

        }
    }

    // Clear Geofence
    private fun clearGeofence() {
        Log.d(TAG, "clearGeofence()")
        LocationServices.GeofencingApi.removeGeofences(
                googleApiClient,
                createGeofencePendingIntent()
        ).setResultCallback { status ->
            if (status.isSuccess) { // remove drawing
                removeGeofenceDraw()
            }
        }
    }

    private fun removeGeofenceDraw() {
        Log.d(TAG, "removeGeofenceDraw()")
        if (geoFenceMarker != null) geoFenceMarker!!.remove()
        if (geoFenceLimits != null) geoFenceLimits!!.remove()
    }

    private fun checkIfWithinGeofenceRange(): Boolean {


        val results1 = FloatArray(1)
        Location.distanceBetween(lastLocation!!.latitude, lastLocation!!.longitude, officeLatitude!!.toDouble(), officeLongitude!!.toDouble(), results1)
        val distanceInMeters = results1[0]

        return distanceInMeters < GEOFENCE_RADIUS
    }


    private fun showPunchDialog(id: Int?,isWithinOfficeRadius: Boolean) {

        val dialogFragment = PunchAttendanceDialog.newInstance()
        dialogFragment.callBack = this
        val bundle = Bundle()
        bundle.putDouble("latitude", lastLocation?.latitude ?: 0.0)
        bundle.putDouble("longitude", lastLocation?.longitude ?: 0.0)
        bundle.putBoolean("isWithinOfficeRadius", isWithinOfficeRadius)
        bundle.putString("officeAddress", address)
        if (id == R.id.btnCheckIn) {
            bundle.putString("calledFrom", "In")
        } else if (id == R.id.btnCheckOut) {
            bundle.putString("calledFrom", "Out")
        }

        dialogFragment.arguments = bundle
        val ft = supportFragmentManager.beginTransaction()
        val prev = supportFragmentManager.findFragmentByTag("checkIn_dialog")
        if (prev != null) {
            ft.remove(prev)
        }

        ft.addToBackStack(null)
        if(dialogFragment.isAdded) { return}
        dialogFragment.show(ft, "checkIn_dialog")

    }

    private fun showMockLocationAlert() {
        val positiveButtonListener = DialogInterface.OnClickListener { dialog12: DialogInterface?, id12: Int -> startActivity(Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS)) }
        val builder = AlertDialog.Builder(this)
        val titleText = "Mock Location Enabled"
        val foregroundColorSpan = ForegroundColorSpan(resources.getColor(android.R.color.holo_red_dark))
        val ssBuilder = SpannableStringBuilder(titleText)
        ssBuilder.setSpan(
                foregroundColorSpan,
                0,
                titleText.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        builder.setTitle(ssBuilder)
                .setMessage(R.string.mock_location_alert)
                .setPositiveButton(R.string.str_settings, positiveButtonListener)
                .show()
    }

    override fun showSuccessDialog(_object: List<Any?>, punchType: String?) {

        val punchSuccessData = _object as List<InsertPunchData?>
        val punchTime = punchSuccessData[0]?.PunchDateTime

        val serverFormat = SimpleDateFormat("M/dd/yyyy hh:mm:ss a", Locale.getDefault())
        val desiredFormat = SimpleDateFormat("hh:mm", Locale.getDefault())
        var formattedPunchTime: String? = ""
        formattedPunchTime = try {
            val date = serverFormat.parse(punchTime)
            desiredFormat.format(date)
        } catch (e: ParseException) {
            getCurrentDateTime().toString("hh:mm")
        }


        val dialogFragment = SuccessMessageDialog.newInstance()
        dialogFragment.callBack = this
        val bundle = Bundle()
        bundle.putString("punchTime", formattedPunchTime)
        bundle.putString("punchType", punchType)
        dialogFragment.arguments = bundle
        dialogFragment.show(supportFragmentManager, "success_dialog")
    }

    override fun redirectToHome() {
        setResult(Activity.RESULT_OK)
        finish()
    }
}