package com.mba.my_mechanic.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.mba.my_mechanic.R;

import java.util.Arrays;

public class HomeFragment extends Fragment implements OnMapReadyCallback {

    private FusedLocationProviderClient fusedLocationProviderClient;
    private GoogleMap mMap;
    private View bottomSheet;
    private TextView placeName, placeAddress, placePhone;

    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize Places
        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), "AIzaSyBPWMFhG63RCR2asfQgpGkBlv9M4FixTGY");
        }

        // Set up FusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());

        // Set up SupportMapFragment
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Bottom sheet setup
        bottomSheet = view.findViewById(R.id.bottom_sheet);
        placeName = view.findViewById(R.id.place_name);
        placeAddress = view.findViewById(R.id.place_address);
        placePhone = view.findViewById(R.id.place_phone);

        // Request location permission
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION_PERMISSION);
        } else {
            getCurrentLocation();
        }

        return view;
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) return;

        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null && mMap != null) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                mMap.moveCamera(com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(latLng, 15));
                mMap.addMarker(new com.google.android.gms.maps.model.MarkerOptions().position(latLng).title("You are here"));
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Enable location if permission granted
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }

        // Handle map click
        mMap.setOnMapClickListener(this::getPlaceDetails);
    }

    private void getPlaceDetails(LatLng latLng) {
        PlacesClient placesClient = Places.createClient(requireContext());

        FindCurrentPlaceRequest request = FindCurrentPlaceRequest.newInstance(Arrays.asList(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.PHONE_NUMBER
        ));

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        placesClient.findCurrentPlace(request).addOnSuccessListener(response -> {
            if (!response.getPlaceLikelihoods().isEmpty()) {
                Place place = response.getPlaceLikelihoods().get(0).getPlace();

                placeName.setText(place.getName());
                placeAddress.setText(place.getAddress());
                placePhone.setText(place.getPhoneNumber() != null ? place.getPhoneNumber() : "Not available");

                bottomSheet.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION &&
                grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {
            Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }
}