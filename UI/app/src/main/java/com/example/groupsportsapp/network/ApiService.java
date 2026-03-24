package com.example.groupsportsapp.network;

import com.example.groupsportsapp.models.AuthResponse;
import com.example.groupsportsapp.models.LoginRequest;
import com.example.groupsportsapp.models.Match;
import com.example.groupsportsapp.models.MatchCreateRequest;
import com.example.groupsportsapp.models.MatchJoinRequest;
import com.example.groupsportsapp.models.RegisterRequest;
import com.example.groupsportsapp.models.SimpleResponse;
import com.example.groupsportsapp.models.Player;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Path;
import retrofit2.http.POST;
import java.util.List;
import retrofit2.http.GET;
import retrofit2.http.DELETE;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface ApiService {

    @POST("login")
    Call<AuthResponse> loginUser(@Body LoginRequest request);

    @POST("register")
    Call<AuthResponse> registerUser(@Body RegisterRequest request);

    @GET("/")
    Call<List<Match>> getMatches();

    @POST("create")
    Call<Match> createMatch(@Body MatchCreateRequest request);

    @POST("join")
    Call<SimpleResponse> joinMatch(@Body MatchJoinRequest request);

    @GET("matches/{match_id}/players")
    Call<List<Player>> getMatchPlayers(@Path("match_id") int matchId);

    @DELETE("matches/{match_id}")
    Call<SimpleResponse> deleteMatch(@Path("match_id") int matchId, @Query("user_id") int userId);

    @PUT("matches/{match_id}")
    Call<SimpleResponse> updateMatch(@Path("match_id") int matchId, @Body MatchCreateRequest request);
}