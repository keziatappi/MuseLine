package com.example.museline.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface LyricsService {
    String BASE_URL = "http://pggco8sksw8k408o80kw8os4.194.233.69.144.sslip.io/";

    @GET("lyrics/")
    Call<LyricsResponse> getLyrics(
            @Query("artist") String artist,
            @Query("song") String track
    );
} 