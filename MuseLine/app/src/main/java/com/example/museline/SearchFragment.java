package com.example.museline;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.museline.adapter.SongAdapter;
import com.example.museline.api.ApiClient;
import com.example.museline.api.LastFmService;
import com.example.museline.api.SearchTracksResponse;
import com.example.museline.data.DatabaseHelper;
import com.example.museline.data.model.FavoriteTrack;
import com.example.museline.data.model.Track;
import com.example.museline.data.model.TrackHistory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment {
    private RecyclerView recyclerView;
    private SongAdapter adapter;
    private EditText searchEditText;
    private ImageView icSearch;
    private DatabaseHelper databaseHelper;
    private final Executor executor = Executors.newSingleThreadExecutor();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        databaseHelper = DatabaseHelper.getInstance(requireContext());

        LinearLayout searchLayout = view.findViewById(R.id.ll_search);
        searchEditText = (EditText) searchLayout.getChildAt(0);
        icSearch = view.findViewById(R.id.ic_search);

        setupRecyclerView(view);
        setupSearchView();

        String searchQuery = getArguments() != null ? getArguments().getString("search_query") : null;
        if (searchQuery != null && !searchQuery.isEmpty()) {
            searchEditText.setText(searchQuery);
            performSearch(searchQuery);
        } else {
            loadSearchHistory();
        }
    }

    private void setupRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new SongAdapter();
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(track -> {
            Intent intent = new Intent(requireContext(), LyricActivity.class);
            intent.putExtra("track_name", track.getName());
            intent.putExtra("artist_name", track.getArtist());
            startActivity(intent);
        });

        adapter.setOnFavoriteClickListener(track -> {
            executor.execute(() -> {
                if (!track.isFavorite()) {
                    databaseHelper.insertFavorite(
                        new FavoriteTrack(track.getName(), track.getArtist())
                    );
                } else {
                    databaseHelper.deleteFavorite(
                        track.getName(), track.getArtist()
                    );
                }
                track.setFavorite(!track.isFavorite());
                requireActivity().runOnUiThread(() -> adapter.notifyDataSetChanged());
            });
        });
    }

    private void setupSearchView() {
        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                String query = searchEditText.getText().toString().trim();
                if (!query.isEmpty()) {
                    performSearch(query);
                    return true;
                }
            }
            return false;
        });

        icSearch.setOnClickListener(v -> {
            String query = searchEditText.getText().toString().trim();
            if (!query.isEmpty()) {
                performSearch(query);
            }
        });
    }

    private void performSearch(String query) {
        ApiClient.getLastFmService().searchTracks(query, LastFmService.API_KEY)
                .enqueue(new Callback<SearchTracksResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<SearchTracksResponse> call,
                                         @NonNull Response<SearchTracksResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<Track> tracks = new ArrayList<>();
                            List<SearchTracksResponse.TrackData> trackList = response.body().getTracks();

                            for (SearchTracksResponse.TrackData trackData : trackList) {
                                Track track = new Track(
                                        trackData.getName(),
                                        trackData.getArtist(),
                                        trackData.getUrl(),
                                        trackData.getImageUrl(),
                                        0,
                                        0
                                );
                                tracks.add(track);
                            }

                            executor.execute(() -> {
                                long currentTime = System.currentTimeMillis();
                                for (Track track : tracks) {
                                    if (databaseHelper.historyExists(track.getName(), track.getArtist())) {
                                        databaseHelper.updateHistoryTimestamp(
                                            track.getName(), track.getArtist(), currentTime
                                        );
                                    } else {
                                        TrackHistory history = new TrackHistory(track.getName(), track.getArtist());
                                        databaseHelper.insertHistory(history);
                                    }
                                }

                                for (Track track : tracks) {
                                    boolean isFavorite = databaseHelper.isFavorite(track.getName(), track.getArtist());
                                    track.setFavorite(isFavorite);
                                }
                                
                                requireActivity().runOnUiThread(() -> 
                                    adapter.setTracks(tracks)
                                );
                            });
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<SearchTracksResponse> call,
                                        @NonNull Throwable t) {
                        searchEditText.setText("");
                        loadSearchHistory();
                    }
                });
    }

    private void loadSearchHistory() {
        executor.execute(() -> {
            List<Track> historyTracks = getHistoryTracks();
            for (Track track : historyTracks) {
                boolean isFavorite = databaseHelper.isFavorite(track.getName(), track.getArtist());
                track.setFavorite(isFavorite);
            }
            requireActivity().runOnUiThread(() -> adapter.setTracks(historyTracks));
        });
    }

    private List<Track> getHistoryTracks() {
        List<TrackHistory> history = databaseHelper.getAllHistory();
        List<Track> historyTracks = new ArrayList<>();

        int count = Math.min(history.size(), 10);
        for (int i = 0; i < count; i++) {
            TrackHistory item = history.get(i);
            Track track = new Track(
                    item.getTrackName(),
                    item.getArtistName(),
                    "",
                    null,
                    0,
                    0
            );
            track.setFavorite(databaseHelper.isFavorite(track.getName(), track.getArtist()));
            historyTracks.add(track);
        }
        
        return historyTracks;
    }
}
