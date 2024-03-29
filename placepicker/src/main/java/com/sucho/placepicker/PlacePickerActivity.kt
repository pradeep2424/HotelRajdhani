package com.sucho.placepicker

import android.content.Intent
import android.content.res.ColorStateList
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_place_picker.*
import java.util.Locale

class PlacePickerActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        private const val TAG = "PlacePickerActivity"
    }

    private lateinit var map: GoogleMap
    private lateinit var constraintLayout: ConstraintLayout
    private lateinit var markerImage: ImageView
    private lateinit var markerShadowImage: ImageView
    //  private lateinit var placeSelectedFab: FloatingActionButton
    private lateinit var placeSelectedFab: TextView
    private lateinit var myLocationFab: FloatingActionButton
    private lateinit var placeNameTextView: TextView
    private lateinit var placeAddressTextView: TextView
    private lateinit var placeCoordinatesTextView: TextView
    private lateinit var placeProgressBar: ProgressBar

    private var latitude = Constants.DEFAULT_LATITUDE
    private var longitude = Constants.DEFAULT_LONGITUDE
    private var initLatitude = Constants.DEFAULT_LATITUDE
    private var initLongitude = Constants.DEFAULT_LONGITUDE
    private var showLatLong = true
    private var zoom = Constants.DEFAULT_ZOOM
    private var addressRequired: Boolean = true
    private var shortAddress = ""
    private var fullAddress = ""
    private var hideMarkerShadow = false
    private var markerDrawableRes: Int = -1
    private var markerColorRes: Int = -1
    private var fabColorRes: Int = -1
    private var primaryTextColorRes: Int = -1
    private var secondaryTextColorRes: Int = -1
    private var mapRawResourceStyleRes: Int = -1
    private var addresses: List<Address>? = null
    private var mapType: MapType = MapType.NORMAL
    private var onlyCoordinates: Boolean = false
    private var maxLocationRetries: Int = 3

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_picker)
        getIntentData()
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        bindViews()
        placeCoordinatesTextView.visibility = if (showLatLong) View.VISIBLE else View.GONE

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (latitude.toInt() == 0 && longitude.toInt() == 0) {
            setCurrentLocation()

//            Handler().postDelayed({
//                setCurrentLocation()
//
//            }, 5000)
        }

        placeSelectedFab.setOnClickListener {
            if (onlyCoordinates) {
                sendOnlyCoordinates()
            } else {
                if (addresses != null) {
                    val addressData = AddressData(latitude, longitude, addresses)
                    val returnIntent = Intent()
                    returnIntent.putExtra(Constants.ADDRESS_INTENT, addressData)
                    setResult(RESULT_OK, returnIntent)
                    finish()
                } else {
                    if (!addressRequired) {
                        sendOnlyCoordinates()
                    } else {
                        Toast.makeText(this@PlacePickerActivity, R.string.no_address, Toast.LENGTH_LONG)
                                .show()
                    }
                }
            }
        }

        myLocationFab.setOnClickListener {
            //            map.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(initLatitude, initLongitude), zoom))
            setCurrentLocation()
        }
        setIntentCustomization()
    }

    private fun bindViews() {
        constraintLayout = findViewById(R.id.cl_rootLayout)
        markerImage = findViewById(R.id.marker_image_view)
        markerShadowImage = findViewById(R.id.marker_shadow_image_view)
        placeSelectedFab = findViewById(R.id.place_chosen_button)
        myLocationFab = findViewById(R.id.my_location_button)
        placeNameTextView = findViewById(R.id.text_view_place_name)
        placeAddressTextView = findViewById(R.id.text_view_place_address)
        placeCoordinatesTextView = findViewById(R.id.text_view_place_coordinates)
        placeProgressBar = findViewById(R.id.progress_bar_place)
    }

    private fun sendOnlyCoordinates() {
        val addressData = AddressData(latitude, longitude, null)
        val returnIntent = Intent()
        returnIntent.putExtra(Constants.ADDRESS_INTENT, addressData)
        setResult(RESULT_OK, returnIntent)
        finish()
    }

    private fun getIntentData() {
        latitude = intent.getDoubleExtra(Constants.INITIAL_LATITUDE_INTENT, Constants.DEFAULT_LATITUDE)
        longitude = intent.getDoubleExtra(Constants.INITIAL_LONGITUDE_INTENT, Constants.DEFAULT_LONGITUDE)
        initLatitude = latitude
        initLongitude = longitude
        showLatLong = intent.getBooleanExtra(Constants.SHOW_LAT_LONG_INTENT, false)
        addressRequired = intent.getBooleanExtra(Constants.ADDRESS_REQUIRED_INTENT, true)
        hideMarkerShadow = intent.getBooleanExtra(Constants.HIDE_MARKER_SHADOW_INTENT, false)
        zoom = intent.getFloatExtra(Constants.INITIAL_ZOOM_INTENT, Constants.DEFAULT_ZOOM)
        markerDrawableRes = intent.getIntExtra(Constants.MARKER_DRAWABLE_RES_INTENT, -1)
        markerColorRes = intent.getIntExtra(Constants.MARKER_COLOR_RES_INTENT, -1)
        fabColorRes = intent.getIntExtra(Constants.FAB_COLOR_RES_INTENT, -1)
        primaryTextColorRes = intent.getIntExtra(Constants.PRIMARY_TEXT_COLOR_RES_INTENT, -1)
        secondaryTextColorRes = intent.getIntExtra(Constants.SECONDARY_TEXT_COLOR_RES_INTENT, -1)
        mapRawResourceStyleRes = intent.getIntExtra(Constants.MAP_RAW_STYLE_RES_INTENT, -1)
        mapType = intent.getSerializableExtra(Constants.MAP_TYPE_INTENT) as MapType
        onlyCoordinates = intent.getBooleanExtra(Constants.ONLY_COORDINATES_INTENT, false)
    }

    private fun setIntentCustomization() {
        markerShadowImage.visibility = if (hideMarkerShadow) View.GONE else View.VISIBLE
        if (markerColorRes != -1) {
            markerImage.setColorFilter(ContextCompat.getColor(this, markerColorRes))
        }
        if (markerDrawableRes != -1) {
            markerImage.setImageDrawable(ContextCompat.getDrawable(this, markerDrawableRes))
        }
        if (fabColorRes != -1) {
            placeSelectedFab.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, fabColorRes))
            myLocationFab.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, fabColorRes))
        }
        if (primaryTextColorRes != -1) {
            placeNameTextView.setTextColor(ContextCompat.getColor(this, primaryTextColorRes))
        }
        if (secondaryTextColorRes != -1) {
            placeAddressTextView.setTextColor(ContextCompat.getColor(this, secondaryTextColorRes))
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        map.setOnCameraMoveStartedListener {
            if (markerImage.translationY == 0f) {
                markerImage.animate()
                        .translationY(-75f)
                        .setInterpolator(OvershootInterpolator())
                        .setDuration(250)
                        .start()
            }
        }

        map.setOnCameraIdleListener {
            markerImage.animate()
                    .translationY(0f)
                    .setInterpolator(OvershootInterpolator())
                    .setDuration(250)
                    .start()

            showLoadingBottomDetails()
            val latLng = map.cameraPosition.target
            latitude = latLng.latitude
            longitude = latLng.longitude
            AsyncTask.execute {
                getAddressForLocation()
                runOnUiThread { setPlaceDetails(latitude, longitude, shortAddress, fullAddress) }
            }
        }
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latitude, longitude), zoom))
        if (mapRawResourceStyleRes != -1) {
            map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, mapRawResourceStyleRes))
        }
        map.mapType = when (mapType) {
            MapType.NORMAL -> GoogleMap.MAP_TYPE_NORMAL
            MapType.SATELLITE -> GoogleMap.MAP_TYPE_SATELLITE
            MapType.HYBRID -> GoogleMap.MAP_TYPE_HYBRID
            MapType.TERRAIN -> GoogleMap.MAP_TYPE_TERRAIN
            MapType.NONE -> GoogleMap.MAP_TYPE_NONE
            else -> GoogleMap.MAP_TYPE_NORMAL
        }
    }

    private fun showLoadingBottomDetails() {
        placeNameTextView.text = ""
        placeAddressTextView.text = ""
        placeCoordinatesTextView.text = ""
        placeProgressBar.visibility = View.VISIBLE
    }

    private fun setPlaceDetails(
            latitude: Double,
            longitude: Double,
            shortAddress: String,
            fullAddress: String
    ) {

        if (latitude == -1.0 || longitude == -1.0) {
            placeNameTextView.text = ""
            placeAddressTextView.text = ""
            placeProgressBar.visibility = View.VISIBLE
            return
        }
        placeProgressBar.visibility = View.INVISIBLE

        placeNameTextView.text = if (shortAddress.isEmpty()) "Dropped Pin" else shortAddress
        placeAddressTextView.text = fullAddress
        placeCoordinatesTextView.text = Location.convert(latitude, Location.FORMAT_DEGREES) + ", " + Location.convert(longitude, Location.FORMAT_DEGREES)
    }

    private fun getAddressForLocation() {
        setAddress(latitude, longitude)
    }

    private fun setCurrentLocation() {
        try {
//            val locationResult = fusedLocationClient.lastLocation

            try {
                val locationResult = fusedLocationClient.lastLocation
                locationResult
                        ?.addOnFailureListener(this) {
                            //                            setDefaultLocation()
                        }
                        ?.addOnSuccessListener(this) { location: Location? ->

                            // In rare cases location may be null...
                            if (location == null) {
                                if (maxLocationRetries > 0) {
                                    maxLocationRetries--
                                    Handler().postDelayed({ setCurrentLocation() }, 1000)
                                } else {
                                    // Location is not available. Give up...
//                                    setDefaultLocation()
                                    Snackbar.make(constraintLayout,
                                            R.string.picker_location_unavailable,
                                            Snackbar.LENGTH_INDEFINITE)
                                            .setAction(R.string.places_try_again) {
                                                setCurrentLocation()
                                            }
                                            .show()
                                }
                                return@addOnSuccessListener
                            }

                            // Set the map's camera position to the current location of the device.
//                            val latitude = location.latitude
//                            val longitude = location.longitude
                            latitude = location.latitude
                            longitude = location.longitude

//                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(initLatitude, initLongitude), zoom))

                            val lastKnownLocation = LatLng(location.latitude, location.longitude)
                            val update = CameraUpdateFactory.newLatLngZoom(lastKnownLocation, zoom)
                            map.animateCamera(update)

//                            if (animate) {
//                                googlemapMap?.animateCamera(update)
//                            } else {
//                                googleMap?.moveCamera(update)
//                            }

//                            // Load the places near this location
//                            loadNearbyPlaces()
                        }
            } catch (e: SecurityException) {
                Log.e(TAG, e.message)
                Toast.makeText(applicationContext, e.message.toString(), Toast.LENGTH_LONG).show();
            }
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(initLatitude, initLongitude), zoom))

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun setAddress(
            latitude: Double,
            longitude: Double
    ) {
        val geoCoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses = geoCoder.getFromLocation(latitude, longitude, 1)
            this.addresses = addresses
            return if (addresses != null && addresses.size != 0) {
                fullAddress = addresses[0].getAddressLine(
                        0
                )
                // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
//                shortAddress = generateFinalAddress(fullAddress).trim()
                shortAddress = addresses[0].subLocality
            } else {
                shortAddress = ""
                fullAddress = ""
            }
        } catch (e: Exception) {
            //Time Out in getting address
            Log.e(TAG, e.message)
            shortAddress = ""
            fullAddress = ""
            addresses = null
        }
    }

    private fun generateFinalAddress(
            address: String
    ): String {
        val s = address.split(",")
        return if (s.size >= 3) s[1] + "," + s[2] else if (s.size == 2) s[1] else s[0]
    }
}
