package com.mba.my_mechanic;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    private ImageView editProfileImage;
    private TextView changePicture;
    private EditText editName, editEmail, editPhone;
    private Button saveProfileButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private Uri imageUri;
    private ProgressDialog progressDialog;
    private FirebaseUser currentUser;
    private String userId;
    private boolean isGoogleUser = false; // Flag to check Google Sign-In user

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        progressDialog = new ProgressDialog(this);
        currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        userId = currentUser.getUid();
        editProfileImage = findViewById(R.id.editProfileImage);
        changePicture = findViewById(R.id.changePicture);
        editName = findViewById(R.id.editName);
        editEmail = findViewById(R.id.editEmail);
        editPhone = findViewById(R.id.editPhone);
        saveProfileButton = findViewById(R.id.saveProfileButton);

        loadUserProfile();

        changePicture.setOnClickListener(v -> chooseImage());
        saveProfileButton.setOnClickListener(v -> saveProfile());
    }

    private void loadUserProfile() {
        DocumentReference docRef = db.collection("users").document(userId);
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // User exists in Firestore (Normal email/password user)
                editName.setText(documentSnapshot.getString("name"));
                editEmail.setText(documentSnapshot.getString("email"));
                editPhone.setText(documentSnapshot.getString("phone"));

                String profileUrl = documentSnapshot.getString("profile_picture_url");
                if (profileUrl != null && !profileUrl.isEmpty()) {
                    Glide.with(this).load(profileUrl).into(editProfileImage);
                }
            } else {
                // Handle Google Sign-In users
                loadGoogleUserProfile();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error fetching user data!", Toast.LENGTH_SHORT).show();
            loadGoogleUserProfile();
        });
    }

    private void loadGoogleUserProfile() {
        isGoogleUser = true; // Mark the user as Google Sign-In user

        if (currentUser != null) {
            editName.setText(currentUser.getDisplayName());
            editEmail.setText(currentUser.getEmail());
            editEmail.setEnabled(false); // Prevent editing for Google users
            editPhone.setText("Phone not available");

            String profileUrl = (currentUser.getPhotoUrl() != null) ? currentUser.getPhotoUrl().toString() : null;
            if (profileUrl != null) {
                Glide.with(this).load(profileUrl).into(editProfileImage);
            }
        } else {
            Toast.makeText(this, "No user data found!", Toast.LENGTH_SHORT).show();
        }
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            editProfileImage.setImageURI(imageUri);
        }
    }

    private void saveProfile() {
        String name = editName.getText().toString().trim();
        String phone = editPhone.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isGoogleUser && TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Please enter your phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Updating profile...");
        progressDialog.show();

        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("updated_at", System.currentTimeMillis());

        if (!isGoogleUser) {
            user.put("phone", phone);
        }

        if (imageUri != null) {
            StorageReference fileRef = storageReference.child("profile_pictures/" + userId + ".jpg");
            fileRef.putFile(imageUri).addOnSuccessListener(taskSnapshot ->
                    fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        user.put("profile_picture_url", uri.toString());
                        updateFirestore(user);
                    })
            );
        } else {
            updateFirestore(user);
        }
    }

    private void updateFirestore(Map<String, Object> user) {
        db.collection("users").document(userId)
                .update(user)
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Profile Updated", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Update Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}