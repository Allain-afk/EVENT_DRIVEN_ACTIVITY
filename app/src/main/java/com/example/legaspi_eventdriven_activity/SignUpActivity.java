package com.example.legaspi_eventdriven_activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import com.example.legaspi_eventdriven_activity.network.ApiClient;
import com.example.legaspi_eventdriven_activity.network.ApiService;
import com.example.legaspi_eventdriven_activity.network.SignUpRequest;
import com.example.legaspi_eventdriven_activity.network.SignUpResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {

    private TextInputLayout tilFullName, tilEmail, tilPassword, tilConfirmPassword;
    private TextInputEditText etFullName, etEmail, etPasswordSignUp, etConfirmPassword;
    private Button btnSignUpSubmit, btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.signUpRoot), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tilFullName = findViewById(R.id.tilFullName);
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);

        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPasswordSignUp = findViewById(R.id.etPasswordSignUp);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        btnSignUpSubmit = findViewById(R.id.btnSignUpSubmit);
        btnCancel = findViewById(R.id.btnCancel);

        setupTextWatchers();

        // When Sign Up button is clicked, validate all inputs
        btnSignUpSubmit.setOnClickListener(v -> {
            if (validateSignUpInput()) {
                performSignUp();
            }
        });

        // When cancel btn is clicked, redirect back to MainActivity
        btnCancel.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }

    private void performSignUp() {
        String fullName = etFullName.getText() != null ? etFullName.getText().toString().trim() : "";
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password = etPasswordSignUp.getText() != null ? etPasswordSignUp.getText().toString().trim() : "";

        // Show a loading state
        btnSignUpSubmit.setEnabled(false);
        btnSignUpSubmit.setText(R.string.text_signing_up);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        SignUpRequest signUpRequest = new SignUpRequest(fullName, email, password);

        Call<SignUpResponse> call = apiService.signUpUser(signUpRequest);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<SignUpResponse> call, Response<SignUpResponse> response) {
                btnSignUpSubmit.setEnabled(true);
                btnSignUpSubmit.setText(R.string.btn_sign_up);

                if (response.isSuccessful() && response.body() != null) {
                    SignUpResponse signUpResponse = response.body();

                    if (signUpResponse.isSuccess()) {
                        // Save user locally in SharedPreferences
                        String[] nameParts = fullName.split(" ", 2);
                        String firstName = nameParts[0];
                        String lastName = nameParts.length > 1 ? nameParts[1] : "";
                        com.example.legaspi_eventdriven_activity.network.User user = new com.example.legaspi_eventdriven_activity.network.User(firstName, lastName, email, password);
                        SharedPrefsHelper.saveUserLocally(SignUpActivity.this, user);

                        Toast.makeText(SignUpActivity.this, R.string.toast_sign_up_success, Toast.LENGTH_SHORT).show();
                        finish(); // Go back to login
                    } else {
                        Toast.makeText(SignUpActivity.this, "Sign Up Failed: " + signUpResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SignUpActivity.this, getString(R.string.toast_error_code, response.code()), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SignUpResponse> call, Throwable t) {
                btnSignUpSubmit.setEnabled(true);
                btnSignUpSubmit.setText(R.string.btn_sign_up);
                Toast.makeText(SignUpActivity.this, getString(R.string.toast_network_error, t.getMessage()), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupTextWatchers() {
        if (etFullName != null) {
            etFullName.addTextChangedListener(new SimpleTextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (tilFullName != null) tilFullName.setError(null);
                }
            });
        }

        if (etEmail != null) {
            etEmail.addTextChangedListener(new SimpleTextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (tilEmail != null) tilEmail.setError(null);
                }
            });
        }

        if (etPasswordSignUp != null) {
            etPasswordSignUp.addTextChangedListener(new SimpleTextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (tilPassword != null) tilPassword.setError(null);
                }
            });
        }

        if (etConfirmPassword != null) {
            etConfirmPassword.addTextChangedListener(new SimpleTextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (tilConfirmPassword != null) tilConfirmPassword.setError(null);
                }
            });
        }
    }

    private boolean validateSignUpInput() {
        boolean isValid = true;

        String fullName = etFullName != null && etFullName.getText() != null ? etFullName.getText().toString().trim() : "";
        String email = etEmail != null && etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password = etPasswordSignUp != null && etPasswordSignUp.getText() != null ? etPasswordSignUp.getText().toString().trim() : "";
        String confirmPassword = etConfirmPassword != null && etConfirmPassword.getText() != null ? etConfirmPassword.getText().toString().trim() : "";

        // Full Name Validation
        if (TextUtils.isEmpty(fullName)) {
            if (tilFullName != null) tilFullName.setError(getString(R.string.error_full_name_required));
            isValid = false;
        } else {
            if (tilFullName != null) tilFullName.setError(null);
        }

        // Email Validation
        if (TextUtils.isEmpty(email)) {
            if (tilEmail != null) tilEmail.setError(getString(R.string.error_email_required));
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            if (tilEmail != null) tilEmail.setError(getString(R.string.error_invalid_email));
            isValid = false;
        } else {
            if (tilEmail != null) tilEmail.setError(null);
        }

        // Password Validation
        if (TextUtils.isEmpty(password)) {
            if (tilPassword != null) tilPassword.setError(getString(R.string.error_password_required));
            isValid = false;
        } else if (password.length() < 6) {
            if (tilPassword != null) tilPassword.setError(getString(R.string.error_password_min_length));
            isValid = false;
        } else {
            if (tilPassword != null) tilPassword.setError(null);
        }

        // Confirm Password Validation
        if (TextUtils.isEmpty(confirmPassword)) {
            if (tilConfirmPassword != null) tilConfirmPassword.setError(getString(R.string.error_confirm_password_required));
            isValid = false;
        } else if (!confirmPassword.equals(password)) {
            if (tilConfirmPassword != null) tilConfirmPassword.setError(getString(R.string.error_password_mismatch));
            isValid = false;
        } else {
            if (tilConfirmPassword != null) tilConfirmPassword.setError(null);
        }

        return isValid;
    }

    private abstract static class SimpleTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void afterTextChanged(Editable s) {}
    }
}