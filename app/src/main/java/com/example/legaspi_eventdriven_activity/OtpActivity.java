package com.example.legaspi_eventdriven_activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Random;

public class OtpActivity extends AppCompatActivity {

    public static final String EXTRA_USER_EMAIL = "USER_EMAIL";
    public static final String EXTRA_USER_NAME = "USER_NAME";

    private static final long OTP_TIMEOUT_MS = 30000; // 30 seconds
    private static final long COUNTDOWN_INTERVAL_MS = 1000; // 1 second

    private TextView tvSubtitle, tvTimer, tvOtpError;
    private TextInputLayout tilOtp;
    private TextInputEditText etOtp;
    private Button btnVerifyOtp, btnResendOtp, btnBackToLogin;

    private String userEmail;
    private String userName;
    private String currentOtp;
    private boolean isOtpExpired = false;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_otp);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.otpRoot), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        userEmail = getIntent().getStringExtra(EXTRA_USER_EMAIL);
        userName = getIntent().getStringExtra(EXTRA_USER_NAME);

        tvSubtitle = findViewById(R.id.tvSubtitle);
        tvTimer = findViewById(R.id.tvTimer);
        tvOtpError = findViewById(R.id.tvOtpError);
        tilOtp = findViewById(R.id.tilOtp);
        etOtp = findViewById(R.id.etOtp);
        btnVerifyOtp = findViewById(R.id.btnVerifyOtp);
        btnResendOtp = findViewById(R.id.btnResendOtp);
        btnBackToLogin = findViewById(R.id.btnBackToLogin);

        if (!TextUtils.isEmpty(userEmail)) {
            tvSubtitle.setText(getString(R.string.otp_subtitle, userEmail));
        } else {
            tvSubtitle.setText(getString(R.string.otp_subtitle, "your email"));
        }

        setupTextWatcher();

        btnVerifyOtp.setOnClickListener(v -> verifyOtp());
        btnResendOtp.setOnClickListener(v -> generateAndStartOtpTimer(true));
        btnBackToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(OtpActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        // Generate first OTP and start 30-sec countdown
        generateAndStartOtpTimer(false);
    }

    private void generateAndStartOtpTimer(boolean isResend) {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        // otp code generation
        Random random = new Random();
        int otpCode = 100000 + random.nextInt(900000);
        currentOtp = String.valueOf(otpCode);
        isOtpExpired = false;

        if (etOtp != null) {
            etOtp.setText("");
        }
        if (tilOtp != null) {
            tilOtp.setError(null);
        }
        tvOtpError.setVisibility(View.GONE);

        // show OTP via toast
        if (isResend) {
            Toast.makeText(this, getString(R.string.toast_new_otp_generated, currentOtp), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, getString(R.string.toast_otp_generated, currentOtp), Toast.LENGTH_LONG).show();
        }

        startCountDownTimer();
    }

    private void startCountDownTimer() {
        countDownTimer = new CountDownTimer(OTP_TIMEOUT_MS, COUNTDOWN_INTERVAL_MS) {
            @Override
            public void onTick(long millisUntilFinished) {
                long secondsLeft = millisUntilFinished / 1000;
                tvTimer.setText(getString(R.string.otp_timer_running, secondsLeft));
            }

            @Override
            public void onFinish() {
                isOtpExpired = true;
                tvTimer.setText(R.string.otp_expired_status);
                tvOtpError.setText(R.string.error_otp_expired);
                tvOtpError.setVisibility(View.VISIBLE);
                if (tilOtp != null) {
                    tilOtp.setError(getString(R.string.error_otp_expired));
                }
            }
        }.start();
    }

    private void verifyOtp() {
        String inputOtp = etOtp.getText() != null ? etOtp.getText().toString().trim() : "";

        if (TextUtils.isEmpty(inputOtp)) {
            if (tilOtp != null) tilOtp.setError(getString(R.string.error_otp_required));
            return;
        }

        if (isOtpExpired) {
            if (tilOtp != null) tilOtp.setError(getString(R.string.error_otp_expired));
            tvOtpError.setText(R.string.error_otp_expired);
            tvOtpError.setVisibility(View.VISIBLE);
            Toast.makeText(this, R.string.error_otp_expired, Toast.LENGTH_SHORT).show();
            return;
        }

        if (!inputOtp.equals(currentOtp)) {
            if (tilOtp != null) tilOtp.setError(getString(R.string.error_otp_invalid));
            return;
        }

        // OTP is valid and within 30-sec window
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        Toast.makeText(this, R.string.toast_otp_success, Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(OtpActivity.this, HomeActivity.class);
        intent.putExtra(HomeActivity.EXTRA_USER_NAME, userName != null ? userName : "User");
        intent.putExtra(HomeActivity.EXTRA_USER_EMAIL, userEmail != null ? userEmail : "");
        startActivity(intent);
        finish();
    }

    private void setupTextWatcher() {
        if (etOtp != null) {
            etOtp.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (tilOtp != null) {
                        tilOtp.setError(null);
                    }
                    if (!isOtpExpired) {
                        tvOtpError.setVisibility(View.GONE);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}