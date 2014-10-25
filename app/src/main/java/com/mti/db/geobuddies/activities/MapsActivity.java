package com.mti.db.geobuddies.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mti.db.geobuddies.R;
import com.mti.db.geobuddies.database.AccountDAO;
import com.mti.db.geobuddies.database.GeoBuddiesDAOFactory;
import com.mti.db.geobuddies.model.GeoFriend;
import com.mti.db.geobuddies.test.LocationGenerator;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private GetFriendsTask getFriendsTask = null;

    private final static String TAG = "MapsActivity";
    private GoogleMap googleMap; // Might be null if Google Play services APK is not available.

    static final LatLng MTI_LOCATION = new LatLng(38.660955, -121.342451);
    static final int ANIMATE_SPEEED_TURN = 7000;

    ArrayList<Marker> markers;
    ArrayList<GeoFriend> friends;
    String myUserName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();

        markers = new ArrayList<Marker>();
        friends = new ArrayList<GeoFriend>();

        //TODO: assign username
        myUserName = "Tom";

        getFriends();

        Button btnRefresh = (Button) findViewById(R.id.refresh_button);
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generateLocations();
            }
        });
    }

    public void generateLocations() {

        LocationGenerator locationGenerator = new LocationGenerator();
        double randomLat;
        double randomLon;

        for (GeoFriend friend : friends) {

            // Generate random location
            randomLat = locationGenerator.generateLatitude();
            randomLon = locationGenerator.generateLongitude();
            friend.setLocation(randomLat, randomLon);
        }

        // Delete old markers
        clearMarkers();

        // Setup new markers
        setUpMarkers();
    }

    public void clearMarkers() {
        googleMap.clear();
        markers.clear();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #googleMap} is not null.
     * <p>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (googleMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (googleMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p>
     * This should only be called once and when we are sure that {@link #googleMap} is not null.
     */
    private void setUpMap() {

        // Enable my location button
        googleMap.setMyLocationEnabled(true);

//        googleMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
        googleMap.addMarker(new MarkerOptions().position(MTI_LOCATION).title("MTI College").snippet("Testing, 1, 2, 3"));

//        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MTI_LOCATION, 3));
//
//        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15),7000,null);


        CameraPosition cameraPosition =
                new CameraPosition.Builder()
                        .target(MTI_LOCATION)
                        .bearing(45)
                        .tilt(90)
                        .zoom(15)
                        .build();

        googleMap.animateCamera(
                CameraUpdateFactory.newCameraPosition(cameraPosition),
                ANIMATE_SPEEED_TURN,
                new GoogleMap.CancelableCallback() {

                    @Override
                    public void onFinish() {
                    }

                    @Override
                    public void onCancel() {
                    }
                }
        );
    }

    private void getFriends() {

        if (getFriendsTask != null) {
            return;
        }

        getFriendsTask = new GetFriendsTask();
        getFriendsTask.execute((Void) null);
    }

    private void setUpMarkers() {

        for (GeoFriend friend : friends) {

            addMarkerToMap(new LatLng(friend.getLatitude(), friend.getLongitude()),
                    friend.getUserName(), friend.getFirstName() + " " + friend.getLastName());
        }
    }

    public void addMarkerToMap(LatLng latLng, String title, String snippet) {

        Marker marker = googleMap.addMarker(new MarkerOptions().position(latLng)
                .title(title)
                .snippet(snippet));

        // Add marker to ArrayList markers
        markers.add(marker);
    }

    public class GetFriendsTask extends AsyncTask<Void, Void, Boolean> {

        // Set up DAO factory.
        GeoBuddiesDAOFactory factory = new GeoBuddiesDAOFactory(getBaseContext());

        // Set up account database implementation.
        AccountDAO daoAccount;

        GetFriendsTask() {}

        @Override
        protected Boolean doInBackground(Void... params) {



            // Create account database implementation.
            daoAccount = factory.createAccountDAO();

            // Get all friends.
            friends = daoAccount.getFriends(myUserName);

            if (friends != null) {
                return true;
            } else {
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            getFriendsTask = null;

            if (success) {
                Log.d(TAG, "Friends found! Setting up markers");
                setUpMarkers();
            } else {
                Log.e(TAG, "No friends were found");
            }

        }

        @Override
        protected void onCancelled() {

        }
    }
}
