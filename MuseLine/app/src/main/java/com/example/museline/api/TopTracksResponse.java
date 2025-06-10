package com.example.museline.api;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class TopTracksResponse {
    @SerializedName("tracks")
    private TracksData tracks;

    public static class TracksData {
        @SerializedName("track")
        private List<TrackData> trackList;

        public List<TrackData> getTrackList() {
            return trackList;
        }
    }

    public static class TrackData {
        @SerializedName("name")
        private String name;
        @SerializedName("playcount")
        private String playcount;
        @SerializedName("listeners")
        private String listeners;
        @SerializedName("url")
        private String url;
        @SerializedName("artist")
        private ArtistData artist;
        @SerializedName("image")
        private List<ImageData> images;

        public String getName() { return name; }
        public long getPlaycount() { return Long.parseLong(playcount); }
        public long getListeners() { return Long.parseLong(listeners); }
        public String getUrl() { return url; }
        public ArtistData getArtist() { return artist; }
        public String getImageUrl() {
            if (images != null && !images.isEmpty()) {
                return images.get(images.size() - 1).url;
            }
            return null;
        }
    }

    public static class ArtistData {
        @SerializedName("name")
        private String name;
        @SerializedName("url")
        private String url;

        public String getName() { return name; }
        public String getUrl() { return url; }
    }

    public static class ImageData {
        @SerializedName("#text")
        private String url;
        @SerializedName("size")
        private String size;

        public String getUrl() { return url; }
        public String getSize() { return size; }
    }

    public TracksData getTracks() {
        return tracks;
    }
} 