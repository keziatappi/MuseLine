package com.example.museline;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.museline.adapter.SongAdapter;
import com.example.museline.api.ApiClient;
import com.example.museline.api.LastFmService;
import com.example.museline.api.TopTracksResponse;
import com.example.museline.data.DatabaseHelper;
import com.example.museline.data.model.Track;
import com.example.museline.data.model.FavoriteTrack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    private RecyclerView recyclerView;
    private SongAdapter adapter;
    private EditText searchEditText;
    private ImageView icSearch;
    private Button btnRefresh;
    private DatabaseHelper databaseHelper;
    private TextView offlineStatus;
    private ProgressBar progressBar;
    private final Executor executor = Executors.newSingleThreadExecutor();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        databaseHelper = DatabaseHelper.getInstance(requireContext());

        LinearLayout searchLayout = view.findViewById(R.id.ll_search);
        searchEditText = (EditText) searchLayout.getChildAt(0);
        icSearch = view.findViewById(R.id.ic_search);
        offlineStatus = view.findViewById(R.id.tv_offline_status);
        btnRefresh = view.findViewById(R.id.btn_refresh);
        progressBar = view.findViewById(R.id.progress_bar);
        
        setupRecyclerView(view);
        setupSearchView();
        setupRefreshButton();
        loadTopTracks();
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
                boolean newFavoriteState = !track.isFavorite();
                if (newFavoriteState) {
                    databaseHelper.insertFavorite(
                        new FavoriteTrack(track.getName(), track.getArtist())
                    );
                } else {
                    databaseHelper.deleteFavorite(
                        track.getName(), track.getArtist()
                    );
                }
                track.setFavorite(newFavoriteState);
                requireActivity().runOnUiThread(() -> adapter.notifyDataSetChanged());
            });
        });
    }

    private void setupRefreshButton() {
        btnRefresh.setOnClickListener(v -> {
            loadTopTracks();
        });
    }

    private void setLoading(boolean loading) {
        if (loading) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void setupSearchView() {
        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH || 
                (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                String query = searchEditText.getText().toString().trim();
                if (!query.isEmpty()) {
                    Bundle bundle = new Bundle();
                    bundle.putString("search_query", query);
                    Navigation.findNavController(requireView())
                            .navigate(R.id.searchFragment, bundle);
                    return true;
                }
            }
            return false;
        });
        icSearch.setOnClickListener(v -> {
            String query = searchEditText.getText().toString().trim();
            if (!query.isEmpty()) {
                Bundle bundle = new Bundle();
                bundle.putString("search_query", query);
                Navigation.findNavController(requireView())
                        .navigate(R.id.searchFragment, bundle);
            }
        });
    }

    private void loadTopTracks() {
        setLoading(true);
        btnRefresh.setVisibility(View.GONE);
        ApiClient.getLastFmService().getTopTracks(LastFmService.API_KEY)
                .enqueue(new Callback<TopTracksResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<TopTracksResponse> call, @NonNull Response<TopTracksResponse> response) {
                        setLoading(false);
                        btnRefresh.setVisibility(View.GONE);
                        offlineStatus.setVisibility(View.GONE);
                        
                        if (response.isSuccessful() && response.body() != null) {
                            List<Track> tracks = new ArrayList<>();
                            List<TopTracksResponse.TrackData> trackList = 
                                    response.body().getTracks().getTrackList();

                            for (TopTracksResponse.TrackData trackData : trackList) {
                                Track track = new Track(
                                        trackData.getName(),
                                        trackData.getArtist().getName(),
                                        trackData.getUrl(),
                                        trackData.getImageUrl(),
                                        trackData.getListeners(),
                                        trackData.getPlaycount()
                                );
                                tracks.add(track);
                            }

                            executor.execute(() -> {
                                for (Track track : tracks) {
                                    boolean isFavorite = databaseHelper.isFavorite(track.getName(), track.getArtist());
                                    track.setFavorite(isFavorite);
                                }
                                requireActivity().runOnUiThread(() -> adapter.setTracks(tracks));
                            });

                            executor.execute(() -> {
                                databaseHelper.insertTracks(tracks);
                            });
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<TopTracksResponse> call, @NonNull Throwable t) {
                        setLoading(false);
                        if (t instanceof IOException) {
                            btnRefresh.setVisibility(View.VISIBLE);
                            offlineStatus.setVisibility(View.VISIBLE);
                            adapter.setTracks(new ArrayList<>());
                        }
                    }
                });
    }
}
