package com.mba.my_mechanic;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class AccountActivity extends AppCompatActivity {

    private TextView userName, userEmail, userPhone, userLocation;
    private ImageView profileImage;
    private Button editProfileButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        // Hide ActionBar
        Objects.requireNonNull(getSupportActionBar()).hide();

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Initialize UI Components
        userName = findViewById(R.id.userName);
        userEmail = findViewById(R.id.userEmail);
        userPhone = findViewById(R.id.userPhone);
        userLocation = findViewById(R.id.userLocation);
        profileImage = findViewById(R.id.profileImage);
        editProfileButton = findViewById(R.id.editProfileButton);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);

        // Set Default Selected Item
        bottomNavigationView.setSelectedItemId(R.id.nav_profile);

        // Load User Data
        loadUserProfile();

        // Edit Profile Button Click
        editProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(AccountActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });

        // Bottom Navigation Handling
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                startActivity(new Intent(AccountActivity.this, HomeActivity.class));
                finish();
                return true;
            } else if (item.getItemId() == R.id.nav_profile) {
                return true;
            }
            return false;
        });
    }

    private void loadUserProfile() {
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        String userId = currentUser.getUid();
        DocumentReference userRef = db.collection("users").document(userId);

        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // User exists in Firestore (Non-Google login)
                String name = documentSnapshot.getString("name");
                String email = documentSnapshot.getString("email");
                String phone = documentSnapshot.getString("phone");
                String profileUrl = documentSnapshot.getString("profile_picture_url");
                Double latitude = documentSnapshot.getDouble("location.latitude");
                Double longitude = documentSnapshot.getDouble("location.longitude");

                // Update UI
                userName.setText(name != null ? name : "No Name");
                userEmail.setText(email != null ? email : "No Email");
                userPhone.setText(phone != null ? phone : "No Phone");
                userLocation.setText((latitude != null && longitude != null) ?
                        "Lat: " + latitude + ", Long: " + longitude : "No Location");

                // Load Profile Picture using Picasso
                if (profileUrl != null && !profileUrl.isEmpty()) {
                    Picasso.get().load(profileUrl).into(profileImage);
                } else {
                    profileImage.setImageResource(R.drawable.ic_profile_placeholder);
                }

            } else {
                // Handle Google Sign-In Users
                loadGoogleUserProfile();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error fetching user data!", Toast.LENGTH_SHORT).show();
            loadGoogleUserProfile();
        });
    }

    private void loadGoogleUserProfile() {
        if (currentUser != null) {
            // Fetch from Firebase Authentication
            String name = currentUser.getDisplayName();
            String email = currentUser.getEmail();
            String profileUrl = (currentUser.getPhotoUrl() != null) ? currentUser.getPhotoUrl().toString() : null;

            // Update UI
            userName.setText(name != null ? name : "No Name");
            userEmail.setText(email != null ? email : "No Email");
            userPhone.setText("Phone not available");
            userLocation.setText("Location not available");

            // Load Profile Picture
            if (profileUrl != null) {
                Picasso.get().load(profileUrl).into(profileImage);
            } else {
                profileImage.setImageResource(R.drawable.ic_profile_placeholder);
            }
        } else {
            Toast.makeText(this, "No user data found!", Toast.LENGTH_SHORT).show();
        }
    }
}