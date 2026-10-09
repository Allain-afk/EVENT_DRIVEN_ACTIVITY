package com.example.legaspi_eventdriven_activity.network;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {

    @GET("users")
    Call<List<User>> getUsers();

    @POST("signup")
    Call<SignUpResponse> signUpUser(@Body SignUpRequest signUpRequest);
}
