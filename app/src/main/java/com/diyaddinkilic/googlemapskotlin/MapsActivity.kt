package com.diyaddinkilic.googlemapskotlin


import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.diyaddinkilic.googlemapskotlin.databinding.ActivityMapsBinding
import java.util.*
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    val REQUEST_SET_TIME = 1
    val REQUEST_SET_TIME_ZONE = 2
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMapLongClickListener(dinleyici)
        //Latitude ->Enlem
        //Longitude ->Boylam

        //39.9324739,32.8311713
/*
        val ankara = LatLng(39.9324739,32.8311713)
        mMap.addMarker(MarkerOptions().position(ankara).title("Ankara"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ankara,15f))

 */
//Casting->as
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                //Lokasyon, konum değişçince yapılacak işlemler
                //    println(location.latitude)
                //    println(location.longitude)
                mMap.clear()
                val guncelKonum = LatLng(location.latitude, location.longitude)
                mMap.addMarker(MarkerOptions().position(guncelKonum).title("Güncel Konumunuz"))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(guncelKonum, 15f))

                val geocoder = Geocoder(this@MapsActivity, Locale.getDefault())
                try {
                    val adresListesi =
                        geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    if (adresListesi != null) {
                        if (adresListesi.size > 1) {
                            println(adresListesi.get(0).toString())
                        }
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            //izin verilmemiş
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        } else {
            //izin zaten verilmiş
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                1,
                1f,
                locationListener
            )
            val sonBilinenKonum = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (sonBilinenKonum != null) {
                val sonBilinenLatLng = LatLng(sonBilinenKonum.latitude, sonBilinenKonum.longitude)
                mMap.addMarker(
                    MarkerOptions().position(sonBilinenLatLng).title("Son bilinen Konumunuz")
                )
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sonBilinenLatLng, 15f))
            }
        }
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.SET_TIME_ZONE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.SET_TIME_ZONE),
                REQUEST_SET_TIME_ZONE
            )
        }
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.SET_TIME
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.SET_TIME),
                REQUEST_SET_TIME
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1) {
            if (grantResults.size > 1) {
                if (ContextCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    //izin verildi
                    locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        1,
                        1f,
                        locationListener
                    )
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    val dinleyici = object : GoogleMap.OnMapLongClickListener {
        override fun onMapLongClick(p0: LatLng) {
            mMap.clear()
            val geocoder = Geocoder(this@MapsActivity, Locale.getDefault())
            if (p0 != null) {
                var adres = ""
                try {
                    val adresListesi = geocoder.getFromLocation(p0.latitude, p0.longitude, 1)
                    if (adresListesi != null) {
                        if (adresListesi.size > 0) {
                            if (adresListesi.get(0).thoroughfare != null) {
                                adres += adresListesi.get(0).thoroughfare
                                if (adresListesi.get(0).subThoroughfare != null) {
                                    adres += adresListesi.get(0).subThoroughfare
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                mMap.addMarker(MarkerOptions().position(p0).title(adres))
            }
        }
    }
}