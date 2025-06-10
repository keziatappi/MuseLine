package com.example.museline;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.museline.api.ApiClient;
import com.example.museline.api.LyricsResponse;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LyricActivity extends AppCompatActivity {
    private TextView titleText;
    private TextView artistText;
    private TextView lyricsText;
    private TextView autoScrollButton;
    private ScrollView scrollView;
    private Button btnRefresh;
    private TextView tvOfflineMode;
    private ProgressBar progressBar;

    private boolean isAutoScrolling = false;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Handler scrollHandler = new Handler(Looper.getMainLooper());
    private final int scrollSpeed = 3;
    private final int scrollDelay = 30;

    private final Runnable autoScrollRunnable = new Runnable() {
        @Override
        public void run() {
            if (isAutoScrolling) {
                scrollView.smoothScrollBy(0, scrollSpeed);
                scrollHandler.postDelayed(this, scrollDelay);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lyric);

        titleText = findViewById(R.id.tvTitle);
        artistText = findViewById(R.id.tvArtistName);
        lyricsText = findViewById(R.id.tv_lyrics);
        autoScrollButton = findViewById(R.id.btnAutoScroll);
        scrollView = findViewById(R.id.scroll_view);
        progressBar = findViewById(R.id.progress_bar);
        btnRefresh = findViewById(R.id.btn_refresh);
        tvOfflineMode = findViewById(R.id.tv_offline_status);

        btnRefresh.setVisibility(View.GONE);
        tvOfflineMode.setVisibility(View.GONE);

        ImageView backButton = findViewById(R.id.iv_back);

        String trackName = getIntent().getStringExtra("track_name");
        String artistName = getIntent().getStringExtra("artist_name");

        if (trackName != null && artistName != null) {
            titleText.setText(trackName);
            artistText.setText(artistName);
            loadLyrics(trackName, artistName);
        }

        setupAutoScroll();
        backButton.setOnClickListener(v -> finish());
    }

    private void setupAutoScroll() {
        autoScrollButton.setOnClickListener(v -> {
            isAutoScrolling = !isAutoScrolling;
            if (isAutoScrolling) {
                scrollHandler.post(autoScrollRunnable);
                autoScrollButton.setText("Stop Auto-Scroll");
            } else {
                scrollHandler.removeCallbacks(autoScrollRunnable);
                autoScrollButton.setText("Auto Scroll");
            }
        });
    }

    private void loadLyrics(String trackName, String artistName) {
        btnRefresh.setVisibility(View.GONE);
        tvOfflineMode.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        executor.execute(() -> {
            ApiClient.getLyricsService().getLyrics(artistName, trackName)
                    .enqueue(new Callback<LyricsResponse>() {
                        @Override
                        public void onResponse(Call<LyricsResponse> call, Response<LyricsResponse> response) {
                            progressBar.setVisibility(View.GONE);
                            if (response.isSuccessful() && response.body() != null) {
                                String lyrics = formatLyrics(response.body().getLyrics());
                                runOnUiThread(() -> displayLyrics(lyrics));
                            } else {
                                runOnUiThread(() -> lyricsText.setText("Lyrics not available"));
                            }
                        }

                        @Override
                        public void onFailure(Call<LyricsResponse> call, Throwable t) {
                            progressBar.setVisibility(View.GONE);
                            if (t instanceof IOException) {
                                btnRefresh.setVisibility(View.VISIBLE);
                                tvOfflineMode.setVisibility(View.VISIBLE);
                            } else {
                                runOnUiThread(() -> lyricsText.setText("Lyrics not available"));
                            }
                        }
                    });
        });
    }

    private String formatLyrics(String lyrics) {
        return lyrics.replace("\\n", "\n")
                .replace("\\r", "")
                .replace("\n\n\n", "\n\n")
                .trim();
    }

    private void displayLyrics(String lyrics) {
        lyricsText.setText(lyrics);
        scrollView.scrollTo(0, 0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopAutoScroll();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopAutoScroll();
    }

    private void stopAutoScroll() {
        isAutoScrolling = false;
        scrollHandler.removeCallbacks(autoScrollRunnable);
        autoScrollButton.setText("Auto Scroll");
    }
}
