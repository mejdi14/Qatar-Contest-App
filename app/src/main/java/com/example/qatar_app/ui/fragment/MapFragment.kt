package com.example.qatar_app.ui.fragment

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.VectorDrawable
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.qatar_app.R
import com.example.qatar_app.helpers.myCluster
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.*
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.google.maps.android.clustering.ClusterManager
import java.util.*

class MapFragment : Fragment(), InfoWindowAdapter, OnInfoWindowClickListener, OnMapReadyCallback,
    ConnectionCallbacks, OnConnectionFailedListener, LocationListener,
    OnMarkerClickListener {


    var mapView: MapView? = null
    private var mMap: GoogleMap? = null
    private var myLocation: LatLng? = null
    private var mGoogleApiClient: GoogleApiClient? = null
    private var mMarkerList: ArrayList<Marker>? = null
    var mylist: RecyclerView? = null
    private var mClusterManager: ClusterManager<myCluster>? = null
    private var mInfoWindowOfficeAddress: TextView? = null
    private var mInfoWindowOfficeName: TextView? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_map, container, false)
        //  gpsCheck();
        mGoogleApiClient = GoogleApiClient.Builder(activity!!)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build()
        return view
    }

    override fun onMapReady(googleMap: GoogleMap) {
        // //Log.d("hello", "onMapReady: ");
        mClusterManager = ClusterManager<myCluster>(activity, googleMap)
        googleMap.setOnCameraIdleListener(mClusterManager)
        googleMap.setOnMarkerClickListener(mClusterManager)
        googleMap.setOnInfoWindowClickListener(mClusterManager)
        addPersonItems()
        mClusterManager!!.cluster()
        mMap = googleMap

        //MapStyleOptions styleOptions=MapStyleOptions.loadRawResourceStyle(getActivity(),R.raw.bigdata);
        // mMap.setMapStyle(styleOptions);

        //updateUI();
        mMap!!.setOnMarkerClickListener(this)
        mMap!!.setInfoWindowAdapter(this)
        mMap!!.setOnInfoWindowClickListener(this)
    }

    override fun onConnected(bundle: Bundle?) {
        //mMap!!.clear()
        /*listProspects =  getLocations();
        initLocationList(listProspects);*/
        // calulateDistances(listProspects);
        // ListProspects =
        //Log.d("locations", String.valueOf(ListProspects));
        if (ActivityCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                activity!!, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        startLocationUpdates()
        val mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient)
        if (mLocation == null) {
            startLocationUpdates()
        }
        if (mLocation != null) {
            val latitude = mLocation.latitude
            val longitude = mLocation.longitude
            myLocation = LatLng(latitude, longitude)
            mMap!!.animateCamera(
                CameraUpdateFactory.newLatLngZoom(myLocation, 13.0f),
                200,
                object : CancelableCallback {
                    override fun onFinish() {}
                    override fun onCancel() {}
                })
            mMap!!.addMarker(
                MarkerOptions().position(myLocation!!)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin))
            )
           /* payers = ArrayList<TaxPayer>()
            val taxPayer = TaxPayer()
            taxPayer.setName("mejdi")
            taxPayer.setAdress("sfax")
            taxPayer.setLatitude("10.5213636")
            taxPayer.setLongitude("34.7303145")
            payers.add(taxPayer)
            mMarkerList = ArrayList<Any?>()
            for (payer in payers!!) {
                try {
                    mMarkerList.add(
                        mMap!!.addMarker(
                            MarkerOptions()
                                .position(
                                    LatLng(
                                        payer.getLatitude().toDouble(),
                                        payer.getLongitude().toDouble()
                                    )
                                )
                                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launchermap))
                                .snippet(0.toString() + "")
                        )
                    )
                    // //Log.d("added ", " pin : " + (prospectRetofit.getNomPrenom()));
                } catch (e2: Exception) {
                    // //Log.d("failed to set", " pin : " + e2.getMessage());
                    Log.d("marker error", "onConnected: $e2")
                }
            }*/
        } else {
            Toast.makeText(activity, "Location not Detected", Toast.LENGTH_SHORT).show()
        }
    }


    private fun startLocationUpdates() {
        // Create the location request
        val mLocationRequest = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(30000)
            .setFastestInterval(5000)
        // Request location updates
        if (ActivityCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                activity!!, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
            mGoogleApiClient,
            mLocationRequest,
            this
        )
        //  //Log.d("reque", "--->>>>");
    }

    override fun onConnectionSuspended(i: Int) {
        // Log.i("location", "Connection Suspended");
        mGoogleApiClient!!.connect()
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        // Log.i("location", "Connection failed. Error: " + connectionResult.getErrorCode());
    }

    override fun onLocationChanged(location: Location?) {}

    override fun getInfoWindow(marker: Marker): View? {
        val v: View = activity!!.layoutInflater.inflate(R.layout.marker_info_custom_window, null)
        val vg = v.findViewById<ViewGroup>(R.id.marker_content)
        val mPosition = Integer.valueOf(marker.snippet).toInt()
        mInfoWindowOfficeName = vg.findViewById(R.id.marker_info_name)
        mInfoWindowOfficeAddress = vg.findViewById(R.id.marker_info_address)
        //AgenciesFragment.this.mInfoWindowZipCode = vg.findViewById(R.id.marker_info_zip);
        //     AgenciesFragment.this.mInfoWindowRateBar = (RatingBar) vg.findViewById(R.id.marker_info_rate);
        //AgenciesFragment.this.mInfoWindowType = vg.findViewById(R.id.marker_info_type);
        /*mInfoWindowOfficeName.setText(payers!![mPosition].getName())
        mInfoWindowOfficeAddress.setText(payers!![mPosition].getAdress())*/
        /* AgenciesFragment.this.mInfoWindowZipCode.setText((agencies.get(mPosition)).get());
            AgenciesFragment.this.mInfoWindowType.setText((agencies.get(mPosition)).getPostalCode() + "");*/
        /* try {
                AgenciesFragment.this.mInfoWindowRateBar.setVisibility(0);
                AgenciesFragment.this.mInfoWindowRateBar.setRating(((PostOffice) PostWebFragment.this.applicationData.getPostsList().get(mPosition)).getNote());
            } catch (NullPointerException e) {

            }*/return v
    }

    override fun getInfoContents(marker: Marker?): View? {
        return null
    }

    override fun onInfoWindowClick(marker: Marker?) {
        /* if (IOUtils.isNetworkAvailable(getActivity())) {
            try {
//                Intent intent = new Intent(getActivity(), DetailProspect.class);
//            intent.putExtra("id", marker.getId());
//            intent.putExtra("type", "prospect");
//
//                startActivity(intent);
            } catch (NullPointerException ignored) {
            }
        }*/
        //  Utils.failureAlert(PostWebFragment.this.mMainActivity);
    }


    override fun onMarkerClick(marker: Marker): Boolean {
        mMap!!.animateCamera(CameraUpdateFactory.newLatLng(marker.position))
        try {
            if (marker.snippet != null) {
                marker.showInfoWindow()
                for (mMarker in mMarkerList!!) {
                    if (mMarker.snippet == marker.snippet) {
                        mMarker.alpha = 1.0f
                    } else {
                        mMarker.alpha = 0.5f
                    }
                }
            }
        } catch (e: NullPointerException) {
        }
        return true
    }


    override fun onStart() {
        super.onStart()
        mGoogleApiClient!!.connect()
    }

    override fun onStop() {
        super.onStop()
        if (mGoogleApiClient!!.isConnected) {
            mGoogleApiClient!!.disconnect()
        }
    }

    fun gpsCheck() {
        val manager = activity!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps()
        }
    }

    private fun buildAlertMessageNoGps() {
        val builder = AlertDialog.Builder(
            activity!!
        )
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
            .setCancelable(false)
            .setPositiveButton(
                "Yes"
            ) { dialog, id -> startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) }
            .setNegativeButton(
                "No"
            ) { dialog, id -> dialog.cancel() }
        val alert = builder.create()
        alert.show()
    }





    private fun addPersonItems() {
        // mClusterManager.addItem(new myCluster(-26.187616, 28.079329, "PJ", "https://twitter.com/pjapplez"));
    }


}