package com.example.groupsportsapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

// Acestea sunt importurile noi pentru Retrofit
import com.example.groupsportsapp.models.AuthResponse;
import com.example.groupsportsapp.models.LoginRequest;
import com.example.groupsportsapp.R;
import com.example.groupsportsapp.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvGoToRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etLoginEmail);
        etPassword = findViewById(R.id.etLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvGoToRegister = findViewById(R.id.tvGoToRegister);

        tvGoToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Completează toate câmpurile!", Toast.LENGTH_SHORT).show();
                    return;
                }

                LoginRequest request = new LoginRequest(email, password);

                RetrofitClient.getApi().loginUser(request).enqueue(new Callback<AuthResponse>() {
                    @Override
                    public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {

                            AuthResponse authResponse = response.body();
                            Toast.makeText(LoginActivity.this, "Salut, " + authResponse.getUsername() + "!", Toast.LENGTH_LONG).show();

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("USER_ID", authResponse.getId());
                            startActivity(intent);
                            finish();

                        } else {
                            Toast.makeText(LoginActivity.this, "Email sau parolă incorectă!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<AuthResponse> call, Throwable t) {
                        Toast.makeText(LoginActivity.this, "Eroare server: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}