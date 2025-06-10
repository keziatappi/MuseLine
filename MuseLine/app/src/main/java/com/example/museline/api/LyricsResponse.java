package com.example.museline.api;

import com.google.gson.annotations.SerializedName;

public class LyricsResponse {
    @SerializedName("lyrics")
    private String lyrics;

    public String getLyrics() {
        return lyrics;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }
} 