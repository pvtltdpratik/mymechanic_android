package com.mba.my_mechanic;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterSuccessfulActivity extends AppCompatActivity {

    private Button login;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_successful);
        getSupportActionBar().hide();

        login = findViewById(R.id.loginButton);
        login.setOnClickListener(
                v -> {
                    startActivity(new Intent(RegisterSuccessfulActivity.this, LoginActivity.class));
                    finish();
                }
        );

    }
}
