package project.projectapp.TeamsFragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import project.projectapp.R;

public class TeamDetailsMapsLocation extends FragmentActivity implements OnMapReadyCallback {

    private static final int PERMISSION_REQUEST_CODE = 10;
    private static final int ZOOM_LEVEL = 16;

    private boolean permission;

    // Google maps display
    private GoogleMap map;

    private Double latitude, longitude;

    private FusedLocationProviderClient locationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_details_location);

        // Default the permission to get the user location to false
        permission = false;

        latitude = Double.parseDouble(getIntent().getStringExtra("latitude"));
        longitude = Double.parseDouble(getIntent().getStringExtra("longitude"));

        locationPermission();
    }


    /**
     * When the map is loaded, it checks if the user has given permission to send their current
     * location such that they can be displayed on the map. Also allows users to click on the
     * screen and set a marker for the meeting location. If they click somewhere else, the previous
     * marker is removed and a new one is set. Also, if they click on the previously set marker
     * again, then it is removed from the screen. If this is the second or more time that the user
     * has opened this activity and a marker was set previously, then this marker is reloaded onto
     * the map
     * @param googleMap - the display for the map
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setZoomGesturesEnabled(true);
        map.getUiSettings().setCompassEnabled(true);

        if (permission) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED) {
                return;
            }
            map.setMyLocationEnabled(true);
        }

        LatLng previousMarker = new LatLng(latitude, longitude);
        map.addMarker(new MarkerOptions().position(previousMarker)
                .title("Marker"));
        setCameraLocation(new LatLng(latitude, longitude), ZOOM_LEVEL);
    }

    /**
     * Used to retrieve the devices location such that a little blue dot can appear on the map
     * showing where the user is located. This location is then set as the default location that
     * the map loads onto
     */
    private void getDeviceLocation(){
        locationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try{
            // Checks if the user gave the activity permission to get their location
            if(permission){
                // Retrieves the last known user location
                final Task location = locationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        // If it has found a user location, show it on the map and move the camera
                        if(task.isSuccessful()){
                            Location thisLocation = (Location) task.getResult();
                            if(latitude == null && longitude == null){
                                setCameraLocation(new LatLng(thisLocation.getLatitude(),
                                        thisLocation.getLongitude()), ZOOM_LEVEL);
                            }
                        }
                    }
                });
            }
        } catch (SecurityException e){
            Log.d("LOCATION", "Security Exception: " + e.getMessage());
        }
    }

    /**
     * Used to set where the map shows once opened
     * @param latLng - the user selected marker location
     * @param zoomLevel - zoom level for the camera
     */
    private void setCameraLocation(LatLng latLng, float zoomLevel){
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));
    }

    /**
     * Used to initialise the map
     */
    private void mapInitialise(){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Used to display a dialog to request permission from the user to show the device location
     */
    private void locationPermission(){
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED){
                // If the permission was granted, then change the permission variable to true
                permission = true;
                mapInitialise();
            } else {
                ActivityCompat.requestPermissions(this, permissions,
                        PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
        }

    }

    /**
     * Called once the user selected an option form the above dialog method, acts upon the selected
     * option. If the user gave permission then the map is initialised, otherwise return and close
     * the activity
     * @param requestCode - a unique ID for the request
     * @param permissions - the permission we pass it i.e. location
     * @param grantResults - the code for the request
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        permission = false;

        // Checks the request code and gives permission for the application to continue if successful
        switch (requestCode){
            case PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0 ){
                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            permission = false;
                            return;
                        }
                    }
                    permission = true;
                    mapInitialise();
                }
            }
        }
    }
}
