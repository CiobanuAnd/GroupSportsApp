package com.example.groupsportsapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.groupsportsapp.models.Match;
import com.example.groupsportsapp.adapters.MatchAdapter;
import com.example.groupsportsapp.R;
import com.example.groupsportsapp.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MatchAdapter adapter;
    private Button btnCreateMatch;
    private int currentUserId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currentUserId = getIntent().getIntExtra("USER_ID", -1);

        recyclerView = findViewById(R.id.recyclerViewMatches);
        btnCreateMatch = findViewById(R.id.btnGoToCreateMatch);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new MatchAdapter(this, currentUserId);
        recyclerView.setAdapter(adapter);

        btnCreateMatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CreateMatchActivity.class);
                intent.putExtra("USER_ID", currentUserId);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchMatchesFromServer();
    }

    private void fetchMatchesFromServer() {
        RetrofitClient.getApi().getMatches().enqueue(new Callback<List<Match>>() {
            @Override
            public void onResponse(Call<List<Match>> call, Response<List<Match>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter.setMatches(response.body());
                } else {
                    Toast.makeText(MainActivity.this, "Eroare la încărcarea meciurilor", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Match>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Eroare server: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}