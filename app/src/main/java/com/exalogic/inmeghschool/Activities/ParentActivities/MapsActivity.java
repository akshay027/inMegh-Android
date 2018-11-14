package com.exalogic.inmeghschool.Activities.ParentActivities;

import android.Manifest;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.exalogic.inmeghschool.API.RetrofitAPI;
import com.exalogic.inmeghschool.Database.PreferencesManger;
import com.exalogic.inmeghschool.MapUtility.LatLngInterpolator;
import com.exalogic.inmeghschool.Models.TeacherModels.TrackTrip.TripLocation;
import com.exalogic.inmeghschool.Models.TeacherModels.TrackTrip.TripLocationResponse;
import com.exalogic.inmeghschool.Models.TeacherModels.TrackTrip.TripStudent;
import com.exalogic.inmeghschool.R;
import com.exalogic.inmeghschool.Utility.Constants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final long TRIP_HISTORY_INTERVAL = 1000 * 4;
    private static final long TIME_OUT = 1000 * 2;
    private GoogleMap mMap;
    private ArrayList<String> list;
    private ArrayAdapter<String> spinAdapter;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private ArrayList<TripStudent> tripStudentArrayList;
    private ArrayList<TripLocation> tripLocationArrayList;
    private String tripId;
    private Marker markerBus, markerCurrent;
    private Handler handler;
    private boolean isMarkerRotating;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        tripLocationArrayList = new ArrayList<>();
        tripStudentArrayList = new ArrayList<>();
        try {
          /*  id = getIntent().getStringExtra(Constants.TRIP_STUDENT);*/
            id = PreferencesManger.getStringFields(getApplicationContext(), Constants.TRIP_STUDENT);
            Log.e("id", "----" + id);
        } catch (Exception e) {
            e.printStackTrace();
        }

        startRepeatingTask();

    }

    @Override
    protected void onResume() {
        super.onResume();
        checkForLocationEnable(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

    }

    @Override
    public void onLocationChanged(Location location) {
        // Add a marker in Sydney and move the camera
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        if (markerCurrent == null) {
            mMap.addMarker(new MarkerOptions().position(latLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.current_location_map)).rotation(location.getBearing()));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(17));
        }
    }


    /*    @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.timetablespinner, menu);
            MenuItem item = menu.findItem(R.id.spinner);

            Spinner spinner = (Spinner) MenuItemCompat.getActionView(item);
            list = new ArrayList<String>();
            for (int i = 0; i < tripStudentArrayList.size(); i++) {
                list.add(tripStudentArrayList.get(i).getName());
            }


            spinAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, list);
            spinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(spinAdapter);
            spinAdapter.notifyDataSetChanged();

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String strid = list.get(position);
                    tripId = tripStudentArrayList.get(position).getTripId();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            return true;
        }*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.left_to_center, R.anim.center_to_right);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.left_to_center, R.anim.center_to_right);
    }

    private void getLocation() {
        try {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("trip_id", id);
            Log.e("akjbajhsbjhsabjhsa :", id);
            jsonObject.addProperty("branch_id", PreferencesManger.getStringFields(getApplicationContext(), Constants.Pref.KEY_BRANCH_ID));
            RetrofitAPI.getInstance(this).getApi().getBusCurrentLocation(jsonObject, new Callback<TripLocationResponse>() {
                @Override
                public void success(TripLocationResponse tripLocationResponse, Response response) {
                    try {
                        tripLocationArrayList.clear();
                        tripLocationArrayList.addAll(tripLocationResponse.getTripDetail());
                        updateMarker();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void failure(RetrofitError error) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateMarker() {

        try {
            if (tripLocationArrayList.size() > 0) {
                TripLocation tripLocation = tripLocationArrayList.get(0);
                LatLng latLng = new LatLng(Double.parseDouble(tripLocation.getLatitude()), Double.parseDouble(tripLocation.getLongitude()));

                if (markerBus != null && !markerBus.isVisible()) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(17));
                }

                if (markerBus == null) {
                    markerBus = mMap.addMarker(new MarkerOptions().position(latLng)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_top_view)).rotation(Float.parseFloat(tripLocation.getBearing())));

                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(17));
                }

                animateMarker(latLng, markerBus);
                rotateMarker(markerBus, Float.parseFloat(tripLocation.getBearing()));
                handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (tripLocationArrayList.size() > 1) {
                            TripLocation tripLocation2 = tripLocationArrayList.get(1);
                            LatLng latLng2 = new LatLng(Double.parseDouble(tripLocation2.getLatitude()), Double.parseDouble(tripLocation2.getLongitude()));
                            animateMarker(latLng2, markerBus);
                            rotateMarker(markerBus, Float.parseFloat(tripLocation2.getBearing()));
                        }
                    }
                }, TIME_OUT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Handler mHandler = new Handler();

    Runnable mHandlerTask = new Runnable() {
        @Override
        public void run() {
            getLocation();
            mHandler.postDelayed(mHandlerTask, TRIP_HISTORY_INTERVAL);

        }
    };

    void startRepeatingTask() {
        mHandlerTask.run();
    }

    void stopRepeatingTask() {
        mHandler.removeCallbacks(mHandlerTask);
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {
        Log.e("Location", "startLocationUpdates ----- startLocationUpdates");
        createLocationRequest();
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    protected void stopLocationUpdates() {
        try {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean checkForLocationEnable(final Context context) {

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (!gps_enabled && !network_enabled) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setTitle("Alert");
            dialog.setMessage("Please enable GPS.");
            dialog.setPositiveButton("GPS", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    context.startActivity(myIntent);
                }
            });
            dialog.setNegativeButton("cancel", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    finish();
                }
            });
            dialog.setCancelable(false);
            dialog.show();
        } else {
            int currentapiVersion = android.os.Build.VERSION.SDK_INT;
            if (currentapiVersion >= Build.VERSION_CODES.M) {
                checkForLocationPermission();
                // Do something for lollipop and above versions
            } else {
                mGoogleApiClient.connect();
                // do something for phones running an SDK before lollipop
            }
        }
        return gps_enabled;
    }

    private void checkForLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, Constants.LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != Constants.LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mGoogleApiClient.connect();
        } else {
            Toast.makeText(this, "Oops you were denied the permission", Toast.LENGTH_LONG).show();

        }
    }

    public static final void animateMarker(final LatLng destination, final Marker marker) {

        if (marker != null) {
            final LatLng startPosition = marker.getPosition();
            final LatLng endPosition = new LatLng(destination.latitude, destination.longitude);

            final float startRotation = marker.getRotation();

            final LatLngInterpolator latLngInterpolator = new LatLngInterpolator.LinearFixed();
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.setDuration(2000); // duration 5 second
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    try {
                        float v = animation.getAnimatedFraction();
                        LatLng newPosition = latLngInterpolator.interpolate(v, startPosition, endPosition);
                        marker.setPosition(newPosition);
//                        marker.setRotation(destination.getBearing());
//                        MapUtility.rotateMarker(marker, destination.getBearing(), googleMap);
//                        marker.setRotation(MapUtility.computeRotation(v, destination.getBearing(), 180));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
            valueAnimator.start();
        }
    }

    private void rotateMarker(final Marker marker, final float toRotation) {
        Log.e("Rotation", " Rotation :: " + toRotation);

        if (!isMarkerRotating && toRotation > 0) {

            final Handler handler = new Handler();
            final long start = SystemClock.uptimeMillis();
            final float startRotation = marker.getRotation();
            final long duration = 2000;
            marker.setAnchor(0.5f, 0.5f);
            final Interpolator interpolator = new LinearInterpolator();

            handler.post(new Runnable() {
                @Override
                public void run() {
                    isMarkerRotating = true;

                    long elapsed = SystemClock.uptimeMillis() - start;
                    float t = interpolator.getInterpolation((float) elapsed / duration);

                    float rot = t * toRotation + (1 - t) * startRotation;

                    marker.setRotation(rot > 180 ? rot / 2 : rot);
//                    marker.setRotation(rot);
                    if (t < 1.0) {
                        // Post again 16ms later.
                        handler.postDelayed(this, 16);
                    } else {
                        isMarkerRotating = false;
                    }
                }
            });
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopRepeatingTask();
        stopLocationUpdates();
    }
}
