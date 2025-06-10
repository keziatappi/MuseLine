package com.example.museline.data.model;

public class Track {
    private int id;
    private String name;
    private String artist;
    private String url;
    private String imageUrl;
    private long listeners;
    private long playcount;
    private boolean isFavorite;
    private long timestamp;

    public Track(String name, String artist, String url, String imageUrl, long listeners, long playcount) {
        this.name = name;
        this.artist = artist;
        this.url = url;
        this.imageUrl = imageUrl;
        this.listeners = listeners;
        this.playcount = playcount;
        this.timestamp = System.currentTimeMillis();
        this.isFavorite = false;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getArtist() { return artist; }
    public void setArtist(String artist) { this.artist = artist; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public long getListeners() { return listeners; }
    public void setListeners(long listeners) { this.listeners = listeners; }
    public long getPlaycount() { return playcount; }
    public void setPlaycount(long playcount) { this.playcount = playcount; }
    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
} 