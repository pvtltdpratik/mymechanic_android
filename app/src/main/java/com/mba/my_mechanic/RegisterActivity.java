package com.mba.my_mechanic;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private EditText emailEditText, fnameEditText, lnameEditText, passwordEditText, reenterPasswordEditText;
    private Button registerButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().hide();

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Link UI elements
        emailEditText = findViewById(R.id.emailEditText);
        fnameEditText = findViewById(R.id.fnameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        reenterPasswordEditText = findViewById(R.id.reenterpasswordEditText);
        registerButton = findViewById(R.id.registerButton);

        registerButton.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String email = emailEditText.getText().toString().trim();
        String fname = fnameEditText.getText().toString().trim();
        String lname = lnameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = reenterPasswordEditText.getText().toString().trim();

        // Validation
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(fname) || TextUtils.isEmpty(lname) ||
                TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Firebase Authentication - Register User
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String userId = mAuth.getCurrentUser().getUid();

                // Store user details in Firestore
                Map<String, Object> user = new HashMap<>();
                user.put("email", email);
                user.put("full_name", fname);
                user.put("role", "user"); // Default role

                db.collection("users").document(userId).set(user)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this, RegisterSuccessfulActivity.class));
                            finish();
                        })
                        .addOnFailureListener(e -> Toast.makeText(RegisterActivity.this, "Database Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());

            } else {
                Toast.makeText(RegisterActivity.this, "Registration Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
