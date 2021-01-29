package com.uniandes.widetech.view

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.os.Looper
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.uniandes.widetech.R
import com.uniandes.widetech.presenter.HomePresenterImpl
import com.uniandes.widetech.view.fragments.ContactFragment
import com.uniandes.widetech.view.fragments.HomeFragment
import com.uniandes.widetech.view.fragments.ProfileFragment
import kotlinx.android.synthetic.main.activity_home.*
import java.io.*
import java.lang.Exception

/**
 * Class that represents main activity in UI.
 */
class HomeActivity : AppCompatActivity(), HomeView, OnMapReadyCallback {

    /**
     * Main presenter reference.
     */
    private var presenter: HomePresenterImpl? = null
    /**
     * Google map reference
     */
    private lateinit var mMap: GoogleMap

    /**
     * Current camera position in map.
     */
    private var cameraPosition: CameraPosition? = null

    /**
     * Default location in google map service.
     */
    private val defaultLocation = LatLng(-33.8523341, 151.2106085)

    /**
     * Represents if the location permission was granted to the app.
     */
    private var locationPermissionGranted = false

    /**
     * Represents the last known location of the device.
     */
    private var lastKnownLocation: Location? = null

    /**
     * Support fragment manager reference.
     */
    private val manager = supportFragmentManager;

    /**
     * Client fused location provider.
     */
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient

    /**
     * Current home fragment reference.
     */
    private lateinit var contactFragment: ContactFragment

    // globally declare LocationRequest
    private lateinit var locationRequest: LocationRequest

    // globally declare LocationCallback
    private lateinit var locationCallback: LocationCallback


    private var line: String = ""

    /**
     * Item selected Listener reference.
     */
    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.nav_home -> {
                val homeFragment = HomeFragment(presenter!!.getProductsList())
                openFragment(homeFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_contact -> {
                contactFragment = ContactFragment()
                openFragment(contactFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_profile -> {
                presenter!!.getImagesInDB(0)
                presenter!!.getImagesInDB(1)
                val profileFragment = ProfileFragment(presenter!!.getImagesList(),presenter!!.getSignaturesList())
                openFragment(profileFragment)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }



    /**
     * Method that indicates if there is any internet connection.
     */
    override fun isConnected(): Boolean {
        val cm = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting == true
    }

    override fun getAppContext(): Context? {
        return applicationContext
    }
    override fun loadProductsList(){
        runOnUiThread {
            val homeFragment = HomeFragment(presenter!!.getProductsList())
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.cont,homeFragment)
                //addToBackStack(null)
                commit()
            }
        }
    }

    override fun loadImageSignatureList(){
        presenter!!.getImagesInDB(0)
        presenter!!.getImagesInDB(1)
        val profileFragment = ProfileFragment(presenter!!.getImagesList(),presenter!!.getSignaturesList())
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.cont, profileFragment)
            //addToBackStack(null)
            commit()
        }
    }

    /**
     * @see AppCompatActivity.onCreate
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_home)

        // Presenter initialization
        presenter = HomePresenterImpl(this)
        presenter!!.initDB()
        presenter!!.getProducts()

        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION)
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION)
        }
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        val bottomNavigation: BottomNavigationView = navigationView
        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        getLocationUpdates()

    }
    //start location updates
    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        mFusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            null /* Looper */
        )


    }

    // stop location updates
    private fun stopLocationUpdates() {
        mFusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    // stop receiving location update when activity not visible/foreground
    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    // start receiving location update when activity  visible/foreground
    override fun onResume() {
        super.onResume()
        startLocationUpdates()
    }

    private fun openFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.cont,fragment)
            //addToBackStack(null)
            commit()
        }
    }

    override fun onBackPressed(){
        super.onBackPressed()
    }

    /**
     * @see onSaveInstanceState
     */
    override fun onSaveInstanceState(outState: Bundle) {
        /*
        if (mMap != null) {
            mMap?.let { map ->
                outState.putParcelable(KEY_CAMERA_POSITION, mMap.cameraPosition)
                outState.putParcelable(KEY_LOCATION, lastKnownLocation)
            }
        }
         */
        super.onSaveInstanceState(outState)
    }


    /**
     * Method that asks for location permission.
     */
    private fun getLocationPermission() {
        locationPermissionGranted = ContextCompat.checkSelfPermission(this@HomeActivity,
            Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED

        if (!locationPermissionGranted) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this@HomeActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this@HomeActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)

            } else {
                ActivityCompat.requestPermissions(this@HomeActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            }
        }
        updateLocationUI()
    }

    /**
     * Method that updates current location on UI.
     */
    fun updateLocationUI() {

        if (mMap == null) {
            return
        }
        if (isConnected()) {
            try {
                if (locationPermissionGranted) {
                    mMap?.isMyLocationEnabled = true
                    mMap?.uiSettings?.isMyLocationButtonEnabled = true
                } else {
                    mMap?.isMyLocationEnabled = false
                    mMap?.uiSettings?.isMyLocationButtonEnabled = false
                    lastKnownLocation = null
                }
            } catch (e: SecurityException) {
                Log.e("Exception: %s", e.message, e)
            }
        }
    }

    /**
     * Method that gets last known location if location permission was granted.
     */
    fun getDeviceLocation() {
        try {
            if (locationPermissionGranted) {
                val locationResult = mFusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Set the map's camera position to the current location of the device.
                        lastKnownLocation = task.result
                        if (lastKnownLocation != null) {
                            mMap?.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(lastKnownLocation!!.latitude,
                                        lastKnownLocation!!.longitude), DEFAULT_ZOOM.toFloat()))

                            Log.e("<>","LOCATION NOT NULL")
                            Log.e("<>","${lastKnownLocation!!.latitude}${lastKnownLocation!!.longitude}")
                        }
                        updateLocationUI()

                    } else {
                        Log.e("<>","LOCATION KNOWN IS NULL")
                        mMap?.moveCamera(
                            CameraUpdateFactory
                                .newLatLngZoom(defaultLocation, DEFAULT_ZOOM.toFloat()))
                        mMap?.uiSettings?.isMyLocationButtonEnabled = false
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
        putSomeMarkers()
        updateLocationUI()
    }

    /**
     * @see onRequestPermissionsResult
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        locationPermissionGranted = false


        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            Log.e("ERR0R","${requestCode}")
            Log.e("ERR0R","${grantResults[0]}")
            Log.e("ERR0R","${permissions[0]}")


            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED && permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION) {
                // Permission has been granted. Start camera preview Activity.
                locationPermissionGranted = true
                getDeviceLocation()
                updateLocationUI()
            }
        }

    }

    /**
     * call this method in onCreate
     * onLocationResult call when location is changed
     */
    private fun getLocationUpdates()
    {
        locationRequest = LocationRequest()
        locationRequest.interval = 5000
        locationRequest.fastestInterval = 5000
        locationRequest.smallestDisplacement = 1f // 170 m = 0.1 mile
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY //set according to your app function
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                if (locationResult.locations.isNotEmpty()) {
                    // get latest location
                    val location =
                        locationResult.lastLocation
                    // use your location object
                    line = line + "Posición guardada! (${location.latitude}:::${location.longitude}})"+"\n"

                    Toast.makeText(baseContext,"Posición guardada! (${location.latitude}:::${location.longitude}})"+"\n" , Toast.LENGTH_SHORT).show()

                    try {
                        val fileOutputStream = openFileOutput("locationLog.txt",Context.MODE_PRIVATE)
                        fileOutputStream.write(line.toByteArray())
                    } catch (e: FileNotFoundException)
                    {
                        e.printStackTrace()
                    }
                    catch (e: Exception)
                    {
                        e.printStackTrace()
                    }

                    // get latitude , longitude and other info from this
                }
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        getLocationPermission()
        // [END_EXCLUDE]

        // Turn on the My Location layer and the related control on the map.
        //updateLocationUI()

        // Get the current location of the device and set the position of the map.
        getDeviceLocation()
    }


    fun saveImage(image: Bitmap){
        val baos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 50, baos)
        val b = baos.toByteArray()
        var stringBased = Base64.encodeToString(b, Base64.DEFAULT)
        presenter!!.addImage(stringBased)

    }
    fun saveSignature(image: Bitmap){
        val baos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 50, baos)
        val b = baos.toByteArray()
        var stringBased = Base64.encodeToString(b, Base64.DEFAULT)
        presenter!!.addSignature(stringBased)

    }

    fun putSomeMarkers(){

        val d1 = LatLng(4.867, -75.627)
        val d2 = LatLng(4.911, -74.627)

        mMap.addMarker(
            MarkerOptions()
                .position(d1)
                .title("WideTech Location")
        )
        mMap.addMarker(
            MarkerOptions()
                .position(d2)
                .title("WideTech Location")
        )

    }

    companion object {
        private const val DEFAULT_ZOOM = 15
        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1

        // Keys for storing activity state.
        // [START maps_current_place_state_keys]
        private const val KEY_CAMERA_POSITION = "camera_position"
        private const val KEY_LOCATION = "location"
        // [END maps_current_place_state_keys]

        // Used for selecting the current place.
        private const val M_MAX_ENTRIES = 5
    }

}
