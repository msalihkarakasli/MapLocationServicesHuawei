package com.karakasli.maplocationservices;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.huawei.hms.location.FusedLocationProviderClient;
import com.huawei.hms.location.LocationServices;
import com.huawei.hms.maps.CameraUpdateFactory;
import com.huawei.hms.maps.HuaweiMap;
import com.huawei.hms.maps.OnMapReadyCallback;
import com.huawei.hms.maps.SupportMapFragment;
import com.huawei.hms.maps.model.BitmapDescriptorFactory;
import com.huawei.hms.maps.model.LatLng;
import com.huawei.hms.maps.model.MarkerOptions;
import com.huawei.hmf.tasks.OnCompleteListener;
import com.huawei.hmf.tasks.Task;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback
{
    private static final String TAG = MapsActivity.class.getSimpleName();

    private HuaweiMap mMap;
    private Location mLastKnownLocation;

    private FusedLocationProviderClient mFusedLocationProviderClient;

    private boolean mLocationPermissionGranted;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int DEFAULT_ZOOM = 15;

    // Keys for storing activity state.
    private static final String KEY_LOCATION = "location";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
        }

        setContentView(R.layout.activity_maps);

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     */
    @Override
    public void onMapReady(HuaweiMap googleMap)
    {
        mMap = googleMap;
        mMap.setPadding(50,50,50,50);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(false);

        // Prompt the user for permission.
        getLocationPermission();

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();

        // Add Custom Markers to Map
        addMarkers();
    }

    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }

    /**
     * Add dummy markers to map
     */
    private void addMarkers() {
        ArrayList<MarkerOptions> markerLocationList = getMarkerList();

        /**
         * Cluster Manager is an utility class of Map.
         */

        for(MarkerOptions m: markerLocationList) {
            mMap.addMarker(m);
        }
    }

    /**
     * Prepare markers list by using static coordinates.
     * @return dummy marker list.
     */
    private ArrayList<MarkerOptions> getMarkerList() {
        ArrayList<MarkerOptions> markerLocationList = new ArrayList<>();

        markerLocationList.add(new MarkerOptions()
                .position(new LatLng(41.0341832,29.1120788))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin)).clusterable(true));

        markerLocationList.add(new MarkerOptions()
                .position(new LatLng(41.0341820,29.1120778))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin2)).clusterable(true));

        markerLocationList.add(new MarkerOptions()
                .position(new LatLng(41.0287075,29.1087746))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin3)).clusterable(true));

        markerLocationList.add(new MarkerOptions()
                .position(new LatLng(41.0194392,29.1054754))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin2)).clusterable(true));

        markerLocationList.add(new MarkerOptions()
                .position(new LatLng(41.0264194,29.1091637))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin)).clusterable(true));

        markerLocationList.add(new MarkerOptions()
                .position(new LatLng(41.0261194,29.1092637))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin3)).clusterable(true));

        markerLocationList.add(new MarkerOptions()
                .position(new LatLng(41.0201681,28.9251092))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin2)).clusterable(true));

        markerLocationList.add(new MarkerOptions()
                .position(new LatLng(40.9906341,28.8873852)).clusterable(true));

        markerLocationList.add(new MarkerOptions()
                .position(new LatLng(40.9942802,28.8870863))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin3)).clusterable(true));

        markerLocationList.add(new MarkerOptions()
                .position(new LatLng(40.6436017,29.2486784))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin)).clusterable(true));

        markerLocationList.add(new MarkerOptions()
                .position(new LatLng(40.5782754,29.218297)).clusterable(true));

        markerLocationList.add(new MarkerOptions()
                .position(new LatLng(39.9032923,32.6226825))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin2)).clusterable(true));


        return markerLocationList;
    }


    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);

                mMap.setPadding(10, 30,50,70);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task locationResult = mFusedLocationProviderClient.getLastLocation();

                locationResult.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = (Location) task.getResult();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-34, 151), DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch(SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }
}
