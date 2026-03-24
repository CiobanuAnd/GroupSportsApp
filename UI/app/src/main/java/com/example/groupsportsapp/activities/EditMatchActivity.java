package com.example.groupsportsapp.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.groupsportsapp.R;
import com.example.groupsportsapp.models.MatchCreateRequest;
import com.example.groupsportsapp.models.SimpleResponse;
import com.example.groupsportsapp.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditMatchActivity extends AppCompatActivity {

    private EditText etTitle, etLocation, etTime;
    private int matchId, createdBy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_match);

        etTitle = findViewById(R.id.etEditMatchTitle);
        etLocation = findViewById(R.id.etEditMatchLocation);
        etTime = findViewById(R.id.etEditMatchTime);

        Button btnBack = findViewById(R.id.btnBackEdit);
        Button btnSave = findViewById(R.id.btnSaveEditedMatch);

        matchId = getIntent().getIntExtra("MATCH_ID", -1);
        String title = getIntent().getStringExtra("MATCH_TITLE");
        String location = getIntent().getStringExtra("MATCH_LOCATION");
        String time = getIntent().getStringExtra("MATCH_TIME");
        createdBy = getIntent().getIntExtra("MATCH_CREATED_BY", -1);

        if (title != null) etTitle.setText(title);
        if (location != null) etLocation.setText(location);
        if (time != null) etTime.setText(time);

        btnBack.setOnClickListener(v -> finish());

        btnSave.setOnClickListener(v -> {
            String newTitle = etTitle.getText().toString().trim();
            String newLocation = etLocation.getText().toString().trim();
            String newTime = etTime.getText().toString().trim();

            if (newTitle.isEmpty() || newLocation.isEmpty() || newTime.isEmpty()) {
                Toast.makeText(EditMatchActivity.this, "Completează toate câmpurile!", Toast.LENGTH_SHORT).show();
                return;
            }

            MatchCreateRequest request = new MatchCreateRequest(newTitle, newLocation, newTime, createdBy);

            RetrofitClient.getApi().updateMatch(matchId, request).enqueue(new Callback<SimpleResponse>() {
                @Override
                public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(EditMatchActivity.this, "S-a actualizat cu succes!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(EditMatchActivity.this, "Eroare la actualizare!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<SimpleResponse> call, Throwable t) {
                    Toast.makeText(EditMatchActivity.this, "Eroare rețea: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}