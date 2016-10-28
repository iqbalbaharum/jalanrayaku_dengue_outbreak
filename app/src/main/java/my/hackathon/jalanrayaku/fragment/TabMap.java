package my.hackathon.jalanrayaku.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import my.hackathon.jalanrayaku.MainActivity;
import my.hackathon.jalanrayaku.R;
import my.hackathon.jalanrayaku.data.Dengue;
import my.hackathon.jalanrayaku.data.Weather;

/**
 * Created by MuhammadIqbal on 25/10/2016.
 */

public class TabMap extends Fragment implements OnMapReadyCallback, LocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private final static String TAG = TabMap.class.getSimpleName();

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    private MapView mMapView;
    private GoogleMap mGoogleMap;
    private GoogleApiClient mGoogleClientApi;

    private LatLng mCurrentLocation;

    private ArrayList<Dengue> mDengueList;
    private ArrayList<Weather> mWeatherHistory;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tab_map, container, false);

        // check for GPS
        LocationManager service = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if(!service.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }

        // Initialise Map
        mMapView = (MapView) view.findViewById(R.id.main_map);
        mMapView.onCreate(savedInstanceState);

        //
        mMapView.getMapAsync(this);
        //
        mGoogleClientApi = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        return view;
    }

    @Override
    public void onDestroy() {
        if (mMapView != null) {
            mMapView.onDestroy();
        }
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();

        if (mMapView != null) {
            mMapView.onLowMemory();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
        if(mGoogleClientApi.isConnected()) {
            mGoogleClientApi.disconnect();
        }
    }

    @Override
    public void onResume() {
        mMapView.onResume();
        mGoogleClientApi.connect();

        super.onResume();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            // get location
            mCurrentLocation = ((MainActivity)getActivity()).getLocation();
            if(mCurrentLocation != null) {
                moveCamera(mCurrentLocation);
                setOutbreakToMap();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        if(checkForLocationPermission()) {
            mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        }
    }

    /***
     * Check for permission needed for location, if don't have they request for it.
     * @return
     */
    private boolean checkForLocationPermission() {

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }

        return true;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "Location services connected.");

        if(checkForLocationPermission()) {
            Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleClientApi);
            if(location != null) {
                handleNewLocation(location);
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Location services suspended. Please reconnect.");
    }


    private void handleNewLocation(Location location) {
//        mCurrentLocation = location;
        // move camera
        moveCamera(new LatLng(location.getLatitude(), location.getLongitude()));
    }

    /***
     * Move camera to location
     * @param latLng
     */
    private void moveCamera(LatLng latLng) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 14);
        mGoogleMap.animateCamera(cameraUpdate);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(getActivity(), CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    /***
     *
     */
    private void setOutbreakToMap() {

        for (Dengue dengue: ((MainActivity)getActivity()).getDengueNearbyHotspot()) {

            int radius = dengue.getTotalCase() * 10;

            int color = 0x5500ff00;
            if(dengue.getOutbreakDuration() >= 35 && dengue.getOutbreakDuration() <= 84) {
                color = 0x55ffff00;
            }else if(dengue.getOutbreakDuration() > 84) {
                color = 0x55ff0000;
            }

            mGoogleMap.addCircle(new CircleOptions()
                    .center(new LatLng(dengue.getLatitude(), dengue.getLongitude()))
                    .radius(radius)
                    .fillColor(color)
                    .strokeWidth(0)
            );
        }
    }

    /*private void getAverage() {

        int index = 0;
        int total = 0;

        for (Dengue dengue: mDengueList) {

            index++;

            if(index == 1) {
                total = dengue.getOutbreakDuration();
            }

            if(dengue.getOutbreakDuration() > total) {
                total = dengue.getOutbreakDuration();
            }
//
//            total += dengue.getOutbreakDuration();
        }

//        double avg = total/index;

        Log.d(TAG, String.valueOf(total));
    }*/
}
