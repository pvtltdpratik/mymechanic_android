package com.mba.my_mechanic.fragments.admin;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.firestore.FirebaseFirestore;
import com.mba.my_mechanic.R;
import com.mba.my_mechanic.models.Garage;

import java.util.HashMap;
import java.util.Map;

public class EditGarageDialogFragment extends DialogFragment {

    private Garage garage;
    private String documentId;

    // Static method to create a new instance of the fragment and pass data
    public static EditGarageDialogFragment newInstance(Garage garage, String documentId) {
        EditGarageDialogFragment fragment = new EditGarageDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable("garage", garage);  // Assuming Garage is Serializable
        args.putString("documentId", documentId);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Retrieve data from arguments (passed via newInstance)
        if (getArguments() != null) {
            garage = (Garage) getArguments().getSerializable("garage");
            documentId = getArguments().getString("documentId");
        }

        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_garage, null);

        EditText nameET = view.findViewById(R.id.editGarageName);
        EditText addressET = view.findViewById(R.id.editGarageAddress);
        EditText latitudeET = view.findViewById(R.id.editGarageLatitude);
        EditText longitudeET = view.findViewById(R.id.editGarageLongitude);
        EditText phoneET = view.findViewById(R.id.editGaragePhone);
        EditText ratingET = view.findViewById(R.id.editGarageRatings);
        EditText stateET = view.findViewById(R.id.editGarageState);
        Button cancelBtn = view.findViewById(R.id.btnCancel);
        Button updateBtn = view.findViewById(R.id.btnUpdate);

        // Pre-fill the fields with existing data
        if (garage != null) {
            nameET.setText(garage.getGarageName());
            addressET.setText(garage.getGarageAddress());
            latitudeET.setText(String.valueOf(garage.getGarage_latitude()));
            longitudeET.setText(String.valueOf(garage.getGarage_longitude()));
            phoneET.setText(garage.getGarage_phone_number());
            ratingET.setText(String.valueOf(garage.getGarage_ratings()));
            stateET.setText(garage.getState() != null ? garage.getState() : "");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(view);
        Dialog dialog = builder.create();

        // Cancel button click
        cancelBtn.setOnClickListener(v -> dialog.dismiss());

        // Update button click
        updateBtn.setOnClickListener(v -> {
            String name = nameET.getText().toString().trim();
            String address = addressET.getText().toString().trim();
            String latitudeStr = latitudeET.getText().toString().trim();
            String longitudeStr = longitudeET.getText().toString().trim();
            String phone = phoneET.getText().toString().trim();
            String ratingStr = ratingET.getText().toString().trim();
            String state = stateET.getText().toString().trim();

            // Validate input
            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(address)) {
                Toast.makeText(getContext(), "Name and address are required", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double latitude = Double.parseDouble(latitudeStr);
                double longitude = Double.parseDouble(longitudeStr);
                double ratings = Double.parseDouble(ratingStr);

                // Update the data in Firestore
                Map<String, Object> updatedData = new HashMap<>();
                updatedData.put("garage_name", name);
                updatedData.put("garage_address", address);
                updatedData.put("garage_latitude", latitude);
                updatedData.put("garage_longitude", longitude);
                updatedData.put("garage_phone_number", phone);
                updatedData.put("garage_ratings", ratings);
                updatedData.put("state", state);

                FirebaseFirestore.getInstance()
                        .collection("garages")
                        .document(documentId)
                        .update(updatedData)
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(getContext(), "Garage updated successfully", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();

                            // Notify parent fragment to reload the garage list
                            if (getTargetFragment() != null) {
                                ((AdminGaragesFragment) getTargetFragment()).reloadGarages();
                            }

                        })
                        .addOnFailureListener(e -> Toast.makeText(getContext(), "Update failed", Toast.LENGTH_SHORT).show());
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Please enter valid numbers for latitude, longitude, and rating", Toast.LENGTH_SHORT).show();
            }
        });

        return dialog;
    }
}