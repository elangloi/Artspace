package com.example.elizabethlanglois.artspace;

import android.Manifest;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    DatabaseReference db;
    private GoogleMap mMap;
    private static final String LOCATION_PERM = "android.permission.ACCESS_FINE_LOCATION";
    private static final int LOCATION_PERM_REQUEST = 1;
    private Location mLastLocation;
    private LocationManager mLocationManager;

    private final long MIN_TIME = 5000;
    private final float MIN_DISTANCE = 1000.0f;
    private static final long FIVE_MINS = 5 * 60 * 1000;
    private Circle mCircle;
    private SeekBar radiusBar;

    private TextView radiusText;

    DatabaseReference dbItems;
    SupportMapFragment mapFragment;

    SharedPreferences sp;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Force users to log in before accessing app
        sp = getSharedPreferences(LoginActivity.MY_PREFS_NAME, MODE_PRIVATE);
        username = sp.getString(LoginActivity.MY_USERNAME, null);
        if(username == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }

        SupportPlaceAutocompleteFragment placeAutocompleteFragment = (SupportPlaceAutocompleteFragment) getSupportFragmentManager().
                findFragmentById(R.id.place_autocomplete_fragment);


        placeAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                mMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 9f));
            }

            @Override
            public void onError(Status status) {

            }
        });

        db = FirebaseDatabase.getInstance().getReference("Users");
        Log.i("TEST", db.toString());

        dbItems = FirebaseDatabase.getInstance().getReference("Art_items");



        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);


        if (checkSelfPermission(LOCATION_PERM) != PackageManager.PERMISSION_GRANTED)
            requestPermissions(new String[]{LOCATION_PERM}, LOCATION_PERM_REQUEST);
        else
            LoadOverlay();


        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        radiusBar = findViewById(R.id.radius);
        radiusText = findViewById(R.id.radius_text);

        radiusBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mCircle.setRadius(progress * 1609.34);
                radiusText.setText("Radius from your Location: " + progress + " mi");
            }

            @Override
            public void onStartTrackingTouch(final SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(final SeekBar seekBar) {
            }
        });







    }

    public void LoadOverlay() {

        mapFragment.getMapAsync(this);

        dbItems.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    ArtItem artItem = snapshot.getValue(ArtItem.class);
                    //Log.i("TEST", Double.toString(artItem.latitude));
                    if (artItem.latitude != 0 && artItem.longitude != 0) {
                        Marker m = mMap.addMarker(new MarkerOptions().position(new LatLng(artItem.latitude, artItem.longitude)));
                        m.setTag(snapshot.getKey());

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == LOCATION_PERM_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted

                LoadOverlay();

            } else {
                Toast.makeText(getApplicationContext(), "This app needs access to your location", Toast.LENGTH_LONG);
            }
        }
    }

    private void getLocationUpdates()
    {

        // This method checks NETWORK_PROVIDER and GPS_PROVIDER for an existing
        // location reading.
        // It should only keep the last reading if it is fresh - less than 5 minutes old.

        if(mLastLocation == null || ageInMilliseconds(mLastLocation) > FIVE_MINS) {
        Location mostRecent = getBestLastKnownLocation();
            if(mostRecent != null) {
                mLastLocation = mostRecent;
                LatLng latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,11));
            }
        }



    }

    private Location getBestLastKnownLocation() {
        Location bestResult = null;
        float bestTime = Float.MAX_VALUE;

        if(mLocationManager != null) {
            List<String> matchingProviders = mLocationManager.getAllProviders();

            for (String provider : matchingProviders) {
                if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    mLocationManager.requestLocationUpdates(provider, MIN_TIME, MIN_DISTANCE, this);

                    Location location = mLocationManager.getLastKnownLocation(provider);

                    if (location != null) {

                        float time = ageInMilliseconds(location);

                        if (time < bestTime) {
                            bestResult = location;
                            bestTime = time;
                        }
                    }
                }
            }
        }


        return bestResult;
    }


    private long ageInMilliseconds(Location location) {
        return System.currentTimeMillis() - location.getTime();
    }



    // create an action bar button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.btnaddart) {
            // Move to art art activity
            startActivity(new Intent(MainActivity.this, AddArt.class));
        } else if(id == R.id.btnmyart){
            startActivity(new Intent(MainActivity.this, MyArt.class));
        } else if(id == R.id.btnexplore) {
            startActivity(new Intent(MainActivity.this, ExploreActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        getLocationUpdates();
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(LOCATION_PERM) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{LOCATION_PERM}, LOCATION_PERM_REQUEST);
        } else {
            mMap.setMyLocationEnabled(true);
        }

        if(mLastLocation != null) {
            LatLng center = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            mMap.animateCamera( CameraUpdateFactory.zoomTo(10f));
            mCircle = mMap.addCircle(new CircleOptions().center(center));
            radiusBar.setProgress(10);
        }

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                startActivity(new Intent(MainActivity.this,
                        LocationView.class).putExtra(LocationView.ART_ITEM_TAG, ((String)marker.getTag()) ));
                return true;
            }
        });


    }

    public void onLocationChanged(Location location)
    {
        mLastLocation = location;


        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());


        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

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
}
