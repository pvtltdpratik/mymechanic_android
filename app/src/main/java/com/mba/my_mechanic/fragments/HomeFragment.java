package com.mba.my_mechanic.fragments;

import static android.widget.Toast.LENGTH_SHORT;

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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mba.my_mechanic.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;

public class HomeFragment extends Fragment implements OnMapReadyCallback {

    private FusedLocationProviderClient fusedLocationProviderClient;
    private GoogleMap mMap;
    private PlacesClient placesClient;
    private View bottomSheet;
    private TextView placeName, placeAddress, placePhone;

    private FirebaseFirestore firestore;

    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);


        firestore = FirebaseFirestore.getInstance();

        // Initialize Places
        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), "AIzaSyBPWMFhG63RCR2asfQgpGkBlv9M4FixTGY");
            placesClient = Places.createClient(this.getContext());
        }

        // Set up FusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());

        // Set up SupportMapFragment
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.getContext());

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
        getLastLocationAndShowGarages();
        fetchGaragesFromFirestore();
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

    private void getLastLocationAndShowGarages() {
        if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));

                fetchNearbyGarages(currentLatLng);
            }
        });
    }

    private void fetchNearbyGarages(LatLng location) {
        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json"
                + "?location=" + location.latitude + "," + location.longitude
                + "&radius=3000"
                + "&type=car_repair"
                + "&key=AIzaSyBPWMFhG63RCR2asfQgpGkBlv9M4FixTGY";

        RequestQueue queue = Volley.newRequestQueue(this.getContext());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray results = response.getJSONArray("results");
                        for (int i = 0; i < results.length(); i++) {
                            JSONObject place = results.getJSONObject(i);
                            String name = place.getString("name");
                            JSONObject geometry = place.getJSONObject("geometry");
                            JSONObject locationObj = geometry.getJSONObject("location");
                            double lat = locationObj.getDouble("lat");
                            double lng = locationObj.getDouble("lng");

                            LatLng latLng = new LatLng(lat, lng);
                            mMap.addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .title(name)
                                    .snippet("Tap marker for more info"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this.getContext(), "Parsing error", LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this.getContext(), "Request failed", LENGTH_SHORT).show());

        queue.add(request);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION &&
                grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {
            Toast.makeText(requireContext(), "Permission denied", LENGTH_SHORT).show();
        }
    }

    private void fetchGaragesFromFirestore() {
        firestore.collection("garages")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        // Extract data from the document
                        String garageName = documentSnapshot.getString("garage_name");
                        String garageAddress = documentSnapshot.getString("garage_address");
                        double garageLatitude = documentSnapshot.getDouble("garage_latitude");
                        double garageLongitude = documentSnapshot.getDouble("garage_longitude");
                        String garagePhone = documentSnapshot.getString("garage_phone_number");
                        double garageRatings = documentSnapshot.getDouble("garage_ratings");

                        // Create LatLng object
                        LatLng garageLocation = new LatLng(garageLatitude, garageLongitude);

                        // Create marker options with the snippet (additional info)
                        MarkerOptions markerOptions = new MarkerOptions()
                                .position(garageLocation)
                                .title(garageName)
                                .snippet("Address: " + garageAddress + "\nPhone: " + garagePhone + "\nRating: " + garageRatings);

                        // Add marker to the map
                        mMap.addMarker(markerOptions);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Failed to fetch garages", Toast.LENGTH_SHORT).show();
                });
    }

}