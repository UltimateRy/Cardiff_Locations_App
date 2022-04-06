package com.example.locationapp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.concurrent.ConcurrentHashMap


class MapFragment : Fragment() {

    private lateinit var mMap: GoogleMap
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var locationManager: LocationManager

    private var markerHashMap: ConcurrentHashMap<String, Marker> = ConcurrentHashMap()

    private lateinit var newMarker : Marker
    private var database = Firebase.firestore

    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.

         */

        googleMap.uiSettings.isZoomControlsEnabled = true

        val sydney = LatLng(-34.0, 151.0)


        markerHashMap["Sydney"] = googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))!!

        //newMarker = googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))!!
         //   ?.let { markerHashMap.("Sydney", it) }


       // markerHashMap["Sydney"] = newMarker

        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

        mMap = googleMap
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.style_json))

        /*
        mMap.setOnMapClickListener { point ->
            val marker = MarkerOptions()
                .position(point)
                .title("New Marker Point")
            mMap.addMarker(marker)
        }
         */
        populateMap()
        toCardiff()
    }

    private fun populateMap() {
/*
        database.collection("locations").get()
            .addOnSuccessListener {
                @Override


            }

 */

    }


    //direct the view to CoFo
    private fun toCardiff() {
        //co-ordinates for CoFo (pull out of Google Maps URL or right click the point)
        val cardiff = LatLng(51.48275351945997, -3.1688197915140925)
        //mMap.addMarker(MarkerOptions().position(coFo)
        //    .title("Welcome to Cardiff"))
        //map appears as though under a `camera'
        mMap.moveCamera(CameraUpdateFactory.zoomTo(12F))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(cardiff))
        //zoom level from 1--20 as float

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    override fun onResume() {
        super.onResume()
    }

    private fun getLastLocation() {
        if (isLocationEnabled()) {
            // checking location permission
            if (ActivityCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // request permission
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 42
                );
                return
            }
            //once the last location is acquired
            mFusedLocationClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
                val location: Location? = task.result
                if (location == null) {
                    //if it couldn't be acquired, get some new location data
                    requestNewLocationData()
                } else {
                    val lat = location.latitude
                    val long = location.longitude

                    Log.i("LocLatLocation", "$lat and $long")
                    val lastLoc = LatLng(lat, long)

                    //update camera
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(lastLoc))
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(lastLoc))

                    var hue : Float = 120F;

                    mMap.animateCamera(CameraUpdateFactory.zoomTo(15F))
                    mMap.addMarker(MarkerOptions().position(lastLoc)
                        .title("Current Location")
                        .icon(BitmapDescriptorFactory.defaultMarker(hue)))
                }
            }
            //couldn't get location, so go to Settings (deprecated??)
        } else {
            val mRootView = requireActivity().findViewById<View>(R.id.map)
            val locSnack = Snackbar.make(mRootView, "R.string.location_switch", Snackbar.LENGTH_LONG)
            locSnack.show()
            val intent = Intent(requireActivity(), SettingsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun isLocationEnabled(): Boolean {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun requestNewLocationData() {
        //parameters for location
        val mLocationRequest = LocationRequest.create().apply {
            interval = 100
            fastestInterval = 50
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            maxWaitTime= 100
        }
        // checking location permission
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // request permission
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 42
            );
            return
        }
        //update the location client
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        //add a callback so that location is repeatedly updated according to parameters
        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()!!
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location = locationResult.lastLocation
            val lat = mLastLocation.latitude
            val long = mLastLocation.longitude

            val lastLoc = LatLng(lat, long)
            Log.i("LocLatLocationCallback", "$lat and $long")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        view.findViewById<FloatingActionButton>(R.id.locFab).setOnClickListener() {
            //markerHashMap["Sydney"]?.remove()
            getLastLocation()
            //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        }
    }
}