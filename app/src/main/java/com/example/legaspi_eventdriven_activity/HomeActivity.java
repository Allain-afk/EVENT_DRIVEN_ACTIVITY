package com.example.legaspi_eventdriven_activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.legaspi_eventdriven_activity.network.ApiClient;
import com.example.legaspi_eventdriven_activity.network.ApiService;
import com.example.legaspi_eventdriven_activity.network.User;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    public static final String EXTRA_USER_NAME = "USER_NAME";
    public static final String EXTRA_USER_EMAIL = "USER_EMAIL";

    private TextView tvWelcomeUser;
    private Button btnLogout;
    private RecyclerView rvUsers;
    private UserAdapter userAdapter;
    
    private List<User> allUsers = new ArrayList<>();
    private List<String> allSources = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.homeRoot), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String userName = getIntent().getStringExtra(EXTRA_USER_NAME);
        tvWelcomeUser = findViewById(R.id.tvWelcomeUser);
        btnLogout = findViewById(R.id.btnLogout);
        rvUsers = findViewById(R.id.rvUsers);

        if (userName != null && !userName.isEmpty()) {
            tvWelcomeUser.setText(getString(R.string.home_welcome_user, userName));
        } else {
            tvWelcomeUser.setText(getString(R.string.home_welcome_user, "User"));
        }

        userAdapter = new UserAdapter();
        rvUsers.setLayoutManager(new LinearLayoutManager(this));
        rvUsers.setAdapter(userAdapter);

        loadUsers();

        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void loadUsers() {
        // Load Local Users
        List<User> localUsers = SharedPrefsHelper.getLocalUsers(this);
        for (User u : localUsers) {
            allUsers.add(u);
            allSources.add("Local (SharedPreferences)");
        }
        userAdapter.setUsers(allUsers, allSources);

        // Load Remote Users
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getUsers().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<User> remoteUsers = response.body();
                    for (User u : remoteUsers) {
                        allUsers.add(u);
                        allSources.add("Remote (Render.com)");
                    }
                    userAdapter.setUsers(allUsers, allSources);
                } else {
                    Toast.makeText(HomeActivity.this, "Failed to load remote users", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(HomeActivity.this, "Error loading remote users: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}