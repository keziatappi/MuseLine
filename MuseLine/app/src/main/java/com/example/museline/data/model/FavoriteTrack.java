package com.example.museline.data.model;

public class FavoriteTrack {
    private int id;
    private String name;
    private String artist;

    public FavoriteTrack(String name, String artist) {
        this.name = name;
        this.artist = artist;
    }

    public static FavoriteTrack fromTrack(Track track) {
        return new FavoriteTrack(track.getName(), track.getArtist());
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getArtist() { return artist; }
    public void setArtist(String artist) { this.artist = artist; }
} 