package com.mba.my_mechanic;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.ola.mapsdk.camera.MapControlSettings;
import com.ola.mapsdk.interfaces.OlaMapCallback;
import com.ola.mapsdk.model.OlaLatLng;
import com.ola.mapsdk.model.OlaMarkerOptions;
import com.ola.mapsdk.view.Marker;
import com.ola.mapsdk.view.OlaMap;
import com.ola.mapsdk.view.OlaMapView;

public class HomeActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private OlaMapView mapView;
    private BottomNavigationView bottomNavigationView;
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private double latitude = 0.0;
    private double longitude = 0.0;
    private double altitude = 0.0;
    private Marker marker1;
    private OlaMap olaMapInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        FirebaseFirestore.getInstance().setFirestoreSettings(settings);

        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            finish();
        }

        // Check if the user is signed in with Google (but DO NOT auto sign out)
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            Log.d("HomeActivity", "User signed in with Google: " + account.getEmail());
        }

        // Initialize UI
        mapView = findViewById(R.id.mapView);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Bottom Navigation Handling
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                return true;  // Already on Home screen
            } else if (item.getItemId() == R.id.nav_profile) {
                if (mAuth.getCurrentUser() != null) {
                    startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
                } else {
                    Toast.makeText(this, "Please log in first!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                }
                return true;
            }
            return false;
        });

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Request location permission if not granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION_PERMISSION);
        } else {
            getCurrentLocation();
        }

        // Load Ola Map
        loadOlaMap();
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                Location location = task.getResult();
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                altitude = location.getAltitude();

                Log.d("HomeActivity", "Latitude: " + latitude + ", Longitude: " + longitude);
                Toast.makeText(HomeActivity.this, "Latitude: " + latitude + ", Longitude: " + longitude, Toast.LENGTH_SHORT).show();

                if (olaMapInstance != null) {
                    updateMapMarker();
                }
            } else {
                Toast.makeText(HomeActivity.this, "Failed to get current location", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadOlaMap() {
        MapControlSettings mapControlSettings = new MapControlSettings.Builder().build();

        mapView.getMap("JvMReRuo3FmjaDsKcsCpE3NInhuLc3liObU8R3Gy", new OlaMapCallback() {
            @Override
            public void onMapReady(OlaMap olaMap) {
                olaMapInstance = olaMap;
                updateMapMarker();
            }

            @Override
            public void onMapError(String error) {
                Log.e("HomeActivity", "Ola Map Error: " + error);
            }
        }, mapControlSettings);
    }

    private void updateMapMarker() {
        if (marker1 != null) {
            marker1.removeMarker();
        }

        OlaMarkerOptions markerOptions1 = new OlaMarkerOptions.Builder()
                .setMarkerId("marker1")
                .setPosition(new OlaLatLng(latitude, longitude, altitude))
                .setIsIconClickable(true)
                .setIconRotation(0f)
                .setIsAnimationEnable(true)
                .setIsInfoWindowDismissOnClick(true)
                .build();

        marker1 = olaMapInstance.addMarker(markerOptions1);
        olaMapInstance.moveCameraToLatLong(new OlaLatLng(latitude, longitude, altitude), 15.0, 2000);
    }

    // Handle permission request results
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(this, "Location permission denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
