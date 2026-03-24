package com.example.groupsportsapp.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.groupsportsapp.R;
import com.example.groupsportsapp.models.Match;
import com.example.groupsportsapp.models.MatchJoinRequest;
import com.example.groupsportsapp.models.Player;
import com.example.groupsportsapp.models.SimpleResponse;
import com.example.groupsportsapp.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MatchAdapter extends RecyclerView.Adapter<MatchAdapter.MatchViewHolder> {

    private List<Match> matchList = new ArrayList<>();
    private Context context;
    private int currentUserId;

    public MatchAdapter(Context context, int currentUserId) {
        this.context = context;
        this.currentUserId = currentUserId;
    }

    public void setMatches(List<Match> matches) {
        this.matchList = matches;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MatchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_match, parent, false);
        return new MatchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MatchViewHolder holder, int position) {
        Match currentMatch = matchList.get(position);

        holder.tvTitle.setText(currentMatch.getTitle());
        holder.tvLocation.setText("Locație: " + currentMatch.getLocation());
        String timeStr = currentMatch.getMatchTime().replace("T", " ");
        holder.tvTime.setText("Data: " + timeStr);

        if (currentUserId == currentMatch.getCreatedBy()) {
            holder.layoutCreatorActions.setVisibility(View.VISIBLE);
        } else {
            holder.layoutCreatorActions.setVisibility(View.GONE);
        }

        holder.btnJoin.setOnClickListener(v -> {
            MatchJoinRequest request = new MatchJoinRequest(currentUserId, currentMatch.getId());
            RetrofitClient.getApi().joinMatch(request).enqueue(new Callback<SimpleResponse>() {
                @Override
                public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(context, "Participi la meciul: " + currentMatch.getTitle(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Ești deja înscris la acest meci!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<SimpleResponse> call, Throwable t) {
                    Toast.makeText(context, "Eroare: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        holder.btnViewPlayers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RetrofitClient.getApi().getMatchPlayers(currentMatch.getId()).enqueue(new Callback<List<Player>>() {
                    @Override
                    public void onResponse(Call<List<Player>> call, Response<List<Player>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<Player> players = response.body();

                            StringBuilder playersText = new StringBuilder();
                            if (players.isEmpty()) {
                                playersText.append("Încă nu s-a înscris nimeni. Fii tu primul!");
                            } else {
                                for (Player p : players) {
                                    playersText.append("• ").append(p.getUsername()).append("\n");
                                }
                            }

                            new AlertDialog.Builder(context)
                                    .setTitle("Jucători Înscriși")
                                    .setMessage(playersText.toString())
                                    .setPositiveButton("Închide", null)
                                    .show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Player>> call, Throwable t) {
                        Toast.makeText(context, "Eroare la preluarea jucătorilor!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        holder.btnDeleteMatch.setOnClickListener(v -> {
            RetrofitClient.getApi().deleteMatch(currentMatch.getId(), currentUserId).enqueue(new Callback<SimpleResponse>() {
                @Override
                public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(context, "Meciul '" + currentMatch.getTitle() + "' a fost șters!", Toast.LENGTH_SHORT).show();

                        matchList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, matchList.size());
                    } else {
                        Toast.makeText(context, "Eroare la ștergerea meciului!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<SimpleResponse> call, Throwable t) {
                    Toast.makeText(context, "Eroare rețea: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        holder.btnEditMatch.setOnClickListener(v -> {

            Intent intent = new Intent(context, com.example.groupsportsapp.activities.EditMatchActivity.class);
            intent.putExtra("MATCH_ID", currentMatch.getId());
            intent.putExtra("MATCH_TITLE", currentMatch.getTitle());
            intent.putExtra("MATCH_LOCATION", currentMatch.getLocation());
            intent.putExtra("MATCH_TIME", currentMatch.getMatchTime());
            intent.putExtra("MATCH_CREATED_BY", currentMatch.getCreatedBy());

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return matchList.size();
    }

    class MatchViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvLocation, tvTime;
        Button btnJoin, btnViewPlayers, btnEditMatch, btnDeleteMatch;
        View layoutCreatorActions;

        public MatchViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvMatchTitle);
            tvLocation = itemView.findViewById(R.id.tvMatchLocation);
            tvTime = itemView.findViewById(R.id.tvMatchTime);
            btnJoin = itemView.findViewById(R.id.btnJoinMatch);
            btnViewPlayers = itemView.findViewById(R.id.btnViewPlayers);

            layoutCreatorActions = itemView.findViewById(R.id.layoutCreatorActions);
            btnEditMatch = itemView.findViewById(R.id.btnEditMatch);
            btnDeleteMatch = itemView.findViewById(R.id.btnDeleteMatch);
        }
    }
}