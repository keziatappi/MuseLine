package com.example.museline.api;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class SearchTracksResponse {
    @SerializedName("results")
    private ResultsData results;

    public static class ResultsData {
        @SerializedName("trackmatches")
        private TrackMatches trackmatches;
    }

    public static class TrackMatches {
        @SerializedName("track")
        private List<TrackData> tracks;

        public List<TrackData> getTracks() {
            return tracks;
        }
    }

    public static class TrackData {
        @SerializedName("name")
        private String name;
        @SerializedName("artist")
        private String artist;
        @SerializedName("url")
        private String url;
        @SerializedName("listeners")
        private String listeners;
        @SerializedName("image")
        private List<ImageData> images;

        public String getName() { return name; }
        public String getArtist() { return artist; }
        public String getUrl() { return url; }
        public long getListeners() { return Long.parseLong(listeners); }
        public String getImageUrl() {
            if (images != null && !images.isEmpty()) {
                return images.get(images.size() - 1).url;
            }
            return null;
        }
    }

    public static class ImageData {
        @SerializedName("#text")
        private String url;
        @SerializedName("size")
        private String size;

        public String getUrl() { return url; }
        public String getSize() { return size; }
    }

    public List<TrackData> getTracks() {
        return results != null && results.trackmatches != null ? 
               results.trackmatches.tracks : null;
    }
} 