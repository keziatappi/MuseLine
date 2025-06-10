package com.example.museline.data.model;

public class TrackHistory {
    private int id;
    private String trackName;
    private String artistName;
    private long timestamp;

    public TrackHistory(String trackName, String artistName) {
        this.trackName = trackName;
        this.artistName = artistName;
        this.timestamp = System.currentTimeMillis();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTrackName() { return trackName; }
    public void setTrackName(String trackName) { this.trackName = trackName; }
    public String getArtistName() { return artistName; }
    public void setArtistName(String artistName) { this.artistName = artistName; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
} 