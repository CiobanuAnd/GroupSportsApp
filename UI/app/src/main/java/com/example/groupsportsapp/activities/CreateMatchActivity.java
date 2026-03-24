package com.example.groupsportsapp.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.groupsportsapp.models.Match;
import com.example.groupsportsapp.models.MatchCreateRequest;
import com.example.groupsportsapp.R;
import com.example.groupsportsapp.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateMatchActivity extends AppCompatActivity {

    private EditText etTitle, etLocation, etTime;
    private Button btnSubmit;
    private int currentUserId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_match);

        currentUserId = getIntent().getIntExtra("USER_ID", -1);

        etTitle = findViewById(R.id.etMatchTitle);
        etLocation = findViewById(R.id.etMatchLocation);
        etTime = findViewById(R.id.etMatchTime);
        btnSubmit = findViewById(R.id.btnSubmitMatch);

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = etTitle.getText().toString().trim();
                String location = etLocation.getText().toString().trim();
                String time = etTime.getText().toString().trim();

                if (title.isEmpty() || location.isEmpty() || time.isEmpty()) {
                    Toast.makeText(CreateMatchActivity.this, "Completează toate câmpurile!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (currentUserId == -1) {
                    Toast.makeText(CreateMatchActivity.this, "Eroare: Nu ești logat!", Toast.LENGTH_SHORT).show();
                    return;
                }

                MatchCreateRequest request = new MatchCreateRequest(title, location, time, currentUserId);

                RetrofitClient.getApi().createMatch(request).enqueue(new Callback<Match>() {
                    @Override
                    public void onResponse(Call<Match> call, Response<Match> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(CreateMatchActivity.this, "Meci creat cu succes!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(CreateMatchActivity.this, "Eroare la creare! Verifică formatul datei.", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Match> call, Throwable t) {
                        Toast.makeText(CreateMatchActivity.this, "Eroare rețea: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}