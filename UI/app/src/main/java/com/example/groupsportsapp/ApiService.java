package com.example.groupsportsapp;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    @POST("login")
    Call<AuthResponse> loginUser(@Body LoginRequest request);

    @POST("register")
    Call<AuthResponse> registerUser(@Body RegisterRequest request);
}