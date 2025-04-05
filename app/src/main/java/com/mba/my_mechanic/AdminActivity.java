package com.mba.my_mechanic;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class AdminActivity extends AppCompatActivity {

    private Button addGarageButton, assignRoleButton;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        db = FirebaseFirestore.getInstance();
        addGarageButton = findViewById(R.id.addGarageButton);
        assignRoleButton = findViewById(R.id.assignRoleButton);

        // Assign Roles
        assignRoleButton.setOnClickListener(v -> assignRole("user123", "Mechanic")); // Example: Change Role
    }

    private void assignRole(String userId, String role) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("role", role);

        db.collection("users").document(userId)
                .update(updates)
                .addOnSuccessListener(aVoid -> Toast.makeText(AdminActivity.this, "Role Updated!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(AdminActivity.this, "Failed to Update Role", Toast.LENGTH_SHORT).show());
    }

    private void addGarageToFirestore(String name, double lat, double lng) {
        Map<String, Object> garage = new HashMap<>();
        garage.put("name", name);
        garage.put("latitude", lat);
        garage.put("longitude", lng);

        db.collection("garages").add(garage)
                .addOnSuccessListener(documentReference -> Toast.makeText(AdminActivity.this, "Garage Added!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(AdminActivity.this, "Error Adding Garage", Toast.LENGTH_SHORT).show());
    }

}