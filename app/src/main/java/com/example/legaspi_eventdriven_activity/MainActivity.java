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
import com.example.legaspi_eventdriven_activity.network.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private Button btnLogin;
    private Button btnSignUp;
    private TextInputLayout tilEmail, tilPassword;
    private TextInputEditText etEmail, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignUp = findViewById(R.id.btnSignUp);

        setupTextWatchers();

        // When login button is clicked, validate inputs
        btnLogin.setOnClickListener(v -> {
            if (validateLoginInput()) {
                performLogin();
            }
        });

        // When SignUp button is clicked, redirect to SignUpActivity
        btnSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
            startActivity(intent);
        });
    }

    private void performLogin() {
        String inputEmail = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String inputPassword = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

        // Show a loading state
        btnLogin.setEnabled(false);
        btnLogin.setText(R.string.text_logging_in);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<List<User>> call = apiService.getUsers();

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                btnLogin.setEnabled(true);
                btnLogin.setText(R.string.btn_login); // Restore original text

                if (response.isSuccessful() && response.body() != null) {
                    List<User> users = response.body();
                    User matchedUser = null;

                    for (User user : users) {
                        if (user.getEmail() != null && user.getEmail().equalsIgnoreCase(inputEmail)
                                && user.getPassword() != null && user.getPassword().equals(inputPassword)) {
                            matchedUser = user;
                            break;
                        }
                    }

                    if (matchedUser != null) {
                        Toast.makeText(MainActivity.this, R.string.toast_login_success, Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(MainActivity.this, OtpActivity.class);
                        intent.putExtra(OtpActivity.EXTRA_USER_EMAIL, matchedUser.getEmail());
                        String fullName = (matchedUser.getFirstName() != null ? matchedUser.getFirstName() : "")
                                + " " + (matchedUser.getLastName() != null ? matchedUser.getLastName() : "");
                        intent.putExtra(OtpActivity.EXTRA_USER_NAME, fullName.trim());
                        startActivity(intent);
                    } else {
                        Toast.makeText(MainActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Error fetching users: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                btnLogin.setEnabled(true);
                btnLogin.setText(R.string.btn_login); // Restore original text
                Toast.makeText(MainActivity.this, getString(R.string.toast_network_error, t.getMessage()), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupTextWatchers() {
        if (etEmail != null) {
            etEmail.addTextChangedListener(new SimpleTextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (tilEmail != null) {
                        tilEmail.setError(null);
                    }
                }
            });
        }

        if (etPassword != null) {
            etPassword.addTextChangedListener(new SimpleTextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (tilPassword != null) {
                        tilPassword.setError(null);
                    }
                }
            });
        }
    }

    private boolean validateLoginInput() {
        boolean isValid = true;

        String email = etEmail != null && etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword != null && etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

        if (TextUtils.isEmpty(email)) {
            if (tilEmail != null) tilEmail.setError(getString(R.string.error_email_required));
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            if (tilEmail != null) tilEmail.setError(getString(R.string.error_invalid_email));
            isValid = false;
        } else {
            if (tilEmail != null) tilEmail.setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            if (tilPassword != null) tilPassword.setError(getString(R.string.error_password_required));
            isValid = false;
        } else if (password.length() < 6) {
            if (tilPassword != null) tilPassword.setError(getString(R.string.error_password_min_length));
            isValid = false;
        } else {
            if (tilPassword != null) tilPassword.setError(null);
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