package com.mba.my_mechanic;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.mba.my_mechanic.fragments.HomeFragment;

import java.util.concurrent.TimeUnit;

public class PhoneAuthActivity extends AppCompatActivity {

    private EditText phoneEditText, otpEditText;
    private Button sendOtpButton, verifyOtpButton;
    private FirebaseAuth mAuth;
    private String verificationId;
    private PhoneAuthProvider.ForceResendingToken resendToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_auth);

        mAuth = FirebaseAuth.getInstance();
        getSupportActionBar().hide();


        phoneEditText = findViewById(R.id.phoneEditText);
        otpEditText = findViewById(R.id.otpEditText);
        sendOtpButton = findViewById(R.id.sendOtpButton);
        verifyOtpButton = findViewById(R.id.verifyOtpButton);

        sendOtpButton.setOnClickListener(v -> sendOtp());
        verifyOtpButton.setOnClickListener(v -> verifyOtp());
    }

    private void sendOtp() {
        String phoneNumber = phoneEditText.getText().toString().trim();
        if (TextUtils.isEmpty(phoneNumber)) {
            phoneEditText.setError("Enter Phone Number");
            return;
        }

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                        signInWithPhoneAuthCredential(credential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(PhoneAuthActivity.this, "Verification Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                        PhoneAuthActivity.this.verificationId = verificationId;
                        resendToken = token;
                        Toast.makeText(PhoneAuthActivity.this, "OTP Sent", Toast.LENGTH_SHORT).show();
                    }
                }).build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void verifyOtp() {
        String otp = otpEditText.getText().toString().trim();
        if (TextUtils.isEmpty(otp)) {
            otpEditText.setError("Enter OTP");
            return;
        }

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(PhoneAuthActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(PhoneAuthActivity.this, HomeFragment.class));
                        finish();
                    } else {
                        Toast.makeText(PhoneAuthActivity.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
