package com.example.qatar_app.ui.fragment;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.VectorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qatar_app.R;
import com.example.qatar_app.helpers.myCluster;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;


public class MyMapFragment extends Fragment implements GoogleMap.InfoWindowAdapter, GoogleMap.OnInfoWindowClickListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, GoogleMap.OnMarkerClickListener {


    MapView mapView;
    private GoogleMap mMap;
    MenuItem road,searchView,run;
    Context context;
    private GoogleApiClient mGoogleApiClient;
    private ArrayList<Marker> mMarkerList;
    RecyclerView mylist;
    private ClusterManager<myCluster> mClusterManager;
    private TextView mInfoWindowOfficeAddress;
    private TextView mInfoWindowOfficeName;

    //private RatingBar mInfoWindowRateBar;
   /* private TextView mInfoWindowType;
    private TextView mInfoWindowZipCode;*/
    //private List<Agency> agencies;
    String count="0";

    private LatLng myLocation;
    BottomSheetDialog myDialog;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        //  gpsCheck();
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        mapView = (MapView) view.findViewById(R.id.map);
        getActivity().setTitle("Carte");



        setHasOptionsMenu(true);
        context=getActivity();
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);
        return view;
    }


    private void updateUI() {
   /*     this.mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(this.mApplicationData.getLastKnownLoaction().getLatitude(), this.mApplicationData.getLastKnownLoaction().getLongitude()), 13.0f), 200, new C20804());
        this.mMap.addMarker(new MarkerOptions().position(new LatLng(this.mApplicationData.getLastKnownLoaction().getLatitude(), this.mApplicationData.getLastKnownLoaction().getLongitude())).icon(BitmapDescriptorFactory.fromResource(C2071R.drawable.icon_pin_user)));
        this.mMap.addMarker(new MarkerOptions().position(new LatLng(this.mTargetPostOffice.getLatitude().doubleValue(), this.mTargetPostOffice.getLongitude().doubleValue())).icon(BitmapDescriptorFactory.fromBitmap(getMatchingPinBitmap(this.mTargetPostOffice.getOfficeType(), this.mTargetPostOffice.getOvercrowding()))));
        new DirectionMapper(this, this.indexToList).run();
*/
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        // //Log.d("hello", "onMapReady: ");
        mClusterManager = new ClusterManager<>(getActivity(), googleMap);
        googleMap.setOnCameraIdleListener(mClusterManager);
        googleMap.setOnMarkerClickListener(mClusterManager);
        googleMap.setOnInfoWindowClickListener(mClusterManager);
        addPersonItems();
        mClusterManager.cluster();
        mMap = googleMap;

        MapStyleOptions styleOptions=MapStyleOptions.loadRawResourceStyle(getActivity(),R.raw.map_style);
         mMap.setMapStyle(styleOptions);

        //updateUI();
        mMap.setOnMarkerClickListener(this);
        mMap.setInfoWindowAdapter(this);
        mMap.setOnInfoWindowClickListener(this);
    }

    @Override
    public void onConnected(Bundle bundle) {

        mMap.clear();
        /*listProspects =  getLocations();
        initLocationList(listProspects);*/
        // calulateDistances(listProspects);
        // ListProspects =
        //Log.d("locations", String.valueOf(ListProspects));
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startLocationUpdates();
        Location mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLocation == null) {
            startLocationUpdates();
        }
        if (mLocation != null) {
            double latitude = mLocation.getLatitude();
            double longitude = mLocation.getLongitude();
            myLocation = new LatLng(latitude, longitude);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 13.0f), 200, new GoogleMap.CancelableCallback() {
                @Override
                public void onFinish() {

                }

                @Override
                public void onCancel() {

                }
            });

            mMap.addMarker(new MarkerOptions().position(myLocation).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_send)));


        } else {
            Toast.makeText(getActivity(), "Location not Detected", Toast.LENGTH_SHORT).show();
        }
    }





    private void startLocationUpdates() {
        // Create the location request
        LocationRequest mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(30000)
                .setFastestInterval(5000);
        // Request location updates
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        //  //Log.d("reque", "--->>>>");
    }

    @Override
    public void onConnectionSuspended(int i) {
        // Log.i("location", "Connection Suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // Log.i("location", "Connection failed. Error: " + connectionResult.getErrorCode());
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public View getInfoWindow(Marker marker) {
        View v = getActivity().getLayoutInflater().inflate(R.layout.marker_info_custom_window, null);
        ViewGroup vg = v.findViewById(R.id.marker_content);
        int mPosition = Integer.valueOf(marker.getSnippet()).intValue();
        MyMapFragment.this.mInfoWindowOfficeName = vg.findViewById(R.id.marker_info_name);
        MyMapFragment.this.mInfoWindowOfficeAddress = vg.findViewById(R.id.marker_info_address);
        //AgenciesFragment.this.mInfoWindowZipCode = vg.findViewById(R.id.marker_info_zip);
        //     AgenciesFragment.this.mInfoWindowRateBar = (RatingBar) vg.findViewById(R.id.marker_info_rate);
        //AgenciesFragment.this.mInfoWindowType = vg.findViewById(R.id.marker_info_type);
        MyMapFragment.this.mInfoWindowOfficeName.setText("hello");
        MyMapFragment.this.mInfoWindowOfficeAddress.setText("nice");
           /* AgenciesFragment.this.mInfoWindowZipCode.setText((agencies.get(mPosition)).get());
            AgenciesFragment.this.mInfoWindowType.setText((agencies.get(mPosition)).getPostalCode() + "");*/
           /* try {
                AgenciesFragment.this.mInfoWindowRateBar.setVisibility(0);
                AgenciesFragment.this.mInfoWindowRateBar.setRating(((PostOffice) PostWebFragment.this.applicationData.getPostsList().get(mPosition)).getNote());
            } catch (NullPointerException e) {

            }*/
        return v;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
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


    @Override
    public boolean onMarkerClick(Marker marker) {
        this.mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
        try {
            if (marker.getSnippet() != null) {
                marker.showInfoWindow();
                for (Marker mMarker : mMarkerList) {
                    if (mMarker.getSnippet().equals(marker.getSnippet())) {
                        mMarker.setAlpha(1.0f);
                    } else {
                        mMarker.setAlpha(0.5f);
                    }
                }
            }
        } catch (NullPointerException e) {
        }
        return true;
    }


    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    public void gpsCheck() {
        final LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }



    /* @Override
     public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
         inflater.inflate(R.menu.menu_map, menu);
 
         searchView =
                 (MenuItem) menu.findItem(R.id.search).getActionView();
         road=((MenuItem) menu.findItem(R.id.road));
         run=((MenuItem) menu.findItem(R.id.run)).setIcon(R.drawable.baseline_directions_white_36);
         if (count.equals("0") || Integer.parseInt(count)<0) run.setVisible(false);
         else {
             run.setVisible(true);
             LayerDrawable icon = (LayerDrawable) road.getIcon();
 
             CountDrawable badge;
 
             // Reuse drawable if possible
             badge = new CountDrawable(context, R.color.red);
 
 
             badge.setCount(count);
             icon.mutate();
             icon.setDrawableByLayerId(R.id.ic_group_count, badge);
         }
 
         road.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
             @Override
             public boolean onMenuItemClick(MenuItem menuItem) {
                 openDialog();
                 return false;
             }
         });
 
         run.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
             @Override
             public boolean onMenuItemClick(MenuItem item) {
                 new DistanceInBackground(choisenList).execute();
                 // mMap.clear();
 
 
                 mMap.clear();
 
                 // calulateDistances(listProspects);
                 // ListProspects =
                 //Log.d("locations", String.valueOf(ListProspects));
                 if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
 
                 }
                 startLocationUpdates();
                 Location mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                 if (mLocation == null) {
                     startLocationUpdates();
                 }
                 if (mLocation != null) {
                     double latitude = mLocation.getLatitude();
                     double longitude = mLocation.getLongitude();
                     myLocation = new LatLng(latitude, longitude);
                     mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 13.0f), 200, new GoogleMap.CancelableCallback() {
                         @Override
                         public void onFinish() {
 
                         }
 
                         @Override
                         public void onCancel() {
 
                         }
                     });
 
                     mMap.addMarker(new MarkerOptions().position(myLocation).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_pin_user)));
 
 
                     mMarkerList = new ArrayList();
                     for (int i = 0; i < choisenList.size(); i++) {
                         ProspectRetofit prospectRetofit = choisenList.get(i);
                         try {
                             mMarkerList.add(mMap.addMarker(new MarkerOptions()
                                     .position(new LatLng(Double.parseDouble(prospectRetofit.getLatitude()), Double.parseDouble(prospectRetofit.getLongitude())))
                                     .icon(getBitmapIcon(i)).snippet(i + "")));
                             // //Log.d("added ", " pin : " + (prospectRetofit.getNomPrenom()));
                         } catch (Exception e2) {
                             Log.d("failed to set", " pin : " + e2.getMessage());
                         }
                     }
 
                 } else {
                     Toast.makeText(getActivity(), "Location not Detected", Toast.LENGTH_SHORT).show();
                 }
                 return false;
             }
 
             private BitmapDescriptor getBitmapIcon(int i) {
                 switch (i){
                     case 0:
                         return BitmapDescriptorFactory.fromResource(R.drawable.one_small);
                     case 1:
                         return BitmapDescriptorFactory.fromResource(R.drawable.two_small);
                     case 2:
                         return BitmapDescriptorFactory.fromResource(R.drawable.three_small);
                     default:
                         return BitmapDescriptorFactory.fromResource(R.mipmap.ic_launchermap);
 
                 }
             }
         });
         menu.findItem(R.id.search).setIcon(new IconicsDrawable(getContext()).icon(GoogleMaterial.Icon.gmd_search)
                 .color(Color.WHITE).sizeDp(20));
     }*/
    private void openDialog() {
        TextView code,raison,adresse,tel,date_start,date_fin,devise,taux;

        // initLocationList();
        myDialog.show();
    }

    private void addPersonItems() {
        // mClusterManager.addItem(new myCluster(-26.187616, 28.079329, "PJ", "https://twitter.com/pjapplez"));
    }

    private BitmapDescriptor getBitmapDescriptor(int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            VectorDrawable vectorDrawable = (VectorDrawable) getActivity().getDrawable(id);

            int h = vectorDrawable.getIntrinsicHeight();
            int w = vectorDrawable.getIntrinsicWidth();

            vectorDrawable.setBounds(0, 0, w, h);

            Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bm);
            vectorDrawable.draw(canvas);

            return BitmapDescriptorFactory.fromBitmap(bm);

        } else {
            return BitmapDescriptorFactory.fromResource(id);
        }
    }

/*    class AsynGetData extends AsyncTask<Void, Void, List<LocationSimpleItem>> {
        List<ProspectRetofit> list;
        public AsynGetData(List<ProspectRetofit> list) {
            this.list=list;
        }

        @Override
        protected List<LocationSimpleItem> doInBackground(Void... params) {

            final List<LocationSimpleItem> items = new ArrayList<>();
            items.clear();
            itemAdapter.clear();


            for (ProspectRetofit prospectRetofit : list) {
                // if (prospectRetofit.getLatitude()!=null && !prospectRetofit.getLatitude().equals("0.0"))
                items.add(new LocationSimpleItem(prospectRetofit));
            }

            return items;
        }

        @Override
        protected void onPostExecute(List<LocationSimpleItem> result) {


            super.onPostExecute(result);
            itemAdapter.add(result);
            mylist.setLayoutManager(new LinearLayoutManager(getActivity()));
            mylist.setItemAnimator(new DefaultItemAnimator());
            mylist.setAdapter(fastAdapter);

            fastAdapter.withOnClickListener(new OnClickListener<LocationSimpleItem>() {
                @Override
                public boolean onClick(View v, IAdapter<LocationSimpleItem> adapter, LocationSimpleItem item, int position) {

                    *//*myDialog.dismiss();
                    double latitude = Double.parseDouble(item.getDemande().getLatitude());
                    double longitude = Double.parseDouble(item.getDemande().getLongitude());
                    LatLng hisLocation = new LatLng(latitude, longitude);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(hisLocation, 13.0f), 200, new GoogleMap.CancelableCallback() {
                        @Override
                        public void onFinish() {

                        }

                        @Override
                        public void onCancel() {

                        }
                    });
 *//*
                    count=String.valueOf(Integer.parseInt(count)+1);
                    getActivity().invalidateOptionsMenu();
                    itemAdapter.remove(position);
                    fastAdapter.notifyAdapterDataSetChanged();
                    choisenList.add(item.getDemande());



                    return false;
                }
            });
        }

    }

    class AsynTemplate extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {


            super.onPostExecute(result);

        }

    }
    class AsynGetLocations extends AsyncTask<Void, Void, List<ProspectRetofit>> {

        @Override
        protected List<ProspectRetofit> doInBackground(Void... params) {


            return dbProspect.clientLocalService().fetchAllLocations();
        }

        @Override
        protected void onPostExecute(List<ProspectRetofit> result) {


            super.onPostExecute(result);


        }

    }

    class DistanceInBackground extends AsyncTask<Void, Void, Void> {
        List<ProspectRetofit> backgroundListProspects;
        public DistanceInBackground(List<ProspectRetofit> listProspects) {
            this.backgroundListProspects=listProspects;
        }

        @Override
        protected Void doInBackground(Void... params) {
            ProspectRetofit prospectRetofit = backgroundListProspects.get(0);

            for (int i = 0; i <backgroundListProspects.size() ; i++) {
                ProspectRetofit loopProspect = backgroundListProspects.get(i);

                float[] distances = new float[1];
                Location.distanceBetween(myLocation.latitude, myLocation.longitude,Double.parseDouble(loopProspect.getLatitude()), Double.parseDouble(loopProspect.getLongitude()),distances);

                //System.out.println("Distance: " + distances[0]);
                // Log.d("Distance", distances[0]+"");

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {


            super.onPostExecute(result);

        }

    }*/
}

