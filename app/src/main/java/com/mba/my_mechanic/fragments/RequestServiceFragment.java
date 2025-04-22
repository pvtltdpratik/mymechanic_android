package com.mba.my_mechanic.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationRequest;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.mba.my_mechanic.R;
import com.mba.my_mechanic.models.GarageItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RequestServiceFragment extends Fragment {

    private EditText etDescription, etVehicleType, etLocation, etPhone;
    private Button submitBtn;
    private FirebaseFirestore firestore;

    private FusedLocationProviderClient fusedLocationClient;
    private Button btnGetLocation;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 101;

    private Spinner spinnerGarage;
    private List<GarageItem> garageList = new ArrayList<>();
    private Location userLocation;

    public RequestServiceFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_request_service, container, false);

        // Initialize views
        etDescription = view.findViewById(R.id.editTextProblemDescription);
        etVehicleType = view.findViewById(R.id.editTextVehicleType);
        etLocation = view.findViewById(R.id.editTextUserLocation);
        etPhone = view.findViewById(R.id.editTextPhone);
        submitBtn = view.findViewById(R.id.buttonSubmitRequest);
        btnGetLocation = view.findViewById(R.id.buttonGetLocation);

        firestore = FirebaseFirestore.getInstance();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        btnGetLocation.setOnClickListener(v -> getCurrentLocation());
        submitBtn.setOnClickListener(v -> submitServiceRequest());

        return view;
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        userLocation = location;
                        fetchAddressFromLocation(location);
//                        fetchNearbyGaragesSorted(location);
                    }
                });
    }

    private void fetchAddressFromLocation(Location location)
    {
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null && !addresses.isEmpty()) {
                String address = addresses.get(0).getAddressLine(0);
                etLocation.setText(address);
            } else {
                etLocation.setText(location.getLatitude() + ", " + location.getLongitude());
            }
        } catch (IOException e) {
            e.printStackTrace();
            etLocation.setText(location.getLatitude() + ", " + location.getLongitude());
        }
    }

    private void submitServiceRequest() {
        String description = etDescription.getText().toString().trim();
        String vehicle = etVehicleType.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        if (TextUtils.isEmpty(description) || TextUtils.isEmpty(vehicle)
                || TextUtils.isEmpty(location) || TextUtils.isEmpty(phone)) {
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Prepare the request object
        Map<String, Object> request = new HashMap<>();
        request.put("problem_description", description);
        request.put("vehicle_type", vehicle);
        request.put("user_location", location);
        request.put("user_phone", phone);
        request.put("status", "pending");
        request.put("created_at", Timestamp.now());
        request.put("updated_at", Timestamp.now());
        request.put("user_id", FirebaseAuth.getInstance().getCurrentUser().getUid());
        request.put("username", FirebaseAuth.getInstance().getCurrentUser().getEmail());
        request.put("mechanic_id", null);

        firestore.collection("service_requests")
                .add(request)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getContext(), "Service request submitted!", Toast.LENGTH_SHORT).show();
                    clearFields();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void clearFields() {
        etDescription.setText("");
        etVehicleType.setText("");
        etLocation.setText("");
        etPhone.setText("");
    }
}