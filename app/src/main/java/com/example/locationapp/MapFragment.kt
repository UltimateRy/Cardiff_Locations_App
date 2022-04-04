package com.example.locationapp

import android.location.Location
import android.location.LocationRequest
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

import android.location.LocationManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.*
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MapFragment : Fragment() {

    private lateinit var mMap: GoogleMap
    private lateinit var mFusedLocationClient: FusedLocationProviderClient


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
        val sydney = LatLng(-34.0, 151.0)
        googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

        mMap = googleMap

        mMap.setOnMapClickListener { point ->
            val marker = MarkerOptions()
                .position(point)
                .title("New Marker Point")
            mMap.addMarker(marker)
        }





        toCoFo()
    }

    //direct the view to CoFo
    private fun toCoFo() {
        //co-ordinates for CoFo (pull out of Google Maps URL or right click the point)
        val coFo = LatLng(51.619543, -3.878634)
        mMap.addMarker(MarkerOptions().position(coFo)
            .title("Computational Foundry"))
        //map appears as though under a `camera'
        mMap.moveCamera(CameraUpdateFactory.newLatLng(coFo))
        //zoom level from 1--20 as float
       // mMap.moveCamera(CameraUpdateFactory.zoomTo(18F))
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
            val sydney = LatLng(-34.0, 151.0)
            //mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        }

        view.findViewById<FloatingActionButton>(R.id.addFAB).setOnClickListener() {
            mMap.animateCamera(CameraUpdateFactory.zoomIn())
        }

        view.findViewById<FloatingActionButton>(R.id.removeFAB).setOnClickListener() {
            mMap.animateCamera(CameraUpdateFactory.zoomOut())
        }

    }
}