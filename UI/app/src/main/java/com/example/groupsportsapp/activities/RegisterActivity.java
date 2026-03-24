package com.example.groupsportsapp.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

// Importurile Retrofit
import com.example.groupsportsapp.models.AuthResponse;
import com.example.groupsportsapp.R;
import com.example.groupsportsapp.models.RegisterRequest;
import com.example.groupsportsapp.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText etUsername, etEmail, etPassword;
    private Button btnRegister;
    private TextView tvGoToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etUsername = findViewById(R.id.etRegisterUsername);
        etEmail = findViewById(R.id.etRegisterEmail);
        etPassword = findViewById(R.id.etRegisterPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvGoToLogin = findViewById(R.id.tvGoToLogin);

        tvGoToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Completează toate câmpurile!", Toast.LENGTH_SHORT).show();
                    return;
                }

                RegisterRequest request = new RegisterRequest(username, email, password);

                RetrofitClient.getApi().registerUser(request).enqueue(new Callback<AuthResponse>() {
                    @Override
                    public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Toast.makeText(RegisterActivity.this, "Cont creat cu succes! Te poți loga.", Toast.LENGTH_LONG).show();

                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Eroare: Email sau Username deja folosit!", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<AuthResponse> call, Throwable t) {
                        Toast.makeText(RegisterActivity.this, "Eroare server: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}