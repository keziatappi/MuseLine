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
import com.example.museline.data.DatabaseHelper;
import com.example.museline.data.model.FavoriteTrack;
import com.example.museline.data.model.Track;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class FavoriteFragment extends Fragment {
    private RecyclerView recyclerView;
    private SongAdapter adapter;
    private DatabaseHelper databaseHelper;
    private EditText searchEditText;
    private ImageView icSearch;
    private final Executor executor = Executors.newSingleThreadExecutor();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorite, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        databaseHelper = DatabaseHelper.getInstance(requireContext());

        LinearLayout searchLayout = view.findViewById(R.id.ll_search);
        icSearch = view.findViewById(R.id.ic_search);
        searchEditText = (EditText) searchLayout.getChildAt(0);
        recyclerView = view.findViewById(R.id.recycler_view);
        setupRecyclerView();
        setupSearchView();
        loadFavorites();
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

    private void setupRecyclerView() {
        adapter = new SongAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(track -> {
            Intent intent = new Intent(requireContext(), LyricActivity.class);
            intent.putExtra("track_name", track.getName());
            intent.putExtra("artist_name", track.getArtist());
            startActivity(intent);
        });

        adapter.setOnFavoriteClickListener(track -> {
            executor.execute(() -> {
                databaseHelper.deleteFavorite(track.getName(), track.getArtist());
                loadFavorites();
            });
        });
    }

    private void loadFavorites() {
        executor.execute(() -> {
            List<FavoriteTrack> favoriteTracks = databaseHelper.getAllFavorites();
            List<Track> tracks = new ArrayList<>();
            
            for (FavoriteTrack favoriteTrack : favoriteTracks) {
                Track track = new Track(
                    favoriteTrack.getName(),
                    favoriteTrack.getArtist(),
                    "",
                    null,
                    0,
                    0
                );
                track.setFavorite(true);
                tracks.add(track);
            }
            
            requireActivity().runOnUiThread(() -> adapter.setTracks(tracks));
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadFavorites();
    }
}
