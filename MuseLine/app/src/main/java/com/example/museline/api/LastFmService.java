package com.example.museline.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface LastFmService {
    String API_KEY = "6da1a12a60292e31ca29b9d9ae1706c0";
    String BASE_URL = "https://ws.audioscrobbler.com/2.0/";

    @GET("?method=chart.gettoptracks&format=json")
    Call<TopTracksResponse> getTopTracks(@Query("api_key") String apiKey);

    @GET("?method=track.search&format=json")
    Call<SearchTracksResponse> searchTracks(
            @Query("track") String track,
            @Query("api_key") String apiKey
    );
} 