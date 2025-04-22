package com.mba.my_mechanic.fragments.admin;

import android.app.AlertDialog;
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
import androidx.fragment.app.DialogFragment;

import com.google.firebase.firestore.FirebaseFirestore;
import com.mba.my_mechanic.R;

import java.util.HashMap;
import java.util.Map;


public class AddGarageDialogFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Set up the dialog for adding a new garage
        // Inflate your custom layout for adding a new garage
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_garage, null);

        EditText nameET = view.findViewById(R.id.editGarageName);
        EditText addressET = view.findViewById(R.id.editGarageAddress);
        EditText latitudeET = view.findViewById(R.id.editGarageLatitude);
        EditText longitudeET = view.findViewById(R.id.editGarageLongitude);
        EditText phoneET = view.findViewById(R.id.editGaragePhone);
        EditText ratingET = view.findViewById(R.id.editGarageRatings);
        EditText stateET = view.findViewById(R.id.editGarageState);
        Button cancelBtn = view.findViewById(R.id.btnCancel);
        EditText username = view.findViewById(R.id.garageusername);
        EditText password = view.findViewById(R.id.garagepassword);
        Button addBtn = view.findViewById(R.id.btnAdd);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(view);
        Dialog dialog = builder.create();

        // Cancel button click
        cancelBtn.setOnClickListener(v -> dialog.dismiss());

        // Add button click
        addBtn.setOnClickListener(v -> {
            // Get input data and validate
            String name = nameET.getText().toString().trim();
            String address = addressET.getText().toString().trim();
            String latitudeStr = latitudeET.getText().toString().trim();
            String longitudeStr = longitudeET.getText().toString().trim();
            String phone = phoneET.getText().toString().trim();
            String ratingStr = ratingET.getText().toString().trim();
            String state = stateET.getText().toString().trim();
            String gusername = username.getText().toString().trim();
            String gpassword = password.getText().toString().trim();

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(address)) {
                Toast.makeText(getContext(), "Name and address are required", Toast.LENGTH_SHORT).show();
                return;
            }

            double latitude = Double.parseDouble(latitudeStr);
            double longitude = Double.parseDouble(longitudeStr);
            double ratings = Double.parseDouble(ratingStr);

            // Add the garage to Firestore
            Map<String, Object> newGarage = new HashMap<>();
            newGarage.put("garage_name", name);
            newGarage.put("garage_address", address);
            newGarage.put("garage_latitude", latitude);
            newGarage.put("garage_longitude", longitude);
            newGarage.put("garage_phone_number", phone);
            newGarage.put("garage_ratings", ratings);
            newGarage.put("state", state);
            newGarage.put("username", gusername);
            newGarage.put("password", gpassword);

            FirebaseFirestore.getInstance()
                    .collection("garages")
                    .add(newGarage)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(getContext(), "Garage added successfully", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();

                        // Notify parent fragment to reload the garage list
                        if (getTargetFragment() != null) {
                            ((AdminGaragesFragment) getTargetFragment()).reloadGarages();
                        }

                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to add garage", Toast.LENGTH_SHORT).show());
        });

        return dialog;
    }
}
