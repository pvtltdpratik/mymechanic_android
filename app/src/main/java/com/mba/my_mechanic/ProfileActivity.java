package com.mba.my_mechanic;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {

    private TextView profileName, profileEmail;
    private SharedPreferences sharedPreferences;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private ImageView profileImage;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        sharedPreferences = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE);


        // Initialize your views and other components here
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        profileName = findViewById(R.id.account_name);
        profileImage = findViewById(R.id.profile_image);
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
                profileName.setText(name != null ? name : "No Name");


                // Load Profile Picture using Picasso
                if (profileUrl != null && !profileUrl.isEmpty()) {
                    Picasso.get().load(profileUrl).into(profileImage);
                } else {
                    profileImage.setImageResource(R.drawable.ic_profile_placeholder);
                }

            } else {
                // Handle Google Sign-In Users
                Toast.makeText(this, "Error fetching user data!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error fetching user data!", Toast.LENGTH_SHORT).show();
        });

    }

}
